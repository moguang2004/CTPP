package com.mo_guang.ctpp.client.renderer;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import com.mo_guang.ctpp.CTPP;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;

public class GTWireCutterRenderer extends CustomRenderedItemModelRenderer {

    private static final ResourceLocation WIRE_CUTTER_CUT = CTPP.id("item/tools/wire_cutter_cut");

    private static final PartialModel OPEN_MODEL = PartialModel.of(WIRE_CUTTER_CUT);

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer,
                          ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light,
                          int overlay) {
        Player player = Minecraft.getInstance().player;
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        CompoundTag tag = stack.getOrCreateTag();

        if (!tag.contains("ProcessingItem") || player == null)
            renderer.render(model.getOriginalModel(), light);
        else {
            float time = ((AnimationTickHolder.getTicks() + AnimationTickHolder.getPartialTicks()) % 10) / 10;
            ItemStack processingItem = ItemStack.of(tag.getCompound("ProcessingItem"));
            ms.pushPose();
            TransformStack.of(ms)
                    .translate(0.1, 0.2, 0)
                    .rotateZDegrees((float) ((AnimationTickHolder.getTicks() + 5) / 10) * -30);
            itemRenderer.renderStatic(processingItem, ItemDisplayContext.GUI, light, overlay, ms, buffer,
                    Minecraft.getInstance().level, 0);

            ms.popPose();
            ms.pushPose();

            TransformStack.of(ms)
                    .translate(0, 0, 0.1)
                    .rotateYDegrees(32);
            if (time > 0.5)
                renderer.render(model.getOriginalModel(), light);
            else
                renderer.render(OPEN_MODEL.get(), light);
            ms.popPose();
        }
    }
}
