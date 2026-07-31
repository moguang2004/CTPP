package com.mo_guang.ctpp.client;

import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.mo_guang.ctpp.common.network.packet.CTPPToolboxSnapshotRequestPacket;
import com.simibubi.create.AllKeys;

public final class CTPPToolboxKeyHandler {

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        if (event.getAction() != 1 || !AllKeys.TOOLBELT.doesModifierAndCodeMatch(event.getKey())) return;
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().screen != null) return;
        GTNetwork.sendToServer(new CTPPToolboxSnapshotRequestPacket());
    }
}
