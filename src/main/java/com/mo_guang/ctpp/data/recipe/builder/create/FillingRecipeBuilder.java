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

/**
 * {@code create:filling} 包装（CTPP 暂无填充配方 builder，单独提供）。
 *
 * <p>
 * 与原数据包一致，条件（{@code create_connected:feature_enabled} 等）内联到配方 JSON 的
 * {@code conditions} 字段。物品输入/输出为对象引用；流体输入为字符串 ID，
 * 可安全引用未加载模组的流体（如 garnished/create_dragons_plus 的染料流体）。
 */
public class FillingRecipeBuilder {

    private final ResourceLocation id;
    private final List<JsonObject> conditions = new ArrayList<>();
    private final List<JsonObject> ingredients = new ArrayList<>();
    private final List<JsonObject> results = new ArrayList<>();

    public FillingRecipeBuilder(String path) {
        this(CTPP.id(path));
    }

    public FillingRecipeBuilder(ResourceLocation id) {
        this.id = id;
    }

    public static FillingRecipeBuilder builder(String path) {
        return new FillingRecipeBuilder(path);
    }

    public static FillingRecipeBuilder builder(ResourceLocation id) {
        return new FillingRecipeBuilder(id);
    }

    /** {@code create_connected:feature_enabled} 条件 */
    public FillingRecipeBuilder feature(String feature) {
        JsonObject condition = new JsonObject();
        condition.addProperty("type", "create_connected:feature_enabled");
        condition.addProperty("feature", feature);
        return condition(condition);
    }

    /** {@code create_connected:feature_enabled_in_copycats} 条件 */
    public FillingRecipeBuilder featureInCopycats(String feature) {
        JsonObject condition = new JsonObject();
        condition.addProperty("type", "create_connected:feature_enabled_in_copycats");
        condition.addProperty("feature", feature);
        return condition(condition);
    }

    /** {@code forge:mod_loaded} 条件 */
    public FillingRecipeBuilder modLoaded(String modid) {
        JsonObject condition = new JsonObject();
        condition.addProperty("type", "forge:mod_loaded");
        condition.addProperty("modid", modid);
        return condition(condition);
    }

    /** {@code forge:not} 条件 */
    public FillingRecipeBuilder not(JsonObject condition) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "forge:not");
        json.add("value", condition);
        return condition(json);
    }

    /** {@code forge:or} 条件 */
    public FillingRecipeBuilder or(JsonObject... conditions) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "forge:or");
        JsonArray values = new JsonArray();
        for (JsonObject condition : conditions) {
            values.add(condition);
        }
        json.add("values", values);
        return condition(json);
    }

    public FillingRecipeBuilder condition(JsonObject condition) {
        if (condition != null) {
            conditions.add(condition);
        }
        return this;
    }

    public FillingRecipeBuilder input(ItemStack stack) {
        return input(Ingredient.of(stack));
    }

    public FillingRecipeBuilder input(Item item) {
        return input(Ingredient.of(item));
    }

    public FillingRecipeBuilder input(TagKey<Item> tag) {
        return input(Ingredient.of(tag));
    }

    public FillingRecipeBuilder input(Ingredient ingredient) {
        ingredients.add(ingredient.toJson().getAsJsonObject());
        return this;
    }

    /** 流体输入：{@code {"amount":N,"fluid":"<id>","nbt":{}}} */
    public FillingRecipeBuilder inputFluid(String fluidId, int amount) {
        JsonObject fluid = new JsonObject();
        fluid.addProperty("amount", amount);
        fluid.addProperty("fluid", fluidId);
        fluid.add("nbt", new JsonObject());
        ingredients.add(fluid);
        return this;
    }

    /** 流体标签输入：{@code {"amount":N,"fluidTag":"<id>"}} */
    public FillingRecipeBuilder inputFluidTag(String fluidTagId, int amount) {
        JsonObject fluid = new JsonObject();
        fluid.addProperty("amount", amount);
        fluid.addProperty("fluidTag", fluidTagId);
        ingredients.add(fluid);
        return this;
    }

    public FillingRecipeBuilder result(ItemStack stack) {
        results.add(serializeItemStack(stack));
        return this;
    }

    private void toJson(JsonObject json) {
        if (ingredients.isEmpty() || results.isEmpty()) {
            throw new IllegalStateException("Filling recipe missing required fields");
        }
        json.addProperty("type", "create:filling");

        if (!conditions.isEmpty()) {
            JsonArray conditionsJson = new JsonArray();
            conditions.forEach(conditionsJson::add);
            json.add("conditions", conditionsJson);
        }

        JsonArray ingredientsJson = new JsonArray();
        ingredients.forEach(ingredientsJson::add);
        json.add("ingredients", ingredientsJson);

        JsonArray resultsJson = new JsonArray();
        results.forEach(resultsJson::add);
        json.add("results", resultsJson);
    }

    public FinishedRecipe build() {
        return new FinishedRecipe() {

            @Override
            public void serializeRecipeData(JsonObject json) {
                FillingRecipeBuilder.this.toJson(json);
            }

            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public RecipeSerializer<?> getType() {
                return Objects.requireNonNull(ForgeRegistries.RECIPE_SERIALIZERS.getValue(
                        ResourceLocation.tryParse("create:filling")), "Create filling serializer not found");
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
