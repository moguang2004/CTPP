package com.mo_guang.ctpp.network.packet;

import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import com.mo_guang.ctpp.common.toolbox.CTPPToolboxBindings;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxService;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxSnapshot;

public final class CTPPToolboxSnapshotRequestPacket implements GTNetwork.INetPacket {

    public CTPPToolboxSnapshotRequestPacket() {}

    public CTPPToolboxSnapshotRequestPacket(FriendlyByteBuf buffer) {}

    @Override
    public void encode(FriendlyByteBuf buffer) {}

    @Override
    public void execute(net.minecraftforge.network.NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player == null) return;
        var snapshots = CTPPToolboxService.collect(player).stream().limit(8).map(CTPPToolboxSnapshot::create).toList();
        GTNetwork.sendToPlayer(player, new CTPPToolboxSnapshotPacket(snapshots, CTPPToolboxBindings.get(player),
                player.getInventory().selected));
    }
}
