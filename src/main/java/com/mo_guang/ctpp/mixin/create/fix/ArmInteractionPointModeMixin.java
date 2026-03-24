package com.mo_guang.ctpp.mixin.create.fix;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ArmInteractionPoint.Mode.class, remap = false)
public class ArmInteractionPointModeMixin {

    @Shadow
    @Final
    private String translationKey;

    @Inject(method = "getTranslationKey", at = @At("HEAD"), cancellable = true)
    private void ctnh$fixModeTranslationKeyPrefix(CallbackInfoReturnable<String> cir) {
        String key = this.translationKey;
        // bug in Create 6.0.6
        while (key.startsWith("create.")) {
            key = key.substring("create.".length());
        }
        if (!key.equals(this.translationKey)) {
            cir.setReturnValue(key);
        }
    }
}
