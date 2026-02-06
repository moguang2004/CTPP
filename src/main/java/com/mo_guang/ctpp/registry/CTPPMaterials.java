package com.mo_guang.ctpp.registry;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet;
import com.mo_guang.ctpp.CTPP;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.GENERATE_SMALL_GEAR;
import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;

public class CTPPMaterials {
    public static Material AndesiteAlloy;
    public static void init() {
        AndesiteAlloy = REGISTRATE.material(CTPP.id("andesite_alloy"))
                .cnlang("安山合金")
                .color(0xA7AD9F)
                .ingot()
                .liquid()
                .iconSet(MaterialIconSet.DULL)
                .flags(GENERATE_PLATE, GENERATE_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR)
                .buildAndRegister().setFormula("(Mg3Si2H4O9)4(KNO3)Fe");
    }
}
