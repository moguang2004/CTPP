package com.mo_guang.ctpp.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

import com.mo_guang.ctpp.common.blockentity.KineticMachineBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

public class KineticMachineBlockEntityRenderer extends KineticBlockEntityRenderer<KineticMachineBlockEntity> {

    public KineticMachineBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected BlockState getRenderedBlockState(KineticMachineBlockEntity blockEntity) {
        return shaft(getRotationAxisOf(blockEntity));
    }
}
