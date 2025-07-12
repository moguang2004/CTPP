package com.mo_guang.ctpp.lang;


import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.machines.GTMachineUtils;
import com.mo_guang.ctpp.CTPPItems;
import com.mo_guang.ctpp.common.data.CTPPMachines;
import com.mo_guang.ctpp.common.data.CTPPMultiblockMachines;
import com.mo_guang.ctpp.common.data.CTPPRecipeTypes;
import com.mo_guang.ctpp.core.CTPPCreativeModeTabs;

public class ChineseLangHandler {
    public static void init(RegistrateCNLangProvider provider){
        provider.add("ctpp.common_tooltip.kinetic_overclock", "-拥有应力超频机制，根据输入的总应力大小获得并行数(非线性)，根据输入的转速大小拥有不同的效果：\n转速 < 64:无效果\n转速 < 128:获得20%耗时减免\n转速 < 256:获得一次超频机会\n转速 < 512:获得一次无损超频机会\n输入速度由所有应力仓中速度最低者决定");
        provider.add("ctpp.common_tooltip.input_speed", "*输入转速以等级最高的应力仓的转速为准");
        provider.add("ctpp.common_tooltip.mechanical_tier", "*机械等级由机械升级仓中的物品决定：无(0)，基础构件(1)，精密构件(2)，\n钢铁构建或基础电子电路(3)，高级电子电路(4)，集成电路(5)");

        provider.add("recipe.capability.su.name", "应力");
        provider.add("recipe.condition.rpm.tooltip", "转速: %d");
        provider.add("recipe.condition.mechanical_tier.tooltip", "机械等级: %s");
        provider.add("ctpp.stress_input","应力输入：%dsu");
        provider.add("ctpp.stress_output","应力输出：%dsu");

        // Multiblock UI info
        provider.add("ctpp.multiblock.kinetic_generator.info.0", "产能功率：%d/%d EU/t");
        provider.add("ctpp.multiblock.kinetic_generator.info.1", "线圈效率：%d%%");

        provider.add("ctpp.multiblock.kinetic_steam_turbine.info.0", "涡轮总效率：%d%%");
        provider.add("ctpp.multiblock.kinetic_steam_turbine.info.1", "应力输出：%dsu");

        provider.add("ctpp.multiblock.windmill_control_center.info.0", "控制的风车数量：%d(最大：%d)");
        provider.add("ctpp.multiblock.windmill_control_center.info.1", "控制的风车总应力：%dsu");
        provider.add("ctpp.multiblock.windmill_control_center.info.2", "总产能效率：%d%%");
        provider.add("ctpp.multiblock.windmill_control_center.info.3", "总应力输出：§a%dsu§r");

        provider.add("ctpp.multiblock.kinetic_workable_multiblock_machine.speed","输入转速：%drpm");
        provider.add("ctpp.multiblock.kinetic_workable_multiblock_machine.parallel","并行数： %d");
        provider.add("ctpp.multiblock.kinetic_workable_multiblock_machine.null","状态：无");
        provider.add("ctpp.multiblock.kinetic_workable_multiblock_machine.reduction","状态：配方耗时减免x0.8");
        provider.add("ctpp.multiblock.kinetic_workable_multiblock_machine.overclock","状态：超频");
        provider.add("ctpp.multiblock.kinetic_workable_multiblock_machine.perfect_overclock","状态：无损超频");

        provider.add("ctpp.multiblock.mechanical_tier", "当前机械等级：%d(%s)");
        provider.add("ctpp.mechanical_tier.0", "无");
        provider.add("ctpp.mechanical_tier.1", "基础");
        provider.add("ctpp.mechanical_tier.2", "黄铜");
        provider.add("ctpp.mechanical_tier.3", "钢铁");
        provider.add("ctpp.mechanical_tier.4", "电子");
        provider.add("ctpp.mechanical_tier.5", "运算");

        // Multiblock Tooltip
        provider.add("ctpp.multiblock.kinetic_generator.tooltip.0", "能量守恒");
        provider.add("ctpp.multiblock.kinetic_generator.tooltip.1", "应力转化EU的基础效率为160：1");
        provider.add("ctpp.multiblock.kinetic_generator.tooltip.2", "需要至少512su以启动机器");
        provider.add("ctpp.multiblock.kinetic_generator.tooltip.3", "线圈等级每升高一级，总效率提高§a10%§r（初始为90%）");
        provider.add("ctpp.multiblock.kinetic_generator.tooltip.4", "若使用石墨烯块作为核心,则效率会再提高§a10%§r");

        provider.add("ctpp.multiblock.kinetic_steam_turbine.tooltip.0", "一个输出应力的机器");
        provider.add("ctpp.multiblock.kinetic_steam_turbine.tooltip.1", "转子支架每升高一级,涡轮效率增加§610%§r");
        provider.add("ctpp.multiblock.kinetic_steam_turbine.tooltip.2", "蒸汽类型的机器在电压等级高于§6HV§r时,每一级发电效率会减少10%");
        provider.add("ctpp.multiblock.kinetic_steam_turbine.tooltip.3", "运行效率会获得(1 + 机械等级/(机械等级 + 1))的效率加成");

        provider.add("ctpp.multiblock.windmill_control_center.tooltip.0", "风力总控！越多越强！");
        provider.add("ctpp.multiblock.windmill_control_center.tooltip.1", "-会检测多方块周围半径(5 + 机械等级)格内的风车轴承\n-总输出的应力为：周围的风车轴承数x(周围风车总应力输出 + 512)\n-§4最多控制(6 + 2 * 机械等级)个风车！§r");
        provider.add("ctpp.multiblock.windmill_control_center.tooltip.2", "输出应力越多，机器顶部的水车旋转会越快");

        provider.add("ctpp.multiblock.boom_of_create.tooltip.0", "艺术就是爆炸！");
        provider.add("ctpp.multiblock.boom_of_create.tooltip.1", "大型聚爆应力厂使用爆炸物以及一小部分电力运行，以此产生大量应力");
        provider.add("ctpp.multiblock.boom_of_create.tooltip.2", "持续运行时会逐渐减少电力消耗，最低可为0");
        provider.add("ctpp.multiblock.boom_of_create.tooltip.3", "§a应力飞升之路§r");

        provider.add("ctpp.recipe.fan_breathing", "批量龙吟");
        provider.add("ctpp.recipe.breathing.fan", "在龙首后放置鼓风机");
        provider.add("ctpp.recipe.fan_acid_washing", "批量酸洗");
        provider.add("ctpp.recipe.acid_washing.fan", "在硫酸后放置鼓风机");


        provider.add("ctpp.copyright.info", "该机器由§6CT++§r添加");

        for (var tier : GTMachineUtils.ALL_TIERS) {
            provider.add(CTPPMachines.KINETIC_INPUT_BOX[tier].getBlock(), GTValues.VNF[tier] + " 应力输入箱");
            provider.add(CTPPMachines.KINETIC_OUTPUT_BOX[tier].getBlock(), GTValues.VNF[tier] + " 应力输出箱");
        }
        for (var tier: GTMachineUtils.LOW_TIERS) {
            provider.add(CTPPMachines.ELECTRIC_GEAR_BOX_2A[tier].getBlock(), "2A" + GTValues.VNF[tier] + " 电力齿轮箱");
            provider.add(CTPPMachines.ELECTRIC_GEAR_BOX_8A[tier].getBlock(), "8A" + GTValues.VNF[tier] + " 电力齿轮箱");
            provider.add(CTPPMachines.ELECTRIC_GEAR_BOX_16A[tier].getBlock(), "16A" + GTValues.VNF[tier] + " 电力齿轮箱");
            provider.add(CTPPMachines.ELECTRIC_GEAR_BOX_32A[tier].getBlock(), "32A" + GTValues.VNF[tier] + " 电力齿轮箱");
            provider.add(CTPPMachines.KINETIC_MIXER[tier].getBlock(), GTValues.VNF[tier] + " 应力搅拌机");
        }
        provider.add(CTPPCreativeModeTabs.MACHINE.get(), "CTPP机器");

        provider.add(CTPPItems.BASIC_MECHANISM.get(), "基础构件");
        provider.add(CTPPItems.STEEL_MECHANISM.get(), "钢铁构件");

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

        provider.add("config.screen.ctpp", "CTPP设置");

        provider.add("config.ctpp.option.gtmConfig", "GTM经典联动联动机器配置");
        provider.add("config.ctpp.option.ctnhConfig", "CTNH机器配置");
      
        provider.add("config.ctpp.option.enableGTMKineticOutputBox", "是否启用GTM的应力输出仓");
        provider.add("config.ctpp.option.kineticInputBoxTorqueMultiplier", "应力输入仓的扭矩乘数");
        provider.add("config.ctpp.option.kineticOutputBoxTorqueMultiplier", "应力输出仓的扭矩乘数");
        provider.add("config.ctpp.option.enableGTMKineticCreateMixer", "是否启用GTM的应力搅拌机");
        provider.add("config.ctpp.option.kineticCreateMixerSpeedMultiplier", "比起同等级的电动搅拌机，应力搅拌机的处理速度倍率");
        provider.add("config.ctpp.option.kineticCreateMixerRPMRequirement", "应力搅拌转速要求");
        provider.add("config.ctpp.option.enableGTMElectricGearBox", "是否启用GTM的电动齿轮箱");
      
        provider.add("config.ctpp.option.enableSmashingFactory", "是否启用CTNH的粉碎工厂");
        provider.add("config.ctpp.option.smashingFactoryMaximumProcessingCapacity", "粉碎工厂的最大处理能力（用整数来表示电压级）");
        provider.add("config.ctpp.option.smashingFactorySpeedMultiplier", "比起同等级的粉碎机，粉碎工厂的处理速度倍率");
        provider.add("config.ctpp.option.smashingFactoryRPMRequirement", "粉碎工厂的转速要求");
        provider.add("config.ctpp.option.smashingFactoryStressRequirement", "粉碎工厂的应力要求（等于原配方的电压需求乘以该倍数）");
        provider.add("config.ctpp.option.enableKineticGenerator", "是否启用CTNH的应力发电机");
        provider.add("config.ctpp.option.kineticGeneratorGeneratingBoost", "应力发电机的电力产出加成");
        provider.add("config.ctpp.option.kineticGeneratorGeneratingRequireLubricant", "应力发电是否消耗润滑油");
        provider.add("config.ctpp.option.kineticGeneratorGeneratingRequireLubricantAmount", "应力发电的润滑油消耗量");
        provider.add("config.ctpp.option.enableKineticSteamTurbine", "是否启用CTNH的机械蒸汽涡轮");
        provider.add("config.ctpp.option.enableSteamTurbineGearBox", "是否启用CTNH的蒸汽涡轮齿轮箱");
        provider.add("config.ctpp.option.steamPoweredKineticGeneratingBoost", "蒸汽动力的应力产出加成");
        provider.add("config.ctpp.option.enableSeaweedFarm", "是否启用CTNH的海草农场");
        provider.add("config.ctpp.option.enableWindmillControlCenter", "是否启用CTNH的风车控制中心");
        provider.add("config.ctpp.option.enableBoomOfCreate", "是否启用CTNH的聚爆应力厂");
    }
}
