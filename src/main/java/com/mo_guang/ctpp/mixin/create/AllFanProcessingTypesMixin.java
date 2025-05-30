package com.mo_guang.ctpp.mixin.create;

import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.common.kinetic.fan.breathing.BreathingFanProcessingType;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AllFanProcessingTypes.class)
public abstract class AllFanProcessingTypesMixin {
    @Shadow(remap = false)
    private static <T extends FanProcessingType> T register(String id, T type) {
        return null;
    }

//    @Unique
//    private static final BreathingFanProcessingType BREATHING = register("breathing", new BreathingFanProcessingType());
}
