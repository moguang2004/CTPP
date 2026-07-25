package com.mo_guang.ctpp.data.recipe.builder.ctpp;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import com.google.gson.JsonObject;
import com.jesz.createdieselgenerators.content.basin_lid.BasinFermentingRecipe;
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.data.recipe.builder.CTPPProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class MetalSmeltingRecipeBuilder extends
                                        CTPPProcessingRecipeBuilder<MetalSmeltingRecipeBuilder, BasinFermentingRecipe> {

    public static final String TYPE = "metal_smelting";

    public MetalSmeltingRecipeBuilder(ResourceLocation recipeId) {
        super(BasinFermentingRecipe::new, recipeId);
    }

    public MetalSmeltingRecipeBuilder(String path) {
        this(CTPP.id(path));
    }
    public static MetalSmeltingRecipeBuilder builder(String path) {
        return new MetalSmeltingRecipeBuilder(path);
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer) {
        consumer.accept(new MetalSmeltingRecipe(build()));
    }

    private class MetalSmeltingRecipe implements FinishedRecipe {

        private final BasinFermentingRecipe recipe;
        private final ProcessingRecipeSerializer<BasinFermentingRecipe> serializer;

        public MetalSmeltingRecipe(BasinFermentingRecipe recipe) {
            this.recipe = recipe;
            this.serializer = (ProcessingRecipeSerializer<BasinFermentingRecipe>) recipe.getSerializer();
        }

        @Override
        public void serializeRecipeData(JsonObject jsonObject) {
            serializer.write(jsonObject, recipe);
        }

        @Override
        @SuppressWarnings("removal")
        public ResourceLocation getId() {
            return new ResourceLocation(recipe.getId().getNamespace(), TYPE + "/" + recipe.getId().getPath());
        }

        @Override
        public RecipeSerializer<?> getType() {
            return serializer;
        }

        @Override
        public @Nullable JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        public @Nullable ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
