package com.mo_guang.ctpp.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.mo_guang.ctpp.common.machine.IKineticMachine;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WindMillControlMachine extends KineticOutputMachine {
    public int efficiency = 0;
    public float TotalOutput = 0;
    public WindMillControlMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public boolean onWorking() {
        if(getOffsetTimer() % 20 == 0) {
            calculateWindmillAround();
        }
        return super.onWorking();
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        boolean result = super.beforeWorking(recipe);
        previousSpeed = speed;
        speed = getOutputSpeed();
        if(speed != previousSpeed){
            updateRotateBlocks(result);
        }
        return result;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) {
            textList.add(Component.translatable("multiblock.ctpp.windmill_control_center1", String.format("%d",efficiency)));
            textList.add(Component.translatable("multiblock.ctpp.windmill_control_center2", String.format("%.1f",TotalOutput)));
            textList.add(Component.translatable("multiblock.ctpp.windmill_control_center.efficiency", String.format("%d",efficiency*100)));
            textList.add(Component.translatable("multiblock.ctpp.windmill_control_center.output",String.format("%.1f",(TotalOutput + 512) * efficiency)));
        }
    }
    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (machine instanceof WindMillControlMachine wmachine) {
            var add = ModifierFunction.builder().outputModifier(ContentModifier.addition(wmachine.TotalOutput)).build();
            return add.andThen(ModifierFunction.builder().outputModifier(ContentModifier.multiplier(wmachine.efficiency)).build());
        }
        return ModifierFunction.NULL;
    }
    public float getOutputSpeed() {
        calculateWindmillAround();
        return Math.min((512 + TotalOutput) * efficiency / 512, AllConfigs.server().kinetics.maxRotationSpeed.get());
    }
    public void calculateWindmillAround() {
        var WindMillAround = new ArrayList<>();
        TotalOutput = 0;
        for (int x = -10; x < 11; x++) {
            for (int y = -10; y < 11; y++) {
                for (int z = -10; z < 11; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                    } else {
                        var kineticBlockEntity = getLevel().getBlockEntity(getPos().offset(x, y, z));
                        if (kineticBlockEntity instanceof WindmillBearingBlockEntity windmillBearingBlockEntity) {
                            var speed = windmillBearingBlockEntity.getGeneratedSpeed();
                            if(speed != 0){
                                WindMillAround.add(speed);
                                TotalOutput += speed * 512;
                            }
                        }
                    }
                }
            }
        }
        efficiency = Math.min(WindMillAround.size(),16);
    }
}
