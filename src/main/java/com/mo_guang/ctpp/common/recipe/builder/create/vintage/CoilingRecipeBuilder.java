package com.mo_guang.ctpp.common.recipe.builder.create.vintage;

import net.minecraft.resources.ResourceLocation;

import com.google.gson.JsonObject;
import com.negodya1.vintageimprovements.VintageRecipes;

public class CoilingRecipeBuilder extends AbstractVintageRecipeBuilder<CoilingRecipeBuilder> {

    private String springColor;

    public CoilingRecipeBuilder(String name) {
        super(name, VintageRecipes.COILING);
    }

    public CoilingRecipeBuilder(ResourceLocation id) {
        super(id, VintageRecipes.COILING);
    }

    public static CoilingRecipeBuilder builder(String name) {
        return new CoilingRecipeBuilder(name);
    }

    public static CoilingRecipeBuilder builder(ResourceLocation id) {
        return new CoilingRecipeBuilder(id);
    }

    public CoilingRecipeBuilder springColor(String springColor) {
        this.springColor = springColor;
        return this;
    }

    @Override
    protected void addExtraJson(JsonObject json) {
        if (springColor != null) json.addProperty("springColor", springColor);
    }
}
