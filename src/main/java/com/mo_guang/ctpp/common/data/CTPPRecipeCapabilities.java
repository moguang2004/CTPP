package com.mo_guang.ctpp.common.data;

import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.mo_guang.ctpp.api.StressRecipeCapability;

public class CTPPRecipeCapabilities {
    public final static RecipeCapability<Float> SU = StressRecipeCapability.CAP;

    public static void init(){
        GTRegistries.RECIPE_CAPABILITIES.register(SU.name, SU);
    }
}
