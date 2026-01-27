package com.mo_guang.ctpp.common.machine.multiblock.windmillController;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.List;

public class WindmillSavedData extends SavedData {
    public static final ResourceLocation ID = ResourceLocation.tryBuild("ctpp", "windmill_data");
    private final List<BlockPos> windmillPositions = new ArrayList<>();
    private final List<BlockPos> formedControllerPositions = new ArrayList<>();
    public WindmillSavedData() {}
    public WindmillSavedData(CompoundTag nbt) {
        this.load(nbt); // 调用自定义的加载逻辑，完成数据反序列化
    }
    /**
     * 注册风车坐标（风车生成/加载时调用）
     */
    public void registerWindmill(BlockPos pos) {
        if (!windmillPositions.contains(pos)) {
            windmillPositions.add(pos);
            setDirty(); // 标记数据已修改，MC会自动保存到存档
        }
    }

    /**
     * 注销风车坐标（风车被破坏/卸载时调用）
     */
    public void unregisterWindmill(BlockPos pos) {
        if (windmillPositions.remove(pos)) {
            setDirty(); // 标记数据已修改，MC会自动保存到存档
        }
    }

    /**
     * 获取当前存档内所有风车坐标
     */
    public List<BlockPos> getAllWindmills() {
        // 返回不可修改列表，防止外部直接篡改底层数据，保证线程安全
        return List.copyOf(windmillPositions);
    }

    /**
     * 清空当前存档内所有风车数据（可选，用于调试/重置）
     */
    public void clearAllWindmills() {
        windmillPositions.clear();
        setDirty();
    }
    /**
     * 注册已成型的风车控制中心
     */
    public void registerFormedController(BlockPos pos) {
        if (!formedControllerPositions.contains(pos)) {
            formedControllerPositions.add(pos);
            setDirty(); // 标记数据修改，触发持久化
        }
    }

    /**
     * 注销失效的风车控制中心（结构破坏时调用）
     */
    public void unregisterFormedController(BlockPos pos) {
        if (formedControllerPositions.remove(pos)) {
            setDirty(); // 标记数据修改，触发持久化
        }
    }

    /**
     * 获取当前存档内所有已成型的风车控制中心
     */
    public List<BlockPos> getAllFormedControllers() {
        return List.copyOf(formedControllerPositions); // 返回不可修改副本，保证数据安全
    }

    /**
     * 校验指定位置周围LEGAL_DISTANCE内是否存在其他已成型控制中心
     * @param currentPos 当前控制中心位置
     * @param legalDistance 合法距离阈值
     * @return true=存在冲突，false=无冲突
     */
    public boolean hasConflictingController(BlockPos currentPos, int legalDistance) {
        // 遍历所有已成型控制中心，排除自身，校验距离
        for (BlockPos controllerPos : formedControllerPositions) {
            if (!currentPos.equals(controllerPos)) { // 排除自己
                // 计算两点之间的距离（平方比较，比sqrt更高效，避免浮点运算）
                double distanceSqr = currentPos.distSqr(controllerPos);
                double legalDistanceSqr = (double) legalDistance * legalDistance;
                if (distanceSqr <= legalDistanceSqr) { // 范围内存在其他控制中心
                    return true;
                }
            }
        }
        return false;
    }
    public void notifyAllControllersRefresh(ServerLevel serverLevel, BlockPos excludePos) {
        // 遍历所有已注册的控制中心
        for (BlockPos controllerPos : formedControllerPositions) {
            // 排除自己（刚失效的那个控制中心）
            if (controllerPos.equals(excludePos)) {
                continue;
            }
            // 获取控制中心的机器实例
            var blockEntity = serverLevel.getBlockEntity(controllerPos);
            if (blockEntity instanceof IMachineBlockEntity machineBlockEntity) {
                var machine = machineBlockEntity.getMetaMachine();
                // 判断是否为存活的风车控制中心
                if (machine instanceof WindMillControlMachine windMillControl && windMillControl.isFormed()) {
                    // 调用刷新方法，重新校验冲突
                    windMillControl.refreshControllerState();
                }
            }
        }
    }
    // ************************ NBT序列化/反序列化（持久化核心） ************************
    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag posList = new ListTag();
        for (BlockPos pos : windmillPositions) {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt("x", pos.getX());
            posTag.putInt("y", pos.getY());
            posTag.putInt("z", pos.getZ());
            posList.add(posTag);
        }
        nbt.put("windmill_positions", posList);
        ListTag controllerList = new ListTag();
        for (BlockPos pos : formedControllerPositions) {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt("cx", pos.getX());
            posTag.putInt("cy", pos.getY());
            posTag.putInt("cz", pos.getZ());
            controllerList.add(posTag);
        }
        nbt.put("formed_controller_positions", controllerList);
        return nbt;
    }

    public void load(CompoundTag nbt) {
        windmillPositions.clear();
        ListTag posList = nbt.getList("windmill_positions", Tag.TAG_COMPOUND);
        for (int i = 0; i < posList.size(); i++) {
            CompoundTag posTag = posList.getCompound(i);
            int x = posTag.getInt("x");
            int y = posTag.getInt("y");
            int z = posTag.getInt("z");
            windmillPositions.add(new BlockPos(x, y, z));
        }
        formedControllerPositions.clear();
        ListTag controllerList = nbt.getList("formed_controller_positions", Tag.TAG_COMPOUND);
        for (int i = 0; i < controllerList.size(); i++) {
            CompoundTag posTag = controllerList.getCompound(i);
            int x = posTag.getInt("cx");
            int y = posTag.getInt("cy");
            int z = posTag.getInt("cz");
            formedControllerPositions.add(new BlockPos(x, y, z));
        }
    }

    public static WindmillSavedData get(ServerLevel level) {
        // 客户端无需存储持久化数据，直接返回空实例
        if (level.isClientSide()) {
            return new WindmillSavedData();
        }

        // 从Level中获取或创建SavedData，绑定到存档的「overworld」（主世界），保证跨维度数据统一（可根据需求修改）
        return level.getDataStorage().computeIfAbsent(
                WindmillSavedData::new,
                WindmillSavedData::new,
                ID.toString()
        );
    }
}
