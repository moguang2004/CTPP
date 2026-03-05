package com.mo_guang.ctpp.common.machine;

import com.gregtechceu.gtceu.api.machine.feature.IMachineFeature;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

import com.mo_guang.ctpp.api.KineticMachineDefinition;
import com.mo_guang.ctpp.common.blockentity.KineticMachineBlockEntity;

import java.util.List;

public interface IKineticMachine extends IMachineFeature {

    default KineticMachineBlockEntity getKineticHolder() {
        return (KineticMachineBlockEntity) self().getHolder();
    }

    default KineticMachineDefinition getKineticDefinition() {
        return (KineticMachineDefinition) self().getDefinition();
    }

    default float getRotationSpeedModifier(Direction direction) {
        return 1;
    }

    default Direction getRotationFacing() {
        var frontFacing = self().getFrontFacing();
        return getKineticDefinition().isFrontRotation() ? frontFacing :
                (frontFacing.getAxis() == Direction.Axis.Y ? Direction.NORTH : frontFacing.getClockWise());
    }

    default boolean hasShaftTowards(Direction face) {
        return face.getAxis() == getRotationFacing().getAxis();
    }

    default boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return false;
    }
}
