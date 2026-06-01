package com.mo_guang.ctpp.common.recipe.builder.create;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
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

public class CompactingRecipeBuilder {

    private final ResourceLocation id;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final List<Integer> ingredientCounts = new ArrayList<>();
    private final List<FluidStack> fluidStacks = new ArrayList<>();
    private final List<ItemStack> results = new ArrayList<>();
    private String heatRequirement = null;

    public CompactingRecipeBuilder(String name) {
        this.id = CTPP.id(name);
    }

    public static CompactingRecipeBuilder builder(String name) {
        return new CompactingRecipeBuilder(name);
    }

    public CompactingRecipeBuilder input(ItemStack stack) {
        return input(Ingredient.of(stack), stack.getCount());
    }

    public CompactingRecipeBuilder input(Item item) {
        return input(Ingredient.of(new ItemStack(item, 1)), 1);
    }

    public CompactingRecipeBuilder input(Item item, int count) {
        return input(Ingredient.of(new ItemStack(item, count)), count);
    }

    public CompactingRecipeBuilder input(TagKey<Item> tag) {
        return input(Ingredient.of(tag), 1);
    }

    public CompactingRecipeBuilder input(Ingredient ingredient) {
        return input(ingredient, 1);
    }

    public CompactingRecipeBuilder input(TagKey<Item> tag, int count) {
        return input(Ingredient.of(tag), count);
    }

    public CompactingRecipeBuilder input(Ingredient ingredient, int count) {
        this.ingredients.add(ingredient);
        this.ingredientCounts.add(count);
        return this;
    }

    public CompactingRecipeBuilder input(FluidStack fluid) {
        this.fluidStacks.add(fluid.copy());
        return this;
    }

    public CompactingRecipeBuilder result(ItemStack stack) {
        this.results.add(stack.copy());
        return this;
    }

    public CompactingRecipeBuilder output(ItemStack stack) {
        return result(stack);
    }

    public CompactingRecipeBuilder heated() {
        this.heatRequirement = "heated";
        return this;
    }

    public CompactingRecipeBuilder superHeated() {
        this.heatRequirement = "superheated";
        return this;
    }

    public void toJson(JsonObject json) {
        if ((ingredients.isEmpty() && fluidStacks.isEmpty()) || results.isEmpty()) {
            throw new IllegalStateException("Compacting recipe missing required fields");
        }

        json.addProperty("type", "create:compacting");

        // TODO: 这里如果使用count键的话配方不识别，暂时使用重复多次解决，但是不本质。
        JsonArray ingredientsJson = new JsonArray();
        for (int i = 0; i < ingredients.size(); i++) {
            JsonObject ingJson = ingredients.get(i).toJson().getAsJsonObject();
            int count = ingredientCounts.get(i);
            for (int j = 0; j < count; j++) {
                ingredientsJson.add(ingJson.deepCopy());
            }
        }
        fluidStacks.forEach(fluid -> {
            JsonObject fluidJson = new JsonObject();
            fluidJson.addProperty("fluid",
                    Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(fluid.getFluid())).toString());
            fluidJson.addProperty("amount", fluid.getAmount());
            ingredientsJson.add(fluidJson);
        });
        json.add("ingredients", ingredientsJson);

        JsonArray resultsJson = new JsonArray();
        results.forEach(stack -> resultsJson.add(serializeItemStack(stack)));
        json.add("results", resultsJson);

        if (heatRequirement != null) {
            json.addProperty("heatRequirement", heatRequirement);
        }
    }

    public FinishedRecipe build() {
        return new FinishedRecipe() {

            @Override
            public void serializeRecipeData(@Nonnull JsonObject pJson) {
                CompactingRecipeBuilder.this.toJson(pJson);
            }

            @Nonnull
            @Override
            public ResourceLocation getId() {
                return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "compacting/" + id.getPath());
            }

            @Nonnull
            @Override
            public RecipeSerializer<?> getType() {
                return Objects.requireNonNull(ForgeRegistries.RECIPE_SERIALIZERS.getValue(
                        ResourceLocation.tryParse("create:compacting")), "Create compacting serializer not found");
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
