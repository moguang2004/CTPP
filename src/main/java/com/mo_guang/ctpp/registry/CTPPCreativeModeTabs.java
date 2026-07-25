package com.mo_guang.ctpp.registry;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;

import net.minecraft.world.item.CreativeModeTab;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.tterrag.registrate.util.entry.RegistryEntry;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;

public class CTPPCreativeModeTabs {

    @CN("CTPP机器")
    @EN("CTPP Machines")
    static Lang machineTitle;

    public static RegistryEntry<CreativeModeTab> MACHINE = REGISTRATE.defaultCreativeTab("machine",
            builder -> builder
                    .displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator("machine", REGISTRATE))
                    .icon(() -> CTPPMachines.KINETIC_INPUT_BOX[GTValues.LV].asStack())
                    .title(machineTitle.translate())
                    .build())
            .register();

    public static void init() {}
}
