package com.mo_guang.ctpp.common.machine;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IWorkableMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.mo_guang.ctpp.common.machine.multiblock.KineticMultiblockMachine;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class KineticPartMachine extends TieredIOPartMachine implements IKineticMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(KineticPartMachine.class,
            TieredIOPartMachine.MANAGED_FIELD_HOLDER);

    @Getter
    @Persisted
    protected final NotifiableStressTrait stressTrait;

    public KineticPartMachine(IMachineBlockEntity holder, int tier, IO io, Object... args) {
        super(holder, tier, io);
        this.stressTrait = createStressTrait(args);
    }
    public IO getIO(){
        return this.io;
    }

    //////////////////////////////////////
    // ***** Initialization *****//
    //////////////////////////////////////
    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    protected NotifiableStressTrait createStressTrait(Object... args) {
        return new NotifiableStressTrait(this, this.io, this.io);
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

    @Override
    public boolean onWaiting(IWorkableMultiController controller) {
        getKineticHolder().stopWorking();
        return super.onWaiting(controller);
    }

    @Override
    public boolean onPaused(IWorkableMultiController controller) {
        getKineticHolder().stopWorking();
        return super.onPaused(controller);
    }

    @Override
    public void removedFromController(IMultiController controller) {
        super.removedFromController(controller);
        getKineticHolder().stopWorking();
    }

    @Override
    public void onChanged() {
        super.onChanged();
        if (!getControllers().isEmpty() && getControllers().first() instanceof KineticMultiblockMachine kineticMultiblockMachine) {
            kineticMultiblockMachine.onChanged();
        }
    }

    @Override
    public void setWorkingEnabled(boolean workingEnabled) {
        if (!workingEnabled) {
            getKineticHolder().stopWorking();
        }
        super.setWorkingEnabled(workingEnabled);
    }

    //////////////////////////////////////
    // ********* GUI *********//
    //////////////////////////////////////
    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return false;
    }
}
