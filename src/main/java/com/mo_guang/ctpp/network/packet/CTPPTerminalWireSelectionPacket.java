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

    public CTPPTerminalWireSelectionPacket(BlockPos pos, ItemStack wire) {
        this.pos = pos.immutable();
        this.wire = wire.copyWithCount(1);
    }

    private CTPPTerminalWireSelectionPacket(@Nullable BlockPos pos, boolean cleared) {
        this.pos = pos == null ? null : pos.immutable();
        this.wire = ItemStack.EMPTY;
    }

    public static CTPPTerminalWireSelectionPacket cleared() {
        return new CTPPTerminalWireSelectionPacket(null, true);
    }

    public CTPPTerminalWireSelectionPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBoolean() ? buffer.readBlockPos() : null;
        wire = buffer.readItem();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(pos != null);
        if (pos != null) buffer.writeBlockPos(pos);
        buffer.writeItem(wire);
    }

    @Override
    public void execute(net.minecraftforge.network.NetworkEvent.Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> TerminalClientSelection.setWireTarget(pos, wire));
    }
}
