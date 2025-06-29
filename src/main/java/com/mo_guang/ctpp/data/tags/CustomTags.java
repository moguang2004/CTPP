package com.mo_guang.ctpp.data.tags;

import com.gregtechceu.gtceu.api.data.tag.TagUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class CustomTags {
    public static TagKey<Block> CATALYST_BREATHING = TagUtil.createBlockTag("fan_catalyst/breathing");
    public static TagKey<Fluid> CATALYST_ACID_WASHING = TagUtil.createFluidTag("fan_catalyst/acid_washing");
}
