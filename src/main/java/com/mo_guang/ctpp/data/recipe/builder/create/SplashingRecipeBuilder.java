package com.mo_guang.ctpp.data.recipe.builder.create;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
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

public class SplashingRecipeBuilder {

    private final ResourceLocation id;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final List<JsonObject> results = new ArrayList<>();

    public SplashingRecipeBuilder(String name) {
        this.id = CTPP.id(name);
    }

    public static SplashingRecipeBuilder builder(String name) {
        return new SplashingRecipeBuilder(name);
    }

    public SplashingRecipeBuilder input(ItemStack stack) {
        return input(Ingredient.of(stack));
    }

    public SplashingRecipeBuilder input(Item item) {
        return input(Ingredient.of(new ItemStack(item, 1)));
    }

    public SplashingRecipeBuilder input(TagKey<Item> tag) {
        return input(Ingredient.of(tag));
    }

    public SplashingRecipeBuilder input(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    public SplashingRecipeBuilder result(ItemStack stack) {
        return result(stack, null);
    }

    public SplashingRecipeBuilder result(ItemStack stack, Double chance) {
        JsonObject obj = new JsonObject();
        obj.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString());
        if (stack.getCount() != 1) obj.addProperty("count", stack.getCount());
        if (chance != null) obj.addProperty("chance", chance);
        this.results.add(obj);
        return this;
    }

    public SplashingRecipeBuilder result(String id, int count, Double chance) {
        JsonObject obj = new JsonObject();
        if (id.startsWith("#")) obj.addProperty("tag", id.substring(1));
        else obj.addProperty("item", id);
        if (count > 1) obj.addProperty("count", count);
        if (chance != null) obj.addProperty("chance", chance);
        this.results.add(obj);
        return this;
    }

    public SplashingRecipeBuilder resultTag(TagKey<Item> tag, int count, Double chance) {
        JsonObject obj = new JsonObject();
        if (tag != null) obj.addProperty("tag", tag.location().toString());
        if (count > 1) obj.addProperty("count", count);
        if (chance != null) obj.addProperty("chance", chance);
        this.results.add(obj);
        return this;
    }

    public void toJson(JsonObject json) {
        if (ingredients.isEmpty() || results.isEmpty()) {
            throw new IllegalStateException("Splashing recipe missing required fields");
        }

        json.addProperty("type", "create:splashing");

        JsonArray ingredientsJson = new JsonArray();
        ingredients.forEach(ing -> ingredientsJson.add(ing.toJson()));
        json.add("ingredients", ingredientsJson);

        JsonArray resultsJson = new JsonArray();
        results.forEach(resultsJson::add);
        json.add("results", resultsJson);
    }

    public FinishedRecipe build() {
        return new FinishedRecipe() {

            @Override
            public void serializeRecipeData(@Nonnull JsonObject pJson) {
                SplashingRecipeBuilder.this.toJson(pJson);
            }

            @Nonnull
            @Override
            public ResourceLocation getId() {
                return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "splashing/" + id.getPath());
            }

            @Nonnull
            @Override
            public RecipeSerializer<?> getType() {
                return Objects.requireNonNull(ForgeRegistries.RECIPE_SERIALIZERS.getValue(
                        ResourceLocation.tryParse("create:splashing")), "Create splashing serializer not found");
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
}
