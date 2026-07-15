package com.mo_guang.ctpp.common.machine;

import com.gregtechceu.gtceu.api.capability.recipe.*;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.RecipeTieredMachine;
import com.gregtechceu.gtceu.api.machine.feature.*;
import com.gregtechceu.gtceu.api.machine.trait.*;
import com.gregtechceu.gtceu.api.recipe.handler.RecipeHandlerList;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class KineticWorkableTieredMachine extends RecipeTieredMachine implements IMufflableMachine {

    @Persisted
    public final NetworkedComputationContainer importComputation;
    @Persisted
    public final NetworkedComputationContainer exportComputation;
    @Persisted
    @DescSynced
    @Getter
    @Setter
    protected boolean isMuffled;
    protected boolean previouslyMuffled = true;

    public KineticWorkableTieredMachine(IMachineBlockEntity holder, int tier, Int2IntFunction tankScalingFunction,
                                        Object... args) {
        super(holder, tier, tankScalingFunction, args);
        this.importComputation = createImportComputationContainer(args);
        this.exportComputation = createExportComputationContainer(args);
    }

    //////////////////////////////////////
    // ***** Initialization ******//
    //////////////////////////////////////
    protected NetworkedComputationContainer createImportComputationContainer(Object... args) {
        return new NetworkedComputationContainer(this, IO.IN);
    }

    protected NetworkedComputationContainer createExportComputationContainer(Object... args) {
        return new NetworkedComputationContainer(this, IO.OUT);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        List<IRecipeHandler<?>> handlers = new ArrayList<>(recipeHandlerList.getAllHandlers());
        handlers.add(importComputation);
        handlers.add(exportComputation);
        recipeHandlerList = RecipeHandlerList.of(handlers);
        traitSubscriptions.add(recipeHandlerList.subscribe(recipeLogic::updateTickSubscription));
    }

    @Override
    public void onUnload() {
        super.onUnload();
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
}
