package com.mo_guang.ctpp.mixin;

import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.integration.kjs.recipe.components.ContentJS;
import com.gregtechceu.gtceu.integration.kjs.recipe.components.GTRecipeComponents;
import com.mo_guang.ctpp.common.data.CTPPRecipeCapabilities;
import com.mojang.datafixers.util.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.IdentityHashMap;
import java.util.Map;

import static com.gregtechceu.gtceu.integration.kjs.recipe.components.GTRecipeComponents.VALID_CAPS;
import static com.mo_guang.ctpp.integration.kjs.CTPPRecipeComponents.SU_IN;
import static com.mo_guang.ctpp.integration.kjs.CTPPRecipeComponents.SU_OUT;

@Mixin(GTRecipeComponents.class)
public class GTRecipeComponentsMixin {
    @Inject(method = "<clinit>",at = @At("TAIL"))
    private static void init(CallbackInfo ci){
        VALID_CAPS.put(CTPPRecipeCapabilities.SU, Pair.of(SU_IN, SU_OUT));
    }
}
