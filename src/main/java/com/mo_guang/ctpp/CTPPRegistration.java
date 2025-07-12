package com.mo_guang.ctpp;

import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.mo_guang.ctpp.core.CTPPCreativeModeTabs;
import com.tterrag.registrate.util.entry.RegistryEntry;
import java.util.function.Supplier;

import net.minecraft.world.item.CreativeModeTab;

public class CTPPRegistration {
    public static final CTPPRegistrate REGISTRATE = CTPPRegistrate.create(CTPP.MODID);
    public static <T> T conditionalRegistration(boolean enable, RegistryEntry<CreativeModeTab> originalTab, Supplier<T> registration) {
        if (!enable) {
            REGISTRATE.creativeModeTab(() -> null);
        }
        T result = registration.get();
        REGISTRATE.creativeModeTab(() -> originalTab);
        return result;
    }

    public static <T> T conditionalRegistration(boolean enable, Supplier<T> registration) {
        return conditionalRegistration(enable, CTPPCreativeModeTabs.MACHINE, registration);
    }
}
