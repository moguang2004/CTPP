package com.mo_guang.ctpp.data.recipe;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import net.minecraft.data.recipes.FinishedRecipe;

import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.registry.CTPPMachines;
import com.tterrag.registrate.util.entry.ItemEntry;

import java.util.function.Consumer;

public final class PlaceableEmitterRecipes {

    private PlaceableEmitterRecipes() {}

    public static void init(Consumer<FinishedRecipe> provider) {
        var emitters = new ItemEntry[] {
                GTItems.EMITTER_LV, GTItems.EMITTER_MV, GTItems.EMITTER_HV, GTItems.EMITTER_EV,
                GTItems.EMITTER_IV, GTItems.EMITTER_LuV, GTItems.EMITTER_ZPM, GTItems.EMITTER_UV };
        String[] tiers = { "lv", "mv", "hv", "ev", "iv", "luv", "zpm", "uv" };
        for (int tier = 0; tier < tiers.length; tier++) {
            VanillaRecipeHelper.addShapedRecipe(provider, CTPP.id("placeable_emitter_" + tiers[tier]),
                    CTPPMachines.PLACEABLE_EMITTER[tier + 1].asStack(),
                    " A ", "BCB", "D D", 'A', emitters[tier].asStack(), 'B',
                    ChemicalHelper.get(TagPrefix.rod, GTMaterials.Brass),
                    'C', CustomTags.LV_CIRCUITS, 'D', ChemicalHelper.get(TagPrefix.wireGtSingle, GTMaterials.Tin));
        }
    }
}
