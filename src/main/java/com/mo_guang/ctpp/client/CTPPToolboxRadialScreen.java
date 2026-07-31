package com.mo_guang.ctpp.client;

import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import com.mo_guang.ctpp.common.network.packet.CTPPToolboxActionPacket;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxBinding;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxSnapshot;
import com.mojang.blaze3d.platform.InputConstants;
import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;

import java.util.List;

public final class CTPPToolboxRadialScreen extends AbstractSimiScreen {

    private static final int CENTER = -2;
    private static final int DEPOSIT = -3;

    private final List<CTPPToolboxSnapshot> sources;
    private final CTPPToolboxBinding binding;
    private final int hotbarSlot;
    private boolean selectingSources;
    private int sourceIndex;
    private int hovered = -1;
    private boolean committed;

    public CTPPToolboxRadialScreen(List<CTPPToolboxSnapshot> sources, CTPPToolboxBinding binding, int hotbarSlot) {
        this.sources = sources;
        this.binding = binding;
        this.hotbarSlot = hotbarSlot;
        sourceIndex = findBoundSource();
        if (sourceIndex < 0) sourceIndex = sources.isEmpty() ? -1 : 0;
        selectingSources = binding == null && sources.size() > 1;
    }

    private int findBoundSource() {
        if (binding == null) return -1;
        for (int i = 0; i < sources.size(); i++) {
            if (sources.get(i).source().identifies(binding.source())) return i;
        }
        return -1;
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        double dx = mouseX - width / 2d;
        double dy = mouseY - height / 2d;
        double distance = dx * dx + dy * dy;
        hovered = distance > 25 && distance < 3600 ?
                Mth.floor((Mth.RAD_TO_DEG * Mth.atan2(dy, dx) + 517.5f) % 360) / 45 : -1;
        if (distance <= 150 && binding != null) hovered = CENTER;
        if (dx > 62 && dx < 98 && dy > -18 && dy < 18) hovered = DEPOSIT;

        var pose = graphics.pose();
        pose.pushPose();
        pose.translate(width / 2f, height / 2f, 0);
        Component tip = null;
        for (int slot = 0; slot < 8; slot++) {
            pose.pushPose();
            pose.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(slot * 45 - 45));
            pose.translate(0, -40, 0);
            pose.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-slot * 45 + 45));
            pose.translate(-12, -12, 0);
            if (selectingSources) {
                if (slot < sources.size()) {
                    AllGuiTextures.TOOLBELT_SLOT.render(graphics, 0, 0);
                    GuiGameElement.of(sources.get(slot).displayStack()).at(3, 3).render(graphics);
                    if (hovered == slot) tip = sources.get(slot).displayName();
                } else {
                    AllGuiTextures.TOOLBELT_EMPTY_SLOT.render(graphics, 0, 0);
                }
            } else {
                CTPPToolboxSnapshot source = currentSource();
                ItemStack filter = source == null ? ItemStack.EMPTY : source.filters().get(slot);
                boolean active = source != null && !filter.isEmpty() && source.counts()[slot] > 0;
                (filter.isEmpty() ? AllGuiTextures.TOOLBELT_EMPTY_SLOT :
                        active ? AllGuiTextures.TOOLBELT_SLOT : AllGuiTextures.TOOLBELT_INACTIVE_SLOT)
                        .render(graphics, 0, 0);
                if (!filter.isEmpty()) GuiGameElement.of(filter).at(3, 3).render(graphics);
                if (active && hovered == slot) tip = filter.getHoverName();
            }
            if (hovered == slot) AllGuiTextures.TOOLBELT_SLOT_HIGHLIGHT.render(graphics, -1, -1);
            pose.popPose();
        }

        pose.pushPose();
        pose.translate(80, 0, 0);
        AllGuiTextures.TOOLBELT_SLOT.render(graphics, -12, -12);
        AllIcons.I_TOOLBOX.render(graphics, -9, -9);
        if (hovered == DEPOSIT) {
            AllGuiTextures.TOOLBELT_SLOT_HIGHLIGHT.render(graphics, -13, -13);
            tip = Component.translatable("create.toolbox.depositAll").withStyle(ChatFormatting.GOLD);
        }
        pose.popPose();

        if (binding != null) {
            AllGuiTextures.TOOLBELT_SLOT.render(graphics, -12, -12);
            AllIcons.I_FLIP.render(graphics, -9, -9);
            if (hovered == CENTER) {
                AllGuiTextures.TOOLBELT_SLOT_HIGHLIGHT.render(graphics, -13, -13);
                tip = currentSource() == null ? Component.translatable("create.toolbox.detach") :
                        Component.translatable("create.toolbox.unequip");
            }
        }
        pose.popPose();
        if (tip != null) graphics.drawCenteredString(font, tip, width / 2, height - 72, 0xffffff);
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        graphics.fillGradient(0, 0, width, height, 0xb0000000, 0xb0000000);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1 && !selectingSources && sources.size() > 1) {
            selectingSources = true;
            return true;
        }
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        if (selectingSources && hovered >= 0 && hovered < sources.size()) {
            sourceIndex = hovered;
            selectingSources = false;
            return true;
        }
        if (hovered == CENTER || hovered == DEPOSIT || hovered >= 0 && isActive(hovered)) {
            committed = true;
            onClose();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void removed() {
        super.removed();
        if (!committed) return;
        if (hovered == CENTER && binding != null) {
            CTPPToolboxActionPacket.Action action = currentSource() == null ?
                    CTPPToolboxActionPacket.Action.DETACH : CTPPToolboxActionPacket.Action.UNEQUIP;
            GTNetwork.sendToServer(new CTPPToolboxActionPacket(action, null, -1, hotbarSlot));
            return;
        }
        CTPPToolboxSnapshot source = currentSource();
        if (source == null) return;
        if (hovered == DEPOSIT) {
            GTNetwork.sendToServer(new CTPPToolboxActionPacket(CTPPToolboxActionPacket.Action.DEPOSIT,
                    source.source(), -1, hotbarSlot));
        } else if (hovered >= 0 && isActive(hovered)) {
            GTNetwork.sendToServer(new CTPPToolboxActionPacket(CTPPToolboxActionPacket.Action.EQUIP,
                    source.source(), hovered, hotbarSlot));
        }
    }

    @Override
    public boolean keyPressed(int code, int scanCode, int modifiers) {
        KeyMapping[] hotbar = minecraft.options.keyHotbarSlots;
        for (int i = 0; i < hotbar.length && i < 8; i++) {
            if (!hotbar[i].matches(code, scanCode)) continue;
            hovered = i;
            if (!selectingSources && isActive(i)) {
                committed = true;
                onClose();
            }
            return true;
        }
        return super.keyPressed(code, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int code, int scanCode, int modifiers) {
        InputConstants.Key key = InputConstants.getKey(code, scanCode);
        if (AllKeys.TOOLBELT.getKeybind().isActiveAndMatches(key)) {
            if (!selectingSources && (hovered == CENTER || hovered == DEPOSIT || isActive(hovered))) {
                committed = true;
            }
            onClose();
            return true;
        }
        return super.keyReleased(code, scanCode, modifiers);
    }

    private CTPPToolboxSnapshot currentSource() {
        return sourceIndex >= 0 && sourceIndex < sources.size() ? sources.get(sourceIndex) : null;
    }

    private boolean isActive(int compartment) {
        CTPPToolboxSnapshot source = currentSource();
        return source != null && compartment >= 0 && compartment < 8 &&
                !source.filters().get(compartment).isEmpty() && source.counts()[compartment] > 0;
    }
}
