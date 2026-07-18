package com.mo_guang.ctpp.registry;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;

import net.minecraft.world.level.ItemLike;

import com.mo_guang.ctpp.CTPP;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;

import java.util.function.Supplier;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;

public class CreateMaterials {

    public static Material AndesiteAlloy;
    public static Material RefinedRadiance;
    public static Material ShadowSteel;
    public static Material SLAG;
    public static Material ASURINE;
    public static Material CRIMSITE;
    public static Material OCHRUM;
    public static Material VERIDIUM;
    public static Material ASURINE_SLURRY;
    public static Material CRIMSITE_SLURRY;
    public static Material OCHRUM_SLURRY;
    public static Material VERIDIUM_SLURRY;

    public static void init() {
        AndesiteAlloy = REGISTRATE.material(CTPP.id("andesite_alloy"))
                .cnlang("安山合金")
                .color(0xA7AD9F)
                .ingot()
                .liquid()
                .iconSet(MaterialIconSet.DULL)
                .flags(GENERATE_PLATE, GENERATE_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR)
                .buildAndRegister().setFormula("(Mg3Si2H4O9)4(KNO3)Fe");

        TagPrefix.ingot.setIgnored(CreateMaterials.AndesiteAlloy, AllItems.ANDESITE_ALLOY::get);
        TagPrefix.rod.setIgnored(CreateMaterials.AndesiteAlloy, (Supplier<? extends ItemLike>) AllBlocks.SHAFT);
        TagPrefix.block.setIgnoredBlock(CreateMaterials.AndesiteAlloy, AllBlocks.ANDESITE_ALLOY_BLOCK);

        RefinedRadiance = REGISTRATE.material(CTPP.id("refined_radiance"))
                .cnlang("光辉石")
                .ingot()
                .fluid()
                .color(0xfffef9)
                .iconSet(MaterialIconSet.METALLIC)
                .appendFlags(EXT2_METAL, GENERATE_FINE_WIRE, GENERATE_GEAR, GENERATE_FRAME)
                .buildAndRegister();

        TagPrefix.ingot.setIgnored(RefinedRadiance, AllItems.REFINED_RADIANCE::get);

        ShadowSteel = REGISTRATE.material(CTPP.id("shadow_steel"))
                .cnlang("暗影钢")
                .ingot()
                .fluid()
                .color(0x35333c)
                .iconSet(MaterialIconSet.METALLIC)
                .appendFlags(EXT2_METAL, GENERATE_FINE_WIRE, GENERATE_GEAR, GENERATE_FRAME)
                .buildAndRegister();

        TagPrefix.ingot.setIgnored(ShadowSteel, AllItems.SHADOW_STEEL::get);

        SLAG = REGISTRATE.material(CTPP.id("slag"))
                .cnlang("炉渣")
                .liquid()
                .ingot()
                .color(0x9E570A)
                .buildAndRegister();

        ASURINE = REGISTRATE.material(CTPP.id("asurine"))
                .cnlang("皓蓝石")
                .dust()
                .color(0x50A0DE)
                .buildAndRegister();

        CRIMSITE = REGISTRATE.material(CTPP.id("crimsite"))
                .cnlang("绯红岩")
                .dust()
                .color(0xBF4848)
                .buildAndRegister();

        OCHRUM = REGISTRATE.material(CTPP.id("ochrum"))
                .cnlang("赭金砂")
                .dust()
                .color(0xC9AF03)
                .buildAndRegister();

        VERIDIUM = REGISTRATE.material(CTPP.id("veridium"))
                .cnlang("辉绿岩")
                .dust()
                .color(0x43B567)
                .buildAndRegister();

        ASURINE_SLURRY = REGISTRATE.material(CTPP.id("asurine_slurry"))
                .cnlang("皓蓝石浆液")
                .liquid()
                .color(0x50A0DE)
                .buildAndRegister();

        CRIMSITE_SLURRY = REGISTRATE.material(CTPP.id("crimsite_slurry"))
                .cnlang("绯红岩浆液")
                .liquid()
                .color(0xBF4848)
                .buildAndRegister();

        OCHRUM_SLURRY = REGISTRATE.material(CTPP.id("ochrum_slurry"))
                .cnlang("赭金砂浆液")
                .liquid()
                .color(0xC9AF03)
                .buildAndRegister();

        VERIDIUM_SLURRY = REGISTRATE.material(CTPP.id("veridium_slurry"))
                .cnlang("辉绿岩浆液")
                .liquid()
                .color(0x43B567)
                .buildAndRegister();
    }
}
