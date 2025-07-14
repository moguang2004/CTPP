package com.mo_guang.ctpp.config;

import com.mo_guang.ctpp.CTPP;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.format.ConfigFormats;
@Config(id = CTPP.MODID)
public class MainConfig {
    public static MainConfig INSTANCE;
    private static final Object LOCK = new Object();

    public static void init() {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = Configuration.registerConfig(MainConfig.class, ConfigFormats.yaml()).getConfigInstance();
            }
        }
    }
    public static String getConfigOptionKey(String fieldName) {
        return "config.ctpp.option." + fieldName;
    }
    @Configurable
    @Configurable.Comment("GTM's Origenal Configs")
    public GtmConfig gtmConfig = new GtmConfig();
    @Configurable
    @Configurable.Comment("CTNH's Configs")
    public CTNHConfig ctnhConfig = new CTNHConfig();

    public static class GtmConfig{
        @Configurable
        @Configurable.Comment("Is GTM KineticOutputBox Enabled?")
        public boolean enableGTMKineticOutputBox = true;
        @Configurable
        @Configurable.Comment("Torque multiplier of KineticInputBox relative to its voltage level (0.5~8.0)")
        @Configurable.DecimalRange(min = 0.5, max = 8.0)
        public float kineticInputBoxTorqueMultiplier = 4;
        @Configurable
        @Configurable.Comment("Torque multiplier of KineticOutputBox relative to its voltage level (0.5~8.0)")
        @Configurable.DecimalRange(min = 0.5, max = 8.0)
        public float kineticOutputBoxTorqueMultiplier = 4;
        @Configurable
        @Configurable.Comment("Is GTM KineticCreateMixer Enabled?")
        public boolean enableGTMKineticCreateMixer = true;
        @Configurable
        @Configurable.Comment("Processing speed Multiplier of KineticCreateMixer relative to its voltage level (0.5~4.0)")
        @Configurable.DecimalRange(min = 0.5, max = 4.0)
        public float kineticCreateMixerSpeedMultiplier = 2;
        @Configurable
        @Configurable.Comment("KineticCreateMixer's RPM Requirement (16~256)")
        @Configurable.Range(min = 16, max = 256)
        public int kineticCreateMixerRPMRequirement = 64;
        @Configurable
        @Configurable.Comment("Is GTM ElectricGearBox Enabled?")
        public boolean enableGTMElectricGearBox = true;
    }
    public static class CTNHConfig{
        @Configurable
        @Configurable.Comment("Is CTNH SmashingFactory Enabled?")
        public boolean enableSmashingFactory = true;
        @Configurable
        @Configurable.Comment("SmashingFactory's Maximum Processing Capacity (0~9 for the voltage level)")
        public int smashingFactoryMaximumProcessingCapacity = 3;
        @Configurable
        @Configurable.Comment("SmashingFactory's Speed Multiplier relative to its voltage level (0.5~4.0)")
        @Configurable.DecimalRange(min = 0.5, max = 4.0)
        public float smashingFactorySpeedMultiplier = 2;
        @Configurable
        @Configurable.Comment("SmashingFactory's RPM Requirement (16~256)")
        @Configurable.Range(min = 16, max = 256)
        public int smashingFactoryRPMRequirement = 64;
        @Configurable
        @Configurable.Comment("SmashingFactory's Stress Requirement (1~1024)")
        @Configurable.DecimalRange(min = 1.0, max = 1024.0)
        public float smashingFactoryStressRequirement = 512;
        @Configurable
        @Configurable.Comment("Is CTNH KineticGenerator Enabled?")
        public boolean enableKineticGenerator = true;
        @Configurable
        @Configurable.Comment("KineticGenerator's Generating Boost (0.5~8.0)")
        @Configurable.DecimalRange(min = 0.5, max = 8.0)
        public float kineticGeneratorGeneratingBoost = 1;
        @Configurable
        @Configurable.Comment("Does KineticGenerator require lubricant?")
        public boolean kineticGeneratorGeneratingRequireLubricant = true;
        @Configurable
        @Configurable.Comment("KineticGenerator's Require Lubricant Amount (mL)")
        public int kineticGeneratorGeneratingRequireLubricantAmount = 1;
        @Configurable
        @Configurable.Comment("Is CTNH KineticSteamTurbine Enabled?")
        public boolean enableKineticSteamTurbine = true;
        @Configurable
        @Configurable.Comment("Steam Powered's Kinetic Generating Boost (0.5~8.0)")
        @Configurable.DecimalRange(min = 0.5, max = 8.0)
        public float steamPoweredKineticGeneratingBoost = 1;
        @Configurable
        @Configurable.Comment("Is CTNH SeaweedFarm Enabled?")
        public boolean enableSeaweedFarm = true;
        @Configurable
        @Configurable.Comment("Is CTNH WindmillControlCenter Enabled?")
        public boolean enableWindmillControlCenter = true;
        @Configurable
        @Configurable.Comment("Is CTNH BoomOfCreate Enabled?")
        public boolean enableBoomOfCreate = true;
    }

}
