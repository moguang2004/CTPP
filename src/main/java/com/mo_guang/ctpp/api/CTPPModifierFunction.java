package com.mo_guang.ctpp.api;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.handler.RecipeHandlerGroup;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;

import net.minecraft.network.chat.Component;

public class CTPPModifierFunction {

    public static RecipeModifier inputStressMultiplier(double multiplier) {
        return (machine, group, recipe) -> {
            multiplyStressContents(recipe.inputs.get(StressRecipeCapability.CAP), multiplier);
            return null;
        };
    }

    public static RecipeModifier outputStressMultiplier(double multiplier) {
        return (machine, group, recipe) -> {
            multiplyStressContents(recipe.outputs.get(StressRecipeCapability.CAP), multiplier);
            multiplyStressContents(recipe.tickOutputs.get(StressRecipeCapability.CAP), multiplier);
            return null;
        };
    }

    public static Component accurateParallel(MetaMachine machine, RecipeHandlerGroup group, GTRecipe recipe,
                                             int limit) {
        int maxParallel = ParallelLogic.getParallelAmount(group, recipe, limit);
        if (maxParallel <= 1) return null;
        recipe.multiplyAllContents(maxParallel);
        recipe.parallels *= maxParallel;
        return null;
    }

    private static void multiplyStressContents(java.util.List<Float> contents, double multiplier) {
        if (contents == null || contents.isEmpty()) return;
        for (int i = 0; i < contents.size(); i++) {
            contents.set(i, (float) (contents.get(i) * multiplier));
        }
    }
}
