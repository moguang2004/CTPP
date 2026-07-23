package com.mo_guang.ctpp.client;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

import com.mo_guang.ctpp.common.blockentity.KineticMachineBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;

public class CarbonBrushesRenderer extends ShaftRenderer<KineticMachineBlockEntity> {

    public CarbonBrushesRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(KineticMachineBlockEntity be, float partialTicks, PoseStack ms,
                              MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;

        SuperByteBuffer coil = CachedBuffers.partial(CTPPPartialModels.CARBON_BRUSHES_COIL, be.getBlockState());
        KineticBlockEntityRenderer.standardKineticRotationTransform(coil, be, light);
        rotateToAxis(coil, KineticBlockEntityRenderer.getRotationAxisOf(be));
        coil.renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    private static void rotateToAxis(SuperByteBuffer buffer, Direction.Axis axis) {
        buffer.center();
        switch (axis) {
            case X -> buffer.rotateDegrees(90, Direction.Axis.Z);
            case Z -> buffer.rotateDegrees(90, Direction.Axis.X);
            case Y -> {}
        }
        buffer.uncenter();
    }
}
