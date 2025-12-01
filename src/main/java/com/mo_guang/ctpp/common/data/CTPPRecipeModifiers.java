package com.mo_guang.ctpp.common.data;

import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.mo_guang.ctpp.api.CTPPModifierFunction;
import com.mo_guang.ctpp.api.CTPPParallelLogic;
import com.mo_guang.ctpp.common.machine.multiblock.KineticOutputMachine;
import com.mo_guang.ctpp.common.machine.multiblock.KineticWorkableMultiblockMachine;

public class CTPPRecipeModifiers {
    public static final RecipeModifier KINETIC_PARALELL = ((machine, recipe) -> {
        if (machine instanceof KineticWorkableMultiblockMachine kmachine) {
            kmachine.parallels = CTPPParallelLogic.getKineticParallelAmount(kmachine,recipe,Integer.MAX_VALUE);
            return CTPPModifierFunction.accurateParallel(kmachine,recipe,kmachine.parallels);
        }
        return ModifierFunction.IDENTITY;
    });

//    public static final RecipeModifier KINETIC_ADJUST = ((machine,recipe) ->{
//        if(machine instanceof KineticOutputMachine kmachine){
//            float output = (float) CTPPRecipeHelper.getOutputStress(recipe);
//            float outputMax = kmachine.getMaxOutputStress();
//            if(outputMax < output){
//                return CTPPModifierFunction.outputStressMultiplier(outputMax/output);
//            }
//        }
//        return ModifierFunction.IDENTITY;
//    });

}
