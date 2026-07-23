package com.mo_guang.ctpp.mixin.create;

import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.ctnhlang.Key;
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
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

@Mixin(value = CreateJEI.class, remap = false)
public abstract class CreateJEIMixin {

    @Key("create.recipe.fan_breathing")
    @CN("批量龙吟")
    @EN("Dragon Fan Processing")
    private static Lang breathingCategory;

    @CN("在龙首后放置鼓风机")
    @EN("Place the fan behind the Dragon head")
    private static Lang breathingFan;

    @Key("create.recipe.fan_acid_washing")
    @CN("批量酸洗")
    @EN("AcidWashing Fan Processing")
    private static Lang acidWashingCategory;

    @CN("在硫酸后放置鼓风机")
    @EN("Place the fan behind the Sulfuric acid")
    private static Lang acidWashingFan;

    @Shadow
    protected abstract <T extends Recipe<?>> CreateJEI.CategoryBuilder<T> builder(Class<? extends T> recipeClass);

    @Inject(method = "loadCategories", at = @At("TAIL"))
    void registerCTPPCategories(CallbackInfo ci) {
        CreateRecipeCategory<?> breathing = builder(BreathingRecipe.class)
                .addTypedRecipes(CTPPRecipeTypeInfo.BREATHING)
                .catalystStack(() -> AllBlocks.ENCASED_FAN.asStack()
                        .setHoverName(breathingFan.translate()
                                .withStyle(style -> style.withItalic(false))))
                .catalystStack(Items.DRAGON_HEAD::getDefaultInstance)
                .doubleItemIcon(AllItems.PROPELLER.get(), Items.DRAGON_BREATH)
                .emptyBackground(178, 72)
                .build("fan_breathing", FanBreathingCategory::new);

        CreateRecipeCategory<?> acidwashing = builder(AcidwashingRecipe.class)
                .addTypedRecipes(CTPPRecipeTypeInfo.ACIDWASHING)
                .catalystStack(() -> AllBlocks.ENCASED_FAN.asStack()
                        .setHoverName(acidWashingFan.translate()
                                .withStyle(style -> style.withItalic(false))))
                .catalystStack(GTMaterials.SulfuricAcid.getBucket()::getDefaultInstance)
                .doubleItemIcon(AllItems.PROPELLER.get(), GTMaterials.SulfuricAcid.getBucket())
                .emptyBackground(178, 72)
                .build("fan_acid_washing", FanAcidWashingCategory::new);
    }
}
