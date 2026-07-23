package com.mo_guang.ctpp.common.block;

import net.minecraft.world.level.block.state.BlockState;

import com.mo_guang.ctpp.data.tags.CustomTags;

public final class MagnetBlock {

    private MagnetBlock() {}

    public static int getStrength(BlockState state) {
        if (state.is(CustomTags.MAGNET_TIER_5)) return 24;
        if (state.is(CustomTags.MAGNET_TIER_4)) return 8;
        if (state.is(CustomTags.MAGNET_TIER_3)) return 4;
        if (state.is(CustomTags.MAGNET_TIER_2)) return 2;
        if (state.is(CustomTags.MAGNET_TIER_1)) return 1;
        return 0;
    }
}
