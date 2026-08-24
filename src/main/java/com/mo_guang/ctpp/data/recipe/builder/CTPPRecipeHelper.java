package com.mo_guang.ctpp.data.recipe.builder;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import com.mo_guang.ctpp.api.StressRecipeCapability;
import org.jetbrains.annotations.NotNull;

public class CTPPRecipeHelper {

    public static float getInputStress(@NotNull GTRecipe recipe) {
        return recipe.getInputContents(StressRecipeCapability.CAP).stream()
                .reduce(0f, Float::sum);
    }

    public static float getOutputStress(@NotNull GTRecipe recipe) {
        return recipe.getOutputContents(StressRecipeCapability.CAP).stream()
                .reduce(0f, Float::sum);
    }

    public static float getStressWithIO(@NotNull GTRecipe recipe) {
        float input = getInputStress(recipe);
        if (input != 0) return input;
        return -getOutputStress(recipe);
    }
}
