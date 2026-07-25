package com.mo_guang.ctpp.data.recipe.builder.vintage;

import net.minecraft.resources.ResourceLocation;

import com.negodya1.vintageimprovements.VintageRecipes;

public class VibratingRecipeBuilder extends AbstractVintageRecipeBuilder<VibratingRecipeBuilder> {

    public VibratingRecipeBuilder(String name) {
        super(name, VintageRecipes.VIBRATING);
    }

    public VibratingRecipeBuilder(ResourceLocation id) {
        super(id, VintageRecipes.VIBRATING);
    }

    public static VibratingRecipeBuilder builder(String name) {
        return new VibratingRecipeBuilder(name);
    }

    public static VibratingRecipeBuilder builder(ResourceLocation id) {
        return new VibratingRecipeBuilder(id);
    }
}
