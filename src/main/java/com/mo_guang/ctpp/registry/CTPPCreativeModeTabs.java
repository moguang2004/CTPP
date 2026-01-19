package com.mo_guang.ctpp.registry;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;
import com.mo_guang.ctpp.CTPP;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.world.item.CreativeModeTab;

import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;

public class CTPPCreativeModeTabs {
    public static RegistryEntry<CreativeModeTab> MACHINE = REGISTRATE.defaultCreativeTab("machine",
                    builder -> builder.displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator("machine", REGISTRATE))
                            .icon(() -> CTPPMachines.KINETIC_INPUT_BOX[GTValues.LV].asStack())
                            .title(REGISTRATE.addLang("itemGroup", CTPP.id("machine"), "CTPP Machines"))
                            .build())
            .register();

    public static void init() {

    }
}
