package com.mo_guang.ctpp.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.IParallelHatch;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.api.gui.fancy.TooltipsPanel;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.mo_guang.ctpp.common.machine.KineticPartMachine;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class KineticWorkableMultiblockMachine extends WorkableMultiblockMachine implements IFancyUIMachine, IDisplayUIMachine {
    @Getter
    public LongSet rotateBlocks;

    public KineticWorkableMultiblockMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        rotateBlocks = getMultiblockState().getMatchContext().getOrDefault("roBlocks", LongSets.emptySet());
        updateActiveBlocks(recipeLogic.isWorking());
    }

    @Override
    public void updateActiveBlocks(boolean active) {
        super.updateActiveBlocks(active);
        if (rotateBlocks != null) {
            for (Long pos : rotateBlocks) {
                var blockPos = BlockPos.of(pos);
                var blockEntity = Objects.requireNonNull(getLevel()).getBlockEntity(blockPos);
                updateRotateBlocks(active,blockEntity);
            }
        }
    }
    public void updateRotateBlocks(boolean active, BlockEntity blockEntity) {
        if (blockEntity instanceof KineticBlockEntity kineticBlockEntity) {
            if (active) {
                kineticBlockEntity.setSpeed(128);
                kineticBlockEntity.onSpeedChanged(0);
                kineticBlockEntity.sendData();
            }
            else {
                kineticBlockEntity.setSpeed(0);
                kineticBlockEntity.onSpeedChanged(128);
                kineticBlockEntity.sendData();
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
    public double getTotalInputStress() {
        for (IMultiPart part : getParts()) {
            if (part instanceof KineticPartMachine) {
            }
        }
        return 0;
    }
//    .recipeModifier((/**@type {$MetaMachine}*/ machine,/**@type {$GTRecipe}*/ recipe) -> {
//            const kineticMachine = machine.getParts().find(part => part instanceof $IKineticMachine)
//        if (kineticMachine === null) {
//            return null;
//        }
//        let speed = kineticMachine.getKineticHolder().getSpeed()
//        speed = Math.abs(speed)
//        let torque = GTValues.V[kineticMachine.getTier()]
//        if (torque * speed < 512) {
//            return null;
//        }
//        let overclock_grade = kineticMachine.getTier() - 1
//        let multiplerate = (speed * torque / 512) / Math.pow(2, overclock_grade)
//        let newrecipe = recipe.copy()
//        if (newrecipe.duration / multiplerate < 1) {
//            newrecipe.duration = 1
//            newrecipe.parallels = multiplerate / 200
//            let GTrecipemodifier = GTRecipeModifiers.accurateParallel(machine, newrecipe, multiplerate / 200, false)
//            return GTrecipemodifier.getFirst()
//        }
//        else {
//            newrecipe.duration = newrecipe.duration / multiplerate
//        }
//        return newrecipe
//    })
}
