package com.mo_guang.ctpp.client;

import net.minecraft.core.Direction;

import com.mo_guang.ctpp.common.blockentity.KineticMachineBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CarbonBrushesVisual extends SplitShaftVisual {

    private final RotatingInstance coil;

    public CarbonBrushesVisual(VisualizationContext context, KineticMachineBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        Direction axisDirection = Direction.get(Direction.AxisDirection.POSITIVE, rotationAxis());
        coil = instancerProvider()
                .instancer(AllInstanceTypes.ROTATING, Models.partial(CTPPPartialModels.CARBON_BRUSHES_COIL))
                .createInstance()
                .rotateToFace(Direction.UP, axisDirection)
                .setup(blockEntity)
                .setPosition(getVisualPosition());
        coil.setChanged();
    }

    @Override
    public void update(float partialTick) {
        super.update(partialTick);
        coil.setup(blockEntity).setChanged();
    }

    @Override
    protected void _delete() {
        super._delete();
        coil.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept(coil);
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(pos, coil);
    }
}
