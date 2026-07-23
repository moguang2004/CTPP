package com.mo_guang.ctpp.mixin.create.jei;

import com.mo_guang.ctpp.util.ICustomSlot;
import dev.nolij.toomanyrecipeviewers.impl.jei.api.gui.builder.RecipeSlotBuilder;
import dev.nolij.toomanyrecipeviewers.impl.jei.api.gui.ingredient.ITMRVSlotWidget;
import mezz.jei.common.util.ImmutableRect2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RecipeSlotBuilder.class, remap = false)
public class RecipeSlotBuilderMixin implements ICustomSlot {

    @Shadow
    private ImmutableRect2i rect;
    @Unique
    private boolean ctpp$hide = false;

    @Override
    public void ctpp$setHide() {
        ctpp$hide = true;
    }

    @Override
    public void ctpp$setRect(int width, int height) {
        rect = new ImmutableRect2i(rect.x(), rect.y(), width, height);
    }

    @Inject(method = "getWidget", at = @At("RETURN"), cancellable = true)
    void hideSlot(CallbackInfoReturnable<ITMRVSlotWidget> cir) {
        var slot = cir.getReturnValue();
        if (slot instanceof ICustomSlot customSlot) {
            if (ctpp$hide) {
                customSlot.ctpp$setHide();
            }
        }
        cir.setReturnValue(slot);
    }
}
