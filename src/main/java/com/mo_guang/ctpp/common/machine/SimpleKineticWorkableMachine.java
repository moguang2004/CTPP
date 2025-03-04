package com.mo_guang.ctpp.common.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;

public class SimpleKineticWorkableMachine extends KineticWorkableTieredMachine{

    public SimpleKineticWorkableMachine(IMachineBlockEntity holder, int tier, Int2IntFunction tankScalingFunction, Object... args) {
        super(holder, tier, tankScalingFunction, args);
    }
}
