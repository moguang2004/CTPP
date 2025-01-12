package com.mo_guang.ctpp.api;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;

import java.util.List;

public class CTPPModifierFunction {
    public static ModifierFunction inputStressMultiplier(double multiplier) {
        return recipe -> {
            GTRecipe copied = recipe.copy();
            copied.inputs.put(StressRecipeCapability.CAP, recipe.inputs.get(StressRecipeCapability.CAP).stream().map(capability->capability.copy(StressRecipeCapability.CAP,ContentModifier.multiplier(multiplier))).toList());
            return copied;
        };
    }
    public static ModifierFunction outputStressMultiplier(double multiplier) {
        return recipe -> {
            GTRecipe copied = recipe.copy();
            copied.outputs.put(StressRecipeCapability.CAP, recipe.outputs.get(StressRecipeCapability.CAP).stream().map(capability->capability.copy(StressRecipeCapability.CAP,ContentModifier.multiplier(multiplier))).toList());
            return copied;
        };
    }
    public static final ModifierFunction accurateParallel(MetaMachine machine, GTRecipe recipe, int parallel) {
        int maxParallel = ParallelLogic.getParallelAmount(machine,recipe,parallel);
        if(recipe.hasTick()) {
            return ModifierFunction.builder()
                    .parallels(maxParallel)
                    .inputModifier(ContentModifier.multiplier(maxParallel))
                    .outputModifier(ContentModifier.multiplier(maxParallel))
                    .eutMultiplier(maxParallel)
                    .build();
        }
        else {
            return ModifierFunction.builder()
                    .parallels(maxParallel)
                    .inputModifier(ContentModifier.multiplier(maxParallel))
                    .outputModifier(ContentModifier.multiplier(maxParallel))
                    .build();
        }
    }
}
