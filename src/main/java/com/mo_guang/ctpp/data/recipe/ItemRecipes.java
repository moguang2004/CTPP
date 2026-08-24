package com.mo_guang.ctpp.data.recipe;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import com.mo_guang.ctpp.registry.CTPPBlocks;
import com.mo_guang.ctpp.registry.CTPPItems;
import com.mo_guang.ctpp.registry.CreateMaterials;
import com.simibubi.create.AllBlocks;

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

        for (var toolbox : CTPPBlocks.TOOLBOXES) {
            var stack = toolbox.asStack();
            var color = toolbox.get().getColor();
            VanillaRecipeHelper.addShapedRecipe(provider, true, "toobox/" + color.getName(),
                    stack,
                    "ABA",
                    "CDC",
                    " E ",
                    'A', color.getTag(),
                    'B', AllBlocks.COGWHEEL,
                    'C', ChemicalHelper.get(TagPrefix.plate, GTMaterials.Gold),
                    'D', Tags.Items.CHESTS,
                    'E', Tags.Items.LEATHER);

        }
    }
}
