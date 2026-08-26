package com.mo_guang.ctpp.client.renderer;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import com.mo_guang.ctpp.api.terminal.TerminalProperties;
import com.mo_guang.ctpp.common.blockentity.VoltageTerminalBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector3f;

import java.util.Map;

public class VoltageTerminalRenderer implements BlockEntityRenderer<VoltageTerminalBlockEntity> {

    public VoltageTerminalRenderer(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(VoltageTerminalBlockEntity terminal, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int packedLight, int packedOverlay) {
        if (terminal.getLevel() == null) return;
        for (Map.Entry<BlockPos, TerminalProperties.Link> entry : terminal.getLinks().entrySet()) {
            if (terminal.getBlockPos().compareTo(entry.getKey()) >= 0) continue;
            if (!(terminal.getLevel().getBlockEntity(entry.getKey()) instanceof VoltageTerminalBlockEntity other))
                continue;
            renderWire(terminal, other, entry.getValue(), poseStack,
                    buffers.getBuffer(CTPPWireRenderTypes.wire()), packedLight);
        }
    }

    /** Renders the uncommitted wire from the first-person hand to the selected terminal. */
    public static void renderPreview(PoseStack poseStack, MultiBufferSource buffers, Vec3 start, Vec3 end,
                                     ItemStack wireItem, int multiplier, int packedLight) {
        TerminalProperties.FineWireSpec wire = TerminalProperties.FineWireSpec.from(wireItem);
        if (wire == null) return;
        TerminalProperties.Link link = new TerminalProperties.Link(BlockPos.ZERO, wire, wireItem,
                TerminalProperties.ConnectionType.fromMultiplier(multiplier));
        renderWirePoints(start, end, link, poseStack, buffers.getBuffer(CTPPWireRenderTypes.wire()), packedLight,
                true);
    }

    private static void renderWire(VoltageTerminalBlockEntity first, VoltageTerminalBlockEntity second,
                                   TerminalProperties.Link link, PoseStack poseStack,
                                   VertexConsumer consumer, int packedLight) {
        Vec3 start = connectionPoint(first).subtract(Vec3.atLowerCornerOf(first.getBlockPos()));
        Vec3 end = connectionPoint(second).subtract(Vec3.atLowerCornerOf(first.getBlockPos()));
        renderWirePoints(start, end, link, poseStack, consumer, packedLight, false);
    }

    private static void renderWirePoints(Vec3 start, Vec3 end, TerminalProperties.Link link,
                                         PoseStack poseStack, VertexConsumer consumer, int packedLight,
                                         boolean capStart) {
        double length = start.distanceTo(end);
        int segments = Math.max(8, Math.min(64, (int) Math.ceil(length * 1.5)));
        float thickness = 0.035f * (float) Math.sqrt(link.connectionType().multiplier());
        int color = linkColor(link);
        final int radialSides = 16;
        Vector3f[][] rings = new Vector3f[segments + 1][radialSides];
        Vector3f previousCenter = null;
        Vector3f previousBasisA = null;
        for (int i = 0; i <= segments; i++) {
            float t = i / (float) segments;
            float sag = (float) Math.min(2.5, length * 0.08);
            Vector3f center = new Vector3f(
                    (float) (start.x + (end.x - start.x) * t),
                    (float) (start.y + (end.y - start.y) * t - sag * 4.0 * t * (1.0 - t)),
                    (float) (start.z + (end.z - start.z) * t));
            float nextT = Math.min(1.0f, t + 1.0f / segments);
            Vector3f nextCenter = new Vector3f(
                    (float) (start.x + (end.x - start.x) * nextT),
                    (float) (start.y + (end.y - start.y) * nextT - sag * 4.0 * nextT * (1.0f - nextT)),
                    (float) (start.z + (end.z - start.z) * nextT));
            Vector3f direction = (i == segments ? new Vector3f(center).sub(previousCenter) :
                    new Vector3f(nextCenter).sub(i == 0 ? center : previousCenter)).normalize();
            Vector3f basisA;
            if (previousBasisA == null) {
                Vector3f up = Math.abs(direction.y) > 0.9f ? new Vector3f(1, 0, 0) : new Vector3f(0, 1, 0);
                basisA = new Vector3f(direction).cross(up).normalize();
            } else {
                basisA = new Vector3f(previousBasisA)
                        .fma(-previousBasisA.dot(direction), direction);
                if (basisA.lengthSquared() < 1.0e-6f) {
                    basisA = new Vector3f(direction).cross(new Vector3f(1, 0, 0));
                    if (basisA.lengthSquared() < 1.0e-6f) {
                        basisA = new Vector3f(direction).cross(new Vector3f(0, 1, 0));
                    }
                }
                basisA.normalize();
            }
            Vector3f basisB = new Vector3f(direction).cross(basisA).normalize();
            for (int side = 0; side < radialSides; side++) {
                double angle = Math.PI * 2.0 * side / radialSides;
                rings[i][side] = new Vector3f(center).add(radialOffset(basisA, basisB, angle, thickness));
            }
            previousCenter = center;
            previousBasisA = basisA;
        }
        for (int i = 0; i < segments; i++) {
            for (int side = 0; side < radialSides; side++) {
                int nextSide = (side + 1) % radialSides;
                float u0 = side / (float) radialSides;
                float u1 = (side + 1) / (float) radialSides;
                float v0 = i * (float) length / segments;
                float v1 = (i + 1) * (float) length / segments;
                addTubeQuad(consumer, poseStack, rings[i][side], rings[i][nextSide],
                        rings[i + 1][nextSide], rings[i + 1][side], color,
                        u0, u1, v0, v1, packedLight);
            }
        }
        if (capStart) {
            // The preview begins at the player's hand. Close that end so the
            // near camera cannot look into the enlarged multi-wire tube.
            Vector3f center = new Vector3f((float) start.x, (float) start.y, (float) start.z);
            for (int side = 0; side < radialSides; side++) {
                int nextSide = (side + 1) % radialSides;
                Vector3f p0 = rings[0][side];
                Vector3f p1 = rings[0][nextSide];
                addTubeQuad(consumer, poseStack, center, p1, p0, center, color,
                        0.5f, 0.5f, 0.5f, 0.5f, packedLight);
            }
        }
    }

    private static int linkColor(TerminalProperties.Link link) {
        int primary = 0xB8B8B8;
        if (link.wireItem().isEmpty()) return primary;
        try {
            var material = ChemicalHelper.getMaterialStack(link.wireItem()).material();
            primary = material.getMaterialRGB();
        } catch (RuntimeException ignored) {
            // Keep the renderer usable for legacy links whose item snapshot
            // cannot be resolved on the client.
        }
        return primary;
    }

    private static Vec3 connectionPoint(VoltageTerminalBlockEntity terminal) {
        // Rendering deliberately uses the terminal body center. This is
        // independent from getElectricalSide(), which controls capabilities.
        return Vec3.atLowerCornerOf(terminal.getBlockPos()).add(0.5, 0.5, 0.5);
    }

    private static Vector3f radialOffset(Vector3f basisA, Vector3f basisB, double angle, float radius) {
        return new Vector3f(basisA).mul((float) Math.cos(angle) * radius)
                .add(new Vector3f(basisB).mul((float) Math.sin(angle) * radius));
    }

    private static void addTubeQuad(VertexConsumer consumer, PoseStack poseStack,
                                    Vector3f from0, Vector3f from1, Vector3f to1,
                                    Vector3f to0, int color, float u0, float u1,
                                    float v0, float v1, int packedLight) {
        vertex(consumer, poseStack, from0, color, u0, v0, packedLight);
        vertex(consumer, poseStack, from1, color, u1, v0, packedLight);
        vertex(consumer, poseStack, to1, color, u1, v1, packedLight);
        vertex(consumer, poseStack, to0, color, u0, v1, packedLight);
    }

    private static void vertex(VertexConsumer consumer, PoseStack poseStack, Vector3f pos,
                               int color, float u, float v, int packedLight) {
        consumer.vertex(poseStack.last().pose(), pos.x, pos.y, pos.z)
                .color(FastColor.ARGB32.red(color) / 255.0f,
                        FastColor.ARGB32.green(color) / 255.0f,
                        FastColor.ARGB32.blue(color) / 255.0f, 1.0f)
                .uv(u, v)
                .uv2(packedLight)
                .endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(VoltageTerminalBlockEntity terminal) {
        return true;
    }
}
