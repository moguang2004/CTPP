package com.mo_guang.ctpp.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.block.ICoilType;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.ingredient.EnergyStack;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.block.CoilBlock;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTUtil;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import com.mo_guang.ctpp.dynamicPart.rotation.SimpleRotatingContraptionEntity;
import lombok.Getter;
import lombok.Setter;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.CN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.EN;

import java.util.ArrayList;
import java.util.List;

import static com.mo_guang.ctpp.common.data.recipe.KineticGeneratorRecipes.GENERATING_BOOST;

public class KineticGeneratorMachine extends KineticWorkableMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            KineticGeneratorMachine.class, KineticMultiblockMachine.MANAGED_FIELD_HOLDER);
    @Getter
    @Setter
    List<SimpleRotatingContraptionEntity> rotatingEntity = new ArrayList<>();
    private ICoilType coilType = CoilBlock.CoilType.CUPRONICKEL;
    public float previousSpeed;
    public float speed;
    public int magnetStrength;
    public double efficiency;
    public double outputEnergy = 0;

    public KineticGeneratorMachine(IMachineBlockEntity holder) {
        super(holder);
        magnetStrength = 0;
        efficiency = getEfficiency();
    }

    public double getEfficiency() {
        return (getCoilTier() * 0.1 + 0.9) * ((double) magnetStrength / (magnetStrength + 36));
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        var type = getMultiblockState().getMatchContext().get("CoilType");
        if (type instanceof ICoilType coil) {
            this.coilType = coil;
        }
        this.magnetStrength = getMultiblockState().getMatchContext().get("MagnetStrength");
        efficiency = getEfficiency();
    }

    @CN("产能功率：%d/%d EU/t (上限§4%d§r EU/t§)")
    @EN("Generator Rate：%d/%d EU/t (Limit %d EU/t)")
    static Lang info0;

    @CN("线圈效率：%d%%")
    @EN("Coil Efficiency：%d%%")
    static Lang info1;

    @CN("磁场强度：%d(%d%% 能量转化率)")
    @EN("Magnet Strength：%d(%d%% Energy Conversion Rate)")
    static Lang info2;

    @CN("总效率：%d%%")
    @EN("Total Efficiency：%d%%")
    static Lang info3;

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) {
            var voltageName = GTValues.VNF[GTUtil.getTierByVoltage((long) outputEnergy)];
            textList.add(textList.size(), info0.translate(FormattingUtil.formatNumbers(outputEnergy), voltageName,
                    (int) (Math.pow(4, tier) * 512)));
            textList.add(textList.size(), info1.translate(String.format("%.1f", (getCoilTier() * 0.1 + 1) * 100))
                    .withStyle(ChatFormatting.YELLOW));
            textList.add(textList.size(),
                    info2.translate(FormattingUtil.formatNumbers(magnetStrength),
                            String.format("%.1f", ((float) magnetStrength) * 100 / (magnetStrength + 36)))
                            .withStyle(ChatFormatting.AQUA));
            textList.add(textList.size(), info3.translate(String.format("%.1f", getEfficiency() * 100)));
        }
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (machine instanceof KineticGeneratorMachine kmachine) {
            int limit = (int) (Math.pow(4, kmachine.tier) * 512);
            kmachine.outputEnergy = Math
                    .min(kmachine.getTotalInputStress() * kmachine.efficiency * GENERATING_BOOST / 128, limit);
            var modifiedRecipe = recipe.copy();
            modifiedRecipe.tickOutputs.put(EURecipeCapability.CAP,
                    EURecipeCapability.makeEUContent(new EnergyStack((long) kmachine.outputEnergy)));
            return recipe1 -> modifiedRecipe;
        }
        return ModifierFunction.NULL;
    }

    @Override
    public boolean regressWhenWaiting() {
        return false;
    }

    public int getCoilTier() {
        return coilType.getTier();
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}
