package com.mo_guang.ctpp.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.ITieredMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IRotorHolderMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.mo_guang.ctpp.api.StressRecipeCapability;
import com.mo_guang.ctpp.common.machine.IKineticMachine;
import com.mo_guang.ctpp.common.machine.KineticPartMachine;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.gregtechceu.gtceu.common.machine.multiblock.generator.LargeTurbineMachine.MIN_DURABILITY_TO_WARN;

public class KineticTurbineMachine extends KineticOutputMachine implements ITieredMachine {
    public double lossrate = 1;
    public KineticTurbineMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public int getTier() {
        return getKineticPart().self().getDefinition().getTier();
    }
    private IKineticMachine getKineticPart() {
        for (IMultiPart part : getParts()) {
            if (part instanceof IKineticMachine kineticMachine) {
                return kineticMachine;
            }
        }
        return null;
    }
    @Nullable
    private IRotorHolderMachine getRotorHolder() {
        for (IMultiPart part : getParts()) {
            if (part instanceof IRotorHolderMachine rotorHolder) {
                return rotorHolder;
            }
        }
        return null;
    }
    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) {
            var rotorHolder = getRotorHolder();

            if (rotorHolder != null && rotorHolder.getRotorEfficiency() > 0) {
                textList.add(Component.translatable("gtceu.multiblock.turbine.rotor_speed",
                        FormattingUtil.formatNumbers(rotorHolder.getRotorSpeed()),
                        FormattingUtil.formatNumbers(rotorHolder.getMaxRotorHolderSpeed())));
                textList.add(Component.translatable("ctpp.multiblock.kinetic_steam_turbine.info.0",
                        FormattingUtil.formatNumbers(rotorHolder.getTotalEfficiency() * lossrate)));
                if (isActive()) {
                    double output = 0;
                    if(recipeLogic.getLastRecipe() != null){
                        output = recipeLogic.getLastRecipe().outputs.get(StressRecipeCapability.CAP).stream().map(Content::getContent).mapToDouble(StressRecipeCapability.CAP::of).sum();
                    }
                    textList.add(Component.translatable("ctpp.multiblock.kinetic_steam_turbine.info.1",FormattingUtil.formatNumbers(output)));
                }

                int rotorDurability = rotorHolder.getRotorDurabilityPercent();
                if (rotorDurability > MIN_DURABILITY_TO_WARN) {
                    textList.add(Component.translatable("gtceu.multiblock.turbine.rotor_durability", rotorDurability));
                } else {
                    textList.add(Component.translatable("gtceu.multiblock.turbine.rotor_durability", rotorDurability)
                            .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                }
            }
        }
    }
    public double getMechanicalEfficiency() {
        return 1 + (double) tier / (1 + tier);
    }
    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if(machine instanceof KineticTurbineMachine kmachine) {
            var parallelResult = ParallelLogic.getParallelAmountFast(kmachine, recipe, GTValues.VH[kmachine.getTier()] / 4);
            ModifierFunction modifiedByKinetic = ModifierFunction.builder().inputModifier(ContentModifier.multiplier(parallelResult))
                        .outputModifier(ContentModifier.multiplier(parallelResult)).build();
                var rotorHolder = kmachine.getRotorHolder();
                if (!rotorHolder.hasRotor()) {
                    return ModifierFunction.NULL;
                }
                double holderEfficiency = rotorHolder.getTotalEfficiency() / 100.0;
                double boostRate = rotorHolder.getRotorSpeed() < rotorHolder.getMaxRotorHolderSpeed() ? (double) rotorHolder.getRotorSpeed() / rotorHolder.getMaxRotorHolderSpeed() : 1.0;
                var tier = Math.max(kmachine.getTier(), rotorHolder.self().getDefinition().getTier());
                if (tier > GTValues.HV) {
                    kmachine.lossrate = Math.max(0.5, 1 - (tier - GTValues.HV) * 0.1);
                }
                var contentModifier = ContentModifier.multiplier(holderEfficiency * boostRate * boostRate * kmachine.lossrate * kmachine.getMechanicalEfficiency());
            ModifierFunction modifiedByRotor = ModifierFunction.builder().outputModifier(contentModifier).build();
            return modifiedByRotor.compose(modifiedByKinetic);
        }
        return ModifierFunction.NULL;
    }
}
