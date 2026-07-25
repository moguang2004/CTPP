package com.mo_guang.ctpp.integration.jei;

import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.createmod.catnip.config.ConfigBase;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

import com.ctnhlang.CN;
import com.ctnhlang.Category;
import com.ctnhlang.EN;
import com.jesz.createdieselgenerators.CDGBlocks;
import com.jesz.createdieselgenerators.CDGRecipes;
import com.jesz.createdieselgenerators.compat.jei.BasinFermentingCategory;
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.common.kinetic.fan.acidwashing.AcidwashingRecipe;
import com.mo_guang.ctpp.common.kinetic.fan.breathing.BreathingRecipe;
import com.mo_guang.ctpp.data.recipe.builder.ctpp.MetalSmeltingRecipeBuilder;
import com.mo_guang.ctpp.data.recipe.fanprocessing.CTPPRecipeTypeInfo;
import com.mo_guang.ctpp.integration.jei.category.FanAcidWashingCategory;
import com.mo_guang.ctpp.integration.jei.category.FanBreathingCategory;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.CreateJEI;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.simibubi.create.compat.jei.CreateJEI.*;

@JeiPlugin
@Category("recipe")
public class CTPPJeiPlugin implements IModPlugin {

    @CN("批量龙吟")
    @EN("Dragon Fan Processing")
    private static Lang breathingCategory;

    @CN("在龙首后放置鼓风机")
    @EN("Place the fan behind the Dragon head")
    private static Lang breathingFan;

    @CN("批量酸洗")
    @EN("AcidWashing Fan Processing")
    private static Lang acidWashingCategory;

    @CN("在硫酸后放置鼓风机")
    @EN("Place the fan behind the Sulfuric acid")
    private static Lang acidWashingFan;

    @CN("发酵")
    @EN("Fermenting")
    private static Lang basinFermentingCategory;

    @CN("金属冶炼")
    @EN("Metal Smelting")
    private static Lang metalSmeltingCategory;

    private final List<CreateRecipeCategory<?>> categories = new ArrayList<>();

    @Override
    public ResourceLocation getPluginUid() {
        return CTPP.id("jei_plugin");
    }

    private void loadCategories() {
        this.categories.clear();
        CreateRecipeCategory<?> breathing = builder(BreathingRecipe.class)
                .addTypedRecipes(CTPPRecipeTypeInfo.BREATHING)
                .catalystStack(() -> AllBlocks.ENCASED_FAN.asStack()
                        .setHoverName(breathingFan.translate()
                                .withStyle(style -> style.withItalic(false))))
                .doubleItemIcon(AllItems.PROPELLER.get(), Items.DRAGON_BREATH)
                .emptyBackground(178, 72)
                .build("fan_breathing", breathingCategory.translate(), FanBreathingCategory::new);

        CreateRecipeCategory<?> acid_washing = builder(AcidwashingRecipe.class)
                .addTypedRecipes(CTPPRecipeTypeInfo.ACIDWASHING)
                .catalystStack(() -> AllBlocks.ENCASED_FAN.asStack()
                        .setHoverName(acidWashingFan.translate()
                                .withStyle(style -> style.withItalic(false))))
                .doubleItemIcon(AllItems.PROPELLER.get(), GTMaterials.SulfuricAcid.getBucket())
                .emptyBackground(178, 72)
                .build("fan_acid_washing", acidWashingCategory.translate(), FanAcidWashingCategory::new);

        CreateRecipeCategory<?> basin_fermenting = builder(BasinRecipe.class)
                .addTypedRecipesIf(CDGRecipes.BASIN_FERMENTING::getType,
                        r -> r.getId().getPath().startsWith("basin_fermenting"))
                .catalyst(CDGBlocks.BASIN_LID::get)
                .catalyst(AllBlocks.BASIN::get)
                .doubleItemIcon(AllBlocks.BASIN.get(), CDGBlocks.BASIN_LID.get())
                .emptyBackground(177, 100)
                .build("basin_fermenting", basinFermentingCategory.translate(), BasinFermentingCategory::new);

        CreateRecipeCategory<?> metal_smelting = builder(BasinRecipe.class)
                .addTypedRecipesIf(CDGRecipes.BASIN_FERMENTING::getType,
                        r -> r.getId().getPath().startsWith(MetalSmeltingRecipeBuilder.TYPE))
                .catalyst(CDGBlocks.BASIN_LID::get)
                .catalyst(AllBlocks.BASIN::get)
                .doubleItemIcon(AllBlocks.BASIN.get(), Items.IRON_INGOT)
                .emptyBackground(177, 100)
                .build(MetalSmeltingRecipeBuilder.TYPE, metalSmeltingCategory.translate(),
                        BasinFermentingCategory::new);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        loadCategories();
        registration.addRecipeCategories(categories.toArray(IRecipeCategory[]::new));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        categories.forEach(category -> category.registerRecipes(registration));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        categories.forEach(category -> category.registerCatalysts(registration));
    }

    private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
        return new CategoryBuilder<>(recipeClass);
    }

    private class CategoryBuilder<T extends Recipe<?>> {

        private final Class<? extends T> recipeClass;
        private Predicate<CRecipes> predicate = cRecipes -> true;

        private IDrawable background;
        private IDrawable icon;

        private final List<Consumer<List<T>>> recipeListConsumers = new ArrayList<>();
        private final List<Supplier<? extends ItemStack>> catalysts = new ArrayList<>();

        public CategoryBuilder(Class<? extends T> recipeClass) {
            this.recipeClass = recipeClass;
        }

        public CategoryBuilder<T> enableIf(Predicate<CRecipes> predicate) {
            this.predicate = predicate;
            return this;
        }

        public CategoryBuilder<T> enableWhen(Function<CRecipes, ConfigBase.ConfigBool> configValue) {
            predicate = c -> configValue.apply(c).get();
            return this;
        }

        public CategoryBuilder<T> addRecipeListConsumer(Consumer<List<T>> consumer) {
            recipeListConsumers.add(consumer);
            return this;
        }

        public CategoryBuilder<T> addRecipes(Supplier<Collection<? extends T>> collection) {
            return addRecipeListConsumer(recipes -> recipes.addAll(collection.get()));
        }

        public CategoryBuilder<T> addAllRecipesIf(Predicate<Recipe<?>> pred) {
            return addRecipeListConsumer(recipes -> consumeAllRecipes(recipe -> {
                if (pred.test(recipe)) {
                    recipes.add((T) recipe);
                }
            }));
        }

        public CategoryBuilder<T> addAllRecipesIf(Predicate<Recipe<?>> pred, Function<Recipe<?>, T> converter) {
            return addRecipeListConsumer(recipes -> consumeAllRecipes(recipe -> {
                if (pred.test(recipe)) {
                    recipes.add(converter.apply(recipe));
                }
            }));
        }

        public CategoryBuilder<T> addTypedRecipes(IRecipeTypeInfo recipeTypeEntry) {
            return addTypedRecipes(recipeTypeEntry::getType);
        }

        public CategoryBuilder<T> addTypedRecipes(Supplier<RecipeType<? extends T>> recipeType) {
            return addRecipeListConsumer(recipes -> CreateJEI.<T>consumeTypedRecipes(recipes::add, recipeType.get()));
        }

        public CategoryBuilder<T> addTypedRecipes(Supplier<RecipeType<? extends T>> recipeType,
                                                  Function<Recipe<?>, T> converter) {
            return addRecipeListConsumer(recipes -> CreateJEI
                    .<T>consumeTypedRecipes(recipe -> recipes.add(converter.apply(recipe)), recipeType.get()));
        }

        public CategoryBuilder<T> addTypedRecipesIf(Supplier<RecipeType<? extends T>> recipeType,
                                                    Predicate<Recipe<?>> pred) {
            return addRecipeListConsumer(recipes -> CreateJEI.<T>consumeTypedRecipes(recipe -> {
                if (pred.test(recipe)) {
                    recipes.add(recipe);
                }
            }, recipeType.get()));
        }

        public CategoryBuilder<T> addTypedRecipesExcluding(Supplier<RecipeType<? extends T>> recipeType,
                                                           Supplier<RecipeType<? extends T>> excluded) {
            return addRecipeListConsumer(recipes -> {
                List<Recipe<?>> excludedRecipes = getTypedRecipes(excluded.get());
                CreateJEI.<T>consumeTypedRecipes(recipe -> {
                    for (Recipe<?> excludedRecipe : excludedRecipes) {
                        if (doInputsMatch(recipe, excludedRecipe)) {
                            return;
                        }
                    }
                    recipes.add(recipe);
                }, recipeType.get());
            });
        }

        public CategoryBuilder<T> removeRecipes(Supplier<RecipeType<? extends T>> recipeType) {
            return addRecipeListConsumer(recipes -> {
                List<Recipe<?>> excludedRecipes = getTypedRecipes(recipeType.get());
                recipes.removeIf(recipe -> {
                    for (Recipe<?> excludedRecipe : excludedRecipes)
                        if (doInputsMatch(recipe, excludedRecipe) && doOutputsMatch(recipe, excludedRecipe))
                            return true;
                    return false;
                });
            });
        }

        public CategoryBuilder<T> catalystStack(Supplier<ItemStack> supplier) {
            catalysts.add(supplier);
            return this;
        }

        public CategoryBuilder<T> catalyst(Supplier<ItemLike> supplier) {
            return catalystStack(() -> new ItemStack(supplier.get()
                    .asItem()));
        }

        public CategoryBuilder<T> icon(IDrawable icon) {
            this.icon = icon;
            return this;
        }

        public CategoryBuilder<T> itemIcon(ItemLike item) {
            icon(new ItemIcon(() -> new ItemStack(item)));
            return this;
        }

        public CategoryBuilder<T> doubleItemIcon(ItemLike item1, ItemLike item2) {
            icon(new DoubleItemIcon(() -> new ItemStack(item1), () -> new ItemStack(item2)));
            return this;
        }

        public CategoryBuilder<T> background(IDrawable background) {
            this.background = background;
            return this;
        }

        public CategoryBuilder<T> emptyBackground(int width, int height) {
            background(new EmptyBackground(width, height));
            return this;
        }

        public CreateRecipeCategory<T> build(String name, Component title, CreateRecipeCategory.Factory<T> factory) {
            Supplier<List<T>> recipesSupplier;
            if (predicate.test(AllConfigs.server().recipes)) {
                recipesSupplier = () -> {
                    List<T> recipes = new ArrayList<>();
                    for (Consumer<List<T>> consumer : recipeListConsumers)
                        consumer.accept(recipes);
                    return recipes;
                };
            } else {
                recipesSupplier = Collections::emptyList;
            }

            CreateRecipeCategory.Info<T> info = new CreateRecipeCategory.Info<>(
                    new mezz.jei.api.recipe.RecipeType<>(CTPP.id(name), recipeClass),
                    title, background, icon, recipesSupplier, catalysts);
            CreateRecipeCategory<T> category = factory.create(info);
            categories.add(category);
            return category;
        }
    }
}
