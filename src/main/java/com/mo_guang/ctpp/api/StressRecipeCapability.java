package com.mo_guang.ctpp.api;

import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.content.SerializerFloat;
import com.mo_guang.ctpp.common.data.CTPPRecipeHelper;
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
    public int getMaxParallelRatio(IRecipeCapabilityHolder holder, GTRecipe recipe, int parallelAmount) {
        if(holder instanceof KineticWorkableMultiblockMachine){
            float inputStress = Math.abs(((KineticWorkableMultiblockMachine) holder).getTotalInputStress());
            float recipeStress = (float) CTPPRecipeHelper.getInputStress(recipe);
            return (int) (inputStress/recipeStress);
        }
        return super.getMaxParallelRatio(holder, recipe, parallelAmount);
    }
}