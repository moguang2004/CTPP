package com.mo_guang.ctpp.common.data;

import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.mo_guang.ctpp.common.machine.multiblock.KineticWorkableMultiblockMachine;
import net.minecraft.Util;

import java.util.function.Function;

import static com.gregtechceu.gtceu.api.recipe.OverclockingLogic.*;
import static com.gregtechceu.gtceu.api.recipe.OverclockingLogic.NON_PERFECT_OVERCLOCK_SUBTICK;

public class CTPPRecipeModifiers {
    public static final RecipeModifier KINETIC_OVERCLOCK = ((machine, recipe) -> {
        if (machine instanceof KineticWorkableMultiblockMachine kmachine) {
        }
        return ModifierFunction.IDENTITY;
    });

}
