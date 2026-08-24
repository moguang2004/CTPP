package com.mo_guang.ctpp.api;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.handler.RecipeHandlerGroup;

import java.util.List;

import static com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic.*;

public class CTPPParallelLogic {

    public static int getKineticParallelAmount(RecipeHandlerGroup group, GTRecipe recipe, int parallelLimit,
                                               boolean perfect) {
        if (parallelLimit <= 1) {
            return parallelLimit;
        } else {
            int maxInputMultiplier = getMaxByInput(group, recipe, parallelLimit, false,
                    List.of(StressRecipeCapability.CAP));
            int maxParallelKinetic = StressRecipeCapability.CAP.getMaxParallelByInput(group, recipe, parallelLimit,
                    false);
            if (!perfect) {
                maxParallelKinetic = (int) Math.sqrt(maxParallelKinetic);
            }
            maxInputMultiplier = Math.min(maxParallelKinetic, maxInputMultiplier);
            if (maxInputMultiplier == 0) {
                return 1;
            } else {
                return limitByOutputMerging(group, recipe, maxInputMultiplier, false, List.of());
            }
        }
    }
}
