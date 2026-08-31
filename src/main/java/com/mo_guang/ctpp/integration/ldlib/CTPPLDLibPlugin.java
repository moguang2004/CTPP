package com.mo_guang.ctpp.integration.ldlib;

import com.lowdragmc.lowdraglib.plugin.ILDLibPlugin;
import com.lowdragmc.lowdraglib.plugin.LDLibPlugin;
import com.lowdragmc.lowdraglib.syncdata.payload.NbtTagPayload;

import com.mo_guang.ctpp.syncdata.TerminalLinkStateAccessor;

import static com.lowdragmc.lowdraglib.syncdata.TypedPayloadRegistries.register;

@LDLibPlugin
public final class CTPPLDLibPlugin implements ILDLibPlugin {

    @Override
    public void onLoad() {
        register(NbtTagPayload.class, NbtTagPayload::new, TerminalLinkStateAccessor.INSTANCE, 50);
    }
}
