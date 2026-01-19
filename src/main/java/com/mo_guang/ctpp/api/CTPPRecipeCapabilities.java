package com.mo_guang.ctpp.api;

import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.registry.GTRegistries;

public class CTPPRecipeCapabilities {
    public final static RecipeCapability<Float> SU = StressRecipeCapability.CAP;

    public static void init(){
        GTRegistries.RECIPE_CAPABILITIES.register(SU.name, SU);
    }
}
