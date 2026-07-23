package com.mo_guang.ctpp.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import com.mo_guang.ctpp.common.blockentity.GeneratorCoilBlockEntity;
import com.mo_guang.ctpp.registry.CTPPBlockEntities;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;

public class GeneratorCoilBlock extends RotatedPillarKineticBlock
                                implements IBE<GeneratorCoilBlockEntity> {

    public GeneratorCoilBlock(Properties properties) {
        super(properties.strength(2.0F, 1.0F));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult ray) {
        if (player.isShiftKeyDown() || !player.mayBuild()) {
            return InteractionResult.PASS;
        }

        var stack = player.getItemInHand(hand);
        if (MagnetPlacementHelper.INSTANCE.matchesItem(stack)) {
            return MagnetPlacementHelper.INSTANCE.getOffset(player, level, state, pos, ray)
                    .placeInWorld(level, (BlockItem) stack.getItem(), player, hand, ray);
        }

        return InteractionResult.PASS;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(AXIS);
    }

    @Override
    public Class<GeneratorCoilBlockEntity> getBlockEntityClass() {
        return GeneratorCoilBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends GeneratorCoilBlockEntity> getBlockEntityType() {
        return CTPPBlockEntities.GENERATOR_COIL.get();
    }
}
