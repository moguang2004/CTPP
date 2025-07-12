package com.mo_guang.ctpp.core;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeConditionType;
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.CTPPItems;
import com.mo_guang.ctpp.common.data.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CTPP.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandler {
    @SubscribeEvent
    public static void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        CTPPMachines.init();
        CTPPItems.init();
        CTPPMultiblockMachines.init();
    }

    @SubscribeEvent
    public static void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {
        CTPPRecipeTypes.init();
    }
    @SubscribeEvent
    public static void registerRecipeConditions(GTCEuAPI.RegisterEvent<ResourceLocation, RecipeConditionType> event) {
        CTPPRecipeConditions.init();
    }
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        if (event.includeServer()) {
            CTPPRecipeProvider.registerAllProcessing(generator, output);
        }
    }
}
