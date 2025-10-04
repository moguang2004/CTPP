package com.mo_guang.ctpp.rotate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class RotationWandItem extends Item {
    private static final String TAG_PIVOT = "RotationPivot";

    public RotationWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        try {
            CompoundTag tag = stack.getOrCreateTag();

            if (!tag.contains(TAG_PIVOT)) {
                // 第一次点击 → 保存 pivot
                tag.putLong(TAG_PIVOT, pos.asLong());
                System.out.println("[RotationWandItem] 已设置旋转点 pivot=" + pos);
                return InteractionResult.SUCCESS;
            } else {
                // 第二次点击 → 取出 pivot
                BlockPos pivot = BlockPos.of(tag.getLong(TAG_PIVOT));
                System.out.println("[RotationWandItem] 已读取 pivot=" + pivot + "，当前组装点=" + pos);

                Direction direction = Direction.UP; // 固定 Y 轴旋转
                var contraption = new SimpleBearingContraption(direction);

                if (!contraption.assemble(level, pos, pivot)) {
                    System.out.println("[RotationWandItem] 组装失败");
                    return InteractionResult.FAIL;
                }

                contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
                System.out.println("[RotationWandItem] 组装成功, 方块数=" + contraption.getBlocks().size());

                // 用 pivot 作为旋转中心
                SimpleRotatingContraptionEntity entity =
                        SimpleRotatingContraptionEntity.create(level, contraption, pivot.getCenter());

                // 实体的初始位置 = 组装点附近
                //entity.setPos(pos.getX()-0.5, pos.getY()-0.5, pos.getZ()-0.5);

                entity.setRotationSpeed(5f, 0f, 0f);

                level.addFreshEntity(entity);
                System.out.println("[RotationWandItem] 实体加入世界完成, 旋转点=" + pivot);

                // 清除 NBT，方便下次重新设置 pivot
                tag.remove(TAG_PIVOT);
            }

        } catch (Exception e) {
            System.err.println("[RotationWandItem] 发生异常:");
            e.printStackTrace();
            return InteractionResult.FAIL;
        }

        return InteractionResult.SUCCESS;
    }
}
