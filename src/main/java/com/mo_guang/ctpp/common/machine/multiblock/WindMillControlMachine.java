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

    public boolean willTick = false;
    public WindMillControlMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        calculateWindmillAround();
    }


    @Override
    public boolean onWorking() {
        if (willTick) {
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
            textList.add(Component.translatable("ctpp.multiblock.windmill_control_center.info.0", String.format("%d",efficiency, 4 + 2 * tier)));
            textList.add(Component.translatable("ctpp.multiblock.windmill_control_center.info.1", String.format("%.1f",TotalOutput)));
            textList.add(Component.translatable("ctpp.multiblock.windmill_control_center.info.2", String.format("%d",efficiency*100)));
            textList.add(Component.translatable("ctpp.multiblock.windmill_control_center.info.3",String.format("%.1f",(TotalOutput + 512) * efficiency)));
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
        return Math.min((512 + TotalOutput) * efficiency / 512, AllConfigs.server().kinetics.maxRotationSpeed.get());
    }
    public void calculateWindmillAround() {
        var WindMillAround = new ArrayList<>();
        TotalOutput = 0;
        for (int x = -5 - tier; x < 5 + tier; x++) {
            for (int y = -5 - tier; y < 5 + tier; y++) {
                for (int z = -5 - tier; z < 5 + tier; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                    } else {
                        var kineticBlockEntity = getLevel().getBlockEntity(getPos().offset(x, y, z));
                        if (kineticBlockEntity instanceof WindmillBearingBlockEntity windmillBearingBlockEntity) {
                            var speed = windmillBearingBlockEntity.getGeneratedSpeed();
                            if(speed != 0 && WindMillAround.size() <= 16){
                                WindMillAround.add(speed);
                                TotalOutput += speed * 512;
                            }
                        }
                    }
                }
            }
        }
        efficiency = Math.min(WindMillAround.size(),6 + tier * 2);
    }
}
