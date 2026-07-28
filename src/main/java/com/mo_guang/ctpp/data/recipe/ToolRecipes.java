package com.mo_guang.ctpp.data.recipe;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.OreProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

import com.mo_guang.ctpp.data.recipe.builder.diesel.HammerRecipeBuilder;
import com.mo_guang.ctpp.data.recipe.builder.diesel.WireCuttingRecipeBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.IS_MAGNETIC;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;

public class ToolRecipes {

    public static void init(@NotNull Consumer<FinishedRecipe> provider) {
        for (Material material : GTCEuAPI.materialManager.getRegisteredMaterials()) {
            if (material.hasFlag(MaterialFlags.DISABLE_MATERIAL_RECIPES)) {
                continue;
            }
            processFineWire(provider, material);
            processOre(provider, material);
        }
    }

    private static void processFineWire(@NotNull Consumer<FinishedRecipe> provider, @NotNull Material material) {
        if (!material.shouldGenerateRecipesFor(wireFine) || !material.hasProperty(PropertyKey.INGOT)) {
            return;
        }
        ItemStack fineWireStack = ChemicalHelper.get(wireFine, material.hasFlag(IS_MAGNETIC) ?
                material.getProperty(PropertyKey.INGOT).getMacerateInto() : material);

        if (!ChemicalHelper.get(foil, material).isEmpty()) {
            WireCuttingRecipeBuilder.builder(String.format("fine_wire_%s", material.getName()))
                    .input(foil, material)
                    .output(fineWireStack)
                    .save(provider);
        }
    }

    private static void processOre(@NotNull Consumer<FinishedRecipe> provider, @NotNull Material material) {
        OreProperty property = material.getProperty(PropertyKey.ORE);
        if (property == null) {
            return;
        }
        if (material.shouldGenerateRecipesFor(crushed)) {
            HammerRecipeBuilder.builder(String.format("crushed_ore_to_dust_%s", material.getName()))
                    .input(crushed, material)
                    .output(dustImpure, material)
                    .save(provider);
        }

        if (material.shouldGenerateRecipesFor(crushedRefined)) {
            HammerRecipeBuilder.builder(String.format("centrifuged_ore_to_dust_%s", material.getName()))
                    .input(crushedRefined, material)
                    .output(dust, material)
                    .save(provider);
        }

        if (material.shouldGenerateRecipesFor(crushedPurified)) {
            HammerRecipeBuilder.builder(String.format("purified_ore_to_dust_%s", material.getName()))
                    .input(crushedPurified, material)
                    .output(dustPure, material)
                    .save(provider);
        }
    }
}
