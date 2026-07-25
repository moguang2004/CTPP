package com.mo_guang.ctpp.data.recipe.builder.diesel;

import net.minecraft.resources.ResourceLocation;

import com.jesz.createdieselgenerators.content.distillation.DistillationRecipe;
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.data.recipe.builder.CTPPProcessingRecipeBuilder;

public class DistillationRecipeBuilder extends
                                       CTPPProcessingRecipeBuilder<DistillationRecipeBuilder, DistillationRecipe> {

    public DistillationRecipeBuilder(ResourceLocation recipeId) {
        super(DistillationRecipe::new, recipeId);
    }

    public DistillationRecipeBuilder(String path) {
        this(CTPP.id(path));
    }
}
