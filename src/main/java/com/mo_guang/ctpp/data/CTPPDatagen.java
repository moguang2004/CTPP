package com.mo_guang.ctpp.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;

import com.mo_guang.ctpp.data.tags.BlockTags;
import com.mo_guang.ctpp.data.tags.FluidTags;
import com.mo_guang.ctpp.data.tags.ItemTags;
import com.tterrag.registrate.providers.ProviderType;

import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;

public class CTPPDatagen {

    public static void init() {
        REGISTRATE.addLangProcessor();
        REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, BlockTags::init);
        REGISTRATE.addDataGenerator(ProviderType.FLUID_TAGS, FluidTags::init);
        REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, ItemTags::init);
    }

    public static void addToolboxData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(event.includeClient(), new ToolboxBlockstates(generator.getPackOutput()));
        generator.addProvider(event.includeServer(), new CuriosTags(generator.getPackOutput()));
    }
}
