package com.mo_guang.ctpp.common.recipe.builder.create.diesel;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import com.google.gson.JsonObject;
import com.jesz.createdieselgenerators.content.basin_lid.BasinFermentingRecipe;
import com.mo_guang.ctpp.CTPP;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

public class BasinFermentingRecipeBuilder {

    private final ResourceLocation id;
    private final boolean exactId;
    private final List<Consumer<ProcessingRecipeBuilder<BasinFermentingRecipe>>> steps = new ArrayList<>();
    private boolean hasIngredient;
    private boolean hasResult;

    public BasinFermentingRecipeBuilder(String name) {
        this.exactId = name.contains(":");
        this.id = exactId ? ResourceLocation.parse(name) : CTPP.id(name);
    }

    public BasinFermentingRecipeBuilder(ResourceLocation id) {
        this.exactId = true;
        this.id = id;
    }

    public static BasinFermentingRecipeBuilder builder(String name) {
        return new BasinFermentingRecipeBuilder(name);
    }

    public static BasinFermentingRecipeBuilder builder(ResourceLocation id) {
        return new BasinFermentingRecipeBuilder(id);
    }

    public BasinFermentingRecipeBuilder input(ItemStack stack) {
        return input(Ingredient.of(stack));
    }

    public BasinFermentingRecipeBuilder input(ItemLike item) {
        return input(Ingredient.of(item));
    }

    public BasinFermentingRecipeBuilder input(TagKey<Item> tag) {
        return input(Ingredient.of(tag));
    }

    public BasinFermentingRecipeBuilder input(Ingredient ingredient) {
        this.steps.add(builder -> builder.require(ingredient));
        this.hasIngredient = true;
        return this;
    }

    public BasinFermentingRecipeBuilder inputFluid(Fluid fluid, int amount) {
        this.steps.add(builder -> builder.require(fluid, amount));
        this.hasIngredient = true;
        return this;
    }

    public BasinFermentingRecipeBuilder inputFluid(FluidStack fluidStack) {
        return inputFluid(fluidStack.getFluid(), fluidStack.getAmount());
    }

    public BasinFermentingRecipeBuilder inputFluid(String fluidId, int amount) {
        ResourceLocation fluid = ResourceLocation.parse(fluidId);
        return inputFluid(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluid), fluidId), amount);
    }

    public BasinFermentingRecipeBuilder result(ItemStack stack) {
        this.steps.add(builder -> builder.output(stack));
        this.hasResult = true;
        return this;
    }

    public BasinFermentingRecipeBuilder result(ItemStack stack, double chance) {
        this.steps.add(builder -> builder.output((float) chance, stack));
        this.hasResult = true;
        return this;
    }

    public BasinFermentingRecipeBuilder result(String itemId, int count, double chance) {
        this.steps.add(builder -> builder.output((float) chance, ResourceLocation.parse(itemId), count));
        this.hasResult = true;
        return this;
    }

    public BasinFermentingRecipeBuilder output(ItemStack stack) {
        return result(stack);
    }

    public BasinFermentingRecipeBuilder resultFluid(Fluid fluid, int amount) {
        this.steps.add(builder -> builder.output(fluid, amount));
        this.hasResult = true;
        return this;
    }

    public BasinFermentingRecipeBuilder resultFluid(FluidStack fluidStack) {
        this.steps.add(builder -> builder.output(fluidStack));
        this.hasResult = true;
        return this;
    }

    public BasinFermentingRecipeBuilder resultFluid(String fluidId, int amount) {
        ResourceLocation fluid = ResourceLocation.parse(fluidId);
        return resultFluid(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluid), fluidId), amount);
    }

    public BasinFermentingRecipeBuilder duration(int ticks) {
        this.steps.add(builder -> builder.duration(ticks));
        return this;
    }

    public BasinFermentingRecipeBuilder processingTime(int ticks) {
        return duration(ticks);
    }

    public void toJson(JsonObject json) {
        if (!hasIngredient || !hasResult) {
            throw new IllegalStateException("Basin fermenting recipe missing required fields");
        }
        serializer().write(json, recipe());
    }

    public FinishedRecipe build() {
        return new FinishedRecipe() {

            @Override
            public void serializeRecipeData(@Nonnull JsonObject pJson) {
                BasinFermentingRecipeBuilder.this.toJson(pJson);
            }

            @Nonnull
            @Override
            public ResourceLocation getId() {
                return recipeId();
            }

            @Nonnull
            @Override
            public RecipeSerializer<?> getType() {
                return recipe().getSerializer();
            }

            @Nullable
            @Override
            public JsonObject serializeAdvancement() {
                return null;
            }

            @Nullable
            @Override
            public ResourceLocation getAdvancementId() {
                return null;
            }
        };
    }

    public void save(Consumer<FinishedRecipe> consumer) {
        consumer.accept(build());
    }

    @SuppressWarnings("unchecked")
    private ProcessingRecipeSerializer<BasinFermentingRecipe> serializer() {
        RecipeSerializer<?> serializer = recipe().getSerializer();
        if (!(serializer instanceof ProcessingRecipeSerializer<?> processingSerializer)) {
            throw new IllegalStateException("Basin fermenting serializer not found");
        }
        return (ProcessingRecipeSerializer<BasinFermentingRecipe>) processingSerializer;
    }

    private BasinFermentingRecipe recipe() {
        ProcessingRecipeBuilder<BasinFermentingRecipe> builder = new ProcessingRecipeBuilder<>(
                BasinFermentingRecipe::new, recipeId());
        steps.forEach(step -> step.accept(builder));
        return builder.build();
    }

    private ResourceLocation recipeId() {
        return exactId ? id :
                ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "basin_fermenting/" + id.getPath());
    }
}
