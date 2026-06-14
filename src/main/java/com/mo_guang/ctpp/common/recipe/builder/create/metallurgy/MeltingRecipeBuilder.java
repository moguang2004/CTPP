package com.mo_guang.ctpp.common.recipe.builder.create.metallurgy;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_lid.MeltingRecipe;

import java.util.function.Consumer;

public class MeltingRecipeBuilder {

    private final ProcessingRecipeBuilder<MeltingRecipe> builder;

    private MeltingRecipeBuilder(ResourceLocation id) {
        this.builder = new ProcessingRecipeBuilder<>(MeltingRecipe::new, id);
    }

    public static MeltingRecipeBuilder builder(String name) {
        return new MeltingRecipeBuilder(MetallurgyRecipeBuilderSupport.id(name));
    }

    public static MeltingRecipeBuilder builder(ResourceLocation id) {
        return new MeltingRecipeBuilder(id);
    }

    public MeltingRecipeBuilder input(ItemStack stack) {
        return input(Ingredient.of(stack));
    }

    public MeltingRecipeBuilder input(ItemLike item) {
        this.builder.require(item);
        return this;
    }

    public MeltingRecipeBuilder input(String itemId) {
        return input(MetallurgyRecipeBuilderSupport.item(itemId));
    }

    public MeltingRecipeBuilder input(TagKey<Item> tag) {
        this.builder.require(tag);
        return this;
    }

    public MeltingRecipeBuilder input(Ingredient ingredient) {
        this.builder.require(ingredient);
        return this;
    }

    public MeltingRecipeBuilder resultFluid(Fluid fluid, int amount) {
        this.builder.output(fluid, amount);
        return this;
    }

    public MeltingRecipeBuilder resultFluid(FluidStack fluidStack) {
        this.builder.output(fluidStack);
        return this;
    }

    public MeltingRecipeBuilder resultFluid(String fluidId, int amount) {
        return resultFluid(MetallurgyRecipeBuilderSupport.fluid(fluidId), amount);
    }

    public MeltingRecipeBuilder heatRequirement(HeatCondition heatCondition) {
        this.builder.requiresHeat(heatCondition);
        return this;
    }

    public MeltingRecipeBuilder heatRequirement(String heatRequirement) {
        return heatRequirement(HeatCondition.deserialize(heatRequirement));
    }

    public MeltingRecipeBuilder duration(int ticks) {
        this.builder.duration(ticks);
        return this;
    }

    public MeltingRecipeBuilder processingTime(int ticks) {
        return duration(ticks);
    }

    public void save(Consumer<FinishedRecipe> consumer) {
        this.builder.build(consumer);
    }
}
