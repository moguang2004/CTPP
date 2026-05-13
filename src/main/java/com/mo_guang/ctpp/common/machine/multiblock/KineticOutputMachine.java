package com.mo_guang.ctpp.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.ctnhlang.Suffix;
import com.mo_guang.ctpp.api.StressRecipeCapability;
import com.mo_guang.ctpp.common.data.recipe.builder.CTPPRecipeHelper;
import com.mo_guang.ctpp.common.machine.multiblock.part.KineticPartMachine;
import com.simibubi.create.infrastructure.config.AllConfigs;
import lombok.Getter;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

import java.util.List;

@Suffix("tooltip")
public class KineticOutputMachine extends KineticMultiblockMachine {

    @Getter
    public float maxOutputStress = 0;

    public KineticOutputMachine(IMachineBlockEntity holder) {
        super(holder);
        this.speed = 64;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        maxOutputStress = 0;
        for (IMultiPart part : getParts()) {
            if (part instanceof KineticPartMachine kineticPart && kineticPart.getIO() == IO.OUT) {
                maxOutputStress += AllConfigs.server().kinetics.maxRotationSpeed.get() *
                        kineticPart.getKineticDefinition().torque;
            }
        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        maxOutputStress = 0;
    }

    @CN("最大应力输出：%s")
    @EN("Max stress output: %s")
    static Lang maxKineticOutput;

    @CN("配方应力产出：%s")
    @EN("Recipe stress output: %s")
    static Lang recipeKineticOutput;

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed() && getRecipeLogic().getLastRecipe() != null) {
            textList.add(maxKineticOutput.translate(
                    FormattingUtil.formatNumbers(maxOutputStress)).withStyle(ChatFormatting.GRAY));
            textList.add(recipeKineticOutput.translate(FormattingUtil.formatNumbers(
                    CTPPRecipeHelper.getOutputStress(getRecipeLogic().getLastRecipe())))
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean canVoidRecipeOutputs(RecipeCapability<?> capability) {
        return super.canVoidRecipeOutputs(capability) || capability == StressRecipeCapability.CAP;
    }
}
