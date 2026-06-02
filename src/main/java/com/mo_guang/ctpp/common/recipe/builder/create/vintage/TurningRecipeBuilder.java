package com.mo_guang.ctpp.common.recipe.builder.create.vintage;

import net.minecraft.resources.ResourceLocation;

import com.negodya1.vintageimprovements.VintageRecipes;

public class TurningRecipeBuilder extends AbstractVintageRecipeBuilder<TurningRecipeBuilder> {

    public TurningRecipeBuilder(String name) {
        super(name, VintageRecipes.TURNING);
    }

    public TurningRecipeBuilder(ResourceLocation id) {
        super(id, VintageRecipes.TURNING);
    }

    public static TurningRecipeBuilder builder(String name) {
        return new TurningRecipeBuilder(name);
    }

    public static TurningRecipeBuilder builder(ResourceLocation id) {
        return new TurningRecipeBuilder(id);
    }
}
