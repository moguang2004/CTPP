package com.mo_guang.ctpp.registry;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.GTRecipes;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.mo_guang.ctpp.config.MainConfig;
import com.mo_guang.ctpp.common.data.recipe.builder.CTPPRecipeBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;
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
            .setMaxIOSize(1,4,0,0)
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
    public static ResourceLocation convert(ResourceLocation id, GTRecipeType recipeType) {
        return ResourceLocation.tryBuild(id.getNamespace(), recipeType.registryName.getPath() + "/" + id.getPath());
    }
    public static void init(){
        MIXER_RECIPES.onRecipeBuild((builder, provider) -> {
            if (!GTRecipes.RECIPE_FILTERS.contains(convert(builder.id, builder.recipeType))) {
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
            if(!GTRecipes.RECIPE_FILTERS.contains(convert(builder.id, builder.recipeType)) &&
                    GTUtil.getTierByVoltage(builder.EUt().voltage()) <= MainConfig.INSTANCE.ctnhConfig.smashingFactoryMaximumProcessingCapacity) {
                var newRecipe = SMASHING_FACTORY_RECIPES.copyFrom(builder)
                .duration(Math.max((int)(builder.duration / MainConfig.INSTANCE.ctnhConfig.smashingFactorySpeedMultiplier), 1))
                        .buildRawRecipe();
                List<Content> output = new ArrayList<>();
                for(var content:newRecipe.getOutputContents(ItemRecipeCapability.CAP)){
                    if (!content.isChanced()) output.add(content);
                }
                newRecipe.outputs.put(ItemRecipeCapability.CAP, output);
                new CTPPRecipeBuilder(newRecipe, SMASHING_FACTORY_RECIPES).rpm(MainConfig.INSTANCE.ctnhConfig.smashingFactoryRPMRequirement)
                        .noEUt()
                        .tier(Math.min(GTUtil.getTierByVoltage(builder.EUt().voltage()) * 2, 5))
                        .inputStress(builder.EUt().voltage() * MainConfig.INSTANCE.ctnhConfig.smashingFactoryStressRequirement)
                        .save(provider);
            }
        });
    }
}
