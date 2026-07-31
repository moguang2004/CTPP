package com.mo_guang.ctpp.common.network.packet;

import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import com.mo_guang.ctpp.common.item.CTPPToolboxItem;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxService;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxSounds;

public final class CTPPToolboxOpenNearestPacket implements GTNetwork.INetPacket {

    public CTPPToolboxOpenNearestPacket() {}

    public CTPPToolboxOpenNearestPacket(FriendlyByteBuf buffer) {}

    @Override
    public void encode(FriendlyByteBuf buffer) {}

    @Override
    public void execute(net.minecraftforge.network.NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player == null) return;
        CTPPToolboxService.findNearest(player).ifPresent(resolved -> {
            CTPPToolboxItem.open(player, resolved.source());
            CTPPToolboxSounds.playOpen(player.level(), player.blockPosition());
        });
    }
}
