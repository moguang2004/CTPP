package com.mo_guang.ctpp.mixin.create.diesel;

import com.jesz.createdieselgenerators.compat.jei.CDGJEI;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = CDGJEI.class, remap = false)
public class CDGJEIMixin {

    @Shadow
    @Final
    private List<CreateRecipeCategory<?>> allCategories;

    @Inject(method = "loadCategories", at = @At("TAIL"))
    void deleteBasinFermenting(CallbackInfo ci) {
        allCategories.removeIf(c -> c.getRecipeType().getRecipeClass() == BasinRecipe.class);
    }
}
