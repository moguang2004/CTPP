package com.mo_guang.ctpp.data.recipe.builder.diesel;

import net.minecraft.resources.ResourceLocation;

import com.jesz.createdieselgenerators.content.tools.wire_cutters.WireCuttingRecipe;
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.data.recipe.builder.CTPPProcessingRecipeBuilder;

public class WireCuttingRecipeBuilder extends CTPPProcessingRecipeBuilder<WireCuttingRecipeBuilder, WireCuttingRecipe> {

    public WireCuttingRecipeBuilder(ResourceLocation recipeId) {
        super(WireCuttingRecipe::new, recipeId);
    }

    public WireCuttingRecipeBuilder(String path) {
        this(CTPP.id(path));
    }

    public static WireCuttingRecipeBuilder builder(String path) {
        return new WireCuttingRecipeBuilder(path);
    }
}
