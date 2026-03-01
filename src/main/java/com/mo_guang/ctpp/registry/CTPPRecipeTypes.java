package com.mo_guang.ctpp.registry;

import com.gregtechceu.gtceu.GTCEu;
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
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.config.MainConfig;
import com.mo_guang.ctpp.common.data.recipe.builder.CTPPRecipeBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;
import static com.lowdragmc.lowdraglib.gui.texture.ProgressTexture.FillDirection.LEFT_TO_RIGHT;
import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;

public class CTPPRecipeTypes {
    public static final String KINETIC = "kinetic";
    public static final GTRecipeType KINETIC_MIXER_RECIPES = REGISTRATE.recipeType(CTPP.id("kinetic_mixer"), KINETIC)
            .cnlang("应力搅拌")
            .setMaxIOSize(6, 1, 2, 1)
                .setSlotOverlay(false, false, GuiTextures.DUST_OVERLAY)
                .setSlotOverlay(true, false, GuiTextures.DUST_OVERLAY)
                .setProgressBar(GuiTextures.PROGRESS_BAR_MIXER, LEFT_TO_RIGHT)
                .setSound(GTSoundEntries.MIXER)
                .setMaxTooltips(4);
    public static final GTRecipeType SMASHING_FACTORY_RECIPES = REGISTRATE.recipeType(CTPP.id("smashing_factory_recipes"), KINETIC)
            .cnlang("粉碎工厂")
            .setMaxIOSize(1,4,0,0)
            .setSlotOverlay(false, false, GuiTextures.DUST_OVERLAY)
            .setSlotOverlay(true, false, GuiTextures.DUST_OVERLAY)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MIXER, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.MIXER)
            .setMaxTooltips(4);
    public static final GTRecipeType KINETIC_GENERATOR_RECIPES = REGISTRATE.recipeType(CTPP.id("kinetic_generator"), KINETIC)
            .cnlang("应力发电")
            .setMaxIOSize(0, 0, 1, 0)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.ARC)
            .setMaxTooltips(4);
    public static final GTRecipeType KINETIC_STEAM_TURBINE_RECIPES = REGISTRATE.recipeType(CTPP.id("kinetic_steam_turbine"), KINETIC)
            .cnlang("蒸汽动力")
            .setMaxIOSize(0, 0, 1, 1)
            .setSlotOverlay(false, false, GuiTextures.SOLIDIFIER_OVERLAY)
            .setProgressBar(GuiTextures.PROGRESS_BAR_GAS_COLLECTOR, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.TURBINE);
    public static final GTRecipeType SEAWEED_FARM = REGISTRATE.recipeType(CTPP.id("seaweed_farm"),ELECTRIC)
            .cnlang("海草养殖")
            .setMaxIOSize(2, 4, 0, 1)
            .setSlotOverlay(false, false, GuiTextures.SOLIDIFIER_OVERLAY)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.BATH);
    public static final GTRecipeType WINDMILL_CONTROL = REGISTRATE.recipeType(CTPP.id("windmill_control_center"),ELECTRIC)
            .cnlang("风车控制中心")
            .setMaxIOSize(0, 0, 1, 0)
            .setSlotOverlay(false, false, GuiTextures.SOLIDIFIER_OVERLAY)
            .setProgressBar(GuiTextures.PROGRESS_BAR_GAS_COLLECTOR, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.TURBINE);
    public static final GTRecipeType BOOM_OF_CREATE = REGISTRATE.recipeType(CTPP.id("boom_of_create"), KINETIC)
            .cnlang("聚爆应力")
            .setMaxIOSize(1, 0, 1, 0)
            .setEUIO(IO.IN)
            .setSlotOverlay(false, false, GuiTextures.SOLIDIFIER_OVERLAY)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.COOLING);
    public static final GTRecipeType BIG_DAM = REGISTRATE.recipeType(GTCEu.id("big_dam"), GTRecipeTypes.ELECTRIC)
            .cnlang("三峡大坝")
            .setMaxIOSize(0, 0, 1, 0)
            .setSlotOverlay(false, false, GuiTextures.SOLIDIFIER_OVERLAY)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.COOLING);
    public static ResourceLocation convert(ResourceLocation id, GTRecipeType recipeType) {
        return ResourceLocation.tryBuild(id.getNamespace(), recipeType.registryName.getPath() + "/" + id.getPath());
    }
    public static void init(){
//        MIXER_RECIPES.onRecipeBuild((builder, provider) -> {
//            if (!GTRecipes.RECIPE_FILTERS.contains(convert(builder.id, builder.recipeType))) {
//                assert KINETIC_MIXER_RECIPES != null;
//                var newrecipe = KINETIC_MIXER_RECIPES.copyFrom(builder)
//                        .duration(Math.max((int) (builder.duration / MainConfig.INSTANCE.gtmConfig.kineticCreateMixerSpeedMultiplier), 1))
//                        .buildRawRecipe();
//                new CTPPRecipeBuilder(newrecipe, KINETIC_MIXER_RECIPES).rpm(MainConfig.INSTANCE.gtmConfig.kineticCreateMixerRPMRequirement)
//                        .save(provider);
//            }
//        });
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
