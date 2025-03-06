package com.mo_guang.ctpp.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.data.GTMaterialBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.mo_guang.ctpp.common.machine.IKineticMachine;
import com.mo_guang.ctpp.common.machine.KineticPartMachine;
import com.mo_guang.ctpp.config.MainConfig;

import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.stream.Collectors;

public class KineticGeneratorMachine extends CoilWorkableElectricMultiblockMachine {
    public static final float GENERATING_BOOST = MainConfig.INSTANCE.ctnhConfig.kineticGeneratorGeneratingBoost;
    public double efficiency;
    public double outputEnergy = 0;
    public KineticGeneratorMachine(IMachineBlockEntity holder) {
        super(holder);
        efficiency = getCoilTier() * 0.1 + 0.9;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        var pos = MachineUtils.getOffset(this,0,0,1);
        if (getLevel().getBlockState(pos).getBlock().equals(GTMaterialBlocks.MATERIAL_BLOCKS.get(TagPrefix.block, GTMaterials.Graphene).get())) {
            efficiency = getCoilTier() * 0.1 + 1;
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) {
            var voltageName = GTValues.VNF[GTUtil.getTierByVoltage((long) outputEnergy)];
            textList.add(textList.size(), Component.translatable("multiblock.ctpp.kinetic_generator", FormattingUtil.formatNumbers(outputEnergy), voltageName));
            textList.add(textList.size(), Component.translatable("multiblock.ctpp.kinetic_generator.efficiency", String.format("%.1f",efficiency*100)));
        }
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (machine instanceof KineticGeneratorMachine kmachine) {
            var kinetic = (KineticPartMachine) kmachine.getParts().stream().filter(part -> part instanceof KineticPartMachine).toList().get(0);
            kmachine.outputEnergy = Math.abs(kinetic.getKineticHolder().getSpeed()) * kinetic.getKineticDefinition().torque * kmachine.efficiency*GENERATING_BOOST / 160;
            var modifiedRecipe = recipe.copy();
            RecipeHelper.setOutputEUt(modifiedRecipe, (long) kmachine.outputEnergy);
            return recipe1 -> modifiedRecipe;
        }
        return ModifierFunction.NULL;
    }
}
