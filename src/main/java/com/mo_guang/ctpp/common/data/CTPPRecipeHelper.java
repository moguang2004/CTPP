package com.mo_guang.ctpp.common.data;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.mo_guang.ctpp.api.StressRecipeCapability;
import org.jetbrains.annotations.NotNull;

public class CTPPRecipeHelper {
    public static float getInputStress(@NotNull GTRecipe recipe) {
        return (float) recipe.getInputContents(StressRecipeCapability.CAP).stream()
                .map(Content::getContent)
                .mapToDouble(StressRecipeCapability.CAP::of)
                .sum();
    }

    public static float getOutputStress(@NotNull GTRecipe recipe) {
        return (float) recipe.getOutputContents(StressRecipeCapability.CAP).stream()
                .map(Content::getContent)
                .mapToDouble(StressRecipeCapability.CAP::of)
                .sum();
    }

    public static float getStressWithIO(@NotNull GTRecipe recipe){
        float input = getInputStress(recipe);
        if(input!=0) return input;
        return -getOutputStress(recipe);
    }
}
