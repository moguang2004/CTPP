package com.mo_guang.ctpp.data.recipe.builder;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.recipe.ingredient.IChancedIngredient;

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
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import java.util.function.Consumer;

public class CTPPProcessingRecipeBuilder<BUILDER extends CTPPProcessingRecipeBuilder<BUILDER, T>,
        T extends ProcessingRecipe<?>> extends ProcessingRecipeBuilder<T> {

    public CTPPProcessingRecipeBuilder(ProcessingRecipeFactory<T> factory, ResourceLocation recipeId) {
        super(factory, recipeId);
    }

    @SuppressWarnings("unchecked")
    public BUILDER getThis() {
        return (BUILDER) this;
    }

    public BUILDER input(ItemStack stack) {
        return input(Ingredient.of(stack));
    }

    public BUILDER input(ItemLike item) {
        return input(Ingredient.of(item));
    }

    public BUILDER input(TagKey<Item> tag) {
        return input(Ingredient.of(tag));
    }

    public BUILDER input(TagPrefix orePrefix, Material material) {
        return input(orePrefix, material, 1);
    }

    public BUILDER input(TagPrefix orePrefix, Material material, int amount) {
        return input(ChemicalHelper.get(orePrefix, material, amount));
    }

    public BUILDER input(Ingredient ingredient) {
        require(ingredient);
        return getThis();
    }

    public BUILDER inputFluid(Fluid fluid, int amount) {
        require(fluid, amount);
        return getThis();
    }

    public BUILDER inputFluid(FluidStack fluidStack) {
        require(FluidIngredient.fromFluidStack(fluidStack));
        return getThis();
    }

    public BUILDER output(ItemStack stack) {
        super.output(stack);
        return getThis();
    }

    public BUILDER output(TagPrefix orePrefix, Material material, int amount) {
        return output(ChemicalHelper.get(orePrefix, material, amount));
    }

    public BUILDER output(TagPrefix orePrefix, Material material) {
        return output(orePrefix, material, 1);
    }

    public BUILDER outputChanced(ItemStack stack, int chance) {
        output((float) chance / IChancedIngredient.MAX_CHANCE, stack);
        return getThis();
    }

    public BUILDER outputFluid(FluidStack fluidStack) {
        output(fluidStack);
        return getThis();
    }

    public BUILDER heat(HeatCondition condition) {
        requiresHeat(condition);
        return getThis();
    }

    public BUILDER duration(int ticks) {
        super.duration(ticks);
        return getThis();
    }

    public void save(Consumer<FinishedRecipe> consumer) {
        build(consumer);
    }
}
