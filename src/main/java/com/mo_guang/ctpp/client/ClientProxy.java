package com.mo_guang.ctpp.client;

import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import com.mo_guang.ctpp.client.ponder.CTPPPonderPlugin;
import com.mo_guang.ctpp.common.CommonProxy;
import com.mo_guang.ctpp.registry.CTPPBlockEntities;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;

public class ClientProxy extends CommonProxy {

    public ClientProxy() {
        super();
        MinecraftForge.EVENT_BUS.register(new MagnetTooltipHandler());
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            PonderIndex.addPlugin(new CTPPPonderPlugin());
            CTPPPartialModels.init();
            SimpleBlockEntityVisualizer.builder(CTPPBlockEntities.GENERATOR_COIL.get())
                    .factory(GeneratorCoilVisual::new)
                    .skipVanillaRender(be -> true)
                    .apply();
            BlockEntityRenderers.register(CTPPBlockEntities.GENERATOR_COIL.get(), GeneratorCoilRenderer::new);
        });
    }
}
