package com.mo_guang.ctpp.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.mo_guang.ctpp.config.MainConfig;

public class EnglishLangHandler {
    public static void init(RegistrateLangProvider provider){
        provider.add("ctpp.common_tooltip.kinetic_overclock", "-Has a stress overclocking mechanism that determines parallelism based on the total input stress. The effects vary based on input speed:\nSpeed < 64: No effect\nSpeed < 128: 25% time reduction\nSpeed < 256: One overclocking opportunity\nSpeed < 512: One lossless overclocking opportunity");
        provider.add("ctpp.common_tooltip.input_speed", "*The input speed is based on the highest-speed stress chamber.");
        provider.add("ctpp.common_tooltip.mechanical_tier", "*§nMechanical Tier§r is determined by items in the Mechanical Upgrade Bus: None (0), §7Basic Components (1)§r, §ePrecision Components (2)§r,\n§8Steel Structures or Basic Electronic Circuits (3)§r, §bAdvanced Electronic Circuits (4)§r, §6Integrated Circuits (5)§r");
        provider.add("ctpp.common_tooltip.mechanical_tier_machine", "§nMechanical Tier§r please be seen in the Tooltip of §lMechanical upgrade bus§r");

        provider.add("recipe.capability.su.name", "Create Stress");
        provider.add("recipe.condition.rpm.tooltip", "RPM: %d");
        provider.add("recipe.condition.mechanical_tier.tooltip", "Mechanical Tier: %s");
        provider.add("ctpp.stress_input","Stress Input：%dsu");
        provider.add("ctpp.stress_output","Stress Output：%dsu");
        provider.add("ctpp.mechanical_tier", "Mechanical Tier：%d(%s)");

        // Multiblock UI info
        provider.add("ctpp.multiblock.kinetic_generator.info.0", "Generator Rate：%d/%d EU/t");
        provider.add("ctpp.multiblock.kinetic_generator.info.1", "Coil Efficiency：%d%%");

        provider.add("ctpp.multiblock.kinetic_steam_turbine.info.0", "Total Turbine Efficiency：%d%%");
        provider.add("ctpp.multiblock.kinetic_steam_turbine.info.1", "Kinetic Output：%dsu");

        provider.add("ctpp.multiblock.windmill_control_center.info.0", "Number of controlled windmills: %d(Max: %d)");
        provider.add("ctpp.multiblock.windmill_control_center.info.1", "Total stress of controlled windmills: %dsu");
        provider.add("ctpp.multiblock.windmill_control_center.info.2", "Total energy efficiency: %d%%");
        provider.add("ctpp.multiblock.windmill_control_center.info.3", "Total stress output: §a%dsu§r");

        provider.add("ctpp.multiblock.kinetic_workable_multiblock_machine.speed", "Input speed: %drpm");
        provider.add("ctpp.multiblock.kinetic_workable_multiblock_machine.parallel", "Parallelism: %d");
        provider.add("ctpp.multiblock.kinetic_workable_multiblock_machine.null", "Status: None");
        provider.add("ctpp.multiblock.kinetic_workable_multiblock_machine.reduction", "Status: Recipe time reduction x0.75");
        provider.add("ctpp.multiblock.kinetic_workable_multiblock_machine.overclock", "Status: Overclocked");
        provider.add("ctpp.multiblock.kinetic_workable_multiblock_machine.perfect_overclock", "Status: Lossless overclocked");

        provider.add("ctpp.multiblock.mechanical_tier", "Current Mechanical Tier：%s(%s)");
        provider.add("ctpp.mechanical_tier.0", "None");
        provider.add("ctpp.mechanical_tier.1", "Basic");
        provider.add("ctpp.mechanical_tier.2", "Bronze");
        provider.add("ctpp.mechanical_tier.3", "Steel");
        provider.add("ctpp.mechanical_tier.4", "Electricity");
        provider.add("ctpp.mechanical_tier.5", "Calculation");

        // Multiblock Tooltip info
        provider.add("ctpp.multiblock.kinetic_generator.tooltip.0", "Energy Conversation");
        provider.add("ctpp.multiblock.kinetic_generator.tooltip.1", "The base conversion efficiency from stress to EU is 160:1");
        provider.add("ctpp.multiblock.kinetic_generator.tooltip.2", "Using a Block of Graphene as the core increases efficiency by §a10%§r");
        provider.add("ctpp.multiblock.kinetic_generator.tooltip.3", "Requires at least 512 SU to activate the machine");
        provider.add("ctpp.multiblock.kinetic_generator.tooltip.4", "Each level of coil tier improves total efficiency by §a10%§r (initial value: 90%)");

        provider.add("ctpp.multiblock.kinetic_steam_turbine.tooltip.0", "A machine that can output kinetic");
        provider.add("ctpp.multiblock.kinetic_steam_turbine.tooltip.1", "Each level of the rotor holder upgrade increases turbine efficiency by §610%§r");
        provider.add("ctpp.multiblock.kinetic_steam_turbine.tooltip.2", "Steam-type machines lose 10% efficiency for each voltage tier above §6HV§r");
        provider.add("ctpp.multiblock.kinetic_steam_turbine.tooltip.3", "Running efficiency will gain an addition of (1 + tier/(tier + 1))");

        provider.add("ctpp.multiblock.windmill_control_center.tooltip.0", "Windmill Master Control! The more, the stronger!");
        provider.add("ctpp.multiblock.windmill_control_center.tooltip.1", "-Detects windmill bearings within a radius of (5 + tier) blocks around the multiblock. \n-The total stress output is calculated as: Number of windmill bearings × (Total stress output of surrounding windmills + 512). \n-§4Can control up to (4 + 2 * tier) windmills!§r");
        provider.add("ctpp.multiblock.windmill_control_center.tooltip.2", "The more stress it outputs, the faster the water wheel will rotate.");

        provider.add("ctpp.multiblock.boom_of_create.tooltip.0", "Art is an explosion!");
        provider.add("ctpp.multiblock.boom_of_create.tooltip.1", "The Explosive Vortex Stress Induction System (EVSIS) using explosives and a small amount of electricity to generate massive stress.");
        provider.add("ctpp.multiblock.boom_of_create.tooltip.2", "Electricity consumption gradually decreases during continuous operation, with a minimum of 0.");
        provider.add("ctpp.multiblock.boom_of_create.tooltip.3", "§aThe Path to Stress Ascension§r");


        provider.add("ctpp.recipe.fan_breathing", "Dragon Fan Processing");
        provider.add("ctpp.recipe.breathing.fan", "Place the fan behind the Dragon head");
        provider.add("ctpp.recipe.fan_acid_washing", "AcidWashing Fan Processing");
        provider.add("ctpp.recipe.acid_washing.fan", "Place the fan behind the Sulfuric acid");

        provider.add("ctpp.copyright.info", "This machine is added by §6CT++§r");

        provider.add("config.screen.ctpp", "CTPP Configuration");

        provider.add(MainConfig.getConfigOptionKey("gtmConfig"), "Classic GTM Linkage Machinery Configuration");
        provider.add(MainConfig.getConfigOptionKey("enableGTMKineticOutputBox"), "Enable GTM Kinetic Output Box");
        provider.add(MainConfig.getConfigOptionKey("kineticInputBoxTorqueMultiplier"), "Kinetic Input Box Torque Multiplier");
        provider.add(MainConfig.getConfigOptionKey("kineticOutputBoxTorqueMultiplier"), "Kinetic Output Box Torque Multiplier");
        provider.add(MainConfig.getConfigOptionKey("enableGTMKineticCreateMixer"), "Enable GTM Kinetic Mixer");
        provider.add(MainConfig.getConfigOptionKey("kineticCreateMixerSpeedMultiplier"), "Kinetic Mixer Speed Multiplier Compared to Mixer");
        provider.add(MainConfig.getConfigOptionKey("kineticCreateMixerRPMRequirement"), "Kinetic Mixer RPM Requirement");
        provider.add(MainConfig.getConfigOptionKey("enableGTMElectricGearBox"), "Enable Electric Gearbox");

        provider.add(MainConfig.getConfigOptionKey("ctnhConfig"), "CTNH Machinery Configuration");
        provider.add(MainConfig.getConfigOptionKey("enableSmashingFactory"), "Enable Smashing Factory");
        provider.add(MainConfig.getConfigOptionKey("smashingFactoryMaximumProcessingCapacity"), "Smashing Factory's Max Capacity (Integer for Voltage Tier)");
        provider.add(MainConfig.getConfigOptionKey("smashingFactorySpeedMultiplier"), "Smashing Factory Speed Multiplier Compared to Maceration");
        provider.add(MainConfig.getConfigOptionKey("smashingFactoryRPMRequirement"), "Smashing Factory RPM Requirement");
        provider.add(MainConfig.getConfigOptionKey("smashingFactoryStressRequirement"), "Smashing Factory Stress Load Factor (Recipe's Voltage × This Value)");

        provider.add(MainConfig.getConfigOptionKey("enableKineticGenerator"), "Enable Kinetic Generator");
        provider.add(MainConfig.getConfigOptionKey("kineticGeneratorGeneratingBoost"), "Kinetic Generator's EU Generation Boost");
        provider.add(MainConfig.getConfigOptionKey("kineticGeneratorGeneratingRequireLubricant"), "Kinetic Generation Require Lubricant");
        provider.add(MainConfig.getConfigOptionKey("kineticGeneratorGeneratingRequireLubricantAmount"), "Kinetic Generation Require Lubricant Amount");
 
        provider.add(MainConfig.getConfigOptionKey("enableKineticSteamTurbine"), "Enable Kinetic Steam Turbine");
        provider.add(MainConfig.getConfigOptionKey("enableSteamTurbineGearBox"), "Enable Steam Turbine GearBox");
        provider.add(MainConfig.getConfigOptionKey("steamPoweredKineticGeneratingBoost"), "Steam Turbine Stress Output Boost");
        provider.add(MainConfig.getConfigOptionKey("enableSeaweedFarm"), "Enable Seaweed Farm");
        provider.add(MainConfig.getConfigOptionKey("enableWindmillControlCenter"), "Enable Windmill Control Center");
        provider.add(MainConfig.getConfigOptionKey("enableBoomOfCreate"), "Enable Boom Of Create");

    }
}
