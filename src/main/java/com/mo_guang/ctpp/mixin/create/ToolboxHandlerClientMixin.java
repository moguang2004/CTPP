package com.mo_guang.ctpp.mixin.create;

import com.simibubi.create.content.equipment.toolbox.ToolboxHandlerClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ToolboxHandlerClient.class, remap = false)
public abstract class ToolboxHandlerClientMixin {

    @Inject(method = "onKeyInput", at = @At("HEAD"), cancellable = true, remap = false)
    private static void ctpp$preferCTPPToolboxes(int key, boolean pressed, CallbackInfo ci) {
        ci.cancel();
    }
}
