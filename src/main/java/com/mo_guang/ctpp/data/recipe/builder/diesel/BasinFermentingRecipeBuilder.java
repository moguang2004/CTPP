package com.mo_guang.ctpp.data.recipe.builder.diesel;

import net.minecraft.resources.ResourceLocation;

import com.jesz.createdieselgenerators.content.basin_lid.BasinFermentingRecipe;
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.data.recipe.builder.CTPPProcessingRecipeBuilder;

public class BasinFermentingRecipeBuilder extends
                                          CTPPProcessingRecipeBuilder<BasinFermentingRecipeBuilder, BasinFermentingRecipe> {

    public BasinFermentingRecipeBuilder(ResourceLocation recipeId) {
        super(BasinFermentingRecipe::new, recipeId);
    }

    public BasinFermentingRecipeBuilder(String path) {
        this(CTPP.id(path));
    }
}
