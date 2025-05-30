package com.mo_guang.ctpp.common.kinetic.fan.breathing;

import com.mo_guang.ctpp.common.data.CTPPRecipeTypeInfo;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class BreathingRecipe extends ProcessingRecipe<BreathingRecipe.BreathingWrapper> {
    public BreathingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(CTPPRecipeTypeInfo.BREATHING, params);
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 12;
    }

    @Override
    public boolean matches(BreathingWrapper breathingWrapper, Level level) {
        if (breathingWrapper.isEmpty())
            return false;
        return ingredients.get(0)
                .test(breathingWrapper.getItem(0));
    }

    public static class BreathingWrapper extends RecipeWrapper{

        public BreathingWrapper() {
            super(new ItemStackHandler(1));
        }
    }
}
