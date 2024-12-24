package com.mo_guang.ctpp.lang;


import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.machines.GTMachineUtils;
import com.mo_guang.ctpp.common.data.CTPPMachines;
import com.mo_guang.ctpp.common.data.CTPPMultiblockMachines;
import com.mo_guang.ctpp.common.data.CTPPRecipeTypes;
import com.mo_guang.ctpp.core.CTPPCreativeModeTabs;

public class ChineseLangHandler {
    public static void init(RegistrateCNLangProvider provider){
        provider.add("recipe.capability.su.name", "应力");
        provider.add("recipe.condition.rpm.tooltip", "转速: %d");
        provider.add("ctpp.stress_input","应力输入：%dsu");
        provider.add("ctpp.stress_output","应力输出：%dsu");
        provider.add("multiblock.ctpp.kinetic_generator", "产能功率：%d/%d EU/t");
        provider.add("multiblock.ctpp.kinetic_generator.efficiency", "线圈效率：%d%%");
        provider.add("kinetic_generator", "能量守恒");
        provider.add("ctpp.kinetic_generator.basic", "应力转化EU的基础效率为160：1");
        provider.add("ctpp.kinetic_generator.extrict", "需要至少512su以启动机器");
        provider.add("ctpp.kinetic_generator.upgrade", "线圈等级每升高一级，总效率提高§a10%§r（初始为90%）");
        provider.add("ctpp.kinetic_generator.core", "若使用石墨烯块作为核心,则效率会再提高§a10%§r");
        provider.add("multiblock.ctpp.kinetic_steam_turbine", "涡轮总效率：%d%%");
        provider.add("multiblock.ctpp.kinetic_steam_turbine_output", "应力输出：%dsu");
        provider.add("kinetic_output", "一个输出应力的机器");
        provider.add("rotor_holder_upgrade", "转子支架每升高一级,涡轮效率增加§610%§r");
        provider.add("steam_up_hv_loss", "蒸汽类型的机器在电压等级高于§6HV§r时,每一级发电效率会减少10%");

        for (var tier : GTMachineUtils.ALL_TIERS) {
            provider.add(CTPPMachines.KINETIC_INPUT_BOX[tier].getBlock(), GTValues.VNF[tier] + " 应力输入仓");
            provider.add(CTPPMachines.KINETIC_OUTPUT_BOX[tier].getBlock(), GTValues.VNF[tier] + " 应力输出仓");
        }
        for (var tier: GTMachineUtils.LOW_TIERS) {
            provider.add(CTPPMachines.ELECTRIC_GEAR_BOX_2A[tier].getBlock(), "2A" + GTValues.VNF[tier] + " 电力齿轮箱");
            provider.add(CTPPMachines.ELECTRIC_GEAR_BOX_8A[tier].getBlock(), "8A" + GTValues.VNF[tier] + " 电力齿轮箱");
            provider.add(CTPPMachines.ELECTRIC_GEAR_BOX_16A[tier].getBlock(), "16A" + GTValues.VNF[tier] + " 电力齿轮箱");
            provider.add(CTPPMachines.ELECTRIC_GEAR_BOX_32A[tier].getBlock(), "32A" + GTValues.VNF[tier] + " 电力齿轮箱");
            provider.add(CTPPMachines.KINETIC_MIXER[tier].getBlock(), GTValues.VNF[tier] + " 应力搅拌机");
        }
        provider.add(CTPPCreativeModeTabs.MACHINE.get(), "CTPP机器");

        provider.add("gtceu.kinetic_generator", "应力发电");
        provider.add("gtceu.kinetic_steam_turbine", "蒸汽动力");


        provider.add(CTPPMultiblockMachines.SMASHING_FACTORY.getBlock(), "粉碎工厂");
        provider.add(CTPPMultiblockMachines.KINETIC_GENERATOR.getBlock(), "应力发电机");
        provider.add(CTPPMultiblockMachines.KINETIC_STEAM_TURBINE.getBlock(), "机械蒸汽涡轮");
    }
}
