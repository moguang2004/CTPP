package com.mo_guang.ctpp.render;

import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.mo_guang.ctpp.common.blockentity.KineticMachineBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IKineticMachineRenderer extends IRenderer {

    default boolean isInvalid(BlockEntity te) {
        return !te.hasLevel() || te.getBlockState().getBlock() == Blocks.AIR;
    }

    default BlockState getRenderedBlockState(KineticMachineBlockEntity te) {
        return te.getBlockState();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    default boolean hasTESR(BlockEntity blockEntity) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    default void render(BlockEntity blockEntity, float partialTicks, PoseStack stack, MultiBufferSource buffer,
                        int combinedLight, int combinedOverlay) {
        if (isInvalid(blockEntity)) return;
        if (blockEntity instanceof KineticMachineBlockEntity kineticMachineBlockEntity) {
            renderSafe(kineticMachineBlockEntity, partialTicks, stack, buffer, combinedLight, combinedOverlay);
        }
    }

    @OnlyIn(Dist.CLIENT)
    default void renderSafe(KineticMachineBlockEntity te, float partialTicks, PoseStack ms,
                            MultiBufferSource bufferSource, int light, int overlay) {
        if (VisualizationManager.supportsVisualization(te.getLevel())) return;
        BlockState state = getRenderedBlockState(te);
        RenderType type = getRenderType(te, state);
        if (type != null) {
            KineticBlockEntityRenderer.renderRotatingBuffer(te, getRotatedModel(te, state), ms,
                    bufferSource.getBuffer(type), light);
        }
    }

    @OnlyIn(Dist.CLIENT)
    default RenderType getRenderType(KineticMachineBlockEntity te, BlockState state) {
        return ItemBlockRenderTypes.getChunkRenderType(state);
    }

    @OnlyIn(Dist.CLIENT)
    default SuperByteBuffer getRotatedModel(KineticMachineBlockEntity te, BlockState state) {
        return CachedBuffers.block(KineticBlockEntityRenderer.KINETIC_BLOCK, state);
    }
}
