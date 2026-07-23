package com.mo_guang.ctpp.data.tags;

import com.gregtechceu.gtceu.api.data.tag.TagUtil;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class CustomTags {

    public static TagKey<Block> CATALYST_BREATHING = TagUtil.createBlockTag("fan_catalyst/breathing");
    public static TagKey<Block> MAGNET_TIER_1 = TagUtil.createBlockTag("magnet/tier_1");
    public static TagKey<Block> MAGNET_TIER_2 = TagUtil.createBlockTag("magnet/tier_2");
    public static TagKey<Block> MAGNET_TIER_3 = TagUtil.createBlockTag("magnet/tier_3");
    public static TagKey<Block> MAGNET_TIER_4 = TagUtil.createBlockTag("magnet/tier_4");
    public static TagKey<Block> MAGNET_TIER_5 = TagUtil.createBlockTag("magnet/tier_5");
    public static TagKey<Fluid> CATALYST_ACID_WASHING = TagUtil.createFluidTag("fan_catalyst/acid_washing");
}
