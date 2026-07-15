package com.mo_guang.ctpp.api;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.handler.RecipeHandlerGroup;

import java.util.List;
import java.util.Objects;

import static com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic.*;

public class CTPPParallelLogic {

    public static int getKineticParallelAmount(MetaMachine machine, GTRecipe recipe, int parallelLimit,
                                               boolean perfect) {
        if (parallelLimit <= 1) {
            return parallelLimit;
        } else if (machine instanceof IRecipeLogicMachine) {
            IRecipeLogicMachine rlm = (IRecipeLogicMachine) machine;
            RecipeHandlerGroup group = rlm.getRecipeHandlerGroups().isEmpty() ? null :
                    rlm.getRecipeHandlerGroups().get(0);
            if (group == null) return 0;
            int maxInputMultiplier = getMaxByInput(group, recipe, parallelLimit, false, List.of());
            int maxParallelKinetic = StressRecipeCapability.CAP.getMaxParallelByInput(group, recipe, parallelLimit,
                    false);
            if (!perfect)
                maxParallelKinetic = (int) Math.sqrt(maxParallelKinetic);
            maxInputMultiplier = Math.min(maxParallelKinetic, maxInputMultiplier);
            if (maxInputMultiplier == 0) {
                return 0;
            } else {
                Objects.requireNonNull(rlm);
                return limitByOutputMerging(group, recipe, maxInputMultiplier, List.of());
            }
        } else {
            return 1;
        }
    }
}
