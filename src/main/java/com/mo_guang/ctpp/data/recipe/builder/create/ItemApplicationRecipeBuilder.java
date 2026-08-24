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

public class ItemApplicationRecipeBuilder {

    private final ResourceLocation id;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private ItemStack result;

    public ItemApplicationRecipeBuilder(String name) {
        this(CTPP.id(name));
    }

    public ItemApplicationRecipeBuilder(ResourceLocation id) {
        this.id = id;
    }

    public static ItemApplicationRecipeBuilder builder(String name) {
        return new ItemApplicationRecipeBuilder(name);
    }

    public static ItemApplicationRecipeBuilder builder(ResourceLocation id) {
        return new ItemApplicationRecipeBuilder(id);
    }

    public ItemApplicationRecipeBuilder input(ItemStack stack) {
        return input(Ingredient.of(stack));
    }

    public ItemApplicationRecipeBuilder input(Item item) {
        return input(Ingredient.of(new ItemStack(item, 1)));
    }

    public ItemApplicationRecipeBuilder input(TagKey<Item> tag) {
        return input(Ingredient.of(tag));
    }

    public ItemApplicationRecipeBuilder input(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    public ItemApplicationRecipeBuilder result(ItemStack stack) {
        this.result = stack.copy();
        return this;
    }

    public ItemApplicationRecipeBuilder output(ItemStack stack) {
        return result(stack);
    }

    public void toJson(JsonObject json) {
        if (ingredients.isEmpty() || result == null) {
            throw new IllegalStateException("Item application recipe missing required fields");
        }

        json.addProperty("type", "create:item_application");

        JsonArray ingr = new JsonArray();
        ingredients.forEach(i -> ingr.add(i.toJson()));
        json.add("ingredients", ingr);

        JsonArray results = new JsonArray();
        results.add(serializeItemStack(result));
        json.add("results", results);
    }

    public FinishedRecipe build() {
        return new FinishedRecipe() {

            @Override
            public void serializeRecipeData(@Nonnull JsonObject pJson) {
                ItemApplicationRecipeBuilder.this.toJson(pJson);
            }

            @Nonnull
            @Override
            public ResourceLocation getId() {
                return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item_application/" + id.getPath());
            }

            @Nonnull
            @Override
            public RecipeSerializer<?> getType() {
                return Objects.requireNonNull(ForgeRegistries.RECIPE_SERIALIZERS.getValue(
                        ResourceLocation.tryParse("create:item_application")),
                        "Create item_application serializer not found");
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
