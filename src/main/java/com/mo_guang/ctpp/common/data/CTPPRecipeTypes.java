package com.mo_guang.ctpp.common.data;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.chance.logic.ChanceLogic;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import com.mo_guang.ctpp.common.condition.RPMCondition;
import com.mo_guang.ctpp.config.MainConfig;
import com.mo_guang.ctpp.recipe.CTPPRecipeBuilder;
import com.mo_guang.ctpp.util.CTPPValues;
import com.simibubi.create.AllBlocks;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;
import static com.gregtechceu.gtceu.common.data.GTRecipes.RECIPE_FILTERS;
import static com.lowdragmc.lowdraglib.gui.texture.ProgressTexture.FillDirection.LEFT_TO_RIGHT;

public class CTPPRecipeTypes {
    public static final String KINETIC = "kinetic";
    public static final GTRecipeType KINETIC_MIXER_RECIPES = GTRecipeTypes.register("kinetic_mixer", KINETIC)
            .setMaxIOSize(6, 1, 2, 1)
                .setSlotOverlay(false, false, GuiTextures.DUST_OVERLAY)
                .setSlotOverlay(true, false, GuiTextures.DUST_OVERLAY)
                .setProgressBar(GuiTextures.PROGRESS_BAR_MIXER, LEFT_TO_RIGHT)
                .setSound(GTSoundEntries.MIXER)
                .setMaxTooltips(4);
    public static final GTRecipeType SMASHING_FACTORY_RECIPES = GTRecipeTypes.register("smashing_factory_recipes", KINETIC)
            .setMaxIOSize(1,6,1,3)
            .setSlotOverlay(false, false, GuiTextures.DUST_OVERLAY)
            .setSlotOverlay(true, false, GuiTextures.DUST_OVERLAY)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MIXER, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.MIXER)
            .setMaxTooltips(4);
    public static final GTRecipeType KINETIC_GENERATOR_RECIPES = GTRecipeTypes.register("kinetic_generator", KINETIC)
            .setMaxIOSize(0, 0, 1, 0)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.ARC)
            .setMaxTooltips(4);
    public static final GTRecipeType KINETIC_STEAM_TURBINE_RECIPES = GTRecipeTypes.register("kinetic_steam_turbine",KINETIC)
            .setMaxIOSize(0, 0, 1, 1)
            .setSlotOverlay(false, false, GuiTextures.SOLIDIFIER_OVERLAY)
            .setProgressBar(GuiTextures.PROGRESS_BAR_GAS_COLLECTOR, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.TURBINE);
    public static final GTRecipeType SEAWEED_FARM = GTRecipeTypes.register("seaweed_farm",ELECTRIC)
            .setMaxIOSize(2, 4, 0, 1)
            .setSlotOverlay(false, false, GuiTextures.SOLIDIFIER_OVERLAY)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.BATH);
    public static final GTRecipeType WINDMILL_CONTROL = GTRecipeTypes.register("windmill_control_center",ELECTRIC)
            .setMaxIOSize(0, 0, 1, 0)
            .setSlotOverlay(false, false, GuiTextures.SOLIDIFIER_OVERLAY)
            .setProgressBar(GuiTextures.PROGRESS_BAR_GAS_COLLECTOR, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.TURBINE);
    public static final GTRecipeType BOOM_OF_CREATE = GTRecipeTypes.register("boom_of_create","ctnh")
            .setMaxIOSize(1, 0, 1, 0)
            .setEUIO(IO.IN)
            .setSlotOverlay(false, false, GuiTextures.SOLIDIFIER_OVERLAY)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.COOLING);
    public static void init(){
        MIXER_RECIPES.onRecipeBuild((builder, provider) -> {
            if (!RECIPE_FILTERS.contains(builder.id)) {
                assert KINETIC_MIXER_RECIPES != null;
                var newrecipe = KINETIC_MIXER_RECIPES.copyFrom(builder)
                        .duration(Math.max((int) (builder.duration / MainConfig.INSTANCE.gtmConfig.kineticCreateMixerSpeedMultiplier), 1))
                        .buildRawRecipe();
                new CTPPRecipeBuilder(newrecipe, KINETIC_MIXER_RECIPES).rpm(MainConfig.INSTANCE.gtmConfig.kineticCreateMixerRPMRequirement)
                        .save(provider);
            }
        });
        MACERATOR_RECIPES.onRecipeBuild((builder, provider) ->{
            assert SMASHING_FACTORY_RECIPES != null;
            if(GTUtil.getTierByVoltage(builder.EUt().voltage()) <= MainConfig.INSTANCE.ctnhConfig.smashingFactoryMaximumProcessingCapacity) {
                var newrecipe = SMASHING_FACTORY_RECIPES.copyFrom(builder)
                .duration(Math.max((int)(builder.duration / MainConfig.INSTANCE.ctnhConfig.smashingFactorySpeedMultiplier), 1))
                        .buildRawRecipe();
                new CTPPRecipeBuilder(newrecipe, SMASHING_FACTORY_RECIPES).rpm(MainConfig.INSTANCE.ctnhConfig.smashingFactoryRPMRequirement)
                        .noEUt()
                        .tier(Math.min(GTUtil.getTierByVoltage(builder.EUt().voltage()) * 2, 5))
                        .inputStress(builder.EUt().voltage() * MainConfig.INSTANCE.ctnhConfig.smashingFactoryStressRequirement)
                        .chancedOutputLogic(ItemRecipeCapability.CAP, ChanceLogic.NONE)
                        .save(provider);
            }
        });
    }
}
