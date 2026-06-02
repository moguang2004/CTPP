package com.mo_guang.ctpp.common.recipe.builder.create.vintage;

import net.minecraft.resources.ResourceLocation;

import com.google.gson.JsonObject;
import com.negodya1.vintageimprovements.VintageRecipes;

public class HammeringRecipeBuilder extends AbstractVintageRecipeBuilder<HammeringRecipeBuilder> {

    private Integer hammerBlows;

    public HammeringRecipeBuilder(String name) {
        super(name, VintageRecipes.HAMMERING);
    }

    public HammeringRecipeBuilder(ResourceLocation id) {
        super(id, VintageRecipes.HAMMERING);
    }

    public static HammeringRecipeBuilder builder(String name) {
        return new HammeringRecipeBuilder(name);
    }

    public static HammeringRecipeBuilder builder(ResourceLocation id) {
        return new HammeringRecipeBuilder(id);
    }

    public HammeringRecipeBuilder hammerBlows(int hammerBlows) {
        this.hammerBlows = hammerBlows;
        return this;
    }

    @Override
    protected void addExtraJson(JsonObject json) {
        if (hammerBlows != null) json.addProperty("hammerBlows", hammerBlows);
    }
}
