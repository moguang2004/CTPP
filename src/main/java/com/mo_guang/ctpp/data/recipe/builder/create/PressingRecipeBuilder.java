package com.mo_guang.ctpp.data.recipe.builder.create;

import net.minecraft.resources.ResourceLocation;

import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.data.recipe.builder.CTPPProcessingRecipeBuilder;
import com.simibubi.create.content.kinetics.press.PressingRecipe;

public class PressingRecipeBuilder extends CTPPProcessingRecipeBuilder<PressingRecipeBuilder, PressingRecipe> {

    public PressingRecipeBuilder(ResourceLocation recipeId) {
        super(PressingRecipe::new, recipeId);
    }

    public static PressingRecipeBuilder builder(ResourceLocation recipeId) {
        return new PressingRecipeBuilder(recipeId);
    }

    public static PressingRecipeBuilder builder(String path) {
        return new PressingRecipeBuilder(CTPP.id(path));
    }
}
