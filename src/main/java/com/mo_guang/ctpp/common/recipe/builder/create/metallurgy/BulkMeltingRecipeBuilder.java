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

import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.BulkMeltingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.FoundryRecipeBuilder;

import java.util.function.Consumer;

public class BulkMeltingRecipeBuilder {

    private final FoundryRecipeBuilder<BulkMeltingRecipe> builder;

    private BulkMeltingRecipeBuilder(ResourceLocation id) {
        this.builder = new FoundryRecipeBuilder<>(BulkMeltingRecipe::new, id);
    }

    public static BulkMeltingRecipeBuilder builder(String name) {
        return new BulkMeltingRecipeBuilder(MetallurgyRecipeBuilderSupport.id(name));
    }

    public static BulkMeltingRecipeBuilder builder(ResourceLocation id) {
        return new BulkMeltingRecipeBuilder(id);
    }

    public BulkMeltingRecipeBuilder input(ItemStack stack) {
        return input(Ingredient.of(stack));
    }

    public BulkMeltingRecipeBuilder input(ItemLike item) {
        this.builder.require(item);
        return this;
    }

    public BulkMeltingRecipeBuilder input(String itemId) {
        return input(MetallurgyRecipeBuilderSupport.item(itemId));
    }

    public BulkMeltingRecipeBuilder input(TagKey<Item> tag) {
        this.builder.require(tag);
        return this;
    }

    public BulkMeltingRecipeBuilder input(Ingredient ingredient) {
        this.builder.require(ingredient);
        return this;
    }

    public BulkMeltingRecipeBuilder resultFluid(Fluid fluid, int amount) {
        this.builder.output(fluid, amount);
        return this;
    }

    public BulkMeltingRecipeBuilder resultFluid(FluidStack fluidStack) {
        this.builder.output(fluidStack);
        return this;
    }

    public BulkMeltingRecipeBuilder resultFluid(String fluidId, int amount) {
        return resultFluid(MetallurgyRecipeBuilderSupport.fluid(fluidId), amount);
    }

    public BulkMeltingRecipeBuilder minHeat(int heat) {
        this.builder.requiresMinHeat(heat);
        return this;
    }

    public BulkMeltingRecipeBuilder maxHeat(int heat) {
        this.builder.requiresMaxHeat(heat);
        return this;
    }

    public BulkMeltingRecipeBuilder duration(int ticks) {
        this.builder.duration(ticks);
        return this;
    }

    public BulkMeltingRecipeBuilder processingTime(int ticks) {
        return duration(ticks);
    }

    public void save(Consumer<FinishedRecipe> consumer) {
        this.builder.build(consumer);
    }
}
