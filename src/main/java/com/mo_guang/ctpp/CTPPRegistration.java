package com.mo_guang.ctpp;

import net.minecraft.world.item.CreativeModeTab;

import com.mo_guang.ctpp.registry.CTPPCreativeModeTabs;
import com.tterrag.registrate.util.entry.RegistryEntry;

import java.util.function.Supplier;

public class CTPPRegistration {

    public static final CTPPRegistrate REGISTRATE = CTPPRegistrate.create(CTPP.MODID);

    public static <T> T conditionalRegistration(boolean enable, RegistryEntry<CreativeModeTab> originalTab,
                                                Supplier<T> registration) {
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
