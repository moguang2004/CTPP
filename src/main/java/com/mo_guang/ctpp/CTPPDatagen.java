package com.mo_guang.ctpp;

import com.mo_guang.ctpp.lang.ChineseLangHandler;
import com.mo_guang.ctpp.lang.EnglishLangHandler;
import com.mo_guang.ctpp.lang.RegistrateCNLangProvider;
import com.tterrag.registrate.providers.ProviderType;

public class CTPPDatagen {
    public static final ProviderType<RegistrateCNLangProvider> CNLANG = ProviderType.register("ctpp_cn_lang", (p, e) -> new RegistrateCNLangProvider(p, e.getGenerator().getPackOutput()));

    public static void init() {
        CTPPRegistration.REGISTRATE.addDataGenerator(ProviderType.LANG, EnglishLangHandler::init);
        CTPPRegistration.REGISTRATE.addDataGenerator(CNLANG, ChineseLangHandler::init);
    }
}
