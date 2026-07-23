package com.mo_guang.ctpp.common.block;

import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import com.mo_guang.ctpp.common.blockentity.GeneratorCoilBlockEntity;
import com.mo_guang.ctpp.registry.CTPPBlocks;

import java.util.function.Predicate;

public class MagnetPlacementHelper implements IPlacementHelper {

    public static final MagnetPlacementHelper INSTANCE = new MagnetPlacementHelper();

    private MagnetPlacementHelper() {}

    @Override
    public Predicate<ItemStack> getItemPredicate() {
        return stack -> stack.getItem() instanceof BlockItem blockItem &&
                MagnetBlock.getStrength(blockItem.getBlock().defaultBlockState()) > 0;
    }

    @Override
    public Predicate<BlockState> getStatePredicate() {
        return state -> state.is(CTPPBlocks.GENERATOR_COIL.get());
    }

    @Override
    public PlacementOffset getOffset(Player player, Level level, BlockState state, BlockPos pos,
                                     BlockHitResult ray) {
        if (!(level.getBlockEntity(ray.getBlockPos()) instanceof GeneratorCoilBlockEntity coil)) {
            return PlacementOffset.fail();
        }

        for (BlockPos magnetPos : coil.getMagnetPositions()) {
            if (level.getBlockState(magnetPos).canBeReplaced()) {
                return PlacementOffset.success(magnetPos);
            }
        }

        return PlacementOffset.fail();
    }
}
