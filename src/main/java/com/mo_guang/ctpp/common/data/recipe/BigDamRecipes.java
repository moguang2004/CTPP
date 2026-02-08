package com.mo_guang.ctpp.common.data.recipe;

import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.registry.CTPPItems;
import com.mo_guang.ctpp.registry.CTPPMultiblockMachines;
import com.mo_guang.ctpp.registry.CTPPRecipeTypes;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import com.simibubi.create.AllBlocks;
import net.minecraft.data.recipes.FinishedRecipe;

import com.mo_guang.ctpp.common.data.recipe.builder.CTPPRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

public class BigDamRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        VanillaRecipeHelper.addShapedRecipe(provider, "big_dam", new ItemStack(CTPPMultiblockMachines.BIG_DAM.getItem()),
                "ABA",
                "CDC",
                "AAA",
                'A', Blocks.STONE_BRICKS.asItem(),
                'B', CTPPItems.BASIC_MECHANISM,
                'C', AllBlocks.SHAFT.get().asItem(),
                'D', AllBlocks.LARGE_WATER_WHEEL.get().asItem());
        CTPPRecipeBuilder.of(GTCEu.id("big_dam"), CTPPRecipeTypes.BIG_DAM)
                .outputStress(2097152)
                .inputFluids(GTMaterials.Lubricant.getFluid(50))
                .duration(200)
                .save(provider);
    }
}
