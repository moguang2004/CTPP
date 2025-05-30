package com.mo_guang.ctpp.common.kinetic.fan.oiling;

import com.mo_guang.ctpp.common.data.CTPPRecipeTypeInfo;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class OilingRecipe extends ProcessingRecipe<OilingRecipe.OilingWrapper> {
    public OilingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(CTPPRecipeTypeInfo.OILING, params);
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
    public boolean matches(OilingWrapper oilingWrapper, Level level) {
        return false;
    }

    public static class OilingWrapper extends RecipeWrapper {

        public OilingWrapper() {
            super(new ItemStackHandler(1));
        }
    }
}
