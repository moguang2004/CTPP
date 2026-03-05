package com.mo_guang.ctpp.common.data.recipe;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

import com.mo_guang.ctpp.registry.CTPPItems;
import com.mo_guang.ctpp.registry.CTPPMultiblockMachines;
import com.simibubi.create.AllBlocks;

import java.util.function.Consumer;

public class KineticSteamTurbineRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        VanillaRecipeHelper.addShapedRecipe(provider, "kinetic_steam_turbine",
                new ItemStack(CTPPMultiblockMachines.KINETIC_STEAM_TURBINE.getItem()),
                "ABA",
                "CDC",
                "AEA",
                'A', ChemicalHelper.get(TagPrefix.plate, GTMaterials.Bronze),
                'B', CTPPItems.STEEL_MECHANISM,
                'C', CustomTags.LV_CIRCUITS,
                'D', GTBlocks.CASING_BRONZE_BRICKS,
                'E', AllBlocks.SHAFT);
    }
}
