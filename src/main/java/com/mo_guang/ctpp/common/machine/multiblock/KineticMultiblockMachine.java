package com.mo_guang.ctpp.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.IParallelHatch;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.api.gui.fancy.TooltipsPanel;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.mo_guang.ctpp.api.StressRecipeCapability;
import com.mo_guang.ctpp.common.machine.NotifiableStressTrait;
import com.mo_guang.ctpp.common.machine.multiblock.part.MechanicalUpgradePartMachine;
import com.mo_guang.ctpp.util.CTPPValues;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class KineticMultiblockMachine extends WorkableMultiblockMachine implements IFancyUIMachine, IDisplayUIMachine {
    @Getter
    public LongSet rotateBlocks;
    @Getter
    public LongSet blazeBlocks;

    @Getter
    public float speed = 64;
    @Getter
    public float previousSpeed = 0;
    public int tier = 0;
    public KineticMultiblockMachine(IMachineBlockEntity holder){
        super(holder);
    }
    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        previousSpeed = 0;
        checkTier();
        rotateBlocks = getMultiblockState().getMatchContext().getOrDefault("roBlocks", LongSets.emptySet());
        blazeBlocks = getMultiblockState().getMatchContext().getOrDefault("bbBlocks", LongSets.emptySet());
        updateActiveBlocks(recipeLogic.isWorking());
    }
    //////////////////////////////////////
    // ********* Recipe Logic **********//
    //////////////////////////////////////
    @Override
    public KineticRecipeLogic getRecipeLogic() {
        return new KineticRecipeLogic(this);
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        getCapabilitiesFlat(IO.OUT, StressRecipeCapability.CAP).forEach(iRecipeHandler -> {
            if (iRecipeHandler instanceof NotifiableStressTrait notifiableStressTrait) {
                notifiableStressTrait.preWorking();
            }
        });
        return super.beforeWorking(recipe);
    }

    public void postWorking() {
        getCapabilitiesFlat(IO.OUT, StressRecipeCapability.CAP).forEach(iRecipeHandler -> {
            if (iRecipeHandler instanceof NotifiableStressTrait notifiableStressTrait) {
                notifiableStressTrait.postWorking();
            }
        });
    }
    public void preWorking() {
        getCapabilitiesFlat(IO.OUT, StressRecipeCapability.CAP).forEach(iRecipeHandler -> {
            if (iRecipeHandler instanceof NotifiableStressTrait notifiableStressTrait) {
                notifiableStressTrait.preWorking();
            }
        });
    }
    @Override
    public void updateActiveBlocks(boolean active) {
        super.updateActiveBlocks(active);
        updateRotateBlocks(active);
        try {
            updateBlazeBlocks(active);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public void checkTier() {
        for (IMultiPart multiPart : getParts()) {
            if (multiPart instanceof MechanicalUpgradePartMachine upgradePartMachine) {
                tier = upgradePartMachine.tier;
            }
        }
    }
    public void updateRotateBlocks(boolean active){
        if (rotateBlocks != null) {
            for (Long pos : rotateBlocks) {
                var blockPos = BlockPos.of(pos);
                var blockEntity = Objects.requireNonNull(getLevel()).getBlockEntity(blockPos);
                updateRotateBlock(active,blockEntity);
            }
        }
    }
    public void updateRotateBlock(boolean active, BlockEntity blockEntity) {
        if (blockEntity instanceof KineticBlockEntity kineticBlockEntity) {
            if (active) {
                kineticBlockEntity.setSpeed(speed);
                kineticBlockEntity.onSpeedChanged(previousSpeed);
                kineticBlockEntity.sendData();
            }
            else {
                kineticBlockEntity.setSpeed(0);
                kineticBlockEntity.onSpeedChanged(speed);
                kineticBlockEntity.sendData();
            }
        }
    }
    public void updateBlazeBlocks(boolean active) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(blazeBlocks != null){
            for(Long pos : blazeBlocks) {
                var blockPos = BlockPos.of(pos);
                if (getLevel().getBlockEntity(blockPos) != null) {
                    var blockEntity = Objects.requireNonNull(getLevel()).getBlockEntity(blockPos);
                    BlazeBurnerBlock.HeatLevel heat = BlazeBurnerBlock.HeatLevel.SMOULDERING;
                    if (active) {
                        if (speed >= 256) {
                            heat = BlazeBurnerBlock.HeatLevel.SEETHING;
                        } else if (speed >= 128) {
                            heat = BlazeBurnerBlock.HeatLevel.KINDLED;
                        } else {
                            heat = BlazeBurnerBlock.HeatLevel.FADING;
                        }
                    }
                    if (blockEntity instanceof BlazeBurnerBlockEntity blazeBurnerBlockEntity) {
                            Method method = BlazeBurnerBlockEntity.class.getDeclaredMethod("setBlockHeat", BlazeBurnerBlock.HeatLevel.class);
                            method.setAccessible(true);
                            method.invoke(blazeBurnerBlockEntity, heat);
                    }
                }
            }
        }
    }
    //////////////////////////////////////
    // ********** GUI ***********//
    //////////////////////////////////////

    @Override
    public void addDisplayText(List<Component> textList) {
        int numParallels = 0;
        Optional<IParallelHatch> optional = this.getParts().stream().filter(IParallelHatch.class::isInstance)
                .map(IParallelHatch.class::cast).findAny();
        if (optional.isPresent()) {
            IParallelHatch parallelHatch = optional.get();
            numParallels = parallelHatch.getCurrentParallel();
        }
        MultiblockDisplayText.builder(textList, isFormed())
                .setWorkingStatus(recipeLogic.isWorkingEnabled(), recipeLogic.isActive())
                .addMachineModeLine(getRecipeType(), getRecipeTypes().length > 1)
                .addParallelsLine(numParallels)
                .addWorkingStatusLine()
                .addProgressLine(recipeLogic.getProgress(), recipeLogic.getMaxProgress(),
                        recipeLogic.getProgressPercent())
                .addOutputLines(recipeLogic.getLastRecipe());
        getDefinition().getAdditionalDisplay().accept(this, textList);
        IDisplayUIMachine.super.addDisplayText(textList);
    }

    @Override
    public Widget createUIWidget() {
        var group = new WidgetGroup(0, 0, 182 + 8, 117 + 8);
        group.addWidget(new DraggableScrollableWidgetGroup(4, 4, 182, 117).setBackground(getScreenTexture())
                .addWidget(new LabelWidget(4, 5, self().getBlockState().getBlock().getDescriptionId()))
                .addWidget(new ComponentPanelWidget(4, 17, this::addDisplayText)
                        .textSupplier(this.getLevel().isClientSide ? null : this::addDisplayText)
                        .setMaxWidthLimit(200)
                        .clickHandler(this::handleDisplayClick)));
        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        return new ModularUI(198, 208, this, entityPlayer).widget(new FancyMachineUIWidget(this, 198, 208));
    }

    @Override
    public List<IFancyUIProvider> getSubTabs() {
        return getParts().stream().filter(Objects::nonNull).map(IFancyUIProvider.class::cast).toList();
    }

    @Override
    public void attachTooltips(TooltipsPanel tooltipsPanel) {
        for (IMultiPart part : getParts()) {
            part.attachFancyTooltipsToController(this, tooltipsPanel);
        }
    }
    public class KineticRecipeLogic extends RecipeLogic {

        public KineticRecipeLogic(IRecipeLogicMachine machine) {
            super(machine);
        }

        @Override
        public void handleRecipeWorking() {
            Status last = this.getStatus();
            super.handleRecipeWorking();
            if (last == Status.WORKING && getStatus() != Status.WORKING) {
                if (machine instanceof KineticMultiblockMachine kineticMultiblockMachine) {
                    kineticMultiblockMachine.postWorking();
                }
            }
            if (last != Status.WORKING && getStatus() == Status.WORKING) {
                if (machine instanceof KineticMultiblockMachine kineticMultiblockMachine) {
                    kineticMultiblockMachine.preWorking();;
                }
            }
        }

//        @Override
//        protected void onStatusSynced(Status newValue, Status oldValue) {
//            if (oldValue.equals(Status.WORKING) && !newValue.equals(Status.WORKING)) {
//                if (machine instanceof KineticMultiblockMachine kineticMultiblockMachine) {
//                    kineticMultiblockMachine.postWorking();
//                }
//            }
//            if (!oldValue.equals(Status.WORKING) && newValue.equals(Status.WORKING)) {
//                if (machine instanceof KineticMultiblockMachine kineticMultiblockMachine) {
//                    kineticMultiblockMachine.preWorking();;
//                }
//            }
//            super.onStatusSynced(newValue, oldValue);
//        }
    }
}