package com.mo_guang.ctpp.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class CTPPWireRenderTypes extends RenderType {
    private static final RenderType WIRE = RenderType.create("ctpp_terminal_wire",
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(POSITION_COLOR_SHADER)
                    .setCullState(NO_CULL)
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .createCompositeState(false));

    private CTPPWireRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                                boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState,
                                Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static RenderType wire() {
        return WIRE;
    }
}
