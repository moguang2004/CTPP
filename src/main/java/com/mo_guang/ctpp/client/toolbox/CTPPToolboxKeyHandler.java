package com.mo_guang.ctpp.client.toolbox;

import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.network.packet.CTPPToolboxOpenNearestPacket;
import com.mo_guang.ctpp.network.packet.CTPPToolboxSnapshotRequestPacket;
import com.simibubi.create.AllKeys;

import static com.mo_guang.ctpp.client.ClientProxy.OPEN_NEAREST;

@Mod.EventBusSubscriber(modid = CTPP.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class CTPPToolboxKeyHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (event.getAction() == 1 && OPEN_NEAREST.matches(event.getKey(), event.getScanCode()) &&
                Minecraft.getInstance().player != null && Minecraft.getInstance().screen == null) {
            GTNetwork.sendToServer(new CTPPToolboxOpenNearestPacket());
            return;
        }
        if (event.getAction() != 1 || !AllKeys.TOOLBELT.doesModifierAndCodeMatch(event.getKey())) return;
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().screen != null) return;
        GTNetwork.sendToServer(new CTPPToolboxSnapshotRequestPacket());
    }
}
