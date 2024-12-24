package com.mo_guang.ctpp.recipe;

import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.mo_guang.ctpp.CTPP;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static com.mo_guang.ctpp.common.data.CTPPRecipeTypes.*;

public class KineticGeneratorRecipes {
    public static void init(Consumer<FinishedRecipe> provider) {
        CTPPRecipeBuilder.of(CTPP.id("generator"),KINETIC_GENERATOR_RECIPES)
                .inputFluids(GTMaterials.Lubricant.getFluid(1))
                .inputStress(512)
                .duration(40)
                .EUt(-32)
                .addData("stress", 512)
                .save(provider);
        CTPPRecipeBuilder.of(CTPP.id("steam"),KINETIC_STEAM_TURBINE_RECIPES)
                .inputFluids(GTMaterials.Steam.getFluid(640))
                .outputFluids(GTMaterials.DistilledWater.getFluid(4))
                .outputStress(8192)
                .addData("stress", 8192)
                .duration(10)
                .save(provider);
    }
}
