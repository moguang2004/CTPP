package com.mo_guang.ctpp.data;

import com.mo_guang.ctpp.CTPPRegistration;
import com.mo_guang.ctpp.data.tags.BlockTags;
import com.mo_guang.ctpp.data.tags.FluidTags;
import com.mo_guang.ctpp.lang.ChineseLangHandler;
import com.mo_guang.ctpp.lang.EnglishLangHandler;
import com.tterrag.registrate.providers.ProviderType;

import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;
import static tech.vixhentx.mcmod.ctnhlib.registrate.data.ProviderTypes.CNLANG;

public class CTPPDatagen {

    public static void init() {
        REGISTRATE.addLangProcessor();
        REGISTRATE.addDataGenerator(ProviderType.LANG, EnglishLangHandler::init);
        REGISTRATE.addDataGenerator(CNLANG, ChineseLangHandler::init);
        REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, BlockTags::init);
        REGISTRATE.addDataGenerator(ProviderType.FLUID_TAGS, FluidTags::init);
    }
}
