package com.mo_guang.ctpp;

import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialEvent;
import com.mo_guang.ctpp.config.MainConfig;
import com.mo_guang.ctpp.core.CTPPCreativeModeTabs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class CommonProxy {
    public CommonProxy() {
        init();
        MainConfig.init();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    }

    public static void init() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        CTPPCreativeModeTabs.init();
        CTPPRegistration.REGISTRATE.registerRegistrate();
        CTPPDatagen.init();
    }

}
