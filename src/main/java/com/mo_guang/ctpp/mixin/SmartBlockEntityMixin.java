package com.mo_guang.ctpp.mixin;

import com.mo_guang.ctpp.common.blockentity.KineticMachineBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SmartBlockEntity.class, remap = false)
public class SmartBlockEntityMixin {

    @Inject(
            method = "setRemoved",
            at = @At("TAIL"))
    public void injectSetRemoved(CallbackInfo ci) {
        if ((Object) this instanceof KineticMachineBlockEntity kineticMachineBlockEntity) {
            kineticMachineBlockEntity.metaMachine.onUnload();
        }
    }
}
