package com.mo_guang.ctpp;

import com.mo_guang.ctpp.data.tags.BlockTags;
import com.mo_guang.ctpp.data.tags.FluidTags;
import com.mo_guang.ctpp.lang.ChineseLangHandler;
import com.mo_guang.ctpp.lang.EnglishLangHandler;
import com.tterrag.registrate.providers.ProviderType;

import static tech.vixhentx.mcmod.ctnhlib.registrate.data.ProviderTypes.CNLANG;

public class CTPPDatagen {


    public static void init() {
        CTPPRegistration.REGISTRATE.addDataGenerator(ProviderType.LANG, EnglishLangHandler::init);
        CTPPRegistration.REGISTRATE.addDataGenerator(CNLANG, ChineseLangHandler::init);
        CTPPRegistration.REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, BlockTags::init);
        CTPPRegistration.REGISTRATE.addDataGenerator(ProviderType.FLUID_TAGS, FluidTags::init);
    }
}
