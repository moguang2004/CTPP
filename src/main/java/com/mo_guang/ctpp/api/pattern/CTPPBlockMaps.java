package com.mo_guang.ctpp.api.pattern;

import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.antarcticgardens.cna.CNABlocks.*;

public class CTPPBlockMaps {

    public static Map<Integer, Supplier<? extends Block>> MagnetBlock = new HashMap<>();

    public static void init() {
        MagnetBlock.put(1, MAGNETITE_BLOCK);
        MagnetBlock.put(2, REDSTONE_MAGNET);
        MagnetBlock.put(3, LAYERED_MAGNET);
        MagnetBlock.put(4, FLUXUATED_MAGNETITE);
        MagnetBlock.put(5, NETHERITE_MAGNET);
    }
}
