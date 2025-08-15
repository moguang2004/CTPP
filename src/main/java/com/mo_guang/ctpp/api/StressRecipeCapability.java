package com.mo_guang.ctpp.api;

import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.content.SerializerFloat;
import com.mo_guang.ctpp.common.data.CTPPRecipeHelper;
import com.mo_guang.ctpp.common.machine.multiblock.KineticOutputMachine;
import com.mo_guang.ctpp.common.machine.multiblock.KineticWorkableMultiblockMachine;

import java.util.Collection;
import java.util.List;

public class StressRecipeCapability extends RecipeCapability<Float> {
    public final static StressRecipeCapability CAP = new StressRecipeCapability();
    protected StressRecipeCapability() {
        super("su", 0xFF77A400, false, 4, SerializerFloat.INSTANCE);
    }
    @Override
    public Float copyInner(Float content) {
        return content;
    }
    @Override
    public Float copyWithModifier(Float content, ContentModifier modifier) {
        return modifier.apply(content);
    }
    @Override
    public List<Object> compressIngredients(Collection<Object> ingredients) {
        return List.of(ingredients.stream().map(Float.class::cast).reduce(0f, Float::sum));
    }
    @Override
    public int getMaxParallelByInput(IRecipeCapabilityHolder holder, GTRecipe recipe, int parallelAmount, boolean tick) {
        if(holder instanceof KineticWorkableMultiblockMachine){
            float inputStress = Math.abs(((KineticWorkableMultiblockMachine) holder).getTotalInputStress());
            float recipeStress = (float) CTPPRecipeHelper.getInputStress(recipe);
            if (recipeStress == 0) return parallelAmount;
            return (int) Math.min(inputStress/recipeStress, parallelAmount);
        }
        return super.getMaxParallelByInput(holder, recipe, parallelAmount, tick);
    }

    @Override
    public int limitMaxParallelByOutput(IRecipeCapabilityHolder holder, GTRecipe recipe, int maxMultiplier, boolean tick) {
        if (holder instanceof KineticOutputMachine kineticOutputMachine){
            float outputStress = Math.abs(kineticOutputMachine.getMaxOutputStress());
            float recipeStress = (float) CTPPRecipeHelper.getOutputStress(recipe);
            if (recipeStress == 0) return maxMultiplier;
            return (int) Math.min(outputStress/recipeStress, maxMultiplier);
        }
        return super.limitMaxParallelByOutput(holder, recipe, maxMultiplier, tick);
    }
}