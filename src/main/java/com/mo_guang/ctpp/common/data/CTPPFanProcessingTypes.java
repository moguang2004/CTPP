package com.mo_guang.ctpp.common.data;

import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.common.kinetic.fan.breathing.BreathingFanProcessingType;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingTypeRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CTPPFanProcessingTypes {
    private static final DeferredRegister<FanProcessingType> FAN_TYPES = DeferredRegister
            .create(CreateRegistries.FAN_PROCESSING_TYPE, CTPP.MODID);
    public static RegistryObject<BreathingFanProcessingType> BREATHING = FAN_TYPES
            .register("breathing", BreathingFanProcessingType::new);
    public static void register(IEventBus modBus) {
        FAN_TYPES.register(modBus);
    }
}
