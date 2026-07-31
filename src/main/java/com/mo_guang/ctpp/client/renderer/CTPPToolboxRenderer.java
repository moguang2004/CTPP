package com.mo_guang.ctpp.client.renderer;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import com.mo_guang.ctpp.common.block.CTPPToolboxBlock;
import com.mo_guang.ctpp.common.blockentity.CTPPToolboxBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;

public final class CTPPToolboxRenderer extends SmartBlockEntityRenderer<CTPPToolboxBlockEntity> {

    public CTPPToolboxRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(CTPPToolboxBlockEntity toolbox, float partialTicks, PoseStack pose,
                              MultiBufferSource buffer, int light, int overlay) {
        BlockState state = toolbox.getBlockState();
        Direction facing = state.getValue(CTPPToolboxBlock.FACING).getOpposite();
        var lid = CachedBuffers.partial(AllPartialModels.TOOLBOX_LIDS.get(toolbox.getColor()), state);
        var drawer = CachedBuffers.partial(AllPartialModels.TOOLBOX_DRAWER, state);
        var consumer = buffer.getBuffer(RenderType.cutoutMipped());

        lid.center().rotateYDegrees(-facing.toYRot()).uncenter()
                .translate(0, 6 / 16f, 12 / 16f)
                .rotateXDegrees(135 * toolbox.lid.getValue(partialTicks))
                .translate(0, -6 / 16f, -12 / 16f)
                .light(light).renderInto(pose, consumer);
        float drawerProgress = toolbox.drawers.getValue(partialTicks);
        for (int offset : Iterate.zeroAndOne) {
            drawer.center().rotateYDegrees(-facing.toYRot()).uncenter()
                    // CTPP reuses Create's body model, whose closed drawer faces
                    // overlap the partial model without this small depth separation.
                    .translate(0, offset / 8f, -0.001f - drawerProgress * 0.175f * (2 - offset))
                    .light(light).renderInto(pose, consumer);
        }
    }
}
