package com.mo_guang.ctpp.common.gui.widget;

import com.lowdragmc.lowdraglib.gui.widget.Widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mo_guang.ctpp.common.machine.simple.PlaceableEmitterMachine;

/**
 * Polar angle dial like greg-emitters': angle around the circle = azimuth, distance from center =
 * zenith. Drag the red handle to aim the beam.
 */
public class EmitterAngleDialWidget extends Widget {

    private static final int UPDATE_ANGLES = 1;

    private final PlaceableEmitterMachine machine;
    private final int radius;
    private boolean dragging;

    public EmitterAngleDialWidget(PlaceableEmitterMachine machine, int x, int y, int radius) {
        super(x, y, radius * 2 + 8, radius * 2 + 8);
        this.machine = machine;
        this.radius = radius;
    }

    private int centerX() {
        return getPositionX() + getSizeWidth() / 2;
    }

    private int centerY() {
        return getPositionY() + getSizeHeight() / 2;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int cx = centerX();
        int cy = centerY();

        // concentric rings
        for (int ring = 1; ring <= 3; ring++) {
            double r = radius * ring / 3.0;
            for (int deg = 0; deg < 360; deg += 4) {
                double a = Math.toRadians(deg);
                int px = cx + (int) Math.round(Math.cos(a) * r);
                int py = cy + (int) Math.round(Math.sin(a) * r);
                graphics.fill(px, py, px + 1, py + 1, 0xFF999999);
            }
        }
        // radial spokes, 12 sectors
        for (int i = 0; i < 12; i++) {
            double a = i * Math.PI / 6;
            for (int s = 0; s <= radius; s++) {
                int px = cx + (int) Math.round(Math.cos(a) * s);
                int py = cy + (int) Math.round(Math.sin(a) * s);
                graphics.fill(px, py, px + 1, py + 1, 0xFF666666);
            }
        }
        // red handle: distance from center = zenith, angle = azimuth
        double dist = machine.getZenith() / (Math.PI / 2) * radius;
        double ang = machine.getAzimuth() + Math.PI / 2; // rotate so dial-up matches azimuth 0
        int hx = cx + (int) Math.round(Math.cos(ang) * dist);
        int hy = cy + (int) Math.round(Math.sin(ang) * dist);
        graphics.fill(hx - 2, hy - 2, hx + 3, hy + 3, 0xFFFF3333);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isMouseOverElement(mouseX, mouseY)) {
            dragging = true;
            updateFromMouse(mouseX, mouseY);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging) {
            updateFromMouse(mouseX, mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void updateFromMouse(double mouseX, double mouseY) {
        double dx = mouseX - centerX();
        double dy = mouseY - centerY();
        double dist = Math.min(1, Math.hypot(dx, dy) / radius);
        double azimuth = Math.atan2(dy, dx) - Math.PI / 2; // dial-up is azimuth 0
        double zenith = dist * Math.PI / 2;
        // local preview; the server-side machine (which owns the beam) is updated through the client action
        machine.setAngles(zenith, azimuth);
        writeClientAction(UPDATE_ANGLES, buf -> {
            buf.writeDouble(zenith);
            buf.writeDouble(azimuth);
        });
    }

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
        if (id == UPDATE_ANGLES) {
            machine.setAngles(buffer.readDouble(), buffer.readDouble());
        } else {
            super.handleClientAction(id, buffer);
        }
    }
}
