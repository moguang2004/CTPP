package com.mo_guang.ctpp.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

/** Renders a toolbox as a small case hanging from either side of the waist. */
public final class CTPPToolboxCurioRenderer implements ICurioRenderer {

    public static final CTPPToolboxCurioRenderer INSTANCE = new CTPPToolboxCurioRenderer();

    // These values are intentionally kept together for easy in-game positioning tweaks.
    private static final float SIDE_OFFSET = 0.2f;
    private static final float HEIGHT_OFFSET = 0.55f;
    private static final float FRONT_OFFSET = 0.2f;
    private static final float SCALE = 0.8f;
    private static final float PITCH = 180f;
    private static final float YAW = -10f;
    private static final float ROLL = 0f;

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
                                                                          ItemStack stack,
                                                                          SlotContext slotContext,
                                                                          PoseStack pose,
                                                                          RenderLayerParent<T, M> renderLayerParent,
                                                                          MultiBufferSource buffer,
                                                                          int light,
                                                                          float limbSwing,
                                                                          float limbSwingAmount,
                                                                          float partialTicks,
                                                                          float ageInTicks,
                                                                          float netHeadYaw,
                                                                          float headPitch) {
        LivingEntity entity = slotContext.entity();

        boolean right = slotContext.index() == 0;
        // Curios supplies a body-relative pose. Index 0 is the entity's left side.
        float side = right ? -SIDE_OFFSET : SIDE_OFFSET;
        pose.pushPose();

        ICurioRenderer.translateIfSneaking(pose, entity);
        ICurioRenderer.rotateIfSneaking(pose, entity);

        pose.translate(side, HEIGHT_OFFSET, FRONT_OFFSET);

        pose.mulPose(Axis.XP.rotationDegrees(PITCH));
        pose.mulPose(Axis.YP.rotationDegrees(right ? -YAW : YAW));
        pose.mulPose(Axis.ZP.rotationDegrees(right ? -ROLL : ROLL));

        pose.scale(SCALE, SCALE, SCALE);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack,
                ItemDisplayContext.FIXED,
                light,
                net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
                pose,
                buffer,
                entity.level(),
                entity.getId());
        pose.popPose();
    }
}
