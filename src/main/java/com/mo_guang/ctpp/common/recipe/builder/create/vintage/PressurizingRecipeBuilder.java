package com.mo_guang.ctpp.common.recipe.builder.create.vintage;

import net.minecraft.resources.ResourceLocation;

import com.google.gson.JsonObject;
import com.negodya1.vintageimprovements.VintageRecipes;

public class PressurizingRecipeBuilder extends AbstractVintageRecipeBuilder<PressurizingRecipeBuilder> {

    private Integer secondaryFluidInput;
    private Integer secondaryFluidOutput;

    public PressurizingRecipeBuilder(String name) {
        super(name, VintageRecipes.PRESSURIZING);
    }

    public PressurizingRecipeBuilder(ResourceLocation id) {
        super(id, VintageRecipes.PRESSURIZING);
    }

    public static PressurizingRecipeBuilder builder(String name) {
        return new PressurizingRecipeBuilder(name);
    }

    public static PressurizingRecipeBuilder builder(ResourceLocation id) {
        return new PressurizingRecipeBuilder(id);
    }

    public PressurizingRecipeBuilder secondaryFluidInput(int secondaryFluidInput) {
        this.secondaryFluidInput = secondaryFluidInput;
        return this;
    }

    public PressurizingRecipeBuilder secondaryFluidOutput(int secondaryFluidOutput) {
        this.secondaryFluidOutput = secondaryFluidOutput;
        return this;
    }

    @Override
    protected void addExtraJson(JsonObject json) {
        if (secondaryFluidInput != null) json.addProperty("secondaryFluidInput", secondaryFluidInput);
        if (secondaryFluidOutput != null) json.addProperty("secondaryFluidOutput", secondaryFluidOutput);
    }
}
