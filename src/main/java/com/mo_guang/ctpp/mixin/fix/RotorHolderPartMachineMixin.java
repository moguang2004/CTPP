package com.mo_guang.ctpp.mixin.fix;

import com.gregtechceu.gtceu.common.machine.multiblock.part.RotorHolderPartMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RotorHolderPartMachine.class)
public class RotorHolderPartMachineMixin {
    @Inject(method = "getTierDifference", at = @At(value = "RETURN",ordinal = 1), cancellable = true,remap = false)
    public void getTierDifference(CallbackInfoReturnable<Integer> cir)  {
        cir.setReturnValue(-20);
    }
}
