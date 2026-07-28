package com.mo_guang.ctpp.data.recipe.builder.diesel;

import net.minecraft.resources.ResourceLocation;

import com.jesz.createdieselgenerators.content.tools.hammer.HammerRecipe;
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.data.recipe.builder.CTPPProcessingRecipeBuilder;

public class HammerRecipeBuilder extends CTPPProcessingRecipeBuilder<HammerRecipeBuilder, HammerRecipe> {

    public HammerRecipeBuilder(ResourceLocation recipeId) {
        super(HammerRecipe::new, recipeId);
    }

    public HammerRecipeBuilder(String path) {
        this(CTPP.id(path));
    }

    public static HammerRecipeBuilder builder(String path) {
        return new HammerRecipeBuilder(path);
    }
}
