package com.mo_guang.ctpp.common.data.recipe;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.common.data.recipe.builder.CTPPRecipeBuilder;
import com.mo_guang.ctpp.registry.CTPPMultiblockMachines;
import com.mo_guang.ctpp.registry.CTPPRecipeTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class SeaweedFarmRecipes {
    public static void init(Consumer<FinishedRecipe> provider) {
        VanillaRecipeHelper.addShapedRecipe(provider, "seaweed_farm", new ItemStack(CTPPMultiblockMachines.SEAWEED_FARM.getItem()),
                "ABA",
                "CDC",
                "EFE",
                'A', AllBlocks.LARGE_COGWHEEL,
                'B', AllBlocks.COGWHEEL,
                'C', AllItems.PRECISION_MECHANISM,
                'D', AllBlocks.ANDESITE_CASING,
                'E', AllBlocks.SHAFT,
                'F', ChemicalHelper.get(TagPrefix.gear, GTMaterials.Bronze));
        CTPPRecipeBuilder.of(CTPP.id("seaweed"), CTPPRecipeTypes.SEAWEED_FARM)
                .inputStress(512)
                .inputItems(GTItems.FERTILIZER.asStack())
                .notConsumable(Items.KELP.getDefaultInstance())
                .duration(400)
                .outputItems(Items.KELP.getDefaultInstance().copyWithCount(4))
                .chancedOutput(Items.KELP.getDefaultInstance().copyWithCount(2), 2000, 500)
                .chancedOutput(Items.KELP.getDefaultInstance().copyWithCount(1), 500, 500)
                .save(provider);
    }
}
