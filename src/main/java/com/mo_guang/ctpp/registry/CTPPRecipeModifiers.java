package com.mo_guang.ctpp.registry;

import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;

import com.mo_guang.ctpp.api.CTPPModifierFunction;
import com.mo_guang.ctpp.api.CTPPParallelLogic;
import com.mo_guang.ctpp.common.machine.multiblock.KineticWorkableMultiblockMachine;

public class CTPPRecipeModifiers {

    public static final RecipeModifier KINETIC_PARALLEL = ((machine, group, recipe) -> {
        if (machine instanceof KineticWorkableMultiblockMachine kmachine) {
            var parallels = CTPPParallelLogic.getKineticParallelAmount(group, recipe, Integer.MAX_VALUE, false);
            var failure = CTPPModifierFunction.inputStressMultiplier(parallels).apply(machine, group, recipe);
            if (failure != null) return failure;
            return CTPPModifierFunction.accurateParallel(kmachine, group, recipe, parallels);
        }
        return null;
    });

    public static final RecipeModifier KINETIC_PERFECT_PARALLEL = ((machine, group, recipe) -> {
        if (machine instanceof KineticWorkableMultiblockMachine kmachine) {
            var parallels = CTPPParallelLogic.getKineticParallelAmount(group, recipe, Integer.MAX_VALUE, true);
            return CTPPModifierFunction.accurateParallel(kmachine, group, recipe, parallels);
        }
        return null;
    });
}
