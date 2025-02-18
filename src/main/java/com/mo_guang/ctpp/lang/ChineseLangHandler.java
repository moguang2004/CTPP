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
        provider.add("multiblock.ctpp.windmill_control_center1", "控制的风车数量：%d");
        provider.add("multiblock.ctpp.windmill_control_center2", "控制的风车总应力：%dsu");
        provider.add("multiblock.ctpp.windmill_control_center.efficiency", "总产能效率：%d%%");
        provider.add("multiblock.ctpp.windmill_control_center.output", "总应力输出：§a%dsu§r");
        provider.add("windmill_control_center", "风力总控！越多越强！");
        provider.add("ctpp.windmill_control_center.mechanism", "会检测多方块周围半径16格内的风车轴承，总输出的应力为：周围的风车轴承数x(周围风车总应力输出 + 512)。§4最多控制16个风车！§r");
        provider.add("ctpp.windmill_control_center.output", "输出应力越多，机器顶部的水车旋转会越快");
        provider.add("boom_of_create", "艺术就是爆炸！");
        provider.add("ctpp.boom_of_create.basic", "大型聚爆应力厂使用爆炸物以及一小部分电力运行，以此产生大量应力");
        provider.add("ctpp.boom_of_create.coolant", "持续运行时会逐渐减少电力消耗，最低可为0");
        provider.add("ctpp.boom_of_create.overclock", "与三峡大坝一样，必须有足够的应力输出口才会开始工作");
        provider.add("ctpp.boom_of_create.safe", "§a应力飞升之路§r");
        provider.add("kinetic_overclock", "拥有应力超频机制，根据输入的总应力大小获得并行数，根据输入的转速大小拥有不同的效果：\n转速 < 64:无效果\n转速 < 128:获得25%耗时减免\n转速 < 256:获得一次超频机会\n转速 < 512:获得一次无损超频机会");
        provider.add("actual_speed", "输入转速以等级最高的应力仓的转速为准");
        provider.add("ctpp.kinetic_workable_multiblock_machine.speed","输入转速：%drpm");
        provider.add("ctpp.kinetic_workable_multiblock_machine.parallel","并行数： %d");
        provider.add("ctpp.kinetic_workable_multiblock_machine.null","状态：无");
        provider.add("ctpp.kinetic_workable_multiblock_machine.reduction","状态：配方耗时减免x0.8");
        provider.add("ctpp.kinetic_workable_multiblock_machine.overclock","状态：超频");
        provider.add("ctpp.kinetic_workable_multiblock_machine.perfect_overclock","状态：无损超频");

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
        provider.add("gtceu.seaweed_farm", "海草养殖");
        provider.add("gtceu.windmill_control_center", "风车控制中心");
        provider.add("gtceu.boom_of_create", "聚爆应力厂");
        provider.add("gtceu.smashing_factory_recipes", "粉碎工厂");
        provider.add("gtceu.kinetic_mixer", "应力搅拌");


        provider.add(CTPPMultiblockMachines.SMASHING_FACTORY.getBlock(), "粉碎工厂");
        provider.add(CTPPMultiblockMachines.KINETIC_GENERATOR.getBlock(), "应力发电机");
        provider.add(CTPPMultiblockMachines.KINETIC_STEAM_TURBINE.getBlock(), "机械蒸汽涡轮");
        provider.add(CTPPMultiblockMachines.SEAWEED_FARM.getBlock(), "海草农场");
        provider.add(CTPPMultiblockMachines.WINDMILL_CONTROL_CENTER.getBlock(), "风车控制中心");
        provider.add(CTPPMultiblockMachines.BOOM_OF_CREATE.getBlock(), "大型聚爆应力厂");
    }
}
