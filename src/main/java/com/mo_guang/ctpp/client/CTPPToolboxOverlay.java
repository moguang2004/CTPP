package com.mo_guang.ctpp.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import com.simibubi.create.foundation.gui.AllGuiTextures;

import static com.simibubi.create.foundation.gui.AllGuiTextures.TOOLBELT_HOTBAR_ON;
import static com.simibubi.create.foundation.gui.AllGuiTextures.TOOLBELT_SELECTED_ON;

public final class CTPPToolboxOverlay {

    public static final IGuiOverlay OVERLAY = CTPPToolboxOverlay::render;

    private CTPPToolboxOverlay() {}

    private static void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height) {
        var player = Minecraft.getInstance().player;
        if (player == null || Minecraft.getInstance().options.hideGui) return;
        int x = width / 2 - 90;
        int y = height - 23;
        CTPPToolboxClientState.bindings().forEach((hotbar, binding) -> {
            boolean selected = player.getInventory().selected == hotbar;
            int offset = selected ? 1 : 0;
            AllGuiTextures texture = selected ? TOOLBELT_SELECTED_ON : TOOLBELT_HOTBAR_ON;
            texture.render(graphics, x + 20 * hotbar - offset, y + offset);
        });
    }
}
