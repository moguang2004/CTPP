package com.mo_guang.ctpp.mixin;

import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockState;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockPattern.class)
public class BlockPatternMixin {
    @Inject(method = "checkPatternAt(Lcom/gregtechceu/gtceu/api/pattern/MultiblockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/core/Direction;ZZ)Z", at = @At(value = "INVOKE", target = "Lcom/gregtechceu/gtceu/api/pattern/TraceabilityPredicate;test(Lcom/gregtechceu/gtceu/api/pattern/MultiblockState;)Z"),remap = false)
    private void injectCode(MultiblockState worldState, BlockPos centerPos, Direction frontFacing, Direction upwardsFacing, boolean isFlipped, boolean savePredicate, CallbackInfoReturnable<Boolean> cir) {
        if (worldState.getBlockState().getBlock() instanceof KineticBlock) {
            worldState.getMatchContext().getOrCreate("roBlocks", LongOpenHashSet::new)
                    .add(worldState.getPos().asLong());
        }
    }
}
