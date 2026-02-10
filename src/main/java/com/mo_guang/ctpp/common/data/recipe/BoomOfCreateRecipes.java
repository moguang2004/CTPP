package com.mo_guang.ctpp.common.data.recipe;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.common.data.recipe.builder.CTPPRecipeBuilder;
import com.mo_guang.ctpp.registry.CTPPMultiblockMachines;
import com.mo_guang.ctpp.registry.CTPPRecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class BoomOfCreateRecipes {
    public static void init(Consumer<FinishedRecipe> provider) {
        VanillaRecipeHelper.addShapedRecipe(provider, "boom_of_create", new ItemStack(CTPPMultiblockMachines.BOOM_OF_CREATE.getItem()),
                "DED",
                "ACA",
                "BAB",
                'A', GTItems.CONVEYOR_MODULE_IV,
                'B', CustomTags.IV_CIRCUITS,
                'C', GTMachines.HULL[GTValues.IV],
                'D', GTItems.ELECTRIC_PUMP_IV,
                'E', CTPPMultiblockMachines.BIG_DAM.get());
        CTPPRecipeBuilder.of(CTPP.id("boom_of_create1"), CTPPRecipeTypes.BOOM_OF_CREATE)
                .inputItems(GTBlocks.INDUSTRIAL_TNT.get().asItem().getDefaultInstance())
                .inputFluids(GTMaterials.PCBCoolant.getFluid(50))
                .outputStress(16777216)
                .duration(200)
                .save(provider);
        CTPPRecipeBuilder.of(CTPP.id("boom_of_create2"),CTPPRecipeTypes.BOOM_OF_CREATE)
                .inputItems(Items.TNT.getDefaultInstance().copyWithCount(4))
                .inputFluids(GTMaterials.PCBCoolant.getFluid(50))
                .outputStress(16777216)
                .duration(200)
                .save(provider);
        CTPPRecipeBuilder.of(CTPP.id("boom_of_create3"),CTPPRecipeTypes.BOOM_OF_CREATE)
                .inputItems(GTItems.DYNAMITE.get().getDefaultInstance().copyWithCount(2))
                .inputFluids(GTMaterials.PCBCoolant.getFluid(50))
                .outputStress(16777216)
                .duration(200)
                .save(provider);
        CTPPRecipeBuilder.of(CTPP.id("boom_of_create4"),CTPPRecipeTypes.BOOM_OF_CREATE)
                .inputItems(GTBlocks.POWDERBARREL.get().asItem().getDefaultInstance().copyWithCount(8))
                .inputFluids(GTMaterials.PCBCoolant.getFluid(50))
                .outputStress(16777216)
                .duration(200)
                .save(provider);
    }
}
