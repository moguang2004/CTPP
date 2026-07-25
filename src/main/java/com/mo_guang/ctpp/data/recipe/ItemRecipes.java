package com.mo_guang.ctpp.data.recipe;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.ItemTags;

import com.mo_guang.ctpp.registry.CTPPItems;
import com.mo_guang.ctpp.registry.CreateMaterials;

import java.util.function.Consumer;

public class ItemRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        VanillaRecipeHelper.addShapedRecipe(provider, "basic_mechanism", CTPPItems.BASIC_MECHANISM.asStack(),
                "ABC",
                "DEF",
                "GGG",
                'A', CustomTags.SAWS,
                'B', ChemicalHelper.get(TagPrefix.rod, CreateMaterials.AndesiteAlloy),
                'C', ChemicalHelper.get(TagPrefix.plate, GTMaterials.Gold),
                'D', ChemicalHelper.get(TagPrefix.plate, CreateMaterials.AndesiteAlloy),
                'E', ChemicalHelper.get(TagPrefix.gear, CreateMaterials.AndesiteAlloy),
                'F', ChemicalHelper.get(TagPrefix.rod, GTMaterials.Iron),
                'G', ItemTags.PLANKS);
    }
}
