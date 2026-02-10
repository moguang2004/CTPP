package com.mo_guang.ctpp.common.data.recipe;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.mo_guang.ctpp.registry.CTPPItems;
import com.mo_guang.ctpp.registry.CTPPMultiblockMachines;
import com.simibubi.create.AllBlocks;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class SmashingFactoryRecipes {
    public static void init(Consumer<FinishedRecipe> provider) {
        VanillaRecipeHelper.addShapedRecipe(provider, "smashing_factory", new ItemStack(CTPPMultiblockMachines.SMASHING_FACTORY.getItem()),
                "ABA",
                "BCB",
                "DED",
                'A', AllBlocks.CRUSHING_WHEEL,
                'B', ChemicalHelper.get(TagPrefix.gear, GTMaterials.Bronze),
                'C', CTPPItems.BASIC_MECHANISM,
                'D', AllBlocks.ANDESITE_CASING,
                'E', CustomTags.ULV_CIRCUITS);
    }
}
