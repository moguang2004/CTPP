package com.mo_guang.ctpp.data.tags;

import com.gregtechceu.gtceu.GTCEu;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import com.simibubi.create.AllTags;
import com.tterrag.registrate.providers.RegistrateTagsProvider;

import java.util.Objects;

public class BlockTags {

    public static void init(RegistrateTagsProvider<Block> provider) {
        create(provider, AllTags.AllBlockTags.FAN_TRANSPARENT.tag, Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD);
        create(provider, CustomTags.CATALYST_BREATHING, Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD);
        createOptional(provider, CustomTags.MAGNET_TIER_1, GTCEu.id("magnetic_iron_block"));
        createOptional(provider, CustomTags.MAGNET_TIER_2, GTCEu.id("magnetic_steel_block"));
        createOptional(provider, CustomTags.MAGNET_TIER_3);
        createOptional(provider, CustomTags.MAGNET_TIER_4);
        createOptional(provider, CustomTags.MAGNET_TIER_5);
    }

    public static void create(RegistrateTagsProvider<Block> provider, TagKey<Block> tagKey, Block... rls) {
        var builder = provider.addTag(tagKey);
        for (Block block : rls) {
            builder.addOptional(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)));
        }
    }

    public static void createOptional(RegistrateTagsProvider<Block> provider, TagKey<Block> tagKey,
                                      ResourceLocation... blockIds) {
        var builder = provider.addTag(tagKey);
        for (var blockId : blockIds) {
            builder.addOptional(blockId);
        }
    }
}
