package com.mo_guang.ctpp.network.packet;

import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import com.mo_guang.ctpp.common.terminal.TerminalNetwork;

public final class CTPPTerminalCancelWireSelectionPacket implements GTNetwork.INetPacket {

    private final BlockPos pos;

    public CTPPTerminalCancelWireSelectionPacket(BlockPos pos) {
        this.pos = pos.immutable();
    }

    public CTPPTerminalCancelWireSelectionPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public void execute(net.minecraftforge.network.NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player != null) TerminalNetwork.cancelWireSelection(player, pos);
    }
}
