package com.mo_guang.ctpp.client;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import com.mo_guang.ctpp.common.blockentity.KineticMachineBlockEntity;
import com.mo_guang.ctpp.common.machine.simple.CarbonBrushesGeneratorMachine;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import org.antarcticgardens.cna.CNAPartialModels;

public class KineticMachineBlockEntityRenderer extends KineticBlockEntityRenderer<KineticMachineBlockEntity> {

    public KineticMachineBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected BlockState getRenderedBlockState(KineticMachineBlockEntity be) {
        return shaft(getRotationAxisOf(be));
    }

    @Override
    protected void renderSafe(KineticMachineBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        // If this machine is our Carbon Brushes generator, render the coil partial inside
        if (be.getMetaMachine() instanceof CarbonBrushesGeneratorMachine) {
            var state = be.getBlockState();
            Direction dir = state.getValue(BlockStateProperties.FACING);

            VertexConsumer consumer = buffer.getBuffer(RenderType.solid());

            SuperByteBuffer coil = CachedBuffers.partial(CNAPartialModels.COIL, state);
            KineticBlockEntityRenderer.standardKineticRotationTransform(coil, be, light);
            rotateToAxis(coil, dir.getAxis());
            coil.renderInto(ms, consumer);
        }
    }

    private void rotateToAxis(SuperByteBuffer buffer, Direction.Axis axis) {
        buffer.center();
        switch (axis) {
            case X -> buffer.rotateDegrees(90, Direction.Axis.Z);
            case Z -> buffer.rotateDegrees(90, Direction.Axis.X);
        }
        buffer.uncenter();
    }
}
