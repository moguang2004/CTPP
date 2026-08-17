package com.mo_guang.ctpp.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.ITieredMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IRotorHolderMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.handler.RecipeHandlerGroup;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.mo_guang.ctpp.api.CTPPModifierFunction;
import org.jetbrains.annotations.Nullable;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

import java.util.List;

import static com.gregtechceu.gtceu.common.machine.multiblock.generator.LargeTurbineMachine.MIN_DURABILITY_TO_WARN;
import static java.lang.Math.pow;

public class KineticTurbineMachine extends KineticOutputMachine implements ITieredMachine {

    @CN({ "涡轮总效率：%d%%", "应力输出：%dsu" })
    @EN({ "Total Turbine Efficiency：%d%%", "Kinetic Output：%dsu" })
    static Lang[] info;

    public double lossrate = 1;

    public KineticTurbineMachine(IMachineBlockEntity holder) {
        super(holder);
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
                textList.add(info[0].translate(
                        FormattingUtil.formatNumbers(rotorHolder.getTotalEfficiency() * lossrate)));
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

    public static @Nullable Component recipeModifier(MetaMachine machine, RecipeHandlerGroup group, GTRecipe recipe) {
        if (machine instanceof KineticTurbineMachine kmachine) {
            int parallelLimit = Math.max(1, (int) (pow(4, kmachine.tier - 3) * 5));
            int parallelResult = ParallelLogic.getParallelAmountFast(group, recipe, parallelLimit);
            var rotorHolder = kmachine.getRotorHolder();
            if (rotorHolder == null || !rotorHolder.hasRotor()) {
                return Component.translatable("gtceu.recipe_modifier.missing_valid_turbine_rotor");
            }
            if (parallelResult > 1) {
                recipe.multiplyAllContents(parallelResult);
                recipe.parallels *= parallelResult;
            }
            double holderEfficiency = rotorHolder.getTotalEfficiency() / 100.0;
            if (holderEfficiency <= 0) return Component.translatable("gtceu.recipe_modifier.missing_valid_turbine_rotor");
            double boostRate = rotorHolder.getRotorSpeed() < rotorHolder.getMaxRotorHolderSpeed() ?
                    (double) rotorHolder.getRotorSpeed() / rotorHolder.getMaxRotorHolderSpeed() : 1.0;
            var tier = Math.max(kmachine.tier, rotorHolder.self().getDefinition().getTier());
            if (tier > GTValues.HV) {
                kmachine.lossrate = Math.max(0.5, 1 - (tier - GTValues.HV) * 0.1);
            }
            var stressModifier = holderEfficiency * boostRate * boostRate * kmachine.lossrate *
                    kmachine.getMechanicalEfficiency();
            return CTPPModifierFunction.outputStressMultiplier(stressModifier).apply(machine, group, recipe);
        }
        return RecipeModifier.nullWrongType(KineticTurbineMachine.class, machine);
    }
}
