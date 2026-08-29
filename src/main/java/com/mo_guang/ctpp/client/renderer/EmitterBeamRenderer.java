package com.mo_guang.ctpp.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.common.beam.EmitterBeam;
import com.mo_guang.ctpp.common.machine.simple.PlaceableEmitterMachine;

import java.util.HashMap;
import java.util.Map;

/**
 * Client-side global beam rendering: beams tracked by the server are drawn every frame,
 * independent of whether the emitter block itself is in view.
 */
@Mod.EventBusSubscriber(modid = CTPP.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class EmitterBeamRenderer {

    public static final Map<Integer, EmitterBeam> BEAMS = new HashMap<>();
    private static final Map<Integer, ResourceKey<Level>> BEAM_DIMS = new HashMap<>();
    private static final int SIDES = 8;
    private static final double TAU = Math.PI * 2;
    private static final double MAX_LENGTH = 128;
    /** Beam radius with no amps flowing and the extra radius added per amp. Placeholder; tweak freely. */
    private static final float BASE_RADIUS = 0.04f;
    private static final float RADIUS_PER_AMP = 0.03f;
    /** Additive glow core: radius/alpha relative to the body. Placeholder; tweak freely. */
    private static final float GLOW_RADIUS_SCALE = 0.55f;
    private static final float GLOW_ALPHA_SCALE = 0.3f;

    /** Translucent body (normal alpha blending) keeps dark tier colors distinguishable. */
    private static final RenderType BEAM_BODY_TYPE = CTPPBeamRenderTypes.beamBody();

    private EmitterBeamRenderer() {}

    public static void setBeam(int id, ResourceKey<Level> dim, EmitterBeam beam) {
        BEAMS.put(id, beam);
        BEAM_DIMS.put(id, dim);
    }

    public static void removeBeam(int id) {
        BEAMS.remove(id);
        BEAM_DIMS.remove(id);
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        var mc = Minecraft.getInstance();
        if (mc.level == null || BEAMS.isEmpty()) return;
        var poseStack = event.getPoseStack();
        Vec3 cam = event.getCamera().getPosition();
        var buffers = mc.renderBuffers().bufferSource();
        var matrix = poseStack.last().pose();

        // pass 1: translucent colored body — normal alpha blending keeps the true tier color
        var body = buffers.getBuffer(BEAM_BODY_TYPE);
        for (var entry : BEAMS.entrySet()) {
            EmitterBeam beam = entry.getValue();
            if (!mc.level.dimension().equals(BEAM_DIMS.get(entry.getKey()))) continue;
            renderBeam(body, matrix, beam.origin().subtract(cam), beam.end().subtract(cam), beam, false);
        }
        buffers.endBatch(BEAM_BODY_TYPE);

        // pass 2: thinner additive core for the energy glow
        var glow = buffers.getBuffer(RenderType.lightning());
        for (var entry : BEAMS.entrySet()) {
            EmitterBeam beam = entry.getValue();
            if (!mc.level.dimension().equals(BEAM_DIMS.get(entry.getKey()))) continue;
            renderBeam(glow, matrix, beam.origin().subtract(cam), beam.end().subtract(cam), beam, true);
        }
        buffers.endBatch(RenderType.lightning());
    }

    private static void renderBeam(com.mojang.blaze3d.vertex.VertexConsumer consumer, org.joml.Matrix4f matrix,
                                   Vec3 origin, Vec3 end, EmitterBeam beam, boolean glow) {
        Vec3 delta = end.subtract(origin);
        double length = delta.length();
        if (length < 1.0E-8) return;
        Vec3 dir = delta.normalize();
        Vec3 side = dir.cross(new Vec3(0, 1, 0));
        if (side.lengthSqr() < 1.0E-8) side = dir.cross(new Vec3(1, 0, 0));
        side = side.normalize();
        Vec3 up = side.cross(dir).normalize();

        // thickness scales with the emission current (1..MAX_CONSUMPTION, set in the emitter UI)
        float radius = (BASE_RADIUS + RADIUS_PER_AMP *
                Math.min(beam.amps(), PlaceableEmitterMachine.MAX_CONSUMPTION)) * (glow ? GLOW_RADIUS_SCALE : 1);
        float alphaScale = glow ? GLOW_ALPHA_SCALE : 1;

        // Simulated-style tip treatment: the end flares outward slightly and its alpha fades
        // with how much of the max range is used, so the beam dissolves instead of cutting off.
        float lengthFrac = (float) Math.min(1, length / MAX_LENGTH);
        float flare = 1f + lengthFrac / 10f;

        // Band rendering: the remaining voltage is recomputed as the beam propagates;
        // every time it crosses a tier boundary the beam switches to that tier's color,
        // so one beam can show e.g. orange (HV) -> aqua (MV) -> gray (LV) simultaneously.
        double loss = PlaceableEmitterMachine.lossPerBlock(beam.tier());
        double logDecay = Math.log(1 - loss);
        double v0 = beam.voltage();
        double d0 = 0;
        while (d0 < length - 1.0E-6) {
            double v = v0 * Math.exp(logDecay * d0);
            int bandColorStart = PlaceableEmitterMachine.colorForVoltage(v);
            int tierIdx = tierIndexOf(v);
            double d1 = length;
            if (tierIdx > 0 && logDecay < 0) {
                double threshold = com.gregtechceu.gtceu.api.GTValues.V[tierIdx - 1];
                if (threshold < v) {
                    // distance from origin where the voltage hits the next lower tier boundary
                    d1 = Math.min(length, Math.log(threshold / v0) / logDecay);
                }
            }
            if (d1 <= d0 + 1.0E-6) d1 = d0 + 1; // keep making progress
            // each band gradients from its own color into the next band's color
            int bandColorEnd = PlaceableEmitterMachine.colorForVoltage(v0 * Math.exp(logDecay * d1));
            boolean isTip = d1 >= length - 1.0E-6;
            float bandFlare = isTip ? flare : 1f;
            float bandAlphaStart = 0.8f;
            float bandAlphaEnd = isTip ? 0.8f * (1 - lengthFrac) : 0.8f;
            renderBand(consumer, matrix, origin, dir, side, up, radius, d0, d1, bandColorStart, bandColorEnd,
                    bandAlphaStart * alphaScale, bandAlphaEnd * alphaScale, bandFlare, glow);
            d0 = d1;
        }
    }

    private static int tierIndexOf(double voltage) {
        int tier = (int) Math.floor(Math.log(Math.max(voltage, 1) / com.gregtechceu.gtceu.api.GTValues.V[0]) /
                Math.log(4));
        return Math.min(Math.max(tier, 0), com.gregtechceu.gtceu.api.GTValues.VCM.length - 1);
    }

    private static void renderBand(com.mojang.blaze3d.vertex.VertexConsumer consumer, org.joml.Matrix4f matrix,
                                   Vec3 origin, Vec3 dir, Vec3 side, Vec3 up, float radius, double d0, double d1,
                                   int colorStart, int colorEnd, float alphaStart, float alphaEnd, float flare,
                                   boolean doubleSided) {
        Vec3 p0 = origin.add(dir.scale(d0));
        Vec3 p1 = origin.add(dir.scale(d1));
        for (int i = 0; i < SIDES; i++) {
            double a0 = i * TAU / SIDES;
            double a1 = (i + 1) * TAU / SIDES;
            Vec3 o0 = side.scale(Math.cos(a0) * radius).add(up.scale(Math.sin(a0) * radius));
            Vec3 o1 = side.scale(Math.cos(a1) * radius).add(up.scale(Math.sin(a1) * radius));
            quad(consumer, matrix, p0, p1, o0, o1, colorStart, colorEnd, alphaStart, alphaEnd, flare);
            // only needed when the render type culls back faces (lightning); the body type is NO_CULL
            if (doubleSided)
                quad(consumer, matrix, p0, p1, o1, o0, colorStart, colorEnd, alphaStart, alphaEnd, flare);
        }
    }

    private static void quad(com.mojang.blaze3d.vertex.VertexConsumer consumer, org.joml.Matrix4f matrix,
                             Vec3 origin, Vec3 end, Vec3 o0, Vec3 o1, int colorStart, int colorEnd, float alphaStart,
                             float alphaEnd, float flare) {
        consumer.vertex(matrix, (float) (origin.x + o0.x), (float) (origin.y + o0.y), (float) (origin.z + o0.z))
                .color(red(colorStart), green(colorStart), blue(colorStart), alphaStart).endVertex();
        consumer.vertex(matrix, (float) (end.x + o0.x * flare), (float) (end.y + o0.y * flare),
                (float) (end.z + o0.z * flare))
                .color(red(colorEnd), green(colorEnd), blue(colorEnd), alphaEnd).endVertex();
        consumer.vertex(matrix, (float) (end.x + o1.x * flare), (float) (end.y + o1.y * flare),
                (float) (end.z + o1.z * flare))
                .color(red(colorEnd), green(colorEnd), blue(colorEnd), alphaEnd).endVertex();
        consumer.vertex(matrix, (float) (origin.x + o1.x), (float) (origin.y + o1.y), (float) (origin.z + o1.z))
                .color(red(colorStart), green(colorStart), blue(colorStart), alphaStart).endVertex();
    }

    private static float red(int color) {
        return (color >> 16 & 255) / 255f;
    }

    private static float green(int color) {
        return (color >> 8 & 255) / 255f;
    }

    private static float blue(int color) {
        return (color & 255) / 255f;
    }
}
