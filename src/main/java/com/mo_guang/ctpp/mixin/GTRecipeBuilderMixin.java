package com.mo_guang.ctpp.mixin;

import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.RecipeCondition;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.mo_guang.ctpp.api.StressRecipeCapability;
import com.mo_guang.ctpp.common.builder.IKineticRecipeBuilder;
import com.mo_guang.ctpp.common.condition.RPMCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GTRecipeBuilder.class)
public abstract class GTRecipeBuilderMixin implements IKineticRecipeBuilder {
    @Shadow(remap = false)
    public abstract <T> GTRecipeBuilder input(RecipeCapability<T> capability, T obj);
    @Shadow(remap = false)
    public abstract <T> GTRecipeBuilder output(RecipeCapability<T> capability, T obj);
    @Shadow(remap = false)
    public abstract GTRecipeBuilder addCondition(RecipeCondition condition);
    @Unique
    public GTRecipeBuilder inputStress_(float stress) {
        return input(StressRecipeCapability.CAP, stress);
    }
    @Unique
    public GTRecipeBuilder outputStress_(float stress) {
        return output(StressRecipeCapability.CAP, stress);
    }
    @Unique
    public GTRecipeBuilder rpm_(float rpm, boolean reverse) {
        return addCondition(new RPMCondition(rpm).setReverse(reverse));
    }
    @Unique
    public GTRecipeBuilder rpm_(float rpm) {
        return rpm_(rpm, false);
    }
}
