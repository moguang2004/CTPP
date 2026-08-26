package com.mo_guang.ctpp.network.packet;

import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import com.mo_guang.ctpp.client.terminal.TerminalClientSelection;
import org.jetbrains.annotations.Nullable;

public final class CTPPTerminalWireSelectionPacket implements GTNetwork.INetPacket {

    private final @Nullable BlockPos pos;
    private final ItemStack wire;
    private final int multiplier;

    public CTPPTerminalWireSelectionPacket(BlockPos pos, ItemStack wire, int multiplier) {
        this.pos = pos.immutable();
        this.wire = wire.copyWithCount(1);
        this.multiplier = Math.max(1, multiplier);
    }

    private CTPPTerminalWireSelectionPacket(@Nullable BlockPos pos, boolean cleared) {
        this.pos = pos == null ? null : pos.immutable();
        this.wire = ItemStack.EMPTY;
        this.multiplier = 1;
    }

    public static CTPPTerminalWireSelectionPacket cleared() {
        return new CTPPTerminalWireSelectionPacket(null, true);
    }

    public CTPPTerminalWireSelectionPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBoolean() ? buffer.readBlockPos() : null;
        wire = buffer.readItem();
        multiplier = Math.max(1, buffer.readVarInt());
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(pos != null);
        if (pos != null) buffer.writeBlockPos(pos);
        buffer.writeItem(wire);
        buffer.writeVarInt(multiplier);
    }

    @Override
    public void execute(net.minecraftforge.network.NetworkEvent.Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> TerminalClientSelection.setWireTarget(pos, wire, multiplier));
    }
}
