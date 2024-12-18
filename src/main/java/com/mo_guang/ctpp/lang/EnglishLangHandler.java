package com.mo_guang.ctpp.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class EnglishLangHandler {
    public static void init(RegistrateLangProvider provider){
        provider.add("recipe.capability.su.name", "Create Stress");
        provider.add("recipe.condition.rpm.tooltip", "RPM: %d");
    }
}
