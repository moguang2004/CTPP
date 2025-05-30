package com.mo_guang.ctpp.integration.jei.category;

import com.mo_guang.ctpp.common.kinetic.fan.breathing.BreathingRecipe;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.Blocks;

import static com.mo_guang.ctpp.common.data.CTPPRecipeTypeInfo.BREATHING;

public class FanBreathingCategory extends ProcessingViaFanCategory.MultiOutput<BreathingRecipe> {
    public FanBreathingCategory(Info<BreathingRecipe> info) {
        super(info);
    }

    @Override
    protected void renderAttachedBlock(GuiGraphics guiGraphics) {
        GuiGameElement.of(Blocks.DRAGON_HEAD)
                .scale(SCALE)
                .atLocal(0, 0, 2)
                .lighting(AnimatedKinetics.DEFAULT_LIGHTING)
                .render(guiGraphics);
    }
}
