package com.mo_guang.ctpp.common.network.packet;

import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import com.mo_guang.ctpp.common.toolbox.CTPPToolboxOperations;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxSourceId;
import org.jetbrains.annotations.Nullable;

public final class CTPPToolboxActionPacket implements GTNetwork.INetPacket {

    public enum Action {
        EQUIP,
        UNEQUIP,
        DETACH,
        DEPOSIT
    }

    private final Action action;
    private final @Nullable CTPPToolboxSourceId source;
    private final int compartment;
    private final int hotbarSlot;

    public CTPPToolboxActionPacket(Action action, @Nullable CTPPToolboxSourceId source, int compartment,
                                   int hotbarSlot) {
        this.action = action;
        this.source = source;
        this.compartment = compartment;
        this.hotbarSlot = hotbarSlot;
    }

    public CTPPToolboxActionPacket(FriendlyByteBuf buffer) {
        action = buffer.readEnum(Action.class);
        source = buffer.readBoolean() ? CTPPToolboxSourceId.read(buffer) : null;
        compartment = buffer.readVarInt();
        hotbarSlot = buffer.readVarInt();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(action);
        buffer.writeBoolean(source != null);
        if (source != null) source.write(buffer);
        buffer.writeVarInt(compartment);
        buffer.writeVarInt(hotbarSlot);
    }

    @Override
    public void execute(net.minecraftforge.network.NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player == null) return;
        switch (action) {
            case EQUIP -> {
                if (source != null) CTPPToolboxOperations.equip(player, source, compartment, hotbarSlot);
            }
            case UNEQUIP -> CTPPToolboxOperations.unequip(player, hotbarSlot, false);
            case DETACH -> CTPPToolboxOperations.unequip(player, hotbarSlot, true);
            case DEPOSIT -> {
                if (source != null) CTPPToolboxOperations.depositAll(player, source);
            }
        }
    }
}
