package com.mo_guang.ctpp.data.recipe;

import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.data.recipe.builder.CTPPRecipeBuilder;
import com.mo_guang.ctpp.registry.CTPPMultiblockMachines;
import com.mo_guang.ctpp.registry.CTPPRecipeTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;

import java.util.function.Consumer;

public class WindmillControlRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        VanillaRecipeHelper.addShapedRecipe(provider, "windmill_control_center",
                new ItemStack(CTPPMultiblockMachines.WINDMILL_CONTROL_CENTER.getItem()),
                " A ",
                "BCB",
                "DED",
                'A', AllBlocks.REDSTONE_LINK,
                'B', AllItems.PRECISION_MECHANISM,
                'C', AllItems.LINKED_CONTROLLER,
                'D', AllBlocks.BRASS_CASING,
                'E', AllBlocks.SHAFT);
        CTPPRecipeBuilder.of(CTPP.id("windmill_control"), CTPPRecipeTypes.WINDMILL_CONTROL)
                .inputFluids(GTMaterials.Lubricant.getFluid(25))
                .duration(200)
                .outputStress(512)
                .save(provider);
    }
}
