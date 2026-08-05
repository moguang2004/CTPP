package com.mo_guang.ctpp.api.pattern;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CTPPBlockMaps {

    public static Map<Integer, Supplier<? extends Block>> MagnetBlock = new HashMap<>();

    public static void init() {
        MagnetBlock.put(1, () -> ChemicalHelper.getBlock(TagPrefix.block, GTMaterials.IronMagnetic));
        MagnetBlock.put(2, () -> ChemicalHelper.getBlock(TagPrefix.block, GTMaterials.SteelMagnetic));
    }
}
