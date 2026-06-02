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

import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.CastingRecipeBuilder;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;

import java.util.function.Consumer;

public class CastingInTableRecipeBuilder {

    private final CastingRecipeBuilder builder;

    private CastingInTableRecipeBuilder(ResourceLocation id) {
        this.builder = new CastingRecipeBuilder(CMRecipeTypes.CASTING_IN_TABLE, id);
    }

    public static CastingInTableRecipeBuilder builder(String name) {
        return new CastingInTableRecipeBuilder(MetallurgyRecipeBuilderSupport.id(name));
    }

    public static CastingInTableRecipeBuilder builder(ResourceLocation id) {
        return new CastingInTableRecipeBuilder(id);
    }

    public CastingInTableRecipeBuilder input(ItemStack stack) {
        return input(Ingredient.of(stack));
    }

    public CastingInTableRecipeBuilder input(ItemLike item) {
        this.builder.require(item);
        return this;
    }

    public CastingInTableRecipeBuilder input(String itemId) {
        return input(MetallurgyRecipeBuilderSupport.item(itemId));
    }

    public CastingInTableRecipeBuilder input(TagKey<Item> tag) {
        this.builder.require(tag);
        return this;
    }

    public CastingInTableRecipeBuilder input(Ingredient ingredient) {
        this.builder.require(ingredient);
        return this;
    }

    public CastingInTableRecipeBuilder inputFluid(Fluid fluid, int amount) {
        this.builder.require(fluid, amount);
        return this;
    }

    public CastingInTableRecipeBuilder inputFluid(FluidStack fluidStack) {
        return inputFluid(fluidStack.getFluid(), fluidStack.getAmount());
    }

    public CastingInTableRecipeBuilder inputFluid(String fluidId, int amount) {
        return inputFluid(MetallurgyRecipeBuilderSupport.fluid(fluidId), amount);
    }

    public CastingInTableRecipeBuilder result(ItemStack stack) {
        this.builder.output(stack);
        return this;
    }

    public CastingInTableRecipeBuilder result(ItemLike item) {
        this.builder.output(item);
        return this;
    }

    public CastingInTableRecipeBuilder result(String itemId) {
        return result(MetallurgyRecipeBuilderSupport.itemStack(itemId, 1));
    }

    public CastingInTableRecipeBuilder result(String itemId, int count) {
        return result(MetallurgyRecipeBuilderSupport.itemStack(itemId, count));
    }

    public CastingInTableRecipeBuilder output(ItemStack stack) {
        return result(stack);
    }

    public CastingInTableRecipeBuilder duration(int ticks) {
        this.builder.duration(ticks);
        return this;
    }

    public CastingInTableRecipeBuilder processingTime(int ticks) {
        return duration(ticks);
    }

    public CastingInTableRecipeBuilder moldConsumed(boolean consumed) {
        this.builder.withMoldConsumed(consumed);
        return this;
    }

    public void save(Consumer<FinishedRecipe> consumer) {
        this.builder.build(consumer);
    }
}
