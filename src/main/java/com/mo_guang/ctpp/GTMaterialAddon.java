package com.mo_guang.ctpp;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.common.data.GTMaterials;

public class GTMaterialAddon {
    public static void init() {
        GTMaterials.SulfuricAcid.getProperty(PropertyKey.FLUID).getQueuedBuilder(FluidStorageKeys.LIQUID)
                .block();
    }
}
