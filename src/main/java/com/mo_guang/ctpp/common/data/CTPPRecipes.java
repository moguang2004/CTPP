package com.mo_guang.ctpp.common.data;

import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.recipe.CTPPRecipeBuilder;
import com.mo_guang.ctpp.recipe.KineticGeneratorRecipes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.data.recipe.CraftingComponent.*;
import static com.gregtechceu.gtceu.data.recipe.CraftingComponent.HULL;
import static com.gregtechceu.gtceu.data.recipe.misc.MetaTileEntityLoader.registerMachineRecipe;

public class CTPPRecipes {
    public static void init(Consumer<FinishedRecipe> provider) {
        registerMachineRecipe(provider, false, CTPPMachines.KINETIC_MIXER, "GRG", "GEG", "CMC", "M", HULL, "R",
                ROTOR, "C", AllItems.PRECISION_MECHANISM, "G", GLASS, "E", AllBlocks.SHAFT);
        registerMachineRecipe(provider, false, CTPPMachines.ELECTRIC_GEAR_BOX_2A, "WMW", "RER", "CHC", "H", HULL,
                "C", CIRCUIT, "E", AllBlocks.SHAFT.asStack(), "W", CABLE, "M", MOTOR, "R", ROTOR);
        registerMachineRecipe(provider, false, CTPPMachines.ELECTRIC_GEAR_BOX_8A, "WMW", "RER", "CHC", "H", HULL,
                "C", CIRCUIT, "E", AllBlocks.SHAFT.asStack(), "W", CABLE_QUAD, "M", MOTOR, "R", ROTOR);
        registerMachineRecipe(provider, false, CTPPMachines.ELECTRIC_GEAR_BOX_16A, "WMW", "RER", "CHC", "H", HULL,
                "C", CIRCUIT, "E", AllBlocks.SHAFT.asStack(), "W", CABLE_OCT, "M", MOTOR, "R", ROTOR);
        registerMachineRecipe(provider, false, CTPPMachines.ELECTRIC_GEAR_BOX_32A, "WMW", "RER", "CHC", "H", HULL,
                "C", CIRCUIT, "E", AllBlocks.SHAFT.asStack(), "W", CABLE_HEX, "M", MOTOR, "R", ROTOR);
        registerMachineRecipe(provider, false, CTPPMachines.KINETIC_INPUT_BOX, " S ", " H ", "   ", "S",
                AllBlocks.SHAFT, "H", HULL);
        registerMachineRecipe(provider, false, CTPPMachines.KINETIC_OUTPUT_BOX, "   ", " H ", " S ", "S",
                AllBlocks.SHAFT, "H", HULL);
        KineticGeneratorRecipes.init(provider);
        CTPPRecipeBuilder.of(CTPP.id("seaweed"),CTPPRecipeTypes.SEAWEED_FARM)
                .inputStress(512)
                .inputItems(GTItems.FERTILIZER.asStack())
                .notConsumable(Items.KELP.getDefaultInstance())
                .duration(400)
                .outputItems(Items.KELP.getDefaultInstance().copyWithCount(4))
                .chancedOutput(Items.KELP.getDefaultInstance().copyWithCount(2), 2000, 500)
                .chancedOutput(Items.KELP.getDefaultInstance().copyWithCount(1), 500, 500)
                .addData("stress", 512)
                .save(provider);
        CTPPRecipeBuilder.of(CTPP.id("windmill_control"),CTPPRecipeTypes.WINDMILL_CONTROL)
                .inputFluids(GTMaterials.Lubricant.getFluid(25))
                .duration(200)
                .outputStress(512)
                .save(provider);
        CTPPRecipeBuilder.of(CTPP.id("boom_of_create1"),CTPPRecipeTypes.BOOM_OF_CREATE)
                .inputItems(GTBlocks.INDUSTRIAL_TNT.get().asItem().getDefaultInstance())
                .inputFluids(GTMaterials.PCBCoolant.getFluid(50))
                .outputStress(16777216)
                .duration(200)
                .addData("stress", 16777216)
                .save(provider);
        CTPPRecipeBuilder.of(CTPP.id("boom_of_create2"),CTPPRecipeTypes.BOOM_OF_CREATE)
                .inputItems(Items.TNT.getDefaultInstance().copyWithCount(4))
                .inputFluids(GTMaterials.PCBCoolant.getFluid(50))
                .outputStress(16777216)
                .duration(200)
                .addData("stress", 16777216)
                .save(provider);
        CTPPRecipeBuilder.of(CTPP.id("boom_of_create3"),CTPPRecipeTypes.BOOM_OF_CREATE)
                .inputItems(GTItems.DYNAMITE.get().getDefaultInstance().copyWithCount(2))
                .inputFluids(GTMaterials.PCBCoolant.getFluid(50))
                .outputStress(16777216)
                .duration(200)
                .addData("stress", 16777216)
                .save(provider);
        CTPPRecipeBuilder.of(CTPP.id("boom_of_create4"),CTPPRecipeTypes.BOOM_OF_CREATE)
                .inputItems(GTBlocks.POWDERBARREL.get().asItem().getDefaultInstance().copyWithCount(8))
                .inputFluids(GTMaterials.PCBCoolant.getFluid(50))
                .outputStress(16777216)
                .duration(200)
                .addData("stress", 16777216)
                .save(provider);
    }
}
