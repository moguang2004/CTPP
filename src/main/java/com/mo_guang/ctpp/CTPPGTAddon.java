package com.mo_guang.ctpp;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockPreviewHighlightRegistry;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.createmod.catnip.placement.PlacementHelpers;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import com.mo_guang.ctpp.api.CTPPPartAbility;
import com.mo_guang.ctpp.api.CTPPRecipeCapabilities;
import com.mo_guang.ctpp.api.pattern.CTPPBlockMaps;
import com.mo_guang.ctpp.common.block.MagnetPlacementHelper;
import com.mo_guang.ctpp.data.recipe.CTPPRecipes;
import com.mo_guang.ctpp.registry.CTPPBlockEntities;
import com.mo_guang.ctpp.registry.CTPPBlocks;

import java.util.function.Consumer;

@GTAddon
public class CTPPGTAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return CTPPRegistration.REGISTRATE;
    }

    @Override
    public void initializeAddon() {
        CTPPBlocks.init();
        CTPPBlockEntities.init();
        CTPPBlockMaps.init();
        PlacementHelpers.register(MagnetPlacementHelper.INSTANCE);
    }

    @Override
    public String addonModId() {
        return CTPP.MODID;
    }

    @Override
    public void registerMultiblockPreviewHighlighters(MultiblockPreviewHighlightRegistry registry) {
        registry.registerAbilityHighlight(MultiblockPreviewHighlightRegistry.INPUT_COLOR,
                CTPPPartAbility.INPUT_KINETIC);
        registry.registerAbilityHighlight(MultiblockPreviewHighlightRegistry.OUTPUT_COLOR,
                CTPPPartAbility.OUTPUT_KINETIC);
        registry.registerAbilityHighlight(MultiblockPreviewHighlightRegistry.MAINTENANCE_COLOR,
                CTPPPartAbility.MECHANICAL_UPGRADE);
    }

    @Override
    public void registerRecipeCapabilities() {
        CTPPRecipeCapabilities.init();
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        CTPPRecipes.init(provider);
    }

    @Override
    public void removeRecipes(Consumer<ResourceLocation> consumer) {}
}
