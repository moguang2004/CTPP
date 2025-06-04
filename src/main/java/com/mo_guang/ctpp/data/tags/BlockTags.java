package com.mo_guang.ctpp.data.tags;

import com.simibubi.create.AllTags;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class BlockTags {
    public static void init(RegistrateTagsProvider<Block> provider) {
        create(provider, AllTags.AllBlockTags.FAN_TRANSPARENT.tag, Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD);
        create(provider, CustomTags.CATALYST_BREATHING, Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD);
    }
    public static void create(RegistrateTagsProvider<Block> provider, TagKey<Block> tagKey, Block... rls) {
        var builder = provider.addTag(tagKey);
        for (Block block : rls) {
            builder.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)));
        }
    }
}
