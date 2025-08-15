package com.mo_guang.ctpp.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.ITieredMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.mo_guang.ctpp.api.CTPPModifierFunction;
import com.mo_guang.ctpp.common.machine.IKineticMachine;
import com.mo_guang.ctpp.common.machine.KineticPartMachine;
import com.simibubi.create.infrastructure.config.AllConfigs;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class KineticWorkableMultiblockMachine extends KineticMultiblockMachine implements ITieredMachine {

    @Getter
    public float maxTorque = 0;
    public int parallels = 1;

    public List<BlockPos> inputPartsMax = new ArrayList<>();

    public KineticWorkableMultiblockMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        for (IMultiPart part : getParts()) {
            if(part instanceof KineticPartMachine kineticPart){
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
        return GTUtil.getTierByVoltage((long) (maxTorque/4)) + speed >= 128? 1 : 0;
    }

    public float getTotalInputStress(){
        float input = 0;
        for (IMultiPart part : getParts()) {
            if(part instanceof KineticPartMachine kineticPart && kineticPart.getIO() == IO.IN){
                input += Math.abs(kineticPart.getKineticHolder().getSpeed()) * kineticPart.getKineticDefinition().torque;
            }
        }
        return input;
    }


    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        boolean result = super.beforeWorking(recipe);
        previousSpeed = speed;
        if(speed != previousSpeed){
            updateRotateBlocks(result);
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
        for (IMultiPart part : getParts()){
            if(part instanceof IKineticMachine kineticPart && inputPartsMax.contains(kineticPart.getKineticHolder().getBlockPos())){
                speed = Math.min(speed, Math.abs(kineticPart.getKineticHolder().getSpeed()));
            }
        }
    }

    /**
     * use to calculate modifierFunction through rotation speed
     * 0 < rpm < 64: no change
     * 64 <= rpm < 128: duration reduction(x0.8)
     * 128 <= rpm < 256: non_perfect overclock(inputStress multiplied by 4 while duration divided by 2)
     * 256 <= rpm < 512: perfect overclock(inputStress multiplied by 4 while duration divided by 4)
     * */
    public ModifierFunction calculateModifier() {
        if(speed < 64){
            return ModifierFunction.IDENTITY;
        }
        else if(speed < 128){
            return ModifierFunction.builder().durationMultiplier(0.8).build();
        }
        else if(speed < 256){
            return ModifierFunction.builder().durationMultiplier(0.5).build().andThen(CTPPModifierFunction.inputStressMultiplier(4));
        }
        else{
            return ModifierFunction.builder().durationMultiplier(0.25).build().andThen(CTPPModifierFunction.inputStressMultiplier(4));
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        textList.add(Component.translatable("ctpp.multiblock.kinetic_workable_multiblock_machine.speed",speed));
        textList.add(Component.translatable("ctpp.multiblock.kinetic_workable_multiblock_machine.parallel",parallels));
        if(speed < 64){
            textList.add(Component.translatable("ctpp.multiblock.kinetic_workable_multiblock_machine.null"));
        }
        else if(speed < 128){
            textList.add(Component.translatable("ctpp.multiblock.kinetic_workable_multiblock_machine.reduction"));
        }
        else if(speed < 256){
            textList.add(Component.translatable("ctpp.multiblock.kinetic_workable_multiblock_machine.overclock"));
        }
        else{
            textList.add(Component.translatable("ctpp.multiblock.kinetic_workable_multiblock_machine.perfect_overclock"));
        }
    }
}
