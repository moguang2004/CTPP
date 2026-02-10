package com.mo_guang.ctpp.common.data.recipe;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.*;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.common.data.recipe.builder.CTPPRecipeBuilder;
import com.mo_guang.ctpp.registry.CTPPMachines;
import com.mo_guang.ctpp.registry.CTPPRecipeTypes;
import com.simibubi.create.AllBlocks;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.data.recipe.GTCraftingComponents.*;
import static com.gregtechceu.gtceu.data.recipe.GTCraftingComponents.HULL;
import static com.gregtechceu.gtceu.data.recipe.misc.MetaTileEntityLoader.registerMachineRecipe;

public class CTPPRecipes {
    public static void init(Consumer<FinishedRecipe> provider) {
        BigDamRecipes.init(provider);
        ItemRecipes.init(provider);
        KineticSteamTurbineRecipes.init(provider);
        SeaweedFarmRecipes.init(provider);
        SmashingFactoryRecipes.init(provider);
        WindmillControlRecipes.init(provider);
        KineticGeneratorRecipes.init(provider);
        BoomOfCreateRecipes.init(provider);

//        MetaTileEntityLoader.registerMachineRecipe(provider, false, CTPPMachines.KINETIC_MIXER, "GRG", "GEG", "CMC", "M", HULL, "R",
//                ROTOR, "C", AllItems.PRECISION_MECHANISM, "G", GLASS, "E", AllBlocks.SHAFT);
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
        VanillaRecipeHelper.addShapedRecipe(provider, "mechanical_upgrade_bus", CTPPMachines.MECHANICAL_UPGRADE_BUS.asStack(),
                "ABA",
                "DCD",
                "ABA",
                'A', GTMaterialItems.MATERIAL_ITEMS.get(TagPrefix.screw, GTMaterials.WroughtIron).asStack(),
                'B', GTMaterialItems.MATERIAL_ITEMS.get(TagPrefix.plate, GTMaterials.Iron).asStack(),
                'C', GTMachines.ITEM_IMPORT_BUS[GTValues.ULV].asStack(),
                'D', Items.GLASS_PANE.getDefaultInstance()
                );
    }
}
