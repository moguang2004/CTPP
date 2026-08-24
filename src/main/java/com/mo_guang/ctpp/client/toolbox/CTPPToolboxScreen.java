package com.mo_guang.ctpp.client.toolbox;

import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import com.mo_guang.ctpp.common.block.CTPPToolboxBlock;
import com.mo_guang.ctpp.common.menu.CTPPToolboxMenu;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxInventory;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxSounds;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxSourceId;
import com.mo_guang.ctpp.network.packet.CTPPToolboxActionPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;

import java.util.List;

public final class CTPPToolboxScreen extends AbstractSimiContainerScreen<CTPPToolboxMenu> {

    private static final AllGuiTextures BACKGROUND = AllGuiTextures.TOOLBOX;
    private static final AllGuiTextures PLAYER = AllGuiTextures.PLAYER_INVENTORY;
    private Slot hoveredToolboxSlot;
    private final LerpedFloat lid = LerpedFloat.linear().startWithValue(0);
    private final LerpedFloat drawers = LerpedFloat.linear().startWithValue(0);

    public CTPPToolboxScreen(CTPPToolboxMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        setWindowSize(30 + BACKGROUND.getWidth(), BACKGROUND.getHeight() + PLAYER.getHeight() - 24);
        setWindowOffset(-11, 0);
        super.init();
        clearWidgets();
        IconButton confirm = new IconButton(leftPos + 30 + BACKGROUND.getWidth() - 33,
                topPos + BACKGROUND.getHeight() - 24, AllIcons.I_CONFIRM);
        confirm.withCallback(() -> minecraft.player.closeContainer());
        addRenderableWidget(confirm);
        IconButton deposit = new IconButton(leftPos + 30 + 81, topPos + 69, AllIcons.I_TOOLBOX);
        deposit.withCallback(() -> GTNetwork.sendToServer(new CTPPToolboxActionPacket(
                CTPPToolboxActionPacket.Action.DEPOSIT, menu.source(), -1,
                minecraft.player.getInventory().selected)));
        addRenderableWidget(deposit);
    }

    @Override
    public void containerTick() {
        lid.chase(1, 0.2f, LerpedFloat.Chaser.LINEAR);
        drawers.chase(1, 0.2f, LerpedFloat.Chaser.EXP);
        lid.tickChaser();
        drawers.tickChaser();
        super.containerTick();
    }

    @Override
    public void removed() {
        if (minecraft.level != null && menu.source().type() != CTPPToolboxSourceId.Type.BLOCK &&
                minecraft.player != null) {
            CTPPToolboxSounds.playCloseLocally(minecraft.level, minecraft.player.blockPosition());
        }
        super.removed();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        menu.renderPass = true;
        super.render(graphics, mouseX, mouseY, partialTicks);
        menu.renderPass = false;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int x = leftPos + imageWidth - BACKGROUND.getWidth();
        int y = topPos;
        BACKGROUND.render(graphics, x, y);
        graphics.drawString(font, title, x + 15, y + 4, 0x592424, false);
        PLAYER.render(graphics, leftPos, topPos + imageHeight - PLAYER.getHeight());
        renderToolbox(graphics, x + BACKGROUND.getWidth() + 50, y + BACKGROUND.getHeight() + 12,
                partialTicks);

        hoveredToolboxSlot = null;
        for (int compartment = 0; compartment < CTPPToolboxInventory.COMPARTMENTS; compartment++) {
            Slot slot = menu.getSlot(compartment * CTPPToolboxInventory.STACKS_PER_COMPARTMENT);
            ItemStack stack = slot.getItem();
            if (stack.isEmpty()) stack = menu.getFilter(compartment);
            if (!stack.isEmpty()) {
                graphics.renderItem(stack, leftPos + slot.x, topPos + slot.y);
                graphics.renderItemDecorations(font, stack, leftPos + slot.x, topPos + slot.y,
                        String.valueOf(menu.totalCountInCompartment(compartment)));
            }
            if (isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY)) {
                hoveredToolboxSlot = slot;
                RenderSystem.disableDepthTest();
                RenderSystem.colorMask(true, true, true, false);
                int color = getSlotColor(slot.index);
                graphics.fillGradient(leftPos + slot.x, topPos + slot.y,
                        leftPos + slot.x + 16, topPos + slot.y + 16, color, color);
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.enableDepthTest();
            }
        }
    }

    private void renderToolbox(GuiGraphics graphics, int x, int y, float partialTicks) {
        ItemStack display = menu.displayStack();
        if (!(display.getItem() instanceof BlockItem blockItem) ||
                !(blockItem.getBlock() instanceof CTPPToolboxBlock block))
            return;
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y, 100);
        pose.scale(50, 50, 50);
        pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-22));
        pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-202));
        GuiGameElement.of(block.defaultBlockState()).render(graphics);
        pose.pushPose();
        pose.translate(0, -6 / 16f, 12 / 16f);
        float lidProgress = lid.getValue(partialTicks);
        float drawerProgress = drawers.getValue(partialTicks);
        if (menu.source().type() == CTPPToolboxSourceId.Type.BLOCK &&
                menu.source().blockPos() != null && minecraft.level.getBlockEntity(menu.source()
                        .blockPos()) instanceof com.mo_guang.ctpp.common.blockentity.CTPPToolboxBlockEntity entity) {
            lidProgress = entity.lid.getValue(partialTicks);
            drawerProgress = entity.drawers.getValue(partialTicks);
        }
        pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-105 * lidProgress));
        pose.translate(0, 6 / 16f, -12 / 16f);
        GuiGameElement.of(AllPartialModels.TOOLBOX_LIDS.get(block.getColor())).render(graphics);
        pose.popPose();
        for (int offset = 0; offset < 2; offset++) {
            pose.pushPose();
            pose.translate(0, -offset / 8f, drawerProgress * -0.175f * (2 - offset));
            GuiGameElement.of(AllPartialModels.TOOLBOX_DRAWER).render(graphics);
            pose.popPose();
        }
        pose.popPose();
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        if (hoveredToolboxSlot != null) hoveredSlot = hoveredToolboxSlot;
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        return List.of(new Rect2i(leftPos + 30 + BACKGROUND.getWidth(),
                topPos + BACKGROUND.getHeight() - 55, 72, 68));
    }
}
