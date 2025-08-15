package com.mo_guang.ctpp.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.mo_guang.ctpp.common.machine.KineticPartMachine;
import com.simibubi.create.infrastructure.config.AllConfigs;
import lombok.Getter;

public class KineticOutputMachine extends KineticMultiblockMachine{
    @Getter
    public float maxOutputStress = 0;
    public KineticOutputMachine(IMachineBlockEntity holder){
        super(holder);
        this.speed = 64;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        maxOutputStress = 0;
        for (IMultiPart part : getParts()) {
            if (part instanceof KineticPartMachine kineticPart && kineticPart.getIO() == IO.OUT) {
                maxOutputStress += AllConfigs.server().kinetics.maxRotationSpeed.get() * kineticPart.getKineticDefinition().torque;
            }
        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        maxOutputStress = 0;
    }
}
