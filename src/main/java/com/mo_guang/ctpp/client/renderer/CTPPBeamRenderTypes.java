package com.mo_guang.ctpp.client.renderer;

import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

@OnlyIn(Dist.CLIENT)
public final class CTPPBeamRenderTypes extends RenderType {

    /**
     * Translucent (normal alpha blending) render type for the emitter beam body. lightning() is
     * additive: 16 stacked quads per band push every color toward white, hiding dark tier colors.
     * Normal alpha blending preserves the hue, so voltage-decay color bands stay distinguishable.
     */
    private static final RenderType BEAM_BODY = RenderType.create("ctpp_emitter_beam_body",
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, true,
            RenderType.CompositeState.builder()
                    .setShaderState(POSITION_COLOR_SHADER)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false));

    private CTPPBeamRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                                boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState,
                                Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static RenderType beamBody() {
        return BEAM_BODY;
    }
}
