package com.mo_guang.ctpp.integration.emi;

import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.common.data.recipe.fan_processing.CTPPRecipeTypeInfo;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiStack;

@EmiEntrypoint
public final class CTPPEmiPlugin implements EmiPlugin {

    private static final EmiRecipeCategory FAN_BREATHING = category("fan_breathing",
            EmiStack.of(AllItems.PROPELLER.asStack()), EmiStack.of(net.minecraft.world.item.Items.DRAGON_BREATH));
    private static final EmiRecipeCategory FAN_ACID_WASHING = category("fan_acid_washing",
            EmiStack.of(AllItems.PROPELLER.asStack()), EmiStack.of(GTMaterials.SulfuricAcid.getBucket()));

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(FAN_BREATHING);
        registry.addCategory(FAN_ACID_WASHING);

        registry.addWorkstation(FAN_BREATHING, fan("breathing"));
        registry.addWorkstation(FAN_ACID_WASHING, fan("acid_washing"));

        addFanRecipes(registry, CTPPRecipeTypeInfo.BREATHING, FAN_BREATHING,
                CTPPFanEmiRecipe.FanAttachment.DRAGON_BREATH);
        addFanRecipes(registry, CTPPRecipeTypeInfo.ACIDWASHING, FAN_ACID_WASHING,
                CTPPFanEmiRecipe.FanAttachment.SULFURIC_ACID);

        addEncasingRecipes(registry);
    }

    private static EmiRecipeCategory category(String id, EmiStack first, EmiStack second) {
        return new EmiRecipeCategory(CTPP.id(id), (graphics, x, y, delta) -> {
            first.render(graphics, x, y, delta);
            second.render(graphics, x + 8, y + 8, delta);
        }) {

            @Override
            public Component getName() {
                return Component.translatable("ctpp.recipe." + id);
            }
        };
    }

    private static EmiStack fan(String category) {
        ItemStack stack = AllBlocks.ENCASED_FAN.asStack();
        stack.setHoverName(Component.translatable("ctpp.recipe." + category + ".fan")
                .withStyle(style -> style.withItalic(false)));
        return EmiStack.of(stack);
    }

    private static void addFanRecipes(EmiRegistry registry, CTPPRecipeTypeInfo type, EmiRecipeCategory category,
                                      CTPPFanEmiRecipe.FanAttachment attachment) {
        for (Recipe<?> recipe : registry.getRecipeManager().getAllRecipesFor(type.getType())) {
            if (recipe instanceof ProcessingRecipe<?> processingRecipe) {
                registry.addRecipe(new CTPPFanEmiRecipe(category, processingRecipe, attachment));
            }
        }
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
                .id(CTPP.id("world/encasing/" + id))
                .leftInput(EmiStack.of(cogwheel))
                .rightInput(EmiStack.of(casing), true)
                .output(EmiStack.of(output))
                .build());
    }
}
