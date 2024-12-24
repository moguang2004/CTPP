package com.mo_guang.ctpp.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

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
    }
}
