package com.mo_guang.ctpp.data.recipe.builder.vintage;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mo_guang.ctpp.CTPP;
import com.negodya1.vintageimprovements.VintageRecipes;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

public abstract class AbstractVintageRecipeBuilder<T extends AbstractVintageRecipeBuilder<T>> {

    private final ResourceLocation id;
    private final VintageRecipes recipe;
    private final boolean exactId;
    private final List<JsonObject> ingredients = new ArrayList<>();
    private final List<VintageRecipeResult> results = new ArrayList<>();
    private String heatRequirement;
    private Integer processingTime;
    private Integer minimalRpm;

    protected AbstractVintageRecipeBuilder(String name, VintageRecipes recipe) {
        this(CTPP.id(name), recipe, false);
    }

    protected AbstractVintageRecipeBuilder(ResourceLocation id, VintageRecipes recipe) {
        this(id, recipe, true);
    }

    private AbstractVintageRecipeBuilder(ResourceLocation id, VintageRecipes recipe, boolean exactId) {
        this.id = id;
        this.recipe = recipe;
        this.exactId = exactId;
    }

    public T input(ItemStack stack) {
        return input(Ingredient.of(stack));
    }

    public T input(Item item) {
        return input(Ingredient.of(new ItemStack(item, 1)));
    }

    public T input(TagKey<Item> tag) {
        return input(Ingredient.of(tag));
    }

    public T input(Ingredient ingredient) {
        this.ingredients.add(ingredient.toJson().getAsJsonObject());
        return self();
    }

    public T inputFluid(Fluid fluid, int amount) {
        return inputFluid(new FluidStack(fluid, amount));
    }

    public T inputFluid(FluidStack stack) {
        this.ingredients.add(serializeFluidStack(stack));
        return self();
    }

    public T inputFluid(String fluidId, int amount) {
        JsonObject json = new JsonObject();
        json.addProperty("fluid", fluidId);
        json.addProperty("amount", amount);
        this.ingredients.add(json);
        return self();
    }

    public T result(ItemStack stack) {
        this.results.add(VintageRecipeResult.item(stack.copy(), null));
        return self();
    }

    public T result(ItemStack stack, double chance) {
        this.results.add(VintageRecipeResult.item(stack.copy(), chance));
        return self();
    }

    public T resultFluid(Fluid fluid, int amount) {
        return resultFluid(new FluidStack(fluid, amount));
    }

    public T resultFluid(FluidStack stack) {
        this.results.add(VintageRecipeResult.fluid(stack.copy()));
        return self();
    }

    public T resultFluid(String fluidId, int amount) {
        JsonObject json = new JsonObject();
        json.addProperty("fluid", fluidId);
        json.addProperty("amount", amount);
        this.results.add(new VintageRecipeResult(json));
        return self();
    }

    public T output(ItemStack stack) {
        return result(stack);
    }

    public T heatRequirement(String heatRequirement) {
        this.heatRequirement = heatRequirement;
        return self();
    }

    public T processingTime(int processingTime) {
        this.processingTime = processingTime;
        return self();
    }

    public T minimalRpm(int minimalRpm) {
        this.minimalRpm = minimalRpm;
        return self();
    }

    public T minimalRPM(int minimalRpm) {
        return minimalRpm(minimalRpm);
    }

    public void toJson(JsonObject json) {
        if (ingredients.isEmpty() || results.isEmpty()) {
            throw new IllegalStateException("Vintage recipe missing required fields");
        }

        json.addProperty("type", recipe.getId().toString());

        JsonArray ingredientsJson = new JsonArray();
        ingredients.forEach(ingredientsJson::add);
        json.add("ingredients", ingredientsJson);

        JsonArray resultsJson = new JsonArray();
        results.forEach(result -> resultsJson.add(result.toJson()));
        json.add("results", resultsJson);

        if (heatRequirement != null) json.addProperty("heatRequirement", heatRequirement);
        if (processingTime != null) json.addProperty("processingTime", processingTime);
        if (minimalRpm != null) json.addProperty("minimalRPM", minimalRpm);
        addExtraJson(json);
    }

    public FinishedRecipe build() {
        return new FinishedRecipe() {

            @Override
            public void serializeRecipeData(@Nonnull JsonObject pJson) {
                AbstractVintageRecipeBuilder.this.toJson(pJson);
            }

            @Nonnull
            @Override
            public ResourceLocation getId() {
                if (exactId) return id;
                return ResourceLocation.fromNamespaceAndPath(id.getNamespace(),
                        recipe.getId().getPath() + "/" + id.getPath());
            }

            @Nonnull
            @Override
            public RecipeSerializer<?> getType() {
                return recipe.getSerializer();
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

    protected void addExtraJson(JsonObject json) {}

    @SuppressWarnings("unchecked")
    private T self() {
        return (T) this;
    }

    private static JsonObject serializeFluidStack(FluidStack stack) {
        JsonObject json = new JsonObject();
        json.addProperty("fluid", Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(stack.getFluid())).toString());
        json.addProperty("amount", stack.getAmount());
        return json;
    }
}
