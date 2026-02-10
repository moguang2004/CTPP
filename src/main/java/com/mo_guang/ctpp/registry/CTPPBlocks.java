package com.mo_guang.ctpp.registry;

import com.mo_guang.ctpp.CTPP;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;

public class CTPPBlocks {
    public static void init() {}
    public static BlockEntry<Block> STEEL_CASING = createCasingBlock("steel_casing", "钢机壳", CTPP.id("block/casings/steel_casing"));
    public static BlockEntry<Block> HEAVY_MACHINERY_CASING = createCasingBlock("heavy_machinery_casing", "重型机械机壳", CTPP.id("block/casings/heavy_machinery_casing"));
    public static BlockEntry<Block> createCasingBlock(String name, String cnName, ResourceLocation texture) {
        return createCasingBlock(name, cnName, Block::new, texture, () -> Blocks.IRON_BLOCK,
                () -> RenderType::cutoutMipped);
    }
    public static BlockEntry<Block> createCasingBlock(String name,
                                                      String cnName,
                                                      NonNullFunction<BlockBehaviour.Properties, Block> blockSupplier,
                                                      ResourceLocation texture,
                                                      NonNullSupplier<? extends Block> properties,
                                                      Supplier<Supplier<RenderType>> type) {
        return REGISTRATE.block(name, blockSupplier)
                .cnlang(cnName)
                .initialProperties(properties)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .addLayer(type)
                .blockstate((ctx, prov) -> {
                    prov.simpleBlock(ctx.getEntry(), prov.models().cubeAll(name, texture));
                })
                .tag(TagKey.create(BuiltInRegistries.BLOCK.key(),
                        ResourceLocation.tryBuild("forge", "mineable/wrench")), BlockTags.MINEABLE_WITH_PICKAXE)
                .item(BlockItem::new)
                .build()
                .register();
    }
}
