package com.mo_guang.ctpp.data.recipe.builder.vintage;

import net.minecraft.resources.ResourceLocation;

import com.google.gson.JsonObject;
import com.negodya1.vintageimprovements.VintageRecipes;

public class CurvingRecipeBuilder extends AbstractVintageRecipeBuilder<CurvingRecipeBuilder> {

    private Integer mode;
    private String itemAsHead;

    public CurvingRecipeBuilder(String name) {
        super(name, VintageRecipes.CURVING);
    }

    public CurvingRecipeBuilder(ResourceLocation id) {
        super(id, VintageRecipes.CURVING);
    }

    public static CurvingRecipeBuilder builder(String name) {
        return new CurvingRecipeBuilder(name);
    }

    public static CurvingRecipeBuilder builder(ResourceLocation id) {
        return new CurvingRecipeBuilder(id);
    }

    public CurvingRecipeBuilder mode(int mode) {
        this.mode = mode;
        return this;
    }

    public CurvingRecipeBuilder head(String head) {
        this.itemAsHead = head;
        return this;
    }

    @Override
    protected void addExtraJson(JsonObject json) {
        if (mode != null) json.addProperty("mode", mode);
        if (itemAsHead != null) json.addProperty("itemAsHead", itemAsHead);
    }
}
