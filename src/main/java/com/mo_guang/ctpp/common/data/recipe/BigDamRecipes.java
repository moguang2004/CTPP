package com.mo_guang.ctpp.common.data.recipe;

import com.mo_guang.ctpp.registry.CTPPRecipeTypes;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.data.recipes.FinishedRecipe;

import com.mo_guang.ctpp.common.data.recipe.builder.CTPPRecipeBuilder;

import java.util.function.Consumer;

public class BigDamRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        CTPPRecipeBuilder.of(GTCEu.id("big_dam"), CTPPRecipeTypes.BIG_DAM)
                .outputStress(2097152)
                .inputFluids(GTMaterials.Lubricant.getFluid(50))
                .duration(200)
                .save(provider);
    }
}
