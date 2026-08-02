package com.mo_guang.ctpp.registry;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.common.block.GeneratorCoilBlock;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.ModelGen;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import java.util.function.Supplier;

import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;

public class CTPPBlocks {

    static {
        REGISTRATE.creativeModeTab(() -> CTPPCreativeModeTabs.MACHINE);
    }

    public static void init() {}

    public static BlockEntry<Block> STEEL_CASING = createCasingBlock("steel_casing", "钢机壳",
            CTPP.id("block/casings/steel_casing"));
    public static BlockEntry<Block> HEAVY_MACHINERY_CASING = createCasingBlock("heavy_machinery_casing",
            "重型钢机壳", CTPP.id("block/casings/heavy_machinery_casing"));

    public static BlockEntry<GeneratorCoilBlock> GENERATOR_COIL = REGISTRATE
            .block("generator_coil", GeneratorCoilBlock::new)
            .cnlang("发电机线圈")
            .lang("Generator Coil")
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .blockstate((ctx, prov) -> BlockStateGen.axisBlock(ctx, prov,
                    (s) -> prov.models().getExistingFile(CTPP.id("block/machine/generator_coil/generator_coil"))))
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .item()
            .transform(ModelGen.customItemModel("machine", "generator_coil", "item"))
            .register();

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
