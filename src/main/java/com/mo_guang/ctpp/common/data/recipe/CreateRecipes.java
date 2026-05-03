package com.mo_guang.ctpp.common.data.recipe;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import com.mo_guang.ctpp.registry.CTPPItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import com.google.gson.JsonObject;
import com.mo_guang.ctpp.common.recipe.builder.create.*;
import com.mo_guang.ctpp.registry.CTPPMaterials;

import java.util.Objects;
import java.util.function.Consumer;

public class CreateRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        // Crushing/milling for gtceu ingots -> dusts
        String[] ingots = new String[] { "tin", "bronze", "zinc", "brass", "nickel", "lead" };
        for (String i : ingots) {
            ItemStack ingot = itemStack("gtceu:" + i + "_ingot");
            ItemStack dust = itemStack("gtceu:" + i + "_dust");
            if (!ingot.isEmpty() && !dust.isEmpty()) {
                CrushingRecipeBuilder.builder("crushing_" + i + "_dust").input(ingot).output(dust).save(provider);
                MillingRecipeBuilder.builder("milling_" + i + "_dust").input(ingot).output(dust).save(provider);
            }
        }

        // andesite_alloy
        ItemStack aaIngot = ChemicalHelper.get(TagPrefix.ingot, CTPPMaterials.AndesiteAlloy);
        ItemStack aaDust = ChemicalHelper.get(TagPrefix.dust, CTPPMaterials.AndesiteAlloy);
        if (!aaIngot.isEmpty() && !aaDust.isEmpty()) {
            CrushingRecipeBuilder.builder("crushing_andesite_alloy_dust").input(aaIngot).output(aaDust).save(provider);
            MillingRecipeBuilder.builder("milling_andesite_alloy_dust").input(aaIngot).output(aaDust).save(provider);
        }

        // coke
        ItemStack cokeGem = itemStack("gtceu:coke_gem");
        ItemStack cokeDust = ChemicalHelper.get(TagPrefix.dust, GTMaterials.Coke);
        if (!cokeGem.isEmpty() && !cokeDust.isEmpty()) {
            CrushingRecipeBuilder.builder("crushing_coke_dust").input(cokeGem).output(cokeDust).save(provider);
            MillingRecipeBuilder.builder("milling_coke_dust").input(cokeGem).output(cokeDust).save(provider);
        }

        // copper/iron/gold gtceu -> minecraft ingots
        String[] vanillaIngots = new String[] { "copper", "iron", "gold" };
        for (String i : vanillaIngots) {
            ItemStack gtIngot = itemStack("gtceu:" + i + "_ingot");
            ItemStack mcIngot = itemStack("minecraft:" + i + "_ingot");
            if (!gtIngot.isEmpty() && !mcIngot.isEmpty()) {
                CrushingRecipeBuilder.builder("crushing_gtceu_" + i + "_to_mc").input(gtIngot).output(mcIngot)
                        .save(provider);
                MillingRecipeBuilder.builder("milling_gtceu_" + i + "_to_mc").input(gtIngot).output(mcIngot)
                        .save(provider);
            }
        }

        // Cutting: shaft from andesite_alloy_ingot
        ItemStack shaft2 = itemStack("create:shaft");
        if (!aaIngot.isEmpty() && !shaft2.isEmpty()) {
            // produce 2x shaft
            CuttingRecipeBuilder.builder("cutting_shaft_from_andesite_alloy_ingot").input(aaIngot)
                    .result(new ItemStack(shaft2.getItem(), 2)).save(provider);
        }

        // Cutting plates -> single_wire (produce 2x)
        String[] plates = new String[] { "copper", "iron", "gold", "lead", "nickel", "tin", "silver", "annealed_copper",
                "cupronickel", "steel", "red_alloy", "mana_steel", "conductive_alloy" };
        for (String p : plates) {
            ItemStack plate = itemStack("gtceu:" + p + "_plate");
            ItemStack wire = itemStack("gtceu:" + p + "_single_wire");
            if (!plate.isEmpty() && !wire.isEmpty()) {
                CuttingRecipeBuilder.builder("cutting_" + p + "_to_single_wire").input(plate)
                        .result(new ItemStack(wire.getItem(), 2)).save(provider);
            }
        }

        // Mechanical crafting (register with basic ingredient set)
        // encased fan - full 5x5 pattern from create.js
        MechanicalCraftingRecipeBuilder.builder("encased_fan")
                .pattern("ABCBA", "DDEDD", "AFBFA", "AFBFA", "GGHGG")
                .key('A', item("create:andesite_casing"))
                .key('B', item("create:shaft"))
                .key('C', item("minecraft:redstone_torch"))
                .key('D', ChemicalHelper.get(TagPrefix.rod, GTMaterials.WroughtIron))
                .key('E', item("minecraft:redstone"))
                .key('F', ChemicalHelper.get(TagPrefix.plate, GTMaterials.WroughtIron))
                .key('G', item("minecraft:iron_bars"))
                .key('H', item("create:propeller"))
                .output(new ItemStack(item("create:encased_fan"))).save(provider);

        // crushing wheel (2x) - 5x5 pattern from create.js
        MechanicalCraftingRecipeBuilder.builder("crushing_wheel")
                .pattern(" AAA ", "ABCBA", "ACDCA", "ABCBA", " AAA ")
                .key('A', ChemicalHelper.get(TagPrefix.plate, GTMaterials.WroughtIron))
                .key('B', ChemicalHelper.get(TagPrefix.plate, CTPPMaterials.AndesiteAlloy))
                .key('C', ChemicalHelper.get(TagPrefix.rod, GTMaterials.Iron))
                .key('D', ChemicalHelper.get(TagPrefix.gear, GTMaterials.WroughtIron))
                .output(new ItemStack(item("create:crushing_wheel"), 2)).save(provider);

        // generator coil - 5x5 pattern from create.js
        MechanicalCraftingRecipeBuilder.builder("generator_coil")
                .pattern("  A  ", " BCB ", "ACDCA", " BCB ", "  A  ")
                .key('A', ChemicalHelper.get(TagPrefix.wireFine, GTMaterials.Copper))
                .key('B', ChemicalHelper.get(TagPrefix.plate, CTPPMaterials.AndesiteAlloy))
                .key('C', item("gtceu:resin_printed_circuit_board"))
                .key('D', item("create:precision_mechanism"))
                .output(new ItemStack(item("create_new_age:generator_coil"))).save(provider);

        // large water wheel - 5x5 pattern from create.js
        MechanicalCraftingRecipeBuilder.builder("large_water_wheel")
                .pattern(" AAA ", "ABCBA", "ACDCA", "ABCBA", " AAA ")
                .key('A', item("gtceu:treated_wood_planks"))
                .key('B', item("gtceu:treated_wood_screw"))
                .key('C', item("gtceu:gold_ring"))
                .key('D', item("create:water_wheel"))
                .output(new ItemStack(item("create:large_water_wheel"))).save(provider);

        // portal block (from server_scripts create.js)
        MechanicalCraftingRecipeBuilder.builder("javd_portal_block")
                .pattern("AAAAA", "ABCBA", "ACDCA", "ABCBA", "AAAAA")
                .key('A', item("gtceu:double_shadow_steel_plate"))
                .key('B', tag("gtceu:circuits/hv"))
                .key('C', ChemicalHelper.get(TagPrefix.plateDouble, GTMaterials.BlackSteel))
                .key('D', item("gtceu:hv_machine_casing"))
                .output(new ItemStack(item("javd:portal_block"))).save(provider);

        // martial morality eye (7x7 pattern from server_scripts create.js)
        MechanicalCraftingRecipeBuilder.builder("martial_morality_eye")
                .pattern("ABCCCBA", "BADCDAB", "BADCDAB", "BAAEAAB", "BADCDAB", "BADCDAB", "ABCCCBA")
                .key('A', item("gtceu:lp_steam_rock_crusher"))
                .key('B', item("ctpp:heavy_machinery_casing"))
                .key('C', item("create:mechanical_drill"))
                .key('D', item("createoreexcavation:drilling_machine"))
                .key('E', item("gtceu:hp_steam_rock_crusher"))
                .output(new ItemStack(item("ctnhcore:martial_morality_eye"))).save(provider);

        // compacting: many plates
        String[] compactIngots = new String[] { "iron", "copper", "gold", "zinc", "brass", "wrought_iron", "steel",
                "rubber", "red_alloy", "andesite_alloy", "bronze", "potin", "nickel", "tin", "mana_steel" };
        for (String i : compactIngots) {
            ItemStack plate = itemStack("gtceu:" + i + "_plate");
            if (!plate.isEmpty()) {
                // input tag #forge:ingots/<i> -> fallback to single ingot item if exists
                Item ing = item("#forge:ingots/" + i);
                if (ing == null) {
                    // try gtceu ingot
                    ItemStack maybeIngot = itemStack("gtceu:" + i + "_ingot");
                    if (!maybeIngot.isEmpty()) CompactingRecipeBuilder.builder("compacting_" + i + "_plate")
                            .input(maybeIngot).output(plate).save(provider);
                } else {
                    // cannot easily represent tag here, skip explicit tag form
                    CompactingRecipeBuilder.builder("compacting_" + i + "_plate").input(plate).output(plate)
                            .save(provider);
                }
            }
        }

        // pressing rings
        if (!ChemicalHelper.get(TagPrefix.rod, GTMaterials.Gold).isEmpty() &&
                !ChemicalHelper.get(TagPrefix.ring, GTMaterials.Gold).isEmpty())
            CompactingRecipeBuilder.builder("pressing_gold_ring")
                    .input(ChemicalHelper.get(TagPrefix.rod, GTMaterials.Gold))
                    .output(ChemicalHelper.get(TagPrefix.ring, GTMaterials.Gold)).save(provider);
        if (!ChemicalHelper.get(TagPrefix.rod, GTMaterials.Iron).isEmpty() &&
                !ChemicalHelper.get(TagPrefix.ring, GTMaterials.Iron).isEmpty())
            CompactingRecipeBuilder.builder("pressing_iron_ring")
                    .input(ChemicalHelper.get(TagPrefix.rod, GTMaterials.Iron))
                    .output(ChemicalHelper.get(TagPrefix.ring, GTMaterials.Iron)).save(provider);
        if (!ChemicalHelper.get(TagPrefix.rod, GTMaterials.Copper).isEmpty() &&
                !ChemicalHelper.get(TagPrefix.ring, GTMaterials.Copper).isEmpty())
            CompactingRecipeBuilder.builder("pressing_copper_ring")
                    .input(ChemicalHelper.get(TagPrefix.rod, GTMaterials.Copper))
                    .output(ChemicalHelper.get(TagPrefix.ring, GTMaterials.Copper)).save(provider);

        // Mixing recipes from create.js
        // 8x potin dust
        ItemStack copperDust = ChemicalHelper.get(TagPrefix.dust, GTMaterials.Copper);
        ItemStack tinDust = ChemicalHelper.get(TagPrefix.dust, GTMaterials.Tin);
        ItemStack leadDust = ChemicalHelper.get(TagPrefix.dust, GTMaterials.Lead);
        ItemStack potinDust = ChemicalHelper.get(TagPrefix.dust, GTMaterials.Potin);
        if (!copperDust.isEmpty() && !tinDust.isEmpty() && !leadDust.isEmpty() && !potinDust.isEmpty()) {
            MixingRecipeBuilder.builder("potin_from_dusts").input(new ItemStack(copperDust.getItem(), 6))
                    .input(new ItemStack(tinDust.getItem(), 2)).input(leadDust)
                    .output(new ItemStack(potinDust.getItem(), 8)).save(provider);
        }

        // rose quartz from quartz + redstone
        ItemStack quartz = itemStack("minecraft:quartz");
        ItemStack redstone = itemStack("minecraft:redstone");
        ItemStack roseQuartz = itemStack("create:rose_quartz");
        if (!quartz.isEmpty() && !redstone.isEmpty() && !roseQuartz.isEmpty()) {
            MixingRecipeBuilder.builder("rose_quartz_from_quartz_redstone").input(quartz)
                    .input(new ItemStack(redstone.getItem(), 4)).output(roseQuartz).save(provider);
        }

        // rose quartz from rose quartz chunk + water
        ItemStack roseChunk = itemStack("biomesoplenty:rose_quartz_chunk");
        if (!roseChunk.isEmpty() && !roseQuartz.isEmpty()) {
            MixingRecipeBuilder.builder("rose_quartz_from_chunk_and_water")
                    .result(itemStack("create:rose_quartz"))
                    .input(itemStack("biomesoplenty:rose_quartz_chunk"))
                    .inputFluid("minecraft:water", 100)
                    .save(provider);
        }

        // concrete fluid result mixing (gtceu:concrete)
        MixingRecipeBuilder.builder("mixing_concrete_from_dusts")
                .input(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Stone))
                .input(ChemicalHelper.get(TagPrefix.dust, GTMaterials.QuartzSand))
                .input(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Clay))
                .input(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Calcite))
                .inputFluid("minecraft:water", 1000)
                .resultFluid("gtceu:concrete", 1000)
                .save(provider);

        // andesite alloy dust from iron fluid + dusts
        MixingRecipeBuilder.builder("andesite_alloy_from_iron")
                .result(new ItemStack(ChemicalHelper.get(TagPrefix.dust, CTPPMaterials.AndesiteAlloy).getItem(), 2))
                .inputFluid("gtceu:iron", 144)
                .input(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Andesite))
                .save(provider);

        // stem cells from growth medium fluid + animal excreta
        MixingRecipeBuilder.builder("stem_cells_from_growth_medium")
                .result(itemStack("gtceu:stem_cells"))
                .inputFluid("gtceu:simple_growth_medium", 144)
                .input(itemStack("ctnhcore:animal_excreta"))
                .save(provider);

        // treated wood planks from creosote + planks tag
        MixingRecipeBuilder.builder("treated_wood_planks_from_creosote")
                .result(new ItemStack(itemStack("gtceu:treated_wood_planks").getItem(), 2))
                .inputFluid("gtceu:creosote", 250)
                .input(tag("minecraft:planks"))
                .save(provider);

        // red alloy dust
        MixingRecipeBuilder.builder("red_alloy_dust")
                .result(ChemicalHelper.get(TagPrefix.dust, GTMaterials.RedAlloy))
                .input(itemStack("minecraft:redstone"))
                .input(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Copper))
                .save(provider);

        // andesite_alloy_dust with chance secondary
        MixingRecipeBuilder.builder("andesite_alloy_dust_with_secondary")
                .input(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Andesite))
                .input(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Iron))
                .result(ChemicalHelper.get(TagPrefix.dust, CTPPMaterials.AndesiteAlloy))
                .result(ChemicalHelper.get(TagPrefix.dust, CTPPMaterials.AndesiteAlloy), 0.3)
                .save(provider);

        // steel precursor mixing (wrought iron + coke or charcoal) - simplified as two recipes
        MixingRecipeBuilder.builder("steel_precursor_from_wrought_and_coke")
                .result(new ItemStack(itemStack("gtceu:steel_precursor_dust").getItem(), 8))
                .input(new ItemStack(ChemicalHelper.get(TagPrefix.dust, GTMaterials.WroughtIron).getItem(), 8))
                .input(new ItemStack(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Coke).getItem(), 3))
                .save(provider);
        MixingRecipeBuilder.builder("steel_precursor_from_wrought_and_charcoal")
                .result(new ItemStack(itemStack("gtceu:steel_precursor_dust").getItem(), 8))
                .input(new ItemStack(ChemicalHelper.get(TagPrefix.dust, GTMaterials.WroughtIron).getItem(), 8))
                .input(tag("forge:dusts/charcoal"))
                .save(provider);

        // bronze dust
        MixingRecipeBuilder.builder("bronze_dust_from_copper_tin")
                .result(new ItemStack(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Bronze).getItem(), 3))
                .input(new ItemStack(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Copper).getItem(), 3))
                .input(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Tin))
                .save(provider);

        // alexscaves magnets
        MixingRecipeBuilder.builder("scarlet_neodymium_ingot")
                .result(new ItemStack(itemStack("alexscaves:scarlet_neodymium_ingot").getItem(), 2))
                .input(itemStack("alexscaves:raw_scarlet_neodymium"))
                .input(ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Iron))
                .save(provider);
        MixingRecipeBuilder.builder("azure_neodymium_ingot")
                .result(new ItemStack(itemStack("alexscaves:azure_neodymium_ingot").getItem(), 2))
                .input(itemStack("alexscaves:raw_azure_neodymium"))
                .input(ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Iron))
                .save(provider);

        // Sequenced assembly recipes

        // item_application: shadow steel casing
        ItemApplicationRecipeBuilder.builder("shadow_steel_casing_item_application")
                .input(itemStack("minecraft:obsidian"))
                .input(itemStack("gtceu:shadow_steel_plate"))
                .result(itemStack("create:shadow_steel_casing"))
                .save(provider);

        // mixing: chromatic compound
        MixingRecipeBuilder.builder("chromatic_compound_from_lava")
                .result(new ItemStack(itemStack("create:chromatic_compound").getItem(), 4))
                .inputFluid("minecraft:lava", 500)
                .input(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Netherite))
                .input(ChemicalHelper.get(TagPrefix.ingot, CTPPMaterials.AndesiteAlloy))
                .input(itemStack("create:polished_rose_quartz"))
                .save(provider);

        // mixing: aqua regia gold nugget extraction
        MixingRecipeBuilder.builder("gold_nuggets_from_aqua_regia")
                .result(new ItemStack(ChemicalHelper.get(TagPrefix.nugget, GTMaterials.Gold).getItem(), 5))
                .inputFluid("gtceu:aqua_regia", 500)
                .input(new ItemStack(itemStack("gtceu:ochrum_dust").getItem(), 2))
                .save(provider);

        // splashing series (ores -> outputs)
        ItemStack asurineIn = itemStack("gtceu:asurine_dust");
        ItemStack asurineSil = ChemicalHelper.get(TagPrefix.dust, GTMaterials.SiliconDioxide);
        ItemStack asurineZn = ChemicalHelper.get(TagPrefix.nugget, GTMaterials.Zinc);
        if (!asurineIn.isEmpty() && !asurineSil.isEmpty()) {
            SplashingRecipeBuilder.builder("splashing_asurine")
                    .input(asurineIn)
                    .result(asurineSil)
                    .result(new ItemStack(asurineZn.getItem(), 4), 0.5)
                    .save(provider);
        }

        ItemStack crimsiteIn = itemStack("gtceu:crimsite_dust");
        ItemStack crimsiteSil = ChemicalHelper.get(TagPrefix.dust, GTMaterials.SiliconDioxide);
        ItemStack crimsiteIron = ChemicalHelper.get(TagPrefix.nugget, GTMaterials.Iron);
        if (!crimsiteIn.isEmpty() && !crimsiteSil.isEmpty()) {
            SplashingRecipeBuilder.builder("splashing_crimsite")
                    .input(crimsiteIn)
                    .result(crimsiteSil)
                    .result(new ItemStack(crimsiteIron.getItem(), 4), 0.5)
                    .save(provider);
        }

        ItemStack ochrumIn = itemStack("gtceu:ochrum_dust");
        ItemStack ochrumSil = ChemicalHelper.get(TagPrefix.dust, GTMaterials.SiliconDioxide);
        ItemStack ochrumPrec = ChemicalHelper.get(TagPrefix.nugget, GTMaterials.Gold);
        if (!ochrumIn.isEmpty() && !ochrumSil.isEmpty()) {
            SplashingRecipeBuilder.builder("splashing_ochrum")
                    .input(ochrumIn)
                    .result(ochrumSil)
                    .result(new ItemStack(ochrumPrec.getItem(), 4), 0.5)
                    .save(provider);
        }

        ItemStack veridiumIn = itemStack("gtceu:veridium_dust");
        ItemStack veridiumSil = ChemicalHelper.get(TagPrefix.dust, GTMaterials.SiliconDioxide);
        ItemStack veridiumCu = ChemicalHelper.get(TagPrefix.nugget, GTMaterials.Copper);
        if (!veridiumIn.isEmpty() && !veridiumSil.isEmpty()) {
            SplashingRecipeBuilder.builder("splashing_veridium")
                    .input(veridiumIn)
                    .result(veridiumSil)
                    .result(new ItemStack(veridiumCu.getItem(), 4), 0.5)
                    .save(provider);
        }

        // deepslate tuff crushing (primary + optional flint secondary)
        ItemStack tuff = itemStack("minecraft:tuff");
        ItemStack deepslateDust = ChemicalHelper.get(TagPrefix.dust, GTMaterials.Deepslate);
        ItemStack flintDust = ChemicalHelper.get(TagPrefix.dust, GTMaterials.Flint);
        if (!tuff.isEmpty() && !deepslateDust.isEmpty()) {
            CrushingRecipeBuilder builder = CrushingRecipeBuilder.builder("crushing_tuff_to_deepslate");
            builder.input(tuff).result(deepslateDust);
            if (!flintDust.isEmpty()) builder.result(flintDust, 0.25);
            builder.save(provider);
        }

        // deepslate splashing (many outputs with chances)
        ItemStack deepslateIn = ChemicalHelper.get(TagPrefix.dust, GTMaterials.Deepslate);
        if (!deepslateIn.isEmpty()) {
            SplashingRecipeBuilder splashBuilder = SplashingRecipeBuilder
                    .builder("splashing_deepslate");
            splashBuilder = splashBuilder.input(deepslateIn);
            ItemStack sd = ChemicalHelper.get(TagPrefix.dust, GTMaterials.Stone);
            if (!sd.isEmpty()) splashBuilder.result(sd);
            ItemStack dia = itemStack("minecraft:diamond");
            if (!dia.isEmpty()) splashBuilder.result(dia, 0.05);
            ItemStack gld = ChemicalHelper.get(TagPrefix.nugget, GTMaterials.Gold);
            if (!gld.isEmpty()) splashBuilder.result(gld, 0.05);
            ItemStack irn = ChemicalHelper.get(TagPrefix.nugget, GTMaterials.Iron);
            if (!irn.isEmpty()) splashBuilder.result(irn, 0.1);
            ItemStack cup = ChemicalHelper.get(TagPrefix.nugget, GTMaterials.Copper);
            if (!cup.isEmpty()) splashBuilder.result(cup, 0.08);
            ItemStack zn = ChemicalHelper.get(TagPrefix.nugget, GTMaterials.Zinc);
            if (!zn.isEmpty()) splashBuilder.result(zn, 0.05);
            ItemStack pt = ChemicalHelper.get(TagPrefix.nugget, GTMaterials.Platinum);
            if (!pt.isEmpty()) splashBuilder.result(pt, 0.01);
            ItemStack mn = ChemicalHelper.get(TagPrefix.nugget, GTMaterials.Manganese);
            if (!mn.isEmpty()) splashBuilder.result(mn, 0.04);
            ItemStack cr = ChemicalHelper.get(TagPrefix.nugget, GTMaterials.Chromium);
            if (!cr.isEmpty()) splashBuilder.result(cr, 0.02);
            splashBuilder.save(provider);
        }

        // crushing tuff -> deepslate_dust + flint chance already added above

        // remove tuff crushing outputs replacements: emulate original removes by not generating those specific create
        // recipes (skip)

        // item_application/mixing/splashing for precious alloy dust -> gold nuggets
        ItemStack preciousIn = itemStack("gtceu:precious_alloy_dust");
        if (!preciousIn.isEmpty()) {
            SplashingRecipeBuilder splash = SplashingRecipeBuilder
                    .builder("splashing_precious_alloy_gold").input(preciousIn);
            ItemStack g3 = new ItemStack(ChemicalHelper.get(TagPrefix.nugget, GTMaterials.Gold).getItem(), 3);
            if (!g3.isEmpty()) splash.result(g3, 0.8);
            ItemStack g2 = new ItemStack(ChemicalHelper.get(TagPrefix.nugget, GTMaterials.Gold).getItem(), 2);
            if (!g2.isEmpty()) splash.result(g2, 0.6);
            ItemStack g1 = new ItemStack(ChemicalHelper.get(TagPrefix.nugget, GTMaterials.Gold).getItem(), 1);
            if (!g1.isEmpty()) splash.result(g1, 0.4);
            ItemStack s1 = ChemicalHelper.get(TagPrefix.nugget, GTMaterials.Silver);
            if (!s1.isEmpty()) splash.result(s1, 0.6);
            splash.save(provider);
        }

        // milling with chance: obsidian -> obsidian_dust (0.75)
            MillingRecipeBuilder.builder("milling_obsidian_chance")
                    .input(Blocks.OBSIDIAN.asItem())
                    .result(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Obsidian), 0.75)
                    .save(provider);

        // basic mechanism from wooden slabs -> ctpp:basic_mechanism
        ItemStack incompleteBasic = itemStack("ctpp:incomplete_basic_mechanism");
        ItemStack basicMechanism = itemStack("ctpp:basic_mechanism");
        if (!incompleteBasic.isEmpty() && !basicMechanism.isEmpty()) {
            SequencedAssemblyRecipeBuilder.builder("basic_mechanism_from_slabs")
                    .input(tag("minecraft:wooden_slabs"))
                    .transitional(incompleteBasic)
                    .result(basicMechanism)
                    .deploying(ChemicalHelper.get(TagPrefix.ingot, CTPPMaterials.AndesiteAlloy))
                    .deploying(ChemicalHelper.get(TagPrefix.plate, GTMaterials.Iron))
                    .cutting()
                    .loops(1)
                    .save(provider);
        }

        // precision mechanism from basic mechanism
        ItemStack incompletePrecision = itemStack("create:incomplete_precision_mechanism");
        ItemStack precision = itemStack("create:precision_mechanism");
        if (!incompletePrecision.isEmpty() && !precision.isEmpty() && !basicMechanism.isEmpty()) {
            SequencedAssemblyRecipeBuilder.builder("precision_mechanism_from_basic")
                    .input(basicMechanism)
                    .transitional(incompletePrecision)
                    .result(precision)
                    .deploying(ChemicalHelper.get(TagPrefix.plate, GTMaterials.Brass))
                    .deploying(itemStack("create:cogwheel"))
                    .deploying(itemStack("create:large_cogwheel"))
                    .filling(incompletePrecision, "alexscaves:acid", 500)
                    .loops(1)
                    .save(provider);
        }

        // electron tube
        ItemStack electronTrans = itemStack("create:electron_tube");
        ItemStack vacuumTube = itemStack("gtceu:vacuum_tube");
        if (!electronTrans.isEmpty() && !vacuumTube.isEmpty()) {
            SequencedAssemblyRecipeBuilder.builder("electron_tube_from_vacuum")
                    .input(vacuumTube)
                    .transitional(electronTrans)
                    .result(itemStack("gtceu:vacuum_tube"))
                    .deploying(itemStack("minecraft:glass"))
                    .deploying(ChemicalHelper.get(TagPrefix.ingot, GTMaterials.Steel))
                    .deploying(ChemicalHelper.get(TagPrefix.wireGtDouble, GTMaterials.Copper))
                    .loops(1)
                    .save(provider);
        }

        // unfinished steel mechanism (create precision -> ctpp:steel_mechanism)
        ItemStack unfinishedSteel = itemStack("kubejs:unfinished_steel_mechanism");
        ItemStack steelMech = itemStack("ctpp:steel_mechanism");
        if (!unfinishedSteel.isEmpty() && !steelMech.isEmpty() && !precision.isEmpty()) {
            SequencedAssemblyRecipeBuilder.builder("steel_mechanism_from_precision")
                    .input(precision)
                    .transitional(unfinishedSteel)
                    .result(steelMech)
                    .deploying(ChemicalHelper.get(TagPrefix.plate, GTMaterials.Steel))
                    .deploying(ChemicalHelper.get(TagPrefix.plate, GTMaterials.RedAlloy))
                    .deploying(itemStack("gtceu:steel_screw"))
                    .filling(unfinishedSteel, GTMaterials.Rubber.getFluid(576))
                    .loops(1)
                    .save(provider);
        }

        // ender pearl dust -> ender eye dust
        ItemStack enderPearlDust = ChemicalHelper.get(TagPrefix.dust, GTMaterials.EnderPearl);
        ItemStack enderEyeDust = ChemicalHelper.get(TagPrefix.dust, GTMaterials.EnderEye);
        if (!enderPearlDust.isEmpty() && !enderEyeDust.isEmpty()) {
            SequencedAssemblyRecipeBuilder.builder("ender_pearl_to_eye_dust")
                    .input(enderPearlDust)
                    .transitional(enderPearlDust)
                    .result(enderEyeDust)
                    .filling(enderPearlDust, GTMaterials.Blaze.getFluid(288))
                    .pressing()
                    .loops(1)
                    .save(provider);
        }

        // bronze machine casing -> steam engine (many steps)
        ItemStack bronzeCasing = itemStack("gtceu:bronze_machine_casing");
        ItemStack steamEngine = itemStack("create:steam_engine");
        if (!bronzeCasing.isEmpty() && !steamEngine.isEmpty()) {
            SequencedAssemblyRecipeBuilder.builder("bronze_machine_casing_to_steam_engine")
                    .input(bronzeCasing)
                    .transitional(bronzeCasing)
                    .result(steamEngine)
                    .deploying(ChemicalHelper.get(TagPrefix.plate, GTMaterials.Steel))
                    .deploying(itemStack("gtceu:long_steel_rod"))
                    .deploying(itemStack("ctpp:steel_mechanism"))
                    .pressing()
                    .filling(bronzeCasing, GTMaterials.Lubricant.getFluid(250))
                    .loops(3)
                    .save(provider);
        }

        // paper -> resistors
        ItemStack paper = itemStack("minecraft:paper");
        ItemStack resistor = itemStack("gtceu:resistor");
        if (!paper.isEmpty() && !resistor.isEmpty()) {
            SequencedAssemblyRecipeBuilder.builder("paper_to_resistor")
                    .input(paper)
                    .transitional(paper)
                    .result(new ItemStack(resistor.getItem(), 2))
                    .deploying(ChemicalHelper.get(TagPrefix.wireGtSingle, GTMaterials.Copper))
                    .deploying(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Coal))
                    .deploying(itemStack("gtceu:sticky_resin"))
                    .pressing()
                    .loops(1)
                    .save(provider);
        }

        // small gallium arsenide -> diode
        ItemStack smallGa = itemStack("gtceu:small_gallium_arsenide_dust");
        ItemStack diode = itemStack("gtceu:diode");
        if (!smallGa.isEmpty() && !diode.isEmpty()) {
            SequencedAssemblyRecipeBuilder.builder("gallium_arsenide_to_diodes")
                    .input(smallGa)
                    .transitional(smallGa)
                    .result(new ItemStack(diode.getItem(), 2))
                    .deploying(ChemicalHelper.get(TagPrefix.wireGtSingle, GTMaterials.Copper))
                    .filling(smallGa, GTMaterials.Tin.getFluid(144))
                    .pressing()
                    .loops(1)
                    .save(provider);
        }

        // high strength concrete -> sintering kiln
        ItemStack highConcrete = itemStack("ctnhcore:high_strength_concrete");
        ItemStack sinteringKiln = itemStack("ctnhcore:sintering_kiln");
        ItemStack steelFirebox = itemStack("gtceu:steel_firebox_casing");
        if (!highConcrete.isEmpty() && !sinteringKiln.isEmpty() && !steelFirebox.isEmpty()) {
            SequencedAssemblyRecipeBuilder.builder("high_strength_concrete_to_sintering_kiln")
                    .input(steelFirebox)
                    .transitional(highConcrete)
                    .result(sinteringKiln)
                    .deploying(ChemicalHelper.get(TagPrefix.block, GTMaterials.Steel))
                    .deploying(itemStack("ctnhcore:advanced_coke_oven"))
                    .deploying(itemStack("gtceu:firebricks"))
                    .filling(highConcrete, GTMaterials.Creosote.getFluid(1000))
                    .loops(1)
                    .save(provider);
        }

        // orange stained glass -> bronze framed glass
        ItemStack orangeGlass = itemStack("minecraft:orange_stained_glass");
        ItemStack bronzeFramed = itemStack("ctnhcore:bronze_framed_glass");
        if (!orangeGlass.isEmpty() && !bronzeFramed.isEmpty()) {
            SequencedAssemblyRecipeBuilder.builder("orange_glass_to_bronze_framed")
                    .input(itemStack("minecraft:glass"))
                    .transitional(orangeGlass)
                    .result(bronzeFramed)
                    .deploying(itemStack("gtceu:bronze_tiny_fluid_pipe"))
                    .deploying(ChemicalHelper.get(TagPrefix.rod, GTMaterials.Bronze))
                    .deploying(itemStack("gtceu:long_bronze_rod"))
                    .loops(2)
                    .save(provider);
        }

        // resin circuit board -> basic electronic circuit (custom sequence)
        ItemStack resinBoard = itemStack("gtceu:resin_circuit_board");
        ItemStack basicCircuit = itemStack("gtceu:basic_electronic_circuit");
        if (!resinBoard.isEmpty() && !basicCircuit.isEmpty()) {
            SequencedAssemblyRecipeBuilder.builder("basic_electronic_circuit_from_resin")
                    .input(resinBoard)
                    .transitional(resinBoard)
                    .result(basicCircuit)
                    .deploying(itemStack("gtceu:copper_quadruple_wire"))
                    .deploying(ChemicalHelper.get(TagPrefix.wireGtDouble, GTMaterials.RedAlloy))
                    .deploying(itemStack("gtceu:vacuum_tube"))
                    .deploying(itemStack("gtceu:resistor"))
                    .filling(resinBoard, GTMaterials.Rubber.getFluid(288))
                    .pressing()
                    .step("create_new_age:energising", json -> json.addProperty("energy_needed", 10000))
                    .loops(1)
                    .save(provider);
        }

        // coke dust -> high quality solid fuel
        ItemStack cokeDustSeq = ChemicalHelper.get(TagPrefix.dust, GTMaterials.Coke);
        ItemStack highQualityFuel = itemStack("kubejs:high_quality_solid_fuel");
        if (!cokeDustSeq.isEmpty() && !highQualityFuel.isEmpty()) {
            SequencedAssemblyRecipeBuilder.builder("coke_dust_to_high_quality_fuel")
                    .input(cokeDustSeq)
                    .transitional(cokeDustSeq)
                    .result(highQualityFuel)
                    .cutting()
                    .deploying(itemStack("gtceu:lignin_dust"))
                    .filling(cokeDustSeq, GTMaterials.Creosote.getFluid(250))
                    .pressing()
                    .loops(1)
                    .save(provider);
        }

        // steam machine casing -> industrial steam casing
        ItemStack steamMachineCasing = itemStack("gtceu:steam_machine_casing");
        ItemStack industrialSteam = itemStack("gtceu:industrial_steam_casing");
        if (!steamMachineCasing.isEmpty() && !industrialSteam.isEmpty()) {
            SequencedAssemblyRecipeBuilder.builder("steam_machine_casing_to_industrial")
                    .input(steamMachineCasing)
                    .transitional(steamMachineCasing)
                    .result(industrialSteam)
                    .deploying(ChemicalHelper.get(TagPrefix.plate, GTMaterials.Brass))
                    .deploying(ChemicalHelper.get(TagPrefix.plate, GTMaterials.Brass))
                    .filling(steamMachineCasing, GTMaterials.SolderingAlloy.getFluid(144))
                    .pressing()
                    .loops(1)
                    .save(provider);
        }

        // blaze cake -> double blaze cake (multiple fill steps)
        ItemStack blazeCake = itemStack("create:blaze_cake");
        ItemStack doubleBlaze = itemStack("ctnhcore:double_blaze_cake");
        if (!blazeCake.isEmpty() && !doubleBlaze.isEmpty()) {
            SequencedAssemblyRecipeBuilder.builder("double_blaze_cake_from_blaze_cake")
                    .input(blazeCake)
                    .transitional(blazeCake)
                    .result(doubleBlaze)
                    .filling(blazeCake, GTMaterials.Lava.getFluid(100))
                    .filling(blazeCake, GTMaterials.Lava.getFluid(100))
                    .filling(blazeCake, GTMaterials.Lava.getFluid(100))
                    .filling(blazeCake, GTMaterials.Lava.getFluid(100))
                    .filling(blazeCake, GTMaterials.Lava.getFluid(100))
                    .filling(blazeCake, GTMaterials.Lava.getFluid(100))
                    .filling(blazeCake, GTMaterials.Lava.getFluid(200))
                    .filling(blazeCake, GTMaterials.Lava.getFluid(200))
                    .loops(1)
                    .save(provider);
        }

        // ulv input bus/hatch (from chest tags)
        ItemStack ulvInputBus = itemStack("gtceu:ulv_input_bus");
        if (!ulvInputBus.isEmpty()) {
            SequencedAssemblyRecipeBuilder.builder("ulv_input_bus_from_wooden_chest")
                    .input(tag("forge:chests/wooden"))
                    .transitional(ulvInputBus)
                    .result(ulvInputBus)
                    .deploying(CTPPItems.STEEL_MECHANISM.asStack())
                    .deploying(itemStack("gtceu:ulv_machine_casing"))
                    .deploying(ChemicalHelper.get(TagPrefix.plateDouble, GTMaterials.WroughtIron))
                    .loops(1)
                    .save(provider);
        }

        ItemStack ulvInputHatch = itemStack("gtceu:ulv_input_hatch");
        if (!ulvInputHatch.isEmpty()) {
            SequencedAssemblyRecipeBuilder.builder("ulv_input_hatch_from_bronze_drum")
                    .input(itemStack("gtceu:bronze_drum"))
                    .transitional(ulvInputHatch)
                    .result(ulvInputHatch)
                    .deploying(CTPPItems.STEEL_MECHANISM.asStack())
                    .deploying(itemStack("gtceu:ulv_machine_casing"))
                    .deploying(ChemicalHelper.get(TagPrefix.plateDouble, GTMaterials.WroughtIron))
                    .loops(1)
                    .save(provider);
        }

        // tungsten steel frame (pick primary output)
        ItemStack tungstenFrame = itemStack("gtceu:tungsten_steel_frame");
        ItemStack assemblyLineCasing = itemStack("gtceu:assembly_line_casing");
        if (!tungstenFrame.isEmpty() && !assemblyLineCasing.isEmpty()) {
            SequencedAssemblyRecipeBuilder.builder("tungsten_steel_frame_sequence")
                    .input(tungstenFrame)
                    .transitional(tungstenFrame)
                    .result(assemblyLineCasing)
                    .deploying(tag("gtceu:circuits/zpm"))
                    .pressing()
                    .pressing()
                    .deploying(tag("gtceu:circuits/zpm"))
                    .pressing()
                    .pressing()
                    .deploying(tag("gtceu:circuits/luv"))
                    .pressing()
                    .loops(2)
                    .save(provider);
        }

        // tungstensteel gearbox
        ItemStack gearbox = itemStack("gtceu:tungstensteel_gearbox");
        ItemStack assemblyLineUnit = itemStack("gtceu:assembly_line_unit");
        if (!gearbox.isEmpty() && !assemblyLineUnit.isEmpty()) {
            SequencedAssemblyRecipeBuilder.builder("tungstensteel_gearbox_sequence")
                    .input(gearbox)
                    .transitional(gearbox)
                    .result(assemblyLineUnit)
                    .deploying(ChemicalHelper.get(TagPrefix.gear, GTMaterials.Ruridit))
                    .pressing()
                    .cutting()
                    .deploying(itemStack("gtceu:iv_robot_arm"))
                    .cutting()
                    .pressing()
                    .loops(4)
                    .save(provider);
        }
    }

    private static Item item(String id) {
        if (id == null) return null;
        // tags (starting with #) cannot be resolved to Item here
        if (id.startsWith("#")) return null;
        ResourceLocation rl = ResourceLocation.tryParse(id);
        if (rl == null) return null;
        return ForgeRegistries.ITEMS.getValue(rl);
    }

    private static ItemStack itemStack(String id) {
        Item it = item(id);
        if (it == null) return ItemStack.EMPTY;
        return new ItemStack(it);
    }

    private static TagKey<Item> tag(String id) {
        if (id == null || id.isEmpty()) return null;
        ResourceLocation rl = ResourceLocation.tryParse(id);
        if (rl == null) return null;
        return TagKey.create(Registries.ITEM, rl);
    }
}
