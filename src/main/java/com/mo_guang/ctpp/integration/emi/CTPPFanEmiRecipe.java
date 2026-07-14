package com.mo_guang.ctpp.integration.emi;

import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.ILightingSettings;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.CustomLightingSettings;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

public final class CTPPFanEmiRecipe extends BasicEmiRecipe {

    private static final int WIDTH = 178;
    private static final int HEIGHT = 72;
    private static final int SCALE = 24;
    private static final ILightingSettings DEFAULT_LIGHTING = CustomLightingSettings.builder()
            .firstLightRotation(12.5f, 45.0f)
            .secondLightRotation(-20.0f, 50.0f)
            .build();

    private final FanAttachment attachment;

    public CTPPFanEmiRecipe(EmiRecipeCategory category, ProcessingRecipe<?> recipe, FanAttachment attachment) {
        super(category, recipe.getId(), WIDTH, HEIGHT);
        this.attachment = attachment;

        inputs.add(EmiIngredient.of(recipe.getIngredients().get(0)));
        for (ProcessingOutput output : recipe.getRollableResults()) {
            outputs.add(EmiStack.of(output.getStack()).setChance(output.getChance()));
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int xOffsetAmount = 1 - Math.min(3, outputs.size());

        widgets.addDrawable(0, 0, width, height, (graphics, mouseX, mouseY, delta) -> renderFan(graphics,
                xOffsetAmount));
        widgets.addSlot(inputs.get(0), 5 * xOffsetAmount + 21, 48);

        boolean excessive = outputs.size() > 9;
        for (int i = 0; i < outputs.size(); i++) {
            int xOffset = (i % 3) * 19 + 9 * xOffsetAmount;
            int yOffset = (i / 3) * -19 + (excessive ? 8 : 0);
            widgets.addSlot(outputs.get(i), 141 + xOffset, 48 + yOffset)
                    .recipeContext(this);
        }
    }

    private void renderFan(GuiGraphics graphics, int xOffsetAmount) {
        AllGuiTextures.JEI_SHADOW.render(graphics, 46, 29);
        AllGuiTextures.JEI_SHADOW.render(graphics, 65, 39);
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 7 * xOffsetAmount + 54, 51);

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(56, 33, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(-12.5f));
        poseStack.mulPose(Axis.YP.rotationDegrees(22.5f));

        GuiGameElement.of(AllPartialModels.ENCASED_FAN_INNER)
                .lighting(DEFAULT_LIGHTING)
                .rotateBlock(180, 0, currentAngle() * 16)
                .scale(SCALE)
                .render(graphics);
        GuiGameElement.of(AllBlocks.ENCASED_FAN.getDefaultState())
                .lighting(DEFAULT_LIGHTING)
                .rotateBlock(0, 180, 0)
                .atLocal(0, 0, 0)
                .scale(SCALE)
                .render(graphics);

        renderAttachedBlock(graphics);
        poseStack.popPose();
    }

    private void renderAttachedBlock(GuiGraphics graphics) {
        switch (attachment) {
            case DRAGON_BREATH -> GuiGameElement.of(
                    new SkullBlockEntity(BlockPos.ZERO, Blocks.DRAGON_HEAD.defaultBlockState()))
                    .rotateBlock(0, 180, 0)
                    .scale(SCALE)
                    .atLocal(0, 0, 2)
                    .lighting(DEFAULT_LIGHTING)
                    .render(graphics);
            case SULFURIC_ACID -> GuiGameElement.of(
                    GTMaterials.SulfuricAcid.getFluid().defaultFluidState().createLegacyBlock())
                    .rotateBlock(0, 180, 0)
                    .scale(SCALE)
                    .atLocal(0, 0, 2)
                    .lighting(DEFAULT_LIGHTING)
                    .render(graphics);
        }
    }

    private static float currentAngle() {
        return (AnimationTickHolder.getRenderTime() * 4) % 360;
    }

    public enum FanAttachment {
        DRAGON_BREATH,
        SULFURIC_ACID
    }
}
