package com.mo_guang.ctpp.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.common.command.CTPPTerminalCommands;
import com.mo_guang.ctpp.common.command.CTPPToolboxCommands;
import com.mo_guang.ctpp.common.terminal.TerminalWireDamageDebug;
import com.mo_guang.ctpp.common.terminal.TerminalWireHazardManager;

@Mod.EventBusSubscriber(modid = CTPP.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CTPPToolboxCommands.register(event.getDispatcher());
        CTPPTerminalCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void levelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel level) {
            TerminalWireHazardManager.tick(level);
        }
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) TerminalWireDamageDebug.tick(event.getServer());
    }

    @SubscribeEvent
    public static void levelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            TerminalWireHazardManager.unload(level);
        }
    }

    @SubscribeEvent
    public static void playerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        TerminalWireDamageDebug.remove(event.getEntity().getUUID());
    }
}
