package com.mo_guang.ctpp.common.machine;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.gregtechceu.gtceu.api.capability.recipe.*;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TieredMachine;
import com.gregtechceu.gtceu.api.machine.feature.*;
import com.gregtechceu.gtceu.api.machine.trait.*;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.lowdragmc.lowdraglib.syncdata.ISubscription;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class KineticWorkableTieredMachine extends TieredMachine implements IRecipeLogicMachine,
        IMachineLife, IMufflableMachine{
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(KineticWorkableTieredMachine.class,
            TieredMachine.MANAGED_FIELD_HOLDER);

    @Getter
    @Persisted
    @DescSynced
    public final RecipeLogic recipeLogic;
    @Getter
    public final GTRecipeType[] recipeTypes;
    @Getter
    @Setter
    @Persisted
    public int activeRecipeType;
    @Getter
    public final Int2IntFunction tankScalingFunction;
    @Nullable
    @Getter
    @Setter
    private ICleanroomProvider cleanroom;
    @Persisted
    public final NotifiableItemStackHandler importItems;
    @Persisted
    public final NotifiableItemStackHandler exportItems;
    @Persisted
    public final NotifiableFluidTank importFluids;
    @Persisted
    public final NotifiableFluidTank exportFluids;
    @Persisted
    public final NotifiableComputationContainer importComputation;
    @Persisted
    public final NotifiableComputationContainer exportComputation;
    @Getter
    protected final Map<IO, List<RecipeHandlerList>> capabilitiesProxy;
    @Getter
    protected final Map<IO, Map<RecipeCapability<?>, List<IRecipeHandler<?>>>> capabilitiesFlat;
    protected final List<ISubscription> traitSubscriptions;
    @Persisted
    @DescSynced
    @Getter
    @Setter
    protected boolean isMuffled;
    protected boolean previouslyMuffled = true;
    public KineticWorkableTieredMachine(IMachineBlockEntity holder, int tier, Int2IntFunction tankScalingFunction, Object... args){
        super(holder, tier);
        this.recipeTypes = getDefinition().getRecipeTypes();
        this.activeRecipeType = 0;
        this.tankScalingFunction = tankScalingFunction;
        this.capabilitiesProxy = new EnumMap<>(IO.class);
        this.capabilitiesFlat = new EnumMap<>(IO.class);
        this.traitSubscriptions = new ArrayList<>();
        this.recipeLogic = createRecipeLogic(args);
        this.importItems = createImportItemHandler(args);
        this.exportItems = createExportItemHandler(args);
        this.importFluids = createImportFluidHandler(args);
        this.exportFluids = createExportFluidHandler(args);
        this.importComputation = createImportComputationContainer(args);
        this.exportComputation = createExportComputationContainer(args);
    }
    //////////////////////////////////////
    // ***** Initialization ******//
    //////////////////////////////////////
    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    protected NotifiableItemStackHandler createImportItemHandler(Object... args) {
        return new NotifiableItemStackHandler(this, getRecipeType().getMaxInputs(ItemRecipeCapability.CAP), IO.IN);
    }

    protected NotifiableItemStackHandler createExportItemHandler(Object... args) {
        return new NotifiableItemStackHandler(this, getRecipeType().getMaxOutputs(ItemRecipeCapability.CAP), IO.OUT);
    }

    protected NotifiableFluidTank createImportFluidHandler(Object... args) {
        return new NotifiableFluidTank(this, getRecipeType().getMaxInputs(FluidRecipeCapability.CAP),
                this.tankScalingFunction.apply(this.getTier()), IO.IN);
    }

    protected NotifiableFluidTank createExportFluidHandler(Object... args) {
        return new NotifiableFluidTank(this, getRecipeType().getMaxOutputs(FluidRecipeCapability.CAP),
                this.tankScalingFunction.apply(this.getTier()), IO.OUT);
    }

    protected NotifiableComputationContainer createImportComputationContainer(Object... args) {
        boolean transmitter = true;
        if (args.length > 0 && args[args.length - 1] instanceof Boolean isTransmitter) {
            transmitter = isTransmitter;
        }
        return new NotifiableComputationContainer(this, IO.IN, transmitter);
    }

    protected NotifiableComputationContainer createExportComputationContainer(Object... args) {
        return new NotifiableComputationContainer(this, IO.OUT, false);
    }

    protected RecipeLogic createRecipeLogic(Object... args) {
        return new RecipeLogic(this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        Map<IO, List<IRecipeHandler<?>>> ioTraits = new EnumMap<>(IO.class);

        for (MachineTrait trait : getTraits()) {
            if (trait instanceof IRecipeHandlerTrait<?> handlerTrait) {
                ioTraits.computeIfAbsent(handlerTrait.getHandlerIO(), i -> new ArrayList<>()).add(handlerTrait);
            }
        }

        for (var entry : ioTraits.entrySet()) {
            var handlerList = RecipeHandlerList.of(entry.getKey(), entry.getValue());
            this.addHandlerList(handlerList);
            traitSubscriptions.add(handlerList.subscribe(recipeLogic::updateTickSubscription));
        }
    }

    @Override
    public void onUnload() {
        super.onUnload();
        traitSubscriptions.forEach(ISubscription::unsubscribe);
        traitSubscriptions.clear();
        capabilitiesProxy.clear();
        capabilitiesFlat.clear();
        recipeLogic.inValid();
    }

    //////////////////////////////////////
    // ********** MISC ***********//
    //////////////////////////////////////

    @Override
    public void onMachineRemoved() {
        clearInventory(importItems.storage);
        clearInventory(exportItems.storage);
    }

    //////////////////////////////////////
    // ****** RECIPE LOGIC *******//
    //////////////////////////////////////

    @Override
    public void clientTick() {
        super.clientTick();
        if (previouslyMuffled != isMuffled) {
            previouslyMuffled = isMuffled;

            if (recipeLogic != null)
                recipeLogic.updateSound();
        }
    }

    @Override
    public boolean keepSubscribing() {
        return false;
    }

    @NotNull
    public GTRecipeType getRecipeType() {
        return recipeTypes[activeRecipeType];
    }
}
