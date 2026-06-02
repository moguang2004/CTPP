package com.mo_guang.ctpp.common.recipe.builder.create.metallurgy;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.CastingRecipeBuilder;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;

import java.util.function.Consumer;

public class CastingInBasinRecipeBuilder {

    private final CastingRecipeBuilder builder;

    private CastingInBasinRecipeBuilder(ResourceLocation id) {
        this.builder = new CastingRecipeBuilder(CMRecipeTypes.CASTING_IN_BASIN, id);
    }

    public static CastingInBasinRecipeBuilder builder(String name) {
        return new CastingInBasinRecipeBuilder(MetallurgyRecipeBuilderSupport.id(name));
    }

    public static CastingInBasinRecipeBuilder builder(ResourceLocation id) {
        return new CastingInBasinRecipeBuilder(id);
    }

    public CastingInBasinRecipeBuilder inputFluid(Fluid fluid, int amount) {
        this.builder.require(fluid, amount);
        return this;
    }

    public CastingInBasinRecipeBuilder inputFluid(FluidStack fluidStack) {
        return inputFluid(fluidStack.getFluid(), fluidStack.getAmount());
    }

    public CastingInBasinRecipeBuilder inputFluid(String fluidId, int amount) {
        return inputFluid(MetallurgyRecipeBuilderSupport.fluid(fluidId), amount);
    }

    public CastingInBasinRecipeBuilder result(ItemStack stack) {
        this.builder.output(stack);
        return this;
    }

    public CastingInBasinRecipeBuilder result(ItemLike item) {
        this.builder.output(item);
        return this;
    }

    public CastingInBasinRecipeBuilder result(String itemId) {
        return result(MetallurgyRecipeBuilderSupport.itemStack(itemId, 1));
    }

    public CastingInBasinRecipeBuilder result(String itemId, int count) {
        return result(MetallurgyRecipeBuilderSupport.itemStack(itemId, count));
    }

    public CastingInBasinRecipeBuilder output(ItemStack stack) {
        return result(stack);
    }

    public CastingInBasinRecipeBuilder duration(int ticks) {
        this.builder.duration(ticks);
        return this;
    }

    public CastingInBasinRecipeBuilder processingTime(int ticks) {
        return duration(ticks);
    }

    public void save(Consumer<FinishedRecipe> consumer) {
        this.builder.build(consumer);
    }
}
