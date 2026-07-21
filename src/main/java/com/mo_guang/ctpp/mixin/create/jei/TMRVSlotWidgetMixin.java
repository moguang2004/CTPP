package com.mo_guang.ctpp.mixin.create.jei;

import net.minecraft.client.gui.GuiGraphics;

import com.mo_guang.ctpp.util.ICustomSlot;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.SlotWidget;
import dev.nolij.toomanyrecipeviewers.impl.jei.api.gui.ingredient.TMRVSlotWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TMRVSlotWidget.class, remap = false)
public abstract class TMRVSlotWidgetMixin extends SlotWidget implements ICustomSlot {

    @Shadow
    public abstract void drawOverlay(GuiGraphics draw, int mouseX, int mouseY, float delta);

    @Unique
    private boolean ctpp$hide = false;

    public TMRVSlotWidgetMixin(EmiIngredient stack, int x, int y) {
        super(stack, x, y);
    }

    @Override
    public void ctpp$setHide() {
        ctpp$hide = true;
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    void hide(GuiGraphics draw, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (ctpp$hide) {
            drawOverlay(draw, mouseX, mouseY, delta);
            ci.cancel();
        }
    }
}
