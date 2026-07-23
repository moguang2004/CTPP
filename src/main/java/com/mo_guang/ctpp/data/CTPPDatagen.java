package com.mo_guang.ctpp.data;

import com.mo_guang.ctpp.data.tags.BlockTags;
import com.mo_guang.ctpp.data.tags.FluidTags;
import com.tterrag.registrate.providers.ProviderType;

import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;
public class CTPPDatagen {

    public static void init() {
        REGISTRATE.addLangProcessor();
        REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, BlockTags::init);
        REGISTRATE.addDataGenerator(ProviderType.FLUID_TAGS, FluidTags::init);
    }
}
