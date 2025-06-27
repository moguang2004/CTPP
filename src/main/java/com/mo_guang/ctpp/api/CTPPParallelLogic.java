package com.mo_guang.ctpp.api;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import java.util.Objects;

import static com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic.limitByInput;
import static com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic.limitByOutputMerging;

public class CTPPParallelLogic {
    public static int getKineticParallelAmount(MetaMachine machine, GTRecipe recipe, int parallelLimit) {
        if (parallelLimit <= 1) {
            return parallelLimit;
        } else if (machine instanceof IRecipeLogicMachine) {
            IRecipeLogicMachine rlm = (IRecipeLogicMachine)machine;
            int maxInputMultiplier = limitByInput(rlm, recipe, parallelLimit);
            int maxParallelKinetic = (int) Math.sqrt(StressRecipeCapability.CAP.getMaxParallelRatio(rlm, recipe, parallelLimit));
            maxInputMultiplier = Math.min(maxParallelKinetic, maxInputMultiplier);
            if (maxInputMultiplier == 0) {
                return 0;
            } else {
                Objects.requireNonNull(rlm);
                return limitByOutputMerging(rlm, recipe, maxInputMultiplier, rlm::canVoidRecipeOutputs);
            }
        } else {
            return 1;
        }
    }
}
