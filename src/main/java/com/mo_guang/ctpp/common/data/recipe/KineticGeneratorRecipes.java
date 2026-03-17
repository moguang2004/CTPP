package com.mo_guang.ctpp.common.data.recipe;

import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.common.data.recipe.builder.CTPPRecipeBuilder;
import com.mo_guang.ctpp.config.MainConfig;
import com.mo_guang.ctpp.registry.CTPPBlocks;
import com.mo_guang.ctpp.registry.CTPPMultiblockMachines;
import org.antarcticgardens.newage.NewAgeBlocks;

import java.util.function.Consumer;

import static com.mo_guang.ctpp.common.machine.multiblock.KineticGeneratorMachine.GENERATING_BOOST;
import static com.mo_guang.ctpp.registry.CTPPRecipeTypes.KINETIC_GENERATOR_RECIPES;
import static com.mo_guang.ctpp.registry.CTPPRecipeTypes.KINETIC_STEAM_TURBINE_RECIPES;

public class KineticGeneratorRecipes {

    public static final boolean REQUIRE_LUBRICANT = MainConfig.INSTANCE.ctnhConfig.kineticGeneratorGeneratingRequireLubricant;
    public static final int GENERATING_REQUIRE_LUBRICANT_AMOUNT = MainConfig.INSTANCE.ctnhConfig.kineticGeneratorGeneratingRequireLubricantAmount;
    public static final float STEAM_POWERED_BOOST = MainConfig.INSTANCE.ctnhConfig.steamPoweredKineticGeneratingBoost;

    public static void init(Consumer<FinishedRecipe> provider) {
        if (REQUIRE_LUBRICANT) {
            CTPPRecipeBuilder.of(CTPP.id("generator"), KINETIC_GENERATOR_RECIPES)
                    .inputFluids(GTMaterials.Lubricant.getFluid(GENERATING_REQUIRE_LUBRICANT_AMOUNT))
                    .inputStress(512)
                    .duration(40)
                    .EUt((long) (-32 * GENERATING_BOOST))
                    .addData("stress", 512)
                    .save(provider);
        } else {
            CTPPRecipeBuilder.of(CTPP.id("generator"), KINETIC_GENERATOR_RECIPES)
                    .notConsumableFluid(GTMaterials.Lubricant.getFluid(GENERATING_REQUIRE_LUBRICANT_AMOUNT))
                    .inputStress(512)
                    .duration(40)
                    .EUt((long) (-32 * GENERATING_BOOST))
                    .addData("stress", 512)
                    .save(provider);
        }

        CTPPRecipeBuilder.of(CTPP.id("steam"), KINETIC_STEAM_TURBINE_RECIPES)
                .inputFluids(GTMaterials.Steam.getFluid(640))
                .outputFluids(GTMaterials.DistilledWater.getFluid(4))
                .outputStress(32768 * STEAM_POWERED_BOOST)
                .addData("stress", 32768 * STEAM_POWERED_BOOST)
                .duration(10)
                .save(provider);

        VanillaRecipeHelper.addShapedRecipe(provider, "kinetic_generator",
                new ItemStack(CTPPMultiblockMachines.KINETIC_GENERATOR.getItem()),
                "ABA",
                "CDC",
                "ABA",
                'A', CTPPBlocks.HEAVY_MACHINERY_CASING,
                'B', GTBlocks.CASING_STEEL_SOLID,
                'C', CustomTags.MV_CIRCUITS,
                'D', NewAgeBlocks.GENERATOR_COIL.asStack());
    }
}
