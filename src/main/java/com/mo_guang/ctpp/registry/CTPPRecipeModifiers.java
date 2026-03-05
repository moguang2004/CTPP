package com.mo_guang.ctpp.registry;

import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;

import com.mo_guang.ctpp.api.CTPPModifierFunction;
import com.mo_guang.ctpp.api.CTPPParallelLogic;
import com.mo_guang.ctpp.common.machine.multiblock.KineticWorkableMultiblockMachine;

public class CTPPRecipeModifiers {

    public static final RecipeModifier KINETIC_PARALLEL = ((machine, recipe) -> {
        if (machine instanceof KineticWorkableMultiblockMachine kmachine) {
            var parallels = CTPPParallelLogic.getKineticParallelAmount(kmachine, recipe, Integer.MAX_VALUE, false);
            return CTPPModifierFunction.inputStressMultiplier(parallels)
                    .andThen(CTPPModifierFunction.accurateParallel(kmachine, recipe, parallels));
        }
        return ModifierFunction.IDENTITY;
    });

    public static final RecipeModifier KINETIC_PERFECT_PARALLEL = ((machine, recipe) -> {
        if (machine instanceof KineticWorkableMultiblockMachine kmachine) {
            var parallels = CTPPParallelLogic.getKineticParallelAmount(kmachine, recipe, Integer.MAX_VALUE, true);
            return CTPPModifierFunction.accurateParallel(kmachine, recipe, parallels);
        }
        return ModifierFunction.IDENTITY;
    });
}
