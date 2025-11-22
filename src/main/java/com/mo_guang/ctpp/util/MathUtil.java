package com.mo_guang.ctpp.util;

import com.simibubi.create.infrastructure.config.AllConfigs;

public class MathUtil {
    public static float rpm2rads(float rpm) {
        return rpm / AllConfigs.server().kinetics.maxRotationSpeed.get() * 360 / 20 * 4;
    }
}
