package com.mo_guang.ctpp.common.machine;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import lombok.Getter;
import net.minecraft.core.Direction;

import javax.annotation.ParametersAreNonnullByDefault;


@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SimpleKineticElectricWorkableMachine extends SimpleTieredMachine implements IKineticMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            SimpleKineticElectricWorkableMachine.class, SimpleTieredMachine.MANAGED_FIELD_HOLDER);

    @Getter
    @Persisted
    protected final NotifiableStressTrait stressTrait;

    public SimpleKineticElectricWorkableMachine(IMachineBlockEntity holder, int tier,
                                                Int2IntFunction tankScalingFunction, Object... args) {
        super(holder, tier, tankScalingFunction, args);
        this.stressTrait = createStressTrait(args);
    }

    //////////////////////////////////////
    // ***** Initialization *****//
    //////////////////////////////////////
    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    protected NotifiableStressTrait createStressTrait(Object... args) {
        return new NotifiableStressTrait(this, IO.IN, IO.IN);
    }

    @Override
    protected NotifiableEnergyContainer createEnergyContainer(Object... args) {
        var container = super.createEnergyContainer(args);
        container.setSideInputCondition(dir -> dir == null || dir.getAxis() != getRotationFacing().getAxis());
        container.setCapabilityValidator(dir -> dir == null || dir.getAxis() != getRotationFacing().getAxis());
        return container;
    }

    @Override
    public void onRotated(Direction oldFacing, Direction newFacing) {
        super.onRotated(oldFacing, newFacing);
        if (!isRemote()) {
            if (oldFacing.getAxis() != newFacing.getAxis()) {
                var holder = getKineticHolder();
                if (holder.hasNetwork()) {
                    holder.getOrCreateNetwork().remove(holder);
                }
                holder.detachKinetics();
                holder.removeSource();
            }
        }
    }
}
