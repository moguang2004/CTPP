package com.mo_guang.ctpp.mixin;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.pattern.MultiblockState;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import com.mo_guang.ctpp.api.pattern.StaticBlockPattern;
import com.mo_guang.ctpp.common.machine.multiblock.KineticMultiblockMachine;
import com.mo_guang.ctpp.dynamicPart.rotation.IContraptionMultiblock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MultiblockState.class)
public class MultiblockStateMixin {

    @Inject(method = "onBlockStateChanged",
            at = @At(value = "INVOKE",
                     target = "Lcom/gregtechceu/gtceu/api/machine/feature/multiblock/IMultiController;isFormed()Z",
                     shift = At.Shift.BEFORE),
            locals = LocalCapture.CAPTURE_FAILHARD,
            remap = false,
            cancellable = true)
    public void onBlockStateChanged(BlockPos pos, BlockState state, CallbackInfo ci, ServerLevel serverLevel,
                                    IMultiController controller) {
        if (controller.isFormed() && controller instanceof KineticMultiblockMachine kineticMultiblockMachine) {
            if (!serverLevel.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
                long posLong = pos.asLong();
                if ((kineticMultiblockMachine.blazeBlocks != null && kineticMultiblockMachine.blazeBlocks.contains(posLong)) ||
                        (kineticMultiblockMachine.rotateBlocks != null && kineticMultiblockMachine.rotateBlocks.contains(posLong))) {
                    ci.cancel();
                }
            }
        }
        if (controller.isFormed() &&
                controller instanceof IContraptionMultiblock<?> &&
                controller.getPattern() instanceof StaticBlockPattern staticBlockPattern) {
            var dynamicParts = staticBlockPattern.getDynamicPart(controller.getMultiblockState()).values();
            for (var dynamicPart : dynamicParts) {
                if (dynamicPart.contains(pos)) {
                    ci.cancel();
                }
            }
        }
    }
}
