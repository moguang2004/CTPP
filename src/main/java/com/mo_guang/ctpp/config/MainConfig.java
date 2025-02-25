package com.mo_guang.ctpp.config;

import com.mo_guang.ctpp.CTPP;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CTPP.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MainConfig {
    public static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec SERVER_SPEC;
//GTM复原设置
    public static final ForgeConfigSpec.BooleanValue ENABLE_GTM_KINETIC_OUPUT_BOX;
    public static final ForgeConfigSpec.DoubleValue KINETIC_INPUT_BOX_TORQUE_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue KINETIC_OUTPUT_BOX_TORQUE_MULTIPLIER;
    
    public static final ForgeConfigSpec.BooleanValue ENABLE_GTM_CREATE_MIXER;
    public static final ForgeConfigSpec.DoubleValue GTM_CREATE_MIXER_SPEED_MULTIPLIER;
    public static final ForgeConfigSpec.IntValue GTM_CREATE_MIXER_RPM_REQUIREMENT;

    public static final ForgeConfigSpec.BooleanValue ENABLE_GTM_ELECTRIC_GEAR_BOX;
//CTHN设置
    public static final ForgeConfigSpec.BooleanValue ENABLE_SMASHING_FACTORY;
    public static final ForgeConfigSpec.IntValue SMASHING_FACTORY_MAXIMUM_PROCESSING_CAPACITY;
    public static final ForgeConfigSpec.DoubleValue SMASHING_FACTORY_SPEED_MULTIPLIER;
    public static final ForgeConfigSpec.IntValue SMASHING_FACTORY_RPM_REQUIREMENT;
    public static final ForgeConfigSpec.IntValue SMASHING_FACTORY_STRESS_REQUIREMENT;

    public static final ForgeConfigSpec.BooleanValue ENABLE_KINETIC_GENERATOR;
    public static final ForgeConfigSpec.DoubleValue KINETIC_GENERATOR_GENERATING_BOOST;
    public static final ForgeConfigSpec.BooleanValue KINETIC_GENERATOR_GENERATING_REQUIRE_LUBRICANT;
    public static final ForgeConfigSpec.IntValue KINETIC_GENERATOR_GENERATING_REQUIRE_LUBRICANT_AMOUNT;

    public static final ForgeConfigSpec.BooleanValue ENABLE_KINETIC_STEAM_TURBINE;
    public static final ForgeConfigSpec.DoubleValue KINETIC_STEAM_TURBINE_GENERATING_BOOST;

    public static final ForgeConfigSpec.BooleanValue ENABLE_SEAWEED_FARM;

    public static final ForgeConfigSpec.BooleanValue ENABLE_WINDMILL_CONTROL_CENTER;

    public static final ForgeConfigSpec.BooleanValue ENABLE_BOOM_OF_CREATE;

    static {
        SERVER_BUILDER.comment("Server Configs").push("server_configs");
            SERVER_BUILDER.comment("GTM's Origenal Configs").push("gtm_configs");
                ENABLE_GTM_KINETIC_OUPUT_BOX = SERVER_BUILDER.comment("Is GTM KineticOutputBox Enabled?")
                    .define("enableGTMKineticOutputBox", true);
                KINETIC_INPUT_BOX_TORQUE_MULTIPLIER = SERVER_BUILDER.comment("Torque multiplier of KineticInputBox relative to its voltage level (0.5~8.0)")
                    .defineInRange("kineticInputBoxTorqueMultiplier", 4.0, 0.5, 8.0); 
                KINETIC_OUTPUT_BOX_TORQUE_MULTIPLIER = SERVER_BUILDER.comment("Torque multiplier of KineticOutputBox relative to its voltage level (0.5~8.0)")
                    .defineInRange("kineticOutputBoxTorqueMultiplier", 4.0, 0.5, 8.0);
                ENABLE_GTM_CREATE_MIXER = SERVER_BUILDER.comment("Is GTM KineticCreateMixer Enabled?")
                    .define("enableGTMKineticCreateMixer", true);
                GTM_CREATE_MIXER_SPEED_MULTIPLIER = SERVER_BUILDER.comment("Processing speed Multiplier of KineticCreateMixer relative to its voltage level (0.5~4.0)")
                    .defineInRange("kineticCreateMixerSpeedMultiplier", 2.0, 0.5, 4.0);
                GTM_CREATE_MIXER_RPM_REQUIREMENT = SERVER_BUILDER.comment("KineticCreateMixer's RPM Requirement (16~256)")
                    .defineInRange("kineticCreateMixerRPMRequirement", 64, 16, 256);
                ENABLE_GTM_ELECTRIC_GEAR_BOX = SERVER_BUILDER.comment("Is GTM ElectricGearBox Enabled?")
                    .define("enableGTMElectricGearBox", true);
            SERVER_BUILDER.pop();

            SERVER_BUILDER.comment("CTNH's Configs").push("ctnh_configs");
                ENABLE_SMASHING_FACTORY = SERVER_BUILDER.comment("Is CTNH SmashingFactory Enabled?")
                    .define("enableSmashingFactory", true); 
                SMASHING_FACTORY_MAXIMUM_PROCESSING_CAPACITY = SERVER_BUILDER.comment("SmashingFactory's Maximum Processing Capacity (0~9 for the voltage level)")
                    .defineInRange("smashingFactoryMaximumProcessingCapacity", 1, 0, 9);
                SMASHING_FACTORY_SPEED_MULTIPLIER = SERVER_BUILDER.comment("SmashingFactory's Speed Multiplier relative to its voltage level (0.5~4.0)")
                    .defineInRange("smashingFactorySpeedMultiplier", 2.0, 0.5, 4.0);
                SMASHING_FACTORY_RPM_REQUIREMENT = SERVER_BUILDER.comment("SmashingFactory's RPM Requirement (16~256)")
                    .defineInRange("smashingFactoryRPMRequirement", 64, 16, 256);
                SMASHING_FACTORY_STRESS_REQUIREMENT = SERVER_BUILDER.comment("SmashingFactory's Stress Requirement (1~1024)")
                    .defineInRange("smashingFactoryStressRequirement", 512, 1, 1024);

                ENABLE_KINETIC_GENERATOR = SERVER_BUILDER.comment("Is CTNH KineticGenerator Enabled?")
                    .define("enableKineticGenerator", true);
                KINETIC_GENERATOR_GENERATING_BOOST = SERVER_BUILDER.comment("KineticGenerator's Generating Boost (0.5~8.0)")
                    .defineInRange("kineticGeneratorGeneratingBoost", 2.0, 0.5, 8.0);
                KINETIC_GENERATOR_GENERATING_REQUIRE_LUBRICANT = SERVER_BUILDER.comment("Does KineticGenerator require lubricant?")
                    .define("kineticGeneratorGeneratingRequireLubricant", true);
                KINETIC_GENERATOR_GENERATING_REQUIRE_LUBRICANT_AMOUNT = SERVER_BUILDER.comment("KineticGenerator's Require Lubricant Amount (mL)")
                    .defineInRange("kineticGeneratorGeneratingRequireLubricantAmount", 1, 1, 10000);

                ENABLE_KINETIC_STEAM_TURBINE = SERVER_BUILDER.comment("Is CTNH KineticSteamTurbine Enabled?")
                    .define("enableKineticSteamTurbine", true);
                KINETIC_STEAM_TURBINE_GENERATING_BOOST = SERVER_BUILDER.comment("KineticSteamTurbine's Generating Boost (0.5~8.0)")
                    .defineInRange("kineticSteamTurbineGeneratingBoost", 1.0, 0.5, 8.0);

                ENABLE_SEAWEED_FARM = SERVER_BUILDER.comment("Is CTNH SeaweedFarm Enabled?")
                    .define("enableSeaweedFarm", true);

                ENABLE_WINDMILL_CONTROL_CENTER = SERVER_BUILDER.comment("Is CTNH WindmillControlCenter Enabled?")
                    .define("enableWindmillControlCenter", true);

                ENABLE_BOOM_OF_CREATE = SERVER_BUILDER.comment("Is CTNH BoomOfCreate Enabled?")
                    .define("enableBoomOfCreate", true);
            SERVER_BUILDER.pop();
        SERVER_BUILDER.pop();
        SERVER_SPEC = SERVER_BUILDER.build();
    }

}
