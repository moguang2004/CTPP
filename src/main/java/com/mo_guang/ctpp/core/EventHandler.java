package com.mo_guang.ctpp.core;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeConditionType;
import com.gregtechceu.gtceu.common.data.GTRecipeConditions;
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.common.data.CTPPMachines;
import com.mo_guang.ctpp.common.data.CTPPMultiblockMachines;
import com.mo_guang.ctpp.common.data.CTPPRecipeConditions;
import com.mo_guang.ctpp.common.data.CTPPRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CTPP.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandler {
    @SubscribeEvent
    public static void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        CTPPMachines.init();
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
}
