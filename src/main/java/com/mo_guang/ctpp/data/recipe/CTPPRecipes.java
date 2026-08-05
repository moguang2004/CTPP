package com.mo_guang.ctpp.data.recipe;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.common.data.*;
import com.gregtechceu.gtceu.data.recipe.CraftingComponent;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.registry.CTPPMachines;
import com.simibubi.create.AllBlocks;
import com.tterrag.registrate.util.entry.ItemProviderEntry;

import java.util.Arrays;
import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.data.recipe.GTCraftingComponents.*;
import static com.gregtechceu.gtceu.data.recipe.GTCraftingComponents.HULL;

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

        OreProcessingRecipes.init(provider);
        ToolRecipes.init(provider);
        // MetaTileEntityLoader.registerMachineRecipe(provider, false, CTPPMachines.KINETIC_MIXER, "GRG", "GEG", "CMC",
        // "M", HULL, "R",
        // ROTOR, "C", AllItems.PRECISION_MECHANISM, "G", GLASS, "E", AllBlocks.SHAFT);
        registerMachineRecipe(provider, false, CTPPMachines.ELECTRIC_GEAR_BOX_2A, "WMW", "RER", "CHC", 'H', HULL,
                'C', CIRCUIT, 'E', AllBlocks.SHAFT.asItem(), 'W', CABLE, 'M', MOTOR, 'R', ROTOR);
        registerMachineRecipe(provider, false, CTPPMachines.ELECTRIC_GEAR_BOX_8A, "WMW", "RER", "CHC", 'H', HULL,
                'C', CIRCUIT, 'E', AllBlocks.SHAFT.asItem(), 'W', CABLE_QUAD, 'M', MOTOR, 'R', ROTOR);
        registerMachineRecipe(provider, false, CTPPMachines.ELECTRIC_GEAR_BOX_16A, "WMW", "RER", "CHC", 'H', HULL,
                'C', CIRCUIT, 'E', AllBlocks.SHAFT.asItem(), 'W', CABLE_OCT, 'M', MOTOR, 'R', ROTOR);
        registerMachineRecipe(provider, false, CTPPMachines.ELECTRIC_GEAR_BOX_32A, "WMW", "RER", "CHC", 'H', HULL,
                'C', CIRCUIT, 'E', AllBlocks.SHAFT.asItem(), 'W', CABLE_HEX, 'M', MOTOR, 'R', ROTOR);
        registerMachineRecipe(provider, true, CTPPMachines.KINETIC_INPUT_BOX, "S", "H", 'S',
                AllBlocks.SHAFT.asStack(), 'H', HULL);
        registerMachineRecipe(provider, true, CTPPMachines.KINETIC_OUTPUT_BOX, "H", "S", 'S',
                AllBlocks.SHAFT.asStack(), 'H', HULL);
        registerMachineRecipe(provider, false, "kinetic_input_box_to_output_", CTPPMachines.KINETIC_INPUT_BOX, "d", "H",
                'H', CTPPMachines.KINETIC_OUTPUT_BOX);
        registerMachineRecipe(provider, false, "kinetic_output_box_to_input_", CTPPMachines.KINETIC_OUTPUT_BOX, "d",
                "H", 'H', CTPPMachines.KINETIC_INPUT_BOX);

        VanillaRecipeHelper.addShapedRecipe(provider, CTPP.id("mechanical_upgrade_bus"),
                CTPPMachines.MECHANICAL_UPGRADE_BUS.asStack(),
                "ABA",
                "DCD",
                "ABA",
                'A', GTMaterialItems.MATERIAL_ITEMS.get(TagPrefix.screw, GTMaterials.WroughtIron).asStack(),
                'B', GTMaterialItems.MATERIAL_ITEMS.get(TagPrefix.plate, GTMaterials.Iron).asStack(),
                'C', GTMachines.ITEM_IMPORT_BUS[GTValues.ULV].asStack(),
                'D', Items.GLASS_PANE.getDefaultInstance());
    }

    public static void registerMachineRecipe(Consumer<FinishedRecipe> provider, boolean setMaterialInfoData,
                                             MachineDefinition[] machines, Object... recipe) {
        registerMachineRecipe(provider, setMaterialInfoData, "", machines, recipe);
    }

    public static void registerMachineRecipe(Consumer<FinishedRecipe> provider,
                                             boolean setMaterialInfoData,
                                             String prefix,
                                             MachineDefinition[] machines,
                                             Object... recipe) {
        for (MachineDefinition machine : machines) {

            // Needed to skip certain tiers if not enabled.
            // Leaves UHV+ machine recipes to be implemented by addons.
            if (machine != null) {
                Object[] prepRecipe = prepareRecipe(machine.getTier(), Arrays.copyOf(recipe, recipe.length));
                if (prepRecipe == null) {
                    return;
                }
                VanillaRecipeHelper.addShapedRecipe(provider, setMaterialInfoData, CTPP.id(prefix + machine.getName()),
                        machine.asStack(),
                        prepRecipe);
            }
        }
    }

    private static Object[] prepareRecipe(int tier, Object... recipe) {
        for (int i = 0; i < recipe.length; i++) {
            if (recipe[i] instanceof CraftingComponent) {
                Object component = ((CraftingComponent) recipe[i]).get(tier);
                recipe[i] = component;
            } else if (recipe[i] instanceof MachineDefinition[] machines) {
                recipe[i] = machines[tier].asStack();
            } else if (recipe[i] instanceof Item item) {
                recipe[i] = new ItemStack(item);
            } else if (recipe[i] instanceof Block block) {
                recipe[i] = new ItemStack(block);
            } else if (recipe[i] instanceof ItemProviderEntry<?> itemEntry) {
                recipe[i] = itemEntry.asStack();
            }
        }
        return recipe;
    }
}
