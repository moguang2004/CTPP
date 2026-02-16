package com.mo_guang.ctpp.mixin;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.pattern.MultiblockState;
import com.mo_guang.ctpp.api.pattern.StaticBlockPattern;
import com.mo_guang.ctpp.common.machine.multiblock.KineticMultiblockMachine;
import com.mo_guang.ctpp.dynamicPart.rotation.IRotationMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MultiblockState.class)
public class MultiblockStateMixin {
    @Inject(method = "onBlockStateChanged",
            at = @At(value = "INVOKE", target = "Lcom/gregtechceu/gtceu/api/machine/feature/multiblock/IMultiController;isFormed()Z", shift = At.Shift.BEFORE),
            locals = LocalCapture.CAPTURE_FAILHARD,
            remap = false,
            cancellable = true)
    public void onBlockStateChanged(BlockPos pos, BlockState state, CallbackInfo ci, ServerLevel serverLevel, IMultiController controller) {
        if (controller.isFormed() && controller instanceof KineticMultiblockMachine kineticMultiblockMachine) {
            var blazeBlocksPos = kineticMultiblockMachine.blazeBlocks.longStream().mapToObj(BlockPos::of).toList();
            var rotateBlockPos = kineticMultiblockMachine.rotateBlocks.longStream().mapToObj(BlockPos::of).toList();
            if (blazeBlocksPos.contains(pos) || rotateBlockPos.contains(pos)) {
                ci.cancel();
            }
        }
        if (controller.isFormed() &&
                controller instanceof IRotationMultiblock &&
                controller.getPattern() instanceof StaticBlockPattern staticBlockPattern) {
            var dynamicParts = staticBlockPattern.getDynamicPart(controller.getMultiblockState()).values();
            for (var dynamicPart: dynamicParts) {
                if (dynamicPart.contains(pos)) {
                    ci.cancel();
                }
            }
        }
    }
}
