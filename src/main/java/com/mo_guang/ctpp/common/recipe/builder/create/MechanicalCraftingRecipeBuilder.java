package com.mo_guang.ctpp.common.recipe.builder.create;

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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

public class MechanicalCraftingRecipeBuilder {

    private final ResourceLocation id;
    private final boolean exactId;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final List<ItemStack> results = new ArrayList<>();
    // optional pattern support (Create's mechanical_crafting supports a pattern + key mapping)
    private final List<String> pattern = new ArrayList<>();
    private final Map<Character, Ingredient> key = new HashMap<>();

    public MechanicalCraftingRecipeBuilder(String name) {
        this(CTPP.id(name), false);
    }

    public MechanicalCraftingRecipeBuilder(ResourceLocation id) {
        this(id, true);
    }

    private MechanicalCraftingRecipeBuilder(ResourceLocation id, boolean exactId) {
        this.id = id;
        this.exactId = exactId;
    }

    public static MechanicalCraftingRecipeBuilder builder(String name) {
        return new MechanicalCraftingRecipeBuilder(name);
    }

    public static MechanicalCraftingRecipeBuilder builder(ResourceLocation id) {
        return new MechanicalCraftingRecipeBuilder(id);
    }

    public MechanicalCraftingRecipeBuilder input(ItemStack stack) {
        return input(Ingredient.of(stack));
    }

    public MechanicalCraftingRecipeBuilder input(Item item) {
        return input(Ingredient.of(new ItemStack(item, 1)));
    }

    public MechanicalCraftingRecipeBuilder input(TagKey<Item> tag) {
        return input(Ingredient.of(tag));
    }

    public MechanicalCraftingRecipeBuilder input(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    /**
     * Provide a pattern for mechanical crafting. Each string is a row (e.g. "ABC").
     * Use {@link #key(char, Ingredient)} to map characters to ingredients.
     */
    public MechanicalCraftingRecipeBuilder pattern(String... rows) {
        this.pattern.clear();
        for (String r : rows) {
            if (r == null) continue;
            this.pattern.add(r);
        }
        return this;
    }

    public MechanicalCraftingRecipeBuilder key(char c, ItemStack stack) {
        return key(c, Ingredient.of(stack));
    }

    public MechanicalCraftingRecipeBuilder key(char c, Item item) {
        return key(c, Ingredient.of(new ItemStack(item, 1)));
    }

    public MechanicalCraftingRecipeBuilder key(char c, TagKey<Item> tag) {
        return key(c, Ingredient.of(tag));
    }

    public MechanicalCraftingRecipeBuilder key(char c, Ingredient ingredient) {
        this.key.put(c, ingredient);
        return this;
    }

    public MechanicalCraftingRecipeBuilder result(ItemStack stack) {
        this.results.add(stack.copy());
        return this;
    }

    public MechanicalCraftingRecipeBuilder output(ItemStack stack) {
        return result(stack);
    }

    public void toJson(JsonObject json) {
        // Validate required fields.
        // Recipes are valid when they have at least one result and either:
        // - a flat ingredients list (legacy behaviour), or
        // - a pattern with an explicit key mapping, or
        // - a pattern and a flat ingredients list (we auto-map pattern characters to inputs).
        boolean hasResults = !results.isEmpty();
        boolean hasIngredients = !ingredients.isEmpty();
        boolean hasPattern = !pattern.isEmpty();
        boolean hasKey = !key.isEmpty();

        if (!hasResults || !(hasIngredients || (hasPattern && hasKey))) {
            throw new IllegalStateException("Mechanical crafting recipe missing required fields");
        }

        json.addProperty("type", "create:mechanical_crafting");

        if (!pattern.isEmpty()) {
            // Decide whether we will emit pattern+key or fall back to a flat ingredients array.
            JsonObject keyObj = new JsonObject();
            boolean emitPatternWithKey = false;

            if (!key.isEmpty()) {
                // explicit key provided -> emit pattern + key
                for (Map.Entry<Character, Ingredient> e : key.entrySet()) {
                    keyObj.add(String.valueOf(e.getKey()), e.getValue().toJson());
                }
                emitPatternWithKey = true;
            } else if (!ingredients.isEmpty()) {
                // try to auto-map inputs to unique pattern characters
                Set<Character> chars = new LinkedHashSet<>();
                for (String row : pattern) {
                    if (row == null) continue;
                    for (char c : row.toCharArray()) {
                        if (c == ' ' || c == '\u0000') continue;
                        chars.add(c);
                    }
                }

                if (chars.size() == ingredients.size()) {
                    int i = 0;
                    for (Character c : chars) {
                        keyObj.add(String.valueOf(c), ingredients.get(i++).toJson());
                    }
                    emitPatternWithKey = true;
                }
            }

            if (emitPatternWithKey) {
                // emit pattern + key mapping
                JsonArray patternArr = new JsonArray();
                pattern.forEach(patternArr::add);
                json.add("pattern", patternArr);
                if (keyObj.size() > 0) json.add("key", keyObj);
            } else {
                // fallback: emit flat ingredients array (legacy behaviour)
                JsonArray ingredientsJson = new JsonArray();
                ingredients.forEach(ing -> ingredientsJson.add(ing.toJson()));
                json.add("ingredients", ingredientsJson);
            }
        } else {
            // fallback: emit flat ingredients array (legacy behaviour)
            JsonArray ingredientsJson = new JsonArray();
            ingredients.forEach(ing -> ingredientsJson.add(ing.toJson()));
            json.add("ingredients", ingredientsJson);
        }

        // Create's mechanical_crafting expects a single "result" object for the output.
        // If multiple outputs are present, fall back to an array named "results".
        if (results.size() == 1) {
            json.add("result", serializeItemStack(results.get(0)));
        } else {
            JsonArray resultsJson = new JsonArray();
            results.forEach(stack -> resultsJson.add(serializeItemStack(stack)));
            json.add("results", resultsJson);
        }
    }

    public FinishedRecipe build() {
        return new FinishedRecipe() {

            @Override
            public void serializeRecipeData(@Nonnull JsonObject pJson) {
                MechanicalCraftingRecipeBuilder.this.toJson(pJson);
            }

            @Nonnull
            @Override
            public ResourceLocation getId() {
                if (exactId) return id;
                return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "mechanical_crafting/" + id.getPath());
            }

            @Nonnull
            @Override
            public RecipeSerializer<?> getType() {
                return Objects.requireNonNull(ForgeRegistries.RECIPE_SERIALIZERS.getValue(
                        ResourceLocation.tryParse("create:mechanical_crafting")),
                        "Create mechanical_crafting serializer not found");
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
