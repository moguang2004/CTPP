package com.mo_guang.ctpp.common.recipe.builder.create;

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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

public class MixingRecipeBuilder {

    private final ResourceLocation id;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final List<ItemStack> results = new ArrayList<>();
    private final List<JsonObject> resultObjects = new ArrayList<>();
    // optional fluid result support
    private JsonObject fluidResult = null;
    private final List<JsonObject> fluidIngredients = new ArrayList<>();

    public MixingRecipeBuilder(String name) {
        this.id = CTPP.id(name);
    }

    public static MixingRecipeBuilder builder(String name) {
        return new MixingRecipeBuilder(name);
    }

    public MixingRecipeBuilder input(ItemStack stack) {
        return input(Ingredient.of(stack));
    }

    public MixingRecipeBuilder input(Item item) {
        return input(Ingredient.of(new ItemStack(item, 1)));
    }

    public MixingRecipeBuilder input(TagKey<Item> tag) {
        return input(Ingredient.of(tag));
    }

    public MixingRecipeBuilder input(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    public MixingRecipeBuilder inputFluid(Fluid fluid, int amount) {
        return inputFluid(fluid.toString(), amount);
    }

    public MixingRecipeBuilder inputFluid(FluidStack fluidStack) {
        return inputFluid(fluidStack.getFluid(), fluidStack.getAmount());
    }

    /**
     * Add a fluid as an ingredient to the mixing recipe.
     */
    public MixingRecipeBuilder inputFluid(String fluidId, int amount) {
        JsonObject json = new JsonObject();
        json.addProperty("fluid", fluidId);
        json.addProperty("amount", amount);
        this.fluidIngredients.add(json);
        return this;
    }

    public MixingRecipeBuilder result(ItemStack stack) {
        this.results.add(stack.copy());
        return this;
    }

    /**
     * Add a result with an optional chance.
     * If chance is null, result is guaranteed.
     */
    public MixingRecipeBuilder result(ItemStack stack, Double chance) {
        JsonObject j = serializeItemStack(stack);
        if (chance != null) j.addProperty("chance", chance);
        this.resultObjects.add(j);
        return this;
    }

    /**
     * Produce a fluid result from mixing (e.g., concrete fluid). Only one fluid result is supported.
     */
    public MixingRecipeBuilder resultFluid(String fluidId, int amount) {
        JsonObject json = new JsonObject();
        json.addProperty("fluid", fluidId);
        json.addProperty("amount", amount);
        this.fluidResult = json;
        return this;
    }

    public MixingRecipeBuilder output(ItemStack stack) {
        return result(stack);
    }

    public void toJson(JsonObject json) {
        if (ingredients.isEmpty() || (results.isEmpty() && fluidResult == null)) {
            throw new IllegalStateException("Mixing recipe missing required fields");
        }

        json.addProperty("type", "create:mixing");

        JsonArray ingredientsJson = new JsonArray();
        ingredients.forEach(ing -> ingredientsJson.add(ing.toJson()));
        // append fluid ingredients if present
        fluidIngredients.forEach(fi -> ingredientsJson.add(fi));
        json.add("ingredients", ingredientsJson);

        JsonArray resultsJson = new JsonArray();
        results.forEach(stack -> resultsJson.add(serializeItemStack(stack)));
        resultObjects.forEach(resultsJson::add);
        if (fluidResult != null) resultsJson.add(fluidResult);
        json.add("results", resultsJson);
    }

    public FinishedRecipe build() {
        return new FinishedRecipe() {

            @Override
            public void serializeRecipeData(@Nonnull JsonObject pJson) {
                MixingRecipeBuilder.this.toJson(pJson);
            }

            @Nonnull
            @Override
            public ResourceLocation getId() {
                return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "mixing/" + id.getPath());
            }

            @Nonnull
            @Override
            public RecipeSerializer<?> getType() {
                return Objects.requireNonNull(ForgeRegistries.RECIPE_SERIALIZERS.getValue(
                        ResourceLocation.tryParse("create:mixing")), "Create mixing serializer not found");
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

    private static JsonObject serializeItemStack(ItemStack stack) {
        JsonObject json = new JsonObject();
        json.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString());
        if (stack.getCount() != 1) json.addProperty("count", stack.getCount());
        if (stack.hasTag()) json.addProperty("nbt", String.valueOf(stack.getTag()));
        return json;
    }
}
