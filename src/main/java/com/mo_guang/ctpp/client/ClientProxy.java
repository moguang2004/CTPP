package com.mo_guang.ctpp.client;

import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import com.mo_guang.ctpp.client.ponder.CTPPPonderPlugin;
import com.mo_guang.ctpp.client.renderer.CTPPToolboxRenderer;
import com.mo_guang.ctpp.common.CommonProxy;
import com.mo_guang.ctpp.registry.CTPPBlockEntities;
import com.mojang.blaze3d.platform.InputConstants;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;

public class ClientProxy extends CommonProxy {

    public ClientProxy() {
        super();
    }

    public static final KeyMapping OPEN_NEAREST = new KeyMapping("key.ctpp.open_nearest_toolbox",
            KeyConflictContext.IN_GAME, KeyModifier.SHIFT,
            InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_B),
            "key.categories.ctpp");

    @SubscribeEvent
    public void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_NEAREST);
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
            BlockEntityRenderers.register(CTPPBlockEntities.TOOLBOX.get(), CTPPToolboxRenderer::new);
        });
    }

    @SubscribeEvent
    public void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "ctpp_toolbox", CTPPToolboxOverlay.OVERLAY);
    }
}
