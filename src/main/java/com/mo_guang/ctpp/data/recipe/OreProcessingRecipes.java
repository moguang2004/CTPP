package com.mo_guang.ctpp.data.recipe;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.mo_guang.ctpp.data.recipe.builder.create.*;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import com.mo_guang.ctpp.data.recipe.builder.ctpp.MetalSmeltingRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

import com.mo_guang.ctpp.registry.CreateMaterials;
import com.simibubi.create.content.processing.recipe.HeatCondition;

import java.util.function.Consumer;

public class OreProcessingRecipes {
    private static final Material[] MATERIAL = {
            GTMaterials.Hematite, GTMaterials.Magnetite,
            GTMaterials.get("gtceu:precious_alloy"),
            GTMaterials.Copper, GTMaterials.Diamond, GTMaterials.Tin, GTMaterials.Silver,
            GTMaterials.VanadiumMagnetite, GTMaterials.Spodumene, GTMaterials.RockSalt, GTMaterials.Salt,
            GTMaterials.Lepidolite, GTMaterials.Lazurite, GTMaterials.Lapis, GTMaterials.Sodalite,
            GTMaterials.Calcite, GTMaterials.Graphite, GTMaterials.Coal, GTMaterials.Zinc, GTMaterials.Gold,
            GTMaterials.Cassiterite, GTMaterials.Chalcopyrite, GTMaterials.Pyrite, GTMaterials.Iron,
            GTMaterials.YellowLimonite, GTMaterials.Malachite, GTMaterials.Oilsands, GTMaterials.Goethite,
            GTMaterials.NetherQuartz, GTMaterials.Quartzite, GTMaterials.Opal, GTMaterials.Redstone,
            GTMaterials.Ruby, GTMaterials.Cinnabar, GTMaterials.Nickel, GTMaterials.Lead, GTMaterials.Pentlandite,
            GTMaterials.Realgar, GTMaterials.GarnetYellow, GTMaterials.GarnetRed, GTMaterials.BasalticMineralSand,
            GTMaterials.GraniticMineralSand, GTMaterials.Beryllium, GTMaterials.Molybdenum,
            GTMaterials.Molybdenite, GTMaterials.Garnierite, GTMaterials.Cobaltite, GTMaterials.Topaz,
            GTMaterials.BlueTopaz, GTMaterials.Sulfur, GTMaterials.Chalcocite, GTMaterials.Bornite,
            GTMaterials.Sphalerite, GTMaterials.Saltpeter, GTMaterials.Diatomite, GTMaterials.Electrotine,
            GTMaterials.Alunite, GTMaterials.Grossular, GTMaterials.Pyrolusite, GTMaterials.Tantalite,
            GTMaterials.CertusQuartz, GTMaterials.Barite, GTMaterials.Spessartine, GTMaterials.Gypsum
    };

    public static void init(Consumer<FinishedRecipe> provider) {
        addOreProcessing(provider);
        addMetalMelting(provider);
        addAlloys(provider);
        addCasting(provider);
    }
    private static void addOreProcessing(Consumer<FinishedRecipe> provider) {
        for (Material material : MATERIAL) {
            int oreMultiplier = material.getProperty(PropertyKey.ORE).getOreMultiplier();
            double basic_chance = 0.3;
            CrushingRecipeBuilder.builder("ctpp/raw_" + material.getName() + "_crushing")
                    .input(ChemicalHelper.get(TagPrefix.rawOre, material).getItem())
                    .output(ChemicalHelper.get(TagPrefix.crushed, material).getItem(), oreMultiplier)
                    .result(ChemicalHelper.get(TagPrefix.crushed, material).getItem().getDefaultInstance(), basic_chance * oreMultiplier)
                    .save(provider);
            CrushingRecipeBuilder.builder("ctpp/crushed_" + material.getName() + "_ore_crushing")
                    .input(ChemicalHelper.get(TagPrefix.crushed, material).getItem())
                    .output(ChemicalHelper.get(TagPrefix.dustImpure, material).getItem(), oreMultiplier)
                    .result(ChemicalHelper.get(TagPrefix.dustImpure, material).getItem().getDefaultInstance(), basic_chance * oreMultiplier)
                    .save(provider);
            SplashingRecipeBuilder.builder("ctpp/crushed_" + material.getName() + "_purified")
                    .input(ChemicalHelper.get(TagPrefix.crushed, material).getItem())
                    .result(ChemicalHelper.get(TagPrefix.crushedPurified, material).getItem().getDefaultInstance())
                    .save(provider);
            SplashingRecipeBuilder.builder("ctpp/impure_" + material.getName() + "_purified")
                    .input(ChemicalHelper.get(TagPrefix.dustImpure, material).getItem())
                    .result(ChemicalHelper.get(TagPrefix.dust, material).getItem().getDefaultInstance())
                    .save(provider);
            if (material.hasProperty(PropertyKey.INGOT)) {
                SplashingRecipeBuilder.builder("ctpp/" + material.getName() + "_nuggets_from_purified_ore")
                        .input(ChemicalHelper.get(TagPrefix.crushedPurified, material).getItem())
                        .result(new ItemStack(ChemicalHelper.get(TagPrefix.nugget, material).getItem(), 11))
                        .result(new ItemStack(ChemicalHelper.get(TagPrefix.nugget, material).getItem(), 2), 0.4)
                        .save(provider);
            }
        }
    }

    private static void addMetalMelting(Consumer<FinishedRecipe> provider) {
        for (Material material : MATERIAL) {
            if (!material.hasProperty(PropertyKey.GEM)) {
                Material smeltInto = material.getProperty(PropertyKey.ORE).getDirectSmeltResult();
                if (!smeltInto.isNull() && smeltInto.hasProperty(PropertyKey.FLUID)) {
                    MetalSmeltingRecipeBuilder.builder("ctpp/melting/curshed_" + material.getName())
                            .input(ChemicalHelper.get(TagPrefix.crushed, material).getItem())
                            .heat(HeatCondition.HEATED)
                            .duration(40)
                            .outputFluid(smeltInto.getFluid(108))
                            .outputFluid(CreateMaterials.SLAG.getFluid(100))
                            .save(provider);
                    MetalSmeltingRecipeBuilder.builder("ctpp/melting/purified_curshed_" + material.getName())
                            .input(ChemicalHelper.get(TagPrefix.crushedPurified, material).getItem())
                            .heat(HeatCondition.HEATED)
                            .duration(40)
                            .outputFluid(smeltInto.getFluid(144))
                            .save(provider);
                    MetalSmeltingRecipeBuilder.builder("ctpp/melting/impure_" + material.getName() + "_dust")
                            .input(ChemicalHelper.get(TagPrefix.dustImpure, material).getItem())
                            .heat(HeatCondition.HEATED)
                            .duration(40)
                            .outputFluid(smeltInto.getFluid(144))
                            .outputFluid(CreateMaterials.SLAG.getFluid(50))
                            .save(provider);
                    MetalSmeltingRecipeBuilder.builder("ctpp/melting/" + material.getName() + "_dust")
                            .input(ChemicalHelper.get(TagPrefix.dust, material).getItem())
                            .heat(HeatCondition.HEATED)
                            .duration(40)
                            .outputFluid(smeltInto.getFluid(144))
                            .save(provider);
                }
            }
            if (material.hasProperty(PropertyKey.INGOT)) {
                MetalSmeltingRecipeBuilder.builder("ctpp/melting/" + material.getName() + "_ingot")
                        .input(ChemicalHelper.get(TagPrefix.crushed, material).getItem())
                        .heat(HeatCondition.HEATED)
                        .duration(80)
                        .outputFluid(material.getFluid(144))
                        .save(provider);
            }
        }
    }

    private static void addAlloys(Consumer<FinishedRecipe> provider) {
        MixingRecipeBuilder.builder("ctpp/mixing/alloying/brass")
                .inputFluid(GTMaterials.Copper.getFluid(432))
                .inputFluid(GTMaterials.Zinc.getFluid(144))
                .heatRequirement(HeatCondition.HEATED)
                .resultFluid(GTMaterials.Brass.getFluid(576))
                .save(provider);
        MixingRecipeBuilder.builder("ctpp/mixing/alloying/bronze")
                .inputFluid(GTMaterials.Copper.getFluid(432))
                .inputFluid(GTMaterials.Tin.getFluid(144))
                .heatRequirement(HeatCondition.HEATED)
                .resultFluid(GTMaterials.Bronze.getFluid(576))
                .save(provider);
        MixingRecipeBuilder.builder("ctpp/mixing/alloying/potin")
                .inputFluid(GTMaterials.Bronze.getFluid(576))
                .inputFluid(GTMaterials.Lead.getFluid(72))
                .heatRequirement(HeatCondition.HEATED)
                .resultFluid(GTMaterials.Potin.getFluid(648))
                .save(provider);
        MixingRecipeBuilder.builder("ctpp/mixing/alloying/tin_alloy")
                .inputFluid(GTMaterials.Iron.getFluid(144))
                .inputFluid(GTMaterials.Tin.getFluid(144))
                .heatRequirement(HeatCondition.HEATED)
                .resultFluid(GTMaterials.TinAlloy.getFluid(288))
                .save(provider);
        MetalSmeltingRecipeBuilder.builder("ctpp/melting/alloying_dust")
                .input(ChemicalHelper.get(TagPrefix.dust, CreateMaterials.AndesiteAlloy))
                .heat(HeatCondition.HEATED)
                .duration(90)
                .outputFluid(CreateMaterials.AndesiteAlloy.getFluid(144))
                .save(provider);
        MetalSmeltingRecipeBuilder.builder("ctpp/melting/alloying_ingot")
                .input(ChemicalHelper.get(TagPrefix.ingot, CreateMaterials.AndesiteAlloy))
                .heat(HeatCondition.HEATED)
                .duration(90)
                .outputFluid(CreateMaterials.AndesiteAlloy.getFluid(144))
                .save(provider);
    }

    private static void addCasting(Consumer<FinishedRecipe> provider) {
        Material[] metal = new Material[]{
                CreateMaterials.AndesiteAlloy, GTMaterials.Brass, GTMaterials.Steel, GTMaterials.Silver, GTMaterials.Nickel, GTMaterials.Lead,
                GTMaterials.Tin, GTMaterials.Zinc, GTMaterials.Bronze, GTMaterials.Iron, GTMaterials.Copper, GTMaterials.Gold
        };
        for (Material material : metal) {
            CompactingRecipeBuilder.builder("ctpp/casting/" + material.getName() + "_ingot")
                    .input(GTItems.SHAPE_MOLD_INGOT.asItem())
                    .inputFluid(material.getFluid(144))
                    .output(ChemicalHelper.get(TagPrefix.ingot, material))
                    .save(provider);
            CompactingRecipeBuilder.builder("ctpp/casting/" + material.getName() + "_plate")
                    .input(GTItems.SHAPE_MOLD_INGOT.asItem())
                    .inputFluid(material.getFluid(216))
                    .output(ChemicalHelper.get(TagPrefix.ingot, material))
                    .save(provider);
            CompactingRecipeBuilder.builder("ctpp/casting/" + material.getName() + "_block")
                    .input(GTItems.SHAPE_MOLD_INGOT.asItem())
                    .inputFluid(material.getFluid(1296))
                    .output(ChemicalHelper.get(TagPrefix.ingot, material))
                    .save(provider);
        }
    }
}
