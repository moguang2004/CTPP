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
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_mixer.AlloyingRecipe;

import java.util.function.Consumer;

public class AlloyingRecipeBuilder {

    private final ProcessingRecipeBuilder<AlloyingRecipe> builder;

    private AlloyingRecipeBuilder(ResourceLocation id) {
        this.builder = new ProcessingRecipeBuilder<>(AlloyingRecipe::new, id);
    }

    public static AlloyingRecipeBuilder builder(String name) {
        return new AlloyingRecipeBuilder(MetallurgyRecipeBuilderSupport.id(name));
    }

    public static AlloyingRecipeBuilder builder(ResourceLocation id) {
        return new AlloyingRecipeBuilder(id);
    }

    public AlloyingRecipeBuilder input(ItemStack stack) {
        return input(Ingredient.of(stack));
    }

    public AlloyingRecipeBuilder input(ItemLike item) {
        this.builder.require(item);
        return this;
    }

    public AlloyingRecipeBuilder input(String itemId) {
        return input(MetallurgyRecipeBuilderSupport.item(itemId));
    }

    public AlloyingRecipeBuilder input(TagKey<Item> tag) {
        this.builder.require(tag);
        return this;
    }

    public AlloyingRecipeBuilder input(Ingredient ingredient) {
        this.builder.require(ingredient);
        return this;
    }

    public AlloyingRecipeBuilder inputFluid(Fluid fluid, int amount) {
        this.builder.require(fluid, amount);
        return this;
    }

    public AlloyingRecipeBuilder inputFluid(FluidStack fluidStack) {
        this.builder.require(fluidStack.getFluid(), fluidStack.getAmount());
        return this;
    }

    public AlloyingRecipeBuilder inputFluid(String fluidId, int amount) {
        return inputFluid(MetallurgyRecipeBuilderSupport.fluid(fluidId), amount);
    }

    public AlloyingRecipeBuilder resultFluid(Fluid fluid, int amount) {
        this.builder.output(fluid, amount);
        return this;
    }

    public AlloyingRecipeBuilder resultFluid(FluidStack fluidStack) {
        this.builder.output(fluidStack);
        return this;
    }

    public AlloyingRecipeBuilder resultFluid(String fluidId, int amount) {
        return resultFluid(MetallurgyRecipeBuilderSupport.fluid(fluidId), amount);
    }

    public AlloyingRecipeBuilder heatRequirement(HeatCondition heatCondition) {
        this.builder.requiresHeat(heatCondition);
        return this;
    }

    public AlloyingRecipeBuilder heatRequirement(String heatRequirement) {
        return heatRequirement(HeatCondition.deserialize(heatRequirement));
    }

    public AlloyingRecipeBuilder duration(int ticks) {
        this.builder.duration(ticks);
        return this;
    }

    public AlloyingRecipeBuilder processingTime(int ticks) {
        return duration(ticks);
    }

    public void save(Consumer<FinishedRecipe> consumer) {
        this.builder.build(consumer);
    }
}
