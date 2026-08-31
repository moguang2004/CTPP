package com.mo_guang.ctpp.syncdata;

import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.accessor.CustomObjectAccessor;
import com.lowdragmc.lowdraglib.syncdata.accessor.IManagedAccessor;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.NbtTagPayload;

import net.minecraft.nbt.CompoundTag;

import com.mo_guang.ctpp.api.terminal.TerminalLinkState;

/** LDLib managed accessor for terminal link elements in a managed collection. */
public final class TerminalLinkStateAccessor extends CustomObjectAccessor<TerminalLinkState> {

    public static final TerminalLinkStateAccessor INSTANCE = new TerminalLinkStateAccessor();

    private TerminalLinkStateAccessor() {
        super(TerminalLinkState.class, false);
    }

    @Override
    public ITypedPayload<?> serialize(AccessorOp op, TerminalLinkState value) {
        IManagedAccessor managedAccessor = new IManagedAccessor();
        return managedAccessor.readFromReadonlyField(op, value);
    }

    @Override
    public TerminalLinkState deserialize(AccessorOp op, ITypedPayload<?> payload) {
        if (!(payload instanceof NbtTagPayload nbtPayload) ||
                !(nbtPayload.getPayload() instanceof CompoundTag)) {
            throw new IllegalArgumentException("Terminal link payload must be a CompoundTag");
        }
        TerminalLinkState result = new TerminalLinkState();
        new IManagedAccessor().writeToReadonlyField(op, result, payload);
        return result;
    }
}
