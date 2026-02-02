package com.mo_guang.ctpp.common.machine.multiblock.windmillController;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = "ctpp")
public class WindmillManager {
    private static final WindmillManager INSTANCE = new WindmillManager();
    // 异步线程池（核心线程数根据服务器性能调整，建议2-4）
    private final ExecutorService asyncScanner = Executors.newFixedThreadPool(2, r -> {
        Thread thread = new Thread(r, "Windmill-Scanner-Thread");
        thread.setDaemon(true); // 守护线程，服务器关闭时自动销毁
        return thread;
    });

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
        asyncScanner.submit(() -> {
            try {
                // ******** 异步扫描逻辑（无世界修改，仅查询）********
                Set<BlockPos> foundWindmills = scanWindmillsInRange(serverLevel, controllerPos, scanRange);
                // ******** 扫描完成，切回服务端主线程更新数据（必须！）********
                serverLevel.getServer().execute(() -> {
                    WindmillSavedData savedData = WindmillSavedData.get(serverLevel);
                    // 1. 将扫描到的风车添加到数据表
                    for (BlockPos foundWindmill : foundWindmills) {
                        if (savedData.getAllWindmills().contains(foundWindmill)) continue;
                        savedData.registerWindmill(controllerPos);
                    }
                    // 2. 顺带做控制器冲突检测（复用你的逻辑）
                    boolean hasConflict = savedData.hasConflictingController(controllerPos, legalDistance);
                    // 3. 更新控制中心的冲突状态（如果能获取到控制中心实例）
                    BlockEntity be = serverLevel.getBlockEntity(controllerPos);
                    if (be instanceof IMachineBlockEntity machineBE && machineBE.getMetaMachine() instanceof WindMillControlMachine controller) {
                        controller.hasConflictingController = hasConflict;
                        controller.windmillAround.clear();
                        controller.windmillAround.addAll(foundWindmills);
                    }
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

    /**
     * 服务器关闭时关闭线程池，释放资源（必须！）
     */
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        try {
            WindmillManager manager = getInstance();
            manager.asyncScanner.shutdown();
            // 等待线程池中的任务执行完成（最多5秒）
            if (!manager.asyncScanner.awaitTermination(5, TimeUnit.SECONDS)) {
                manager.asyncScanner.shutdownNow(); // 强制关闭未完成的任务
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}