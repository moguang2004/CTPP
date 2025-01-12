package com.mo_guang.ctpp.common.data;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.mo_guang.ctpp.api.StressRecipeCapability;

public class CTPPRecipeHelper {
    public static double getInputStress(GTRecipe recipe) {
        return recipe.getInputContents(StressRecipeCapability.CAP).stream()
                .map(Content::getContent)
                .mapToDouble(StressRecipeCapability.CAP::of)
                .sum();
    }
    public static int getInputStressTier(GTRecipe recipe) {
        double stress = getInputStress(recipe);
        if (recipe.parallels > 1) stress /= recipe.parallels;
        return GTUtil.getTierByVoltage((long) (stress/64));
    }
    public static double getOutputStress(GTRecipe recipe) {
        return recipe.getOutputContents(StressRecipeCapability.CAP).stream()
                .map(Content::getContent)
                .mapToDouble(StressRecipeCapability.CAP::of)
                .sum();
    }
}
