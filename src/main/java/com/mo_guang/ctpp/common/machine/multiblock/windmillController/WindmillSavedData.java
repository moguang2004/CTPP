package com.mo_guang.ctpp.common.machine.multiblock.windmillController;

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
    }

    public static WindmillSavedData get(ServerLevel level) {
        // 客户端无需存储持久化数据，直接返回空实例
        if (level.isClientSide()) {
            return new WindmillSavedData();
        }

        // 从Level中获取或创建SavedData，绑定到存档的「overworld」（主世界），保证跨维度数据统一（可根据需求修改）
        return level.getDataStorage().computeIfAbsent(
                WindmillSavedData::new,          // 数据不存在时的创建逻辑
                WindmillSavedData::new,          // 从NBT加载数据的逻辑
                ID.toString()                    // 数据唯一标识
        );
    }
}
