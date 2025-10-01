package com.mo_guang.ctpp.rotate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class RotationWandItem extends Item {

    public RotationWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        System.out.println("[RotationWandItem.useOn] 玩家点击了方块: " + pos + " side=" + context.getClickedFace());

        if (level.isClientSide) {
            System.out.println("[RotationWandItem.useOn] 客户端调用, 不做处理");
            return InteractionResult.SUCCESS;
        }

        try {
            // 固定旋转轴：Y 轴
            Direction direction = Direction.UP;
            System.out.println("[RotationWandItem.useOn] 使用 SimpleBearingContraption 组装, 轴=" + direction);

            // 使用我们写的简单 Contraption
            var contraption = new SimpleBearingContraption(direction);
            if (!contraption.assemble(level, pos)) {
                System.out.println("[RotationWandItem.useOn] 组装失败, contraption.assemble 返回 false");
                return InteractionResult.FAIL;
            }

            System.out.println("[RotationWandItem.useOn] 组装成功, 方块数=" + contraption.getBlocks().size());


            // 把原方块移除并转为结构
            contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
            System.out.println("[RotationWandItem.useOn] removeBlocksFromWorld 调用完成");

            SimpleRotatingContraptionEntity entity = SimpleRotatingContraptionEntity.create(level, contraption);
            // 设定锚点（结构的中心点）
            BlockPos anchor = pos.relative(direction);
            entity.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
            entity.setRotationAxis(direction.getAxis());
            System.out.println("[RotationWandItem.useOn] 实体位置=" + anchor);

            // 设置旋转速度（度/每tick）
            entity.setRotationSpeed(5f);

            // 加入世界
            level.addFreshEntity(entity);
            System.out.println("[RotationWandItem.useOn] 实体加入世界完成");

        } catch (Exception e) {
            System.err.println("[RotationWandItem.useOn] 发生异常:");
            e.printStackTrace();
            return InteractionResult.FAIL;
        }

        return InteractionResult.SUCCESS;
    }
}
