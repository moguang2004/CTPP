package com.mo_guang.ctpp.common.machine.multiblock.part;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

import com.mo_guang.ctpp.common.machine.IKineticMachine;
import com.mo_guang.ctpp.common.machine.NotifiableStressTrait;
import com.mo_guang.ctpp.common.machine.multiblock.KineticMultiblockMachine;
import com.mo_guang.ctpp.common.machine.multiblock.KineticOutputMachine;
import lombok.Getter;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class KineticPartMachine extends TieredIOPartMachine implements IKineticMachine {

    @Getter
    protected final NotifiableStressTrait stressTrait;

    @Nullable
    protected TickableSubscription selfCheckSubs;
    /**
     * 上一次检查时的输出绑定有效性，用于检测重载后异步重检成型的“无效→有效”跃迁。
     */
    private boolean wasValidBinding = true;

    public KineticPartMachine(IMachineBlockEntity holder, int tier, IO io, Object... args) {
        super(holder, tier, io);
        this.stressTrait = attachTrait(createStressTrait(args));
    }

    public IO getIO() {
        return this.io;
    }

    public boolean isValidOutputBinding() {
        if (io != IO.OUT || !getKineticDefinition().isSource()) {
            return true;
        }
        if (!isFormed() || getControllers().isEmpty()) {
            return false;
        }
        var controller = getControllers().first();
        return controller instanceof KineticOutputMachine outputMachine &&
                outputMachine.isFormed() &&
                outputMachine.isActive() &&
                outputMachine.getParts().contains(this);
    }

    //////////////////////////////////////
    // ***** Initialization *****//
    //////////////////////////////////////
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
    public void removedFromController(IMultiController controller) {
        super.removedFromController(controller);
        stressTrait.stopWorking();
    }

    @Override
    public void onChanged() {
        super.onChanged();
        if (!getControllers().isEmpty() &&
                getControllers().first() instanceof KineticMultiblockMachine kineticMultiblockMachine) {
            kineticMultiblockMachine.onChanged();
        }
    }

    @Override
    public void setWorkingEnabled(boolean workingEnabled) {
        if (!workingEnabled) {
            stressTrait.stopWorking();
        }
        super.setWorkingEnabled(workingEnabled);
    }

    void checkWorking() {
        if (getOffsetTimer() % 100 == 0 && !GTCEu.isClientSide()) {
            boolean valid = isValidOutputBinding();
            if (valid) {
                if (!wasValidBinding) {
                    // 重载后异步重检成型恢复：之前未成型导致的停机已结束，
                    // 重新通知 holder 同步 Create 网络源（速度由配方逻辑恢复）。
                    wasValidBinding = true;
                    getKineticHolder().reActivateSource = true;
                }
            } else {
                wasValidBinding = false;
                if (!getKineticHolder().isGraceActive()) {
                    stressTrait.stopWorking();
                }
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (selfCheckSubs == null) {
            selfCheckSubs = subscribeServerTick(this::checkWorking);
        }
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (selfCheckSubs != null) {
            selfCheckSubs.unsubscribe();
            selfCheckSubs = null;
        }
    }

    //////////////////////////////////////
    // ********* GUI *********//
    //////////////////////////////////////
    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return false;
    }

    @Override
    public boolean canShared() {
        return false;
    }
}
