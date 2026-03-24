package com.mo_guang.ctpp.common;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialEvent;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeConditionType;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.mo_guang.ctpp.CTPPRegistration;
import com.mo_guang.ctpp.api.CTPPRecipeConditions;
import com.mo_guang.ctpp.common.data.GTArmInteractionPointTypes;
import com.mo_guang.ctpp.common.data.recipe.builder.CTPPRecipeProvider;
import com.mo_guang.ctpp.common.data.recipe.fan_processing.CTPPFanProcessingTypes;
import com.mo_guang.ctpp.common.data.recipe.fan_processing.CTPPRecipeTypeInfo;
import com.mo_guang.ctpp.config.MainConfig;
import com.mo_guang.ctpp.data.CTPPDatagen;
import com.mo_guang.ctpp.registry.*;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;

@SuppressWarnings("removal")
public class CommonProxy {

    public CommonProxy() {
        init();
        MainConfig.init();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::addMaterialFlag);
        modEventBus.addListener(this::commonSetup);
        modEventBus.register(this);
    }

    public void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        CTPPCreativeModeTabs.init();
        CTPPRegistration.REGISTRATE.registerRegistrate();
        CTPPDatagen.init();
        CTPPRecipeTypeInfo.register(modEventBus);
        CTPPFanProcessingTypes.register(modEventBus);
        modEventBus.addGenericListener(MachineDefinition.class, this::registerMachines);
        modEventBus.addGenericListener(RecipeConditionType.class, this::registerRecipeConditions);
        modEventBus.addGenericListener(GTRecipeType.class, this::registerRecipeTypes);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Registry.register(
                    CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE,
                    new ResourceLocation("ctpp", "gt_machine"),
                    new GTArmInteractionPointTypes.GTMachineType());
        });
    }

    public void addMaterialFlag(MaterialEvent event) {
        GTMaterialAddon.init();
    }

    public void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        CTPPMachines.init();
        CTPPItems.init();
        CTPPMultiblockMachines.init();
    }

    public void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {
        CTPPRecipeTypes.init();
    }

    public void registerRecipeConditions(GTCEuAPI.RegisterEvent<ResourceLocation, RecipeConditionType> event) {
        CTPPRecipeConditions.init();
    }

    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        if (event.includeServer()) {
            CTPPRecipeProvider.registerAllProcessing(generator, output);
        }
    }

    @SubscribeEvent
    public void registerMaterial(MaterialEvent event) {
        CTPPMaterials.init();
    }
}
