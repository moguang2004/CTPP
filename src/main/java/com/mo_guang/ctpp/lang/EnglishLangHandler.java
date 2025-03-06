package com.mo_guang.ctpp.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.mo_guang.ctpp.config.MainConfig;

public class EnglishLangHandler {
    public static void init(RegistrateLangProvider provider){
        provider.add("recipe.capability.su.name", "Create Stress");
        provider.add("recipe.condition.rpm.tooltip", "RPM: %d");
        provider.add("ctpp.stress_input","Stress Input：%dsu");
        provider.add("ctpp.stress_output","Stress Output：%dsu");
        provider.add("multiblock.ctpp.kinetic_generator", "Generator Rate：%d/%d EU/t");
        provider.add("multiblock.ctpp.kinetic_generator.efficiency", "Coil Efficiency：%d%%");
        provider.add("multiblock.ctpp.kinetic_steam_turbine", "Total Turbine Efficiency：%d%%");
        provider.add("multiblock.ctpp.kinetic_steam_turbine_output", "Kinetic Output：%dsu");
        provider.add("kinetic_output", "A machine that can output kinetic");
        provider.add("rotor_holder_upgrade", "Each level of the rotor holder upgrade increases turbine efficiency by §610%§r");
        provider.add("steam_up_hv_loss", "Steam-type machines lose 10% efficiency for each voltage tier above §6HV§r");
        provider.add("multiblock.ctpp.windmill_control_center1", "Number of controlled windmills: %d");
        provider.add("multiblock.ctpp.windmill_control_center2", "Total stress of controlled windmills: %dsu");
        provider.add("multiblock.ctpp.windmill_control_center.efficiency", "Total energy efficiency: %d%%");
        provider.add("multiblock.ctpp.windmill_control_center.output", "Total stress output: §a%dsu§r");
        provider.add("windmill_control_center", "Windmill Master Control! The more, the stronger!");
        provider.add("ctpp.windmill_control_center.mechanism", "Detects windmill bearings within a radius of 16 blocks around the multiblock. The total stress output is calculated as: Number of windmill bearings × (Total stress output of surrounding windmills + 512). §4Can control up to 16 windmills!§r");
        provider.add("ctpp.windmill_control_center.output", "Ensure that the stress output container is large enough to handle all the stress, otherwise the machine will not work.");
        provider.add("boom_of_create", "Art is an explosion!");
        provider.add("ctpp.boom_of_create.basic", "The Explosive Vortex Stress Induction System (EVSIS) using explosives and a small amount of electricity to generate massive stress.");
        provider.add("ctpp.boom_of_create.coolant", "Electricity consumption gradually decreases during continuous operation, with a minimum of 0.");
        provider.add("ctpp.boom_of_create.overclock", "Like the Three Gorges Dam, sufficient stress output capacity is required for it to start working.");
        provider.add("ctpp.boom_of_create.safe", "§aThe Path to Stress Ascension§r");
        provider.add("ctpp.kinetic_generator.basic", "The base conversion efficiency from stress to EU is 160:1");
        provider.add("ctpp.kinetic_generator.core", "Using a Block of Graphene as the core increases efficiency by §a10%§r");
        provider.add("ctpp.kinetic_generator.extrict", "Requires at least 512 SU to activate the machine");
        provider.add("ctpp.kinetic_generator.upgrade", "Each level of coil tier improves total efficiency by §a10%§r (initial value: 90%)");
        provider.add("kinetic_overclock", "Has a stress overclocking mechanism that determines parallelism based on the total input stress. The effects vary based on input speed:\nSpeed < 64: No effect\nSpeed < 128: 25% time reduction\nSpeed < 256: One overclocking opportunity\nSpeed < 512: One lossless overclocking opportunity");
        provider.add("actual_speed", "The input speed is based on the highest-speed stress chamber.");
        provider.add("ctpp.kinetic_workable_multiblock_machine.speed", "Input speed: %drpm");
        provider.add("ctpp.kinetic_workable_multiblock_machine.parallel", "Parallelism: %d");
        provider.add("ctpp.kinetic_workable_multiblock_machine.null", "Status: None");
        provider.add("ctpp.kinetic_workable_multiblock_machine.reduction", "Status: Recipe time reduction x0.75");
        provider.add("ctpp.kinetic_workable_multiblock_machine.overclock", "Status: Overclocked");
        provider.add("ctpp.kinetic_workable_multiblock_machine.perfect_overclock", "Status: Lossless overclocked");

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
