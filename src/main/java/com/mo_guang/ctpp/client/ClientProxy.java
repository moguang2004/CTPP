package com.mo_guang.ctpp.client;

import net.createmod.ponder.foundation.PonderIndex;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import com.mo_guang.ctpp.client.ponder.CTPPPonderPlugin;
import com.mo_guang.ctpp.common.CommonProxy;

public class ClientProxy extends CommonProxy {

    public ClientProxy() {
        super();
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> PonderIndex.addPlugin(new CTPPPonderPlugin()));
    }
}
