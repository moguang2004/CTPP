package com.mo_guang.ctpp.lang;


import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.mo_guang.ctpp.common.data.CTPPMachines;
import com.mo_guang.ctpp.core.CTPPCreativeModeTabs;

public class ChineseLangHandler {
    public static void init(RegistrateCNLangProvider provider){
        provider.add("recipe.capability.su.name", "应力");
        provider.add("recipe.condition.rpm.tooltip", "转速: %d");

        for (var tier : GTMachines.ALL_TIERS) {
            provider.add(CTPPMachines.KINETIC_INPUT_BOX[tier].getBlock(), GTValues.VNF[tier] + " 应力输入仓");
            provider.add(CTPPMachines.KINETIC_OUTPUT_BOX[tier].getBlock(), GTValues.VNF[tier] + " 应力输出仓");
        }
        for (var tier: GTMachines.LOW_TIERS) {
            provider.add(CTPPMachines.ELECTRIC_GEAR_BOX_2A[tier].getBlock(), "2A" + GTValues.VNF[tier] + " 电力齿轮箱");
            provider.add(CTPPMachines.ELECTRIC_GEAR_BOX_8A[tier].getBlock(), "8A" + GTValues.VNF[tier] + " 电力齿轮箱");
            provider.add(CTPPMachines.ELECTRIC_GEAR_BOX_16A[tier].getBlock(), "16A" + GTValues.VNF[tier] + " 电力齿轮箱");
            provider.add(CTPPMachines.ELECTRIC_GEAR_BOX_32A[tier].getBlock(), "32A" + GTValues.VNF[tier] + " 电力齿轮箱");
            provider.add(CTPPMachines.KINETIC_MIXER[tier].getBlock(), GTValues.VNF[tier] + " 应力搅拌机");
        }
        provider.add(CTPPCreativeModeTabs.MACHINE.get(), "CTPP机器");
    }
}
