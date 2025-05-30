package com.mo_guang.ctpp.common.kinetic.fan.acidwashing;

import com.mo_guang.ctpp.common.data.CTPPRecipeTypeInfo;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class AcidwashingRecipe extends ProcessingRecipe<AcidwashingRecipe.AcidwashingWrapper> {
    public AcidwashingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(CTPPRecipeTypeInfo.ACIDWASHING, params);
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
    public boolean matches(AcidwashingWrapper acidwashingWrapper, Level level) {
        return false;
    }

    public static class AcidwashingWrapper extends RecipeWrapper {

        public AcidwashingWrapper() {
            super(new ItemStackHandler(1));
        }
    }
}
