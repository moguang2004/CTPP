package com.mo_guang.ctpp.common.recipe.builder.create.diesel;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import com.google.gson.JsonObject;
import com.jesz.createdieselgenerators.content.distillation.DistillationRecipe;
import com.mo_guang.ctpp.CTPP;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

public class DistillationRecipeBuilder {

    private final ResourceLocation id;
    private final boolean exactId;
    private final List<Consumer<ProcessingRecipeBuilder<DistillationRecipe>>> steps = new ArrayList<>();
    private boolean hasIngredient;
    private boolean hasResult;

    public DistillationRecipeBuilder(String name) {
        this.exactId = name.contains(":");
        this.id = exactId ? ResourceLocation.parse(name) : CTPP.id(name);
    }

    public DistillationRecipeBuilder(ResourceLocation id) {
        this.exactId = true;
        this.id = id;
    }

    public static DistillationRecipeBuilder builder(String name) {
        return new DistillationRecipeBuilder(name);
    }

    public static DistillationRecipeBuilder builder(ResourceLocation id) {
        return new DistillationRecipeBuilder(id);
    }

    public DistillationRecipeBuilder inputFluid(Fluid fluid, int amount) {
        this.steps.add(builder -> builder.require(fluid, amount));
        this.hasIngredient = true;
        return this;
    }

    public DistillationRecipeBuilder inputFluid(FluidStack fluidStack) {
        return inputFluid(fluidStack.getFluid(), fluidStack.getAmount());
    }

    public DistillationRecipeBuilder inputFluid(String fluidId, int amount) {
        ResourceLocation fluid = ResourceLocation.parse(fluidId);
        return inputFluid(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluid), fluidId), amount);
    }

    public DistillationRecipeBuilder inputFluid(TagKey<Fluid> tag, int amount) {
        this.steps.add(builder -> builder.require(tag, amount));
        this.hasIngredient = true;
        return this;
    }

    public DistillationRecipeBuilder resultFluid(Fluid fluid, int amount) {
        this.steps.add(builder -> builder.output(fluid, amount));
        this.hasResult = true;
        return this;
    }

    public DistillationRecipeBuilder resultFluid(FluidStack fluidStack) {
        this.steps.add(builder -> builder.output(fluidStack));
        this.hasResult = true;
        return this;
    }

    public DistillationRecipeBuilder resultFluid(String fluidId, int amount) {
        ResourceLocation fluid = ResourceLocation.parse(fluidId);
        return resultFluid(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluid), fluidId), amount);
    }

    public DistillationRecipeBuilder heatRequirement(HeatCondition heatCondition) {
        this.steps.add(builder -> builder.requiresHeat(heatCondition));
        return this;
    }

    public DistillationRecipeBuilder heatRequirement(String heatRequirement) {
        return heatRequirement(HeatCondition.deserialize(heatRequirement));
    }

    public DistillationRecipeBuilder duration(int ticks) {
        this.steps.add(builder -> builder.duration(ticks));
        return this;
    }

    public DistillationRecipeBuilder processingTime(int ticks) {
        return duration(ticks);
    }

    public void toJson(JsonObject json) {
        if (!hasIngredient || !hasResult) {
            throw new IllegalStateException("Distillation recipe missing required fields");
        }
        serializer().write(json, recipe());
    }

    public FinishedRecipe build() {
        return new FinishedRecipe() {

            @Override
            public void serializeRecipeData(@Nonnull JsonObject pJson) {
                DistillationRecipeBuilder.this.toJson(pJson);
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
    private ProcessingRecipeSerializer<DistillationRecipe> serializer() {
        RecipeSerializer<?> serializer = recipe().getSerializer();
        if (!(serializer instanceof ProcessingRecipeSerializer<?> processingSerializer)) {
            throw new IllegalStateException("Distillation serializer not found");
        }
        return (ProcessingRecipeSerializer<DistillationRecipe>) processingSerializer;
    }

    private DistillationRecipe recipe() {
        ProcessingRecipeBuilder<DistillationRecipe> builder = new ProcessingRecipeBuilder<>(DistillationRecipe::new,
                recipeId());
        steps.forEach(step -> step.accept(builder));
        return builder.build();
    }

    private ResourceLocation recipeId() {
        return exactId ? id : ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "distillation/" + id.getPath());
    }
}
