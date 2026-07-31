package com.mo_guang.ctpp.event;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.common.command.CTPPToolboxCommands;

@Mod.EventBusSubscriber(modid = CTPP.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CTPPToolboxCommands.register(event.getDispatcher());
    }
}
