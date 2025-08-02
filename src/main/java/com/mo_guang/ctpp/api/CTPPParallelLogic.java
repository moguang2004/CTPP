package com.mo_guang.ctpp.api;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import java.util.List;
import java.util.Objects;

import static com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic.*;

public class CTPPParallelLogic {
    public static int getKineticParallelAmount(MetaMachine machine, GTRecipe recipe, int parallelLimit) {
        if (parallelLimit <= 1) {
            return parallelLimit;
        } else if (machine instanceof IRecipeLogicMachine) {
            IRecipeLogicMachine rlm = (IRecipeLogicMachine)machine;
            int maxInputMultiplier = getMaxByInput(rlm, recipe, parallelLimit, List.of());
            int maxParallelKinetic = (int) Math.sqrt(StressRecipeCapability.CAP.getMaxParallelRatio(rlm, recipe, parallelLimit));
            maxInputMultiplier = Math.min(maxParallelKinetic, maxInputMultiplier);
            if (maxInputMultiplier == 0) {
                return 0;
            } else {
                Objects.requireNonNull(rlm);
                return limitByOutputMerging(rlm, recipe, maxInputMultiplier, rlm::canVoidRecipeOutputs, List.of());
            }
        } else {
            return 1;
        }
    }
}
