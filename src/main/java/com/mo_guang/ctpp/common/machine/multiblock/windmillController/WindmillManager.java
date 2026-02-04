package com.mo_guang.ctpp.common.machine.multiblock.windmillController;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;

import java.util.*;
import java.util.concurrent.*;

@Mod.EventBusSubscriber(modid = "ctpp")
public class WindmillManager {
    private static final WindmillManager INSTANCE = new WindmillManager();

    private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder()
            .setNameFormat("CTNH Windmill Scanner Thread-%d")
            .setDaemon(true)
            .setPriority(Thread.NORM_PRIORITY - 1)
            .build();
    private volatile ExecutorService asyncScanner;

    private ExecutorService getAsyncScanner() {
        // 双重校验锁：避免多线程重复创建，懒加载（用到才创建）
        if (asyncScanner == null || asyncScanner.isShutdown() || asyncScanner.isTerminated()) {
            synchronized (WindmillManager.class) {
                if (asyncScanner == null || asyncScanner.isShutdown() || asyncScanner.isTerminated()) {
                    // 单线程池：完全规避MC区块/BE的并发查询问题，性能最优
                    asyncScanner = Executors.newSingleThreadExecutor(THREAD_FACTORY);
                }
            }
        }
        return asyncScanner;
    }

    private WindmillManager() {}

    public static WindmillManager getInstance() {
        return INSTANCE;
    }

    /**
     * 提交风车扫描任务
     * @param serverLevel 服务端世界（必须非客户端）
     * @param controllerPos 风车控制中心位置
     * @param scanRange 扫描半径（你的代码中是32，可传参灵活调整）
     * @param legalDistance 控制器冲突检测距离（你的代码中是64）
     */
    public void submitScanTask(ServerLevel serverLevel, BlockPos controllerPos, int scanRange, int legalDistance) {
        // 仅服务端执行，避免客户端调用世界方法崩溃
        if (serverLevel.isClientSide()) return;
        // 提交异步任务到线程池
        getAsyncScanner().submit(() -> {
            try {
                // ******** 异步扫描逻辑（无世界修改，仅查询）********
                Set<BlockPos> foundWindmills = scanWindmillsInRange(serverLevel, controllerPos, scanRange);
                // ******** 扫描完成，切回服务端主线程更新数据（必须！）********
                serverLevel.getServer().execute(() -> {
                    updateWindmillData(serverLevel, controllerPos, foundWindmills, legalDistance);
                });
            } catch (Exception e) {
                // 捕获异步异常，避免线程池挂掉
                e.printStackTrace();
            }
        });
    }

    /**
     * 异步扫描指定范围内的所有风车轴承（WindmillBearingBlockEntity）
     * 仅做世界查询，无任何修改操作，保证线程安全
     */
    private Set<BlockPos> scanWindmillsInRange(ServerLevel serverLevel, BlockPos center, int scanRange) {
        Set<BlockPos> windmillPos = new HashSet<>();
        int range = scanRange;
        // 遍历范围内的所有区块（减少无效遍历）
        for (int cx = center.getX() - range; cx <= center.getX() + range; cx += 16) {
            for (int cz = center.getZ() - range; cz <= center.getZ() + range; cz += 16) {
                LevelChunk chunk = serverLevel.getChunkAt(new BlockPos(cx, 0, cz));
                // 遍历区块内的所有BlockEntity，筛选风车轴承
                chunk.getBlockEntities().forEach((pos, be) -> {
                    // 1. 检查是否在扫描半径内
                    if (pos.distSqr(center) > (long) range * range) return;
                    // 2. 检查是否是风车轴承且能生成转速
                    if (be instanceof WindmillBearingBlockEntity windmillBE && windmillBE.getGeneratedSpeed() != 0) {
                        windmillPos.add(pos.immutable()); // 存入不可变坐标，避免后续修改
                    }
                });
            }
        }
        return windmillPos;
    }
    private void updateWindmillData(ServerLevel serverLevel, BlockPos controllerPos, Set<BlockPos> foundWindmills, int legalDistance) {
        WindmillSavedData savedData = WindmillSavedData.get(serverLevel);
        // 1. 注册新发现的风车
        for (BlockPos foundWindmill : foundWindmills) {
            if (!savedData.getAllWindmills().contains(foundWindmill)) {
                savedData.registerWindmill(foundWindmill);
                savedData.setDirty(); // 必须调用！标记数据修改，否则不会持久化到硬盘
            }
        }
        // 2. 控制器冲突检测
        boolean hasConflict = savedData.hasConflictingController(controllerPos, legalDistance);
        // 3. 更新控制中心状态（严格判空，避免机器被破坏后空指针）
        BlockEntity be = serverLevel.getBlockEntity(controllerPos);
        if (be instanceof IMachineBlockEntity machineBE && machineBE.getMetaMachine() instanceof WindMillControlMachine controller) {
            controller.hasConflictingController = hasConflict;
            controller.windmillAround.clear();
            controller.windmillAround.addAll(foundWindmills);
        }
    }

    /**
     * 服务器关闭时关闭线程池，释放资源（必须！）
     */
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        WindmillManager manager = getInstance();
        if (manager.asyncScanner != null) {
            manager.asyncScanner.shutdownNow(); // 立即关闭，避免线程残留
            try {
                // 等待5秒，确保任务完成，超时则强制关闭
                if (!manager.asyncScanner.awaitTermination(5, TimeUnit.SECONDS)) {
                    manager.asyncScanner.shutdownNow();
                }
            } catch (InterruptedException e) {
                manager.asyncScanner.shutdownNow();
            }
            manager.asyncScanner = null;
        }
    }
}