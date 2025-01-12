package com.mo_guang.ctpp.common.data;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.mo_guang.ctpp.api.CTPPModifierFunction;
import com.mo_guang.ctpp.common.machine.IKineticMachine;
import com.mo_guang.ctpp.common.machine.multiblock.KineticOutputMachine;
import com.mo_guang.ctpp.common.machine.multiblock.KineticWorkableMultiblockMachine;
import net.minecraft.Util;

import java.util.function.Function;

import static com.gregtechceu.gtceu.api.recipe.OverclockingLogic.*;
import static com.gregtechceu.gtceu.api.recipe.OverclockingLogic.NON_PERFECT_OVERCLOCK_SUBTICK;

public class CTPPRecipeModifiers {
    public static final RecipeModifier KINETIC_OVERCLOCK = ((machine, recipe) -> {
        if (machine instanceof KineticWorkableMultiblockMachine kmachine) {
            var modifier = kmachine.calculateModifier();
            kmachine.parallels = ParallelLogic.getParallelAmount(kmachine,modifier.apply(recipe),2147483647);
            return CTPPModifierFunction.accurateParallel(kmachine,recipe,kmachine.parallels).compose(modifier);
        }
        return ModifierFunction.IDENTITY;
    });

    public static final RecipeModifier KINETIC_ADJUST = ((machine,recipe) ->{
        if(machine instanceof KineticOutputMachine kmachine){
            float output = (float) CTPPRecipeHelper.getOutputStress(recipe);
            float outputMax = kmachine.getMaxOutputStress();
            if(outputMax < output){
                return CTPPModifierFunction.outputStressMultiplier(outputMax/output);
            }
        }
        return ModifierFunction.IDENTITY;
    });

}
