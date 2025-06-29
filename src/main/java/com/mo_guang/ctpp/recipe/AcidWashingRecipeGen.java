package com.mo_guang.ctpp.recipe;

import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterialItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.common.data.CTPPRecipeTypeInfo;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

public class AcidWashingRecipeGen extends ProcessingRecipeGen {
    public AcidWashingRecipeGen(PackOutput generator) {
        super(generator, CTPP.MODID);
    }

    GeneratedRecipe ETHYLENE_TO_PLASTIC = convert(GTMaterialItems.MATERIAL_ITEMS.get( TagPrefix.plate, GTMaterials.Polyethylene).get(), GTItems.PLASTIC_BOARD.asItem());
    GeneratedRecipe PC_TO_PLASTIC = convert(GTMaterialItems.MATERIAL_ITEMS.get(TagPrefix.plate, GTMaterials.PolyvinylChloride).get(), GTItems.PLASTIC_BOARD.asStack(2));
    GeneratedRecipe PFE_TO_PLASTIC = convert(GTMaterialItems.MATERIAL_ITEMS.get(TagPrefix.plate, GTMaterials.Polytetrafluoroethylene).get(), GTItems.PLASTIC_BOARD.asStack(4));
    GeneratedRecipe PBI_TO_PLASTIC = convert(GTMaterialItems.MATERIAL_ITEMS.get(TagPrefix.plate, GTMaterials.Polybenzimidazole).get(), GTItems.PLASTIC_BOARD.asStack(8));
    GeneratedRecipe EPOXY_TO_BOARD = convert(GTMaterialItems.MATERIAL_ITEMS.get(TagPrefix.plate, GTMaterials.Epoxy).get(), GTItems.EPOXY_BOARD.asStack());
    GeneratedRecipe REINFORCED_EPOXY_TO_BOARD = convert(GTMaterialItems.MATERIAL_ITEMS.get(TagPrefix.plate, GTMaterials.ReinforcedEpoxyResin).get(), GTItems.FIBER_BOARD.asStack());
    GeneratedRecipe TOLUENE_TO_TNT = convert(GTItems.GELLED_TOLUENE.asStack(4), Items.TNT.getDefaultInstance());
    GeneratedRecipe IRON_BLOCK = convert(AllBlocks.WEATHERED_IRON_BLOCK.asItem(), AllBlocks.INDUSTRIAL_IRON_BLOCK.asItem());


    public GeneratedRecipe convert(ItemLike input, ItemLike result) {
        return convert(() -> Ingredient.of(input), () -> result);
    }
    public GeneratedRecipe convert(ItemLike input, ItemStack result) {
        return create(CTPP.id(CatnipServices.REGISTRIES.getKeyOrThrow(result.getItem())
                        .getPath() + ((result.getCount() == 1) ? "" : "_" + result.getCount())),
                p -> p.withItemIngredients(Ingredient.of(input))
                        .output(result));
    }
    public GeneratedRecipe convert(ItemStack input, ItemStack result) {
        return create(CTPP.id(CatnipServices.REGISTRIES.getKeyOrThrow(result.getItem())
                        .getPath() + ((result.getCount() == 1) ? "" : "_" + result.getCount())),
                p -> p.withItemIngredients(Ingredient.of(input))
                        .output(result));
    }

    public GeneratedRecipe convert(Supplier<Ingredient> input, Supplier<ItemLike> result) {
        return create(CTPP.id(CatnipServices.REGISTRIES.getKeyOrThrow(result.get()
                                .asItem())
                        .getPath()),
                p -> p.withItemIngredients(input.get())
                        .output(result.get()));
    }
    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CTPPRecipeTypeInfo.ACIDWASHING;
    }
}
