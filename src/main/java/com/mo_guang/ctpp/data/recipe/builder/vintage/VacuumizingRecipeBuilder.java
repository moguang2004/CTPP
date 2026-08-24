package com.mo_guang.ctpp.data.recipe.builder.vintage;

import net.minecraft.resources.ResourceLocation;

import com.negodya1.vintageimprovements.VintageRecipes;

public class VacuumizingRecipeBuilder extends AbstractVintageRecipeBuilder<VacuumizingRecipeBuilder> {

    public VacuumizingRecipeBuilder(String name) {
        super(name, VintageRecipes.VACUUMIZING);
    }

    public VacuumizingRecipeBuilder(ResourceLocation id) {
        super(id, VintageRecipes.VACUUMIZING);
    }

    public static VacuumizingRecipeBuilder builder(String name) {
        return new VacuumizingRecipeBuilder(name);
    }

    public static VacuumizingRecipeBuilder builder(ResourceLocation id) {
        return new VacuumizingRecipeBuilder(id);
    }
}
