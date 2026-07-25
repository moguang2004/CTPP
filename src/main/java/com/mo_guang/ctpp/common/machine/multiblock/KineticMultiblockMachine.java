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
import com.gregtechceu.gtceu.api.machine.multiblock.RecipeMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.machine.trait.WorkLogic;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.*;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.mo_guang.ctpp.api.StressRecipeCapability;
import com.mo_guang.ctpp.common.blockentity.IKineticBlockEntityExtension;
import com.mo_guang.ctpp.common.machine.NotifiableStressTrait;
import com.mo_guang.ctpp.common.machine.multiblock.part.MechanicalUpgradePartMachine;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import lombok.Getter;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public abstract class KineticMultiblockMachine extends RecipeMultiblockMachine
                                               implements IFancyUIMachine, IDisplayUIMachine {

    @Getter
    public LongSet rotateBlocks;
    @Getter
    public LongSet blazeBlocks;

    @Getter
    public float speed = 64;
    @Getter
    public float previousSpeed = 0;
    public int tier = 0;

    @CN("暂停中：")
    @EN("Waiting：")
    static Lang waiting;

    public KineticMultiblockMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        previousSpeed = 0;
        checkTier();
        rotateBlocks = getMultiblockState().getMatchContext().getOrDefault("roBlocks", LongSets.emptySet());
        blazeBlocks = getMultiblockState().getMatchContext().getOrDefault("bbBlocks", LongSets.emptySet());
        for (var pos : rotateBlocks) {
            var blockEntity = getLevel().getBlockEntity(BlockPos.of(pos));
            if (blockEntity instanceof KineticBlockEntity kineticBlockEntity) {
                IKineticBlockEntityExtension mixin = ((IKineticBlockEntityExtension) kineticBlockEntity);
                mixin.setCTNHInMultiblock(true);
            }
        }
        updateActiveBlocks(getRecipeLogic().isWorking());
    }

    @Override
    public void onStructureInvalid() {
        stopWorking();
        super.onStructureInvalid();
        for (var pos : rotateBlocks) {
            var blockEntity = getLevel().getBlockEntity(BlockPos.of(pos));
            if (blockEntity instanceof KineticBlockEntity kineticBlockEntity) {
                IKineticBlockEntityExtension mixin = ((IKineticBlockEntityExtension) kineticBlockEntity);
                mixin.setCTNHInMultiblock(false);
            }
        }
    }

    public void onTierChanged() {}

    public void stopWorking() {
        getCapabilitiesFlat(IO.OUT, StressRecipeCapability.CAP).forEach(iRecipeHandler -> {
            if (iRecipeHandler instanceof NotifiableStressTrait notifiableStressTrait) {
                notifiableStressTrait.stopWorking();
            }
        });
    }

    @Override
    public void notifyWorkStatusChanged(WorkLogic.Status oldStatus,
                                        WorkLogic.Status newStatus) {
        super.notifyWorkStatusChanged(oldStatus, newStatus);
        if (newStatus != WorkLogic.Status.WORKING) stopWorking();
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
                tier = Math.max(upgradePartMachine.tier, tier);
            }
        }
    }

    public void updateRotateBlocks(boolean active) {
        if (rotateBlocks != null) {
            for (Long pos : rotateBlocks) {
                var blockPos = BlockPos.of(pos);
                var blockEntity = Objects.requireNonNull(getLevel()).getBlockEntity(blockPos);
                updateRotateBlock(active, blockEntity);
            }
        }
    }

    public void updateRotateBlock(boolean active, BlockEntity blockEntity) {
        if (blockEntity instanceof KineticBlockEntity kineticBlockEntity) {
            if (active) {
                float currentSpeed = kineticBlockEntity.getSpeed();
                kineticBlockEntity.setSpeed(speed);
                kineticBlockEntity.onSpeedChanged(currentSpeed);
                kineticBlockEntity.sendData();
            } else {
                kineticBlockEntity.setSpeed(0);
                kineticBlockEntity.onSpeedChanged(kineticBlockEntity.getSpeed());
                kineticBlockEntity.sendData();
            }
        }
    }

    public void updateBlazeBlocks(boolean active) throws NoSuchMethodException, InvocationTargetException,
                                                  IllegalAccessException {
        if (blazeBlocks != null) {
            for (Long pos : blazeBlocks) {
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
                        Method method = BlazeBurnerBlockEntity.class.getDeclaredMethod("setBlockHeat",
                                BlazeBurnerBlock.HeatLevel.class);
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
        int numParallels;
        int subtickParallels;
        int batchParallels;
        int totalRuns;
        boolean exact = false;
        if (getRecipeLogic().isActive() && getRecipeLogic().getLastRecipe() != null) {
            numParallels = getRecipeLogic().getLastRecipe().parallels;
            subtickParallels = getRecipeLogic().getLastRecipe().subtickParallels;
            batchParallels = getRecipeLogic().getLastRecipe().batchParallels;
            totalRuns = getRecipeLogic().getLastRecipe().getTotalRuns();
            exact = true;
        } else {
            numParallels = getParallelHatch()
                    .map(IParallelHatch::getCurrentParallel)
                    .orElse(0);
            subtickParallels = 0;
            batchParallels = 0;
            totalRuns = 0;
        }
        if (getRecipeLogic().isWaiting()) {
            textList.add(waiting.translate()
                    .withStyle(ChatFormatting.RED));
            for (var reason : getRecipeLogic().getFancyTooltip()) {
                textList.add(Component.literal(" - " + reason.getString()));
            }
        }
        MultiblockDisplayText.builder(textList, isFormed())
                .setWorkingStatus(getRecipeLogic().isWorkingEnabled(), getRecipeLogic().isActive())
                .addMachineModeLine(getRecipeType(), getRecipeTypes().length > 1)
                .addTotalRunsLine(totalRuns)
                .addParallelsLine(numParallels, exact)
                .addSubtickParallelsLine(subtickParallels)
                .addBatchModeLine(isBatchEnabled(), batchParallels)
                .addWorkingStatusLine()
                .addProgressLine(getRecipeLogic().getProgress(), getRecipeLogic().getMaxProgress(),
                        getRecipeLogic().getProgressPercent())
                .addOutputLines(getRecipeLogic().getLastRecipe());
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
    }
}
