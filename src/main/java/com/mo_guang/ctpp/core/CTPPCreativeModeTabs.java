package com.mo_guang.ctpp.core;

import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.common.data.CTPPMachines;
import com.simibubi.create.AllItems;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;

import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;

public class CTPPCreativeModeTabs {
    public static RegistryEntry<CreativeModeTab> MACHINE = REGISTRATE.defaultCreativeTab("machine",
                    builder -> builder.displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator("machine", REGISTRATE))
                            .icon(Items.GRASS_BLOCK::getDefaultInstance)
                            .title(REGISTRATE.addLang("itemGroup", CTPP.id("machine"), "CTPP Machines"))
                            .build())
            .register();

    public static void init() {

    }
}
