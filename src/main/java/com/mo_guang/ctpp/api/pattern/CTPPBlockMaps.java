package com.mo_guang.ctpp.api.pattern;

import com.gregtechceu.gtceu.GTCEu;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CTPPBlockMaps {

    public static Map<Integer, Supplier<? extends Block>> MagnetBlock = new HashMap<>();

    public static void init() {
        MagnetBlock.put(1, () -> ForgeRegistries.BLOCKS.getValue(GTCEu.id("magnetic_iron_block")));
        MagnetBlock.put(2, () -> ForgeRegistries.BLOCKS.getValue(GTCEu.id("magnetic_steel_block")));
    }
}
