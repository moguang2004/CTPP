package com.mo_guang.ctpp.api;

import com.gregtechceu.gtceu.api.block.IMachineBlock;
import com.mo_guang.ctpp.common.machine.KineticMachineDefinition;
import com.simibubi.create.api.stress.BlockStressValues;
import net.createmod.catnip.data.Couple;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

public class IBlockStressValues {
    public IBlockStressValues() {
        super();
    }
            public static double getImpact(Block block) {
                if (block instanceof IMachineBlock machineBlock &&
                        machineBlock.getDefinition() instanceof KineticMachineDefinition definition) {
                    if (!definition.isSource()) {
                        return definition.getTorque();
                    }
                }
                return 0;
            }

            public static double getCapacity(Block block) {
                if (block instanceof IMachineBlock machineBlock &&
                        machineBlock.getDefinition() instanceof KineticMachineDefinition definition) {
                    if (definition.isSource()) {
                        return definition.getTorque();
                    }
                }
                return 0;
            }

            public boolean hasImpact(Block block) {
                if (block instanceof IMachineBlock machineBlock &&
                        machineBlock.getDefinition() instanceof KineticMachineDefinition definition) {
                    return !definition.isSource();
                }
                return false;
            }

            public boolean hasCapacity(Block block) {
                if (block instanceof IMachineBlock machineBlock &&
                        machineBlock.getDefinition() instanceof KineticMachineDefinition definition) {
                    return definition.isSource();
                }
                return false;
            }

            @Nullable
            public Couple<Integer> getGeneratedRPM(Block block) {
                return null;
            }
}
