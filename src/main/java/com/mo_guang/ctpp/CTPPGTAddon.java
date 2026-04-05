package com.mo_guang.ctpp;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.addon.events.KJSRecipeKeyEvent;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import com.mo_guang.ctpp.api.CTPPRecipeCapabilities;
import com.mo_guang.ctpp.api.StressRecipeCapability;
import com.mo_guang.ctpp.api.pattern.CTPPBlockMaps;
import com.mo_guang.ctpp.common.data.recipe.CTPPRecipes;
import com.mo_guang.ctpp.registry.CTPPBlocks;
import com.mojang.datafixers.util.Pair;

import java.util.List;
import java.util.function.Consumer;

import static com.mo_guang.ctpp.integration.kjs.CTPPRecipeComponents.SU_IN;
import static com.mo_guang.ctpp.integration.kjs.CTPPRecipeComponents.SU_OUT;

@GTAddon
public class CTPPGTAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return CTPPRegistration.REGISTRATE;
    }

    @Override
    public void initializeAddon() {
        CTPPBlocks.init();
        CTPPBlockMaps.init();
    }

    @Override
    public String addonModId() {
        return CTPP.MODID;
    }

    // @Override
    // public void registerMultiblockPreviewHighlighters(MultiblockPreviewHighlightRegistry registry) {
    // registry.registerAbilityHighlight(MultiblockPreviewHighlightRegistry.INPUT_COLOR,
    // CTPPPartAbility.INPUT_KINETIC);
    // registry.registerAbilityHighlight(MultiblockPreviewHighlightRegistry.OUTPUT_COLOR,
    // CTPPPartAbility.OUTPUT_KINETIC);
    // registry.registerAbilityHighlight(MultiblockPreviewHighlightRegistry.MAINTENANCE_COLOR,
    // CTPPPartAbility.MECHANICAL_UPGRADE);
    // }

    @Override
    public void registerRecipeCapabilities() {
        CTPPRecipeCapabilities.init();
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        CTPPRecipes.init(provider);
    }

    @Override
    public void registerRecipeKeys(KJSRecipeKeyEvent event) {
        event.registerKey(
                StressRecipeCapability.CAP,
                Pair.of(SU_IN, SU_OUT));
    }

    @Override
    public void removeRecipes(Consumer<ResourceLocation> consumer) {
        List<String> path = List.of(
                "create_new_age:shaped/carbon_brushes");

        path.forEach(s -> consumer.accept(ResourceLocation.tryParse(s)));
    }
}
