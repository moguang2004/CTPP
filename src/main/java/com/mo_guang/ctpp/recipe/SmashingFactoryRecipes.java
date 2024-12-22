package com.mo_guang.ctpp.recipe;

import com.mo_guang.ctpp.CTPP;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.MIXER_RECIPES;
import static com.mo_guang.ctpp.common.data.CTPPRecipeTypes.KINETIC_MIXER_RECIPES;
import static com.mo_guang.ctpp.common.data.CTPPRecipeTypes.SMASHING_FACTORY_RECIPES;

public class SmashingFactoryRecipes {
    public static void init(Consumer<FinishedRecipe> provider) {
        CTPPRecipeBuilder.of(CTPP.id("test"),SMASHING_FACTORY_RECIPES)
                .inputItems(Items.PAPER.getDefaultInstance())
                .outputItems(Items.PAPER.getDefaultInstance().copyWithCount(2))
                .duration(100)
                .rpm(64)
                .save(provider);
        CTPPRecipeBuilder.of(CTPP.id("test2"),KINETIC_MIXER_RECIPES)
                .inputItems(Items.PAPER.getDefaultInstance())
                .outputItems(Items.PAPER.getDefaultInstance().copyWithCount(2))
                .duration(100)
                .save(provider);
        MIXER_RECIPES.recipeBuilder("test2")
                .inputItems(Items.PAPER.getDefaultInstance())
                .outputItems(Items.PAPER.getDefaultInstance().copyWithCount(3))
                .duration(100)
                .EUt(32)
                .save(provider);
        KINETIC_MIXER_RECIPES.recipeBuilder("test3")
                .inputItems(Items.PAPER.getDefaultInstance())
                .outputItems(Items.PAPER.getDefaultInstance().copyWithCount(3))
                .duration(100)
                .save(provider);
    }
}
