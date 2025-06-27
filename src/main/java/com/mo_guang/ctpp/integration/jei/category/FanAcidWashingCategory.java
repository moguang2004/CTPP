package com.mo_guang.ctpp.integration.jei.category;

import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.mo_guang.ctpp.common.kinetic.fan.acidwashing.AcidwashingRecipe;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

public class FanAcidWashingCategory extends ProcessingViaFanCategory.MultiOutput<AcidwashingRecipe> {
    public FanAcidWashingCategory(Info<AcidwashingRecipe> info) {
        super(info);
    }

    @Override
    protected void renderAttachedBlock(GuiGraphics guiGraphics) {
        GuiGameElement.of(GTMaterials.SulfuricAcid.getFluid().defaultFluidState().createLegacyBlock())
                .rotateBlock(0, 180, 0)
                .scale(SCALE)
                .atLocal(0, 0, 2)
                .lighting(AnimatedKinetics.DEFAULT_LIGHTING)
                .render(guiGraphics);
    }
}
