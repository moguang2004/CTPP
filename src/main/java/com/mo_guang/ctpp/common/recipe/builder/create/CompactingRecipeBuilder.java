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

public class CompactingRecipeBuilder {

    private final ResourceLocation id;
    private final boolean exactId;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final List<Integer> ingredientCounts = new ArrayList<>();
    private final List<ItemStack> results = new ArrayList<>();
    private String heatRequirement = null;
    private final List<JsonObject> fluidIngredients = new ArrayList<>();
    private final List<JsonObject> fluidResults = new ArrayList<>();

    public CompactingRecipeBuilder(String name) {
        this(CTPP.id(name), false);
    }

    public CompactingRecipeBuilder(ResourceLocation id) {
        this(id, true);
    }

    private CompactingRecipeBuilder(ResourceLocation id, boolean exactId) {
        this.id = id;
        this.exactId = exactId;
    }

    public static CompactingRecipeBuilder builder(String name) {
        return new CompactingRecipeBuilder(name);
    }

    public static CompactingRecipeBuilder builder(ResourceLocation id) {
        return new CompactingRecipeBuilder(id);
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

    public CompactingRecipeBuilder inputFluid(Fluid fluid, int amount) {
        this.fluidIngredients.add(serializeFluidStack(new FluidStack(fluid, amount)));
        return this;
    }

    public CompactingRecipeBuilder inputFluid(FluidStack fluidStack) {
        this.fluidIngredients.add(serializeFluidStack(fluidStack));
        return this;
    }

    public CompactingRecipeBuilder inputFluid(String fluidId, int amount) {
        ResourceLocation fluid = ResourceLocation.parse(fluidId);
        return inputFluid(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluid), fluidId), amount);
    }

    public CompactingRecipeBuilder result(ItemStack stack) {
        this.results.add(stack.copy());
        return this;
    }

    public CompactingRecipeBuilder resultFluid(Fluid fluid, int amount) {
        this.fluidResults.add(serializeFluidStack(new FluidStack(fluid, amount)));
        return this;
    }

    public CompactingRecipeBuilder resultFluid(FluidStack fluidStack) {
        this.fluidResults.add(serializeFluidStack(fluidStack));
        return this;
    }

    public CompactingRecipeBuilder resultFluid(String fluidId, int amount) {
        ResourceLocation fluid = ResourceLocation.parse(fluidId);
        return resultFluid(Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(fluid), fluidId), amount);
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
        if ((ingredients.isEmpty() && fluidIngredients.isEmpty()) || (results.isEmpty() && fluidResults.isEmpty())) {
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
        fluidIngredients.forEach(ingredientsJson::add);
        json.add("ingredients", ingredientsJson);

        JsonArray resultsJson = new JsonArray();
        results.forEach(stack -> resultsJson.add(serializeItemStack(stack)));
        fluidResults.forEach(resultsJson::add);
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
                return exactId ? id :
                        ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "compacting/" + id.getPath());
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

    private static JsonObject serializeFluidStack(FluidStack stack) {
        JsonObject json = new JsonObject();
        json.addProperty("fluid", Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(stack.getFluid())).toString());
        json.addProperty("amount", stack.getAmount());
        return json;
    }
}
