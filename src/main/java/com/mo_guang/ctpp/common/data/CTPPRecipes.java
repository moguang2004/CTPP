package com.mo_guang.ctpp.common.data;

import com.mo_guang.ctpp.recipe.KineticGeneratorRecipes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.data.recipe.CraftingComponent.*;
import static com.gregtechceu.gtceu.data.recipe.CraftingComponent.HULL;
import static com.gregtechceu.gtceu.data.recipe.misc.MetaTileEntityLoader.registerMachineRecipe;

public class CTPPRecipes {
    public static void init(Consumer<FinishedRecipe> provider) {
        registerMachineRecipe(provider, false, CTPPMachines.KINETIC_MIXER, "GRG", "GEG", "CMC", 'M', HULL, 'R',
                ROTOR, 'C', AllItems.PRECISION_MECHANISM, 'G', GLASS, 'E', AllBlocks.SHAFT);
        registerMachineRecipe(provider, false, CTPPMachines.ELECTRIC_GEAR_BOX_2A, "WMW", "RER", "CHC", 'H', HULL,
                'C', CIRCUIT, 'E', AllBlocks.SHAFT.asStack(), 'W', CABLE, 'M', MOTOR, 'R', ROTOR);
        registerMachineRecipe(provider, false, CTPPMachines.ELECTRIC_GEAR_BOX_8A, "WMW", "RER", "CHC", 'H', HULL,
                'C', CIRCUIT, 'E', AllBlocks.SHAFT.asStack(), 'W', CABLE_QUAD, 'M', MOTOR, 'R', ROTOR);
        registerMachineRecipe(provider, false, CTPPMachines.ELECTRIC_GEAR_BOX_16A, "WMW", "RER", "CHC", 'H', HULL,
                'C', CIRCUIT, 'E', AllBlocks.SHAFT.asStack(), 'W', CABLE_OCT, 'M', MOTOR, 'R', ROTOR);
        registerMachineRecipe(provider, false, CTPPMachines.ELECTRIC_GEAR_BOX_32A, "WMW", "RER", "CHC", 'H', HULL,
                'C', CIRCUIT, 'E', AllBlocks.SHAFT.asStack(), 'W', CABLE_HEX, 'M', MOTOR, 'R', ROTOR);
        registerMachineRecipe(provider, false, CTPPMachines.KINETIC_INPUT_BOX, " S ", " H ", "   ", 'S',
                AllBlocks.SHAFT, 'H', HULL);
        registerMachineRecipe(provider, false, CTPPMachines.KINETIC_OUTPUT_BOX, "   ", " H ", " S ", 'S',
                AllBlocks.SHAFT, 'H', HULL);
        KineticGeneratorRecipes.init(provider);
    }
}
