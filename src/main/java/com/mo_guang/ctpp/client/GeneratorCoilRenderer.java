package com.mo_guang.ctpp.client;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

import com.mo_guang.ctpp.common.blockentity.GeneratorCoilBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;

public class GeneratorCoilRenderer extends ShaftRenderer<GeneratorCoilBlockEntity> {

    public GeneratorCoilRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(GeneratorCoilBlockEntity blockEntity, float partialTicks, PoseStack poseStack,
                              MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, poseStack, buffer, light, overlay);
        if (VisualizationManager.supportsVisualization(blockEntity.getLevel())) {
            return;
        }

        SuperByteBuffer coil = CachedBuffers.partial(CTPPPartialModels.GENERATOR_COIL, blockEntity.getBlockState());
        KineticBlockEntityRenderer.standardKineticRotationTransform(coil, blockEntity, light);
        rotateToAxis(coil, KineticBlockEntityRenderer.getRotationAxisOf(blockEntity));
        coil.renderInto(poseStack, buffer.getBuffer(RenderType.solid()));
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
