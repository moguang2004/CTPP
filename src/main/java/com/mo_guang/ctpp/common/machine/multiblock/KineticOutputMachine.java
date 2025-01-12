package com.mo_guang.ctpp.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.mo_guang.ctpp.common.machine.KineticPartMachine;
import com.simibubi.create.infrastructure.config.AllConfigs;

public class KineticOutputMachine extends KineticMultiblockMachine{
    public KineticOutputMachine(IMachineBlockEntity holder){
        super(holder);
        this.speed = 64;
    }
    public float getMaxOutputStress(){
        float output = 0;
        for (IMultiPart part : getParts()) {
            if(part instanceof KineticPartMachine kineticPart && kineticPart.getIO() == IO.OUT){
                output += AllConfigs.server().kinetics.maxRotationSpeed.get() * kineticPart.getKineticDefinition().torque;
            }
        }
        return output;
    }
}
