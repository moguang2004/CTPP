package com.mo_guang.ctpp.integration.emi;

import net.minecraft.world.item.ItemStack;

import com.mo_guang.ctpp.CTPP;
import com.simibubi.create.AllBlocks;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiStack;

@EmiEntrypoint
public final class CTPPEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        addEncasingRecipes(registry);
    }

    private static void addEncasingRecipes(EmiRegistry registry) {
        addEncasingRecipe(registry, "andesite_cogwheel", AllBlocks.COGWHEEL.asStack(),
                AllBlocks.ANDESITE_CASING.asStack(), AllBlocks.ANDESITE_ENCASED_COGWHEEL.asStack());
        addEncasingRecipe(registry, "andesite_large_cogwheel", AllBlocks.LARGE_COGWHEEL.asStack(),
                AllBlocks.ANDESITE_CASING.asStack(), AllBlocks.ANDESITE_ENCASED_LARGE_COGWHEEL.asStack());
        addEncasingRecipe(registry, "brass_cogwheel", AllBlocks.COGWHEEL.asStack(), AllBlocks.BRASS_CASING.asStack(),
                AllBlocks.BRASS_ENCASED_COGWHEEL.asStack());
        addEncasingRecipe(registry, "brass_large_cogwheel", AllBlocks.LARGE_COGWHEEL.asStack(),
                AllBlocks.BRASS_CASING.asStack(), AllBlocks.BRASS_ENCASED_LARGE_COGWHEEL.asStack());
    }

    private static void addEncasingRecipe(EmiRegistry registry, String id, ItemStack cogwheel, ItemStack casing,
                                          ItemStack output) {
        registry.addRecipe(EmiWorldInteractionRecipe.builder()
                .id(CTPP.id("/world/encasing/" + id))
                .leftInput(EmiStack.of(cogwheel))
                .rightInput(EmiStack.of(casing), true)
                .output(EmiStack.of(output))
                .build());
    }
}
