package com.mo_guang.ctpp.mixin.create;

import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import com.mo_guang.ctpp.common.data.recipe.fan_processing.CTPPRecipeTypeInfo;
import com.mo_guang.ctpp.common.kinetic.fan.acidwashing.AcidwashingRecipe;
import com.mo_guang.ctpp.common.kinetic.fan.breathing.BreathingRecipe;
import com.mo_guang.ctpp.integration.jei.category.FanAcidWashingCategory;
import com.mo_guang.ctpp.integration.jei.category.FanBreathingCategory;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.CreateJEI;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CreateJEI.class, remap = false)
public abstract class CreateJEIMixin {

    @Shadow
    protected abstract <T extends Recipe<?>> CreateJEI.CategoryBuilder<T> builder(Class<? extends T> recipeClass);

    @Inject(method = "loadCategories", at = @At("TAIL"))
    void registerCTPPCategories(CallbackInfo ci) {
        CreateRecipeCategory<?> breathing = builder(BreathingRecipe.class)
                .addTypedRecipes(CTPPRecipeTypeInfo.BREATHING)
                .catalystStack(() -> AllBlocks.ENCASED_FAN.asStack()
                        .setHoverName(Component.translatable("ctpp.recipe.breathing.fan")
                                .withStyle(style -> style.withItalic(false))))
                .catalystStack(Items.DRAGON_HEAD::getDefaultInstance)
                .doubleItemIcon(AllItems.PROPELLER.get(), Items.DRAGON_BREATH)
                .emptyBackground(178, 72)
                .build("fan_breathing", FanBreathingCategory::new);

        CreateRecipeCategory<?> acidwashing = builder(AcidwashingRecipe.class)
                .addTypedRecipes(CTPPRecipeTypeInfo.ACIDWASHING)
                .catalystStack(() -> AllBlocks.ENCASED_FAN.asStack()
                        .setHoverName(Component.translatable("ctpp.recipe.acid_washing.fan")
                                .withStyle(style -> style.withItalic(false))))
                .catalystStack(GTMaterials.SulfuricAcid.getBucket()::getDefaultInstance)
                .doubleItemIcon(AllItems.PROPELLER.get(), GTMaterials.SulfuricAcid.getBucket())
                .emptyBackground(178, 72)
                .build("fan_acid_washing", FanAcidWashingCategory::new);
    }
}
