package com.mo_guang.ctpp.client;

import com.mo_guang.ctpp.CTPP;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class CTPPPartialModels {

    public static final PartialModel CARBON_BRUSHES_COIL = block("machine/carbon_brushes/coil");
    public static final PartialModel GENERATOR_COIL = block("machine/generator_coil/generator_coil");

    private static PartialModel block(String path) {
        return PartialModel.of(CTPP.id("block/" + path));
    }

    public static void init() {}
}
