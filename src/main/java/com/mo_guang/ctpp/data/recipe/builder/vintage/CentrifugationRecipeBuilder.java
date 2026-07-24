package com.mo_guang.ctpp.data.recipe.builder.vintage;

import net.minecraft.resources.ResourceLocation;

import com.negodya1.vintageimprovements.VintageRecipes;

public class CentrifugationRecipeBuilder extends AbstractVintageRecipeBuilder<CentrifugationRecipeBuilder> {

    public CentrifugationRecipeBuilder(String name) {
        super(name, VintageRecipes.CENTRIFUGATION);
    }

    public CentrifugationRecipeBuilder(ResourceLocation id) {
        super(id, VintageRecipes.CENTRIFUGATION);
    }

    public static CentrifugationRecipeBuilder builder(String name) {
        return new CentrifugationRecipeBuilder(name);
    }

    public static CentrifugationRecipeBuilder builder(ResourceLocation id) {
        return new CentrifugationRecipeBuilder(id);
    }
}
