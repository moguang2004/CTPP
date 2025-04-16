package com.mo_guang.ctpp.mixin.create;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.mo_guang.ctpp.common.machine.multiblock.WindMillControlMachine;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WindmillBearingBlock.class)
public class WindmillBearingBlockMixin extends Block {

    public WindmillBearingBlockMixin(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
        notifyWindmillController(pLevel, pPos);
    }
    @Override
    public void onRemove(BlockState p_60515_, Level level, BlockPos pos, BlockState p_60518_, boolean p_60519_) {
        super.onRemove(p_60515_, level, pos, p_60518_, p_60519_);
        notifyWindmillController(level, pos);
    }
    @Inject(method = "use", at = @At(value = "RETURN", ordinal = 3), remap = false)
    public void use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        notifyWindmillController(worldIn, pos);
    }

    @Unique
    public void notifyWindmillController(Level level, BlockPos pos) {
        BlockPos.betweenClosed(pos.offset(-10, -10, -10), pos.offset(10, 10, 10))
                .forEach(checkPos -> {
                    MetaMachine machine = MetaMachine.getMachine(level, checkPos);
                    if (machine instanceof WindMillControlMachine wmachine) {
                        wmachine.willTick = true;
                    }
                });
    }
}
