package com.mo_guang.ctpp.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.ITieredMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import com.mo_guang.ctpp.common.machine.IKineticMachine;
import com.mo_guang.ctpp.common.machine.multiblock.part.KineticPartMachine;
import com.simibubi.create.infrastructure.config.AllConfigs;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class KineticWorkableMultiblockMachine extends KineticMultiblockMachine implements ITieredMachine {

    @Getter
    public float maxTorque = 0;

    public List<BlockPos> inputPartsMax = new ArrayList<>();

    public KineticWorkableMultiblockMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        for (IMultiPart part : getParts()) {
            if (part instanceof KineticPartMachine kineticPart) {
                if (kineticPart.getIO() == IO.IN) {
                    if (kineticPart.getKineticDefinition().torque > maxTorque) {
                        maxTorque = kineticPart.getKineticDefinition().torque;
                        inputPartsMax.clear();
                        inputPartsMax.add(kineticPart.getKineticHolder().getBlockPos());
                    } else if (kineticPart.getKineticDefinition().torque == maxTorque) {
                        {
                            inputPartsMax.add(kineticPart.getKineticHolder().getBlockPos());
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getTier() {
        return GTUtil.getTierByVoltage((long) (maxTorque / 4)) + speed >= 128 ? 1 : 0;
    }

    public float getTotalInputStress() {
        float input = 0;
        for (IMultiPart part : getParts()) {
            if (part instanceof KineticPartMachine kineticPart && kineticPart.getIO() == IO.IN) {
                input += Math.abs(kineticPart.getKineticHolder().getSpeed()) *
                        kineticPart.getKineticDefinition().torque;
            }
        }
        return input;
    }

    @Override
    public @Nullable Component beforeWorking(@NotNull GTRecipe recipe) {
        Component result = super.beforeWorking(recipe);
        previousSpeed = speed;
        if (speed != previousSpeed) {
            updateRotateBlocks(result == null);
        }
        return result;
    }

    @Override
    public void onChanged() {
        super.onChanged();
        updateMachineSpeed();
    }

    public void updateMachineSpeed() {
        speed = AllConfigs.server().kinetics.maxRotationSpeed.get();
        for (IMultiPart part : getParts()) {
            if (part instanceof IKineticMachine kineticPart &&
                    inputPartsMax.contains(kineticPart.getKineticHolder().getBlockPos())) {
                speed = Math.min(speed, Math.abs(kineticPart.getKineticHolder().getSpeed()));
            }
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed) {
            textList.add(Component.translatable("ctpp.multiblock.kinetic_workable_multiblock_machine.input_stress",
                    getTotalInputStress()));
            var lastRecipe = getRecipeLogic().getLastRecipe();
            if (lastRecipe != null)
                textList.add(Component.translatable("ctpp.multiblock.kinetic_workable_multiblock_machine.parallel",
                        lastRecipe.parallels));
        }
    }
}
