package com.mo_guang.ctpp.api.terminal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/** Shared centerline geometry for terminal wire rendering and server collision checks. */
public final class TerminalWireGeometry {

    public static final double MAX_RADIUS = radius(TerminalProperties.ConnectionType.SIXTEEN);

    private TerminalWireGeometry() {}

    public static Vec3 connectionPoint(BlockPos pos) {
        return Vec3.atLowerCornerOf(pos).add(0.5, 0.5, 0.5);
    }

    public static int segmentCount(Vec3 start, Vec3 end) {
        return Math.max(8, Math.min(64, (int) Math.ceil(start.distanceTo(end) * 1.5)));
    }

    public static double radius(TerminalProperties.ConnectionType connectionType) {
        int multiplier = connectionType == null ? 1 : connectionType.multiplier();
        return 0.035 * Math.sqrt(multiplier);
    }

    public static List<Vec3> points(Vec3 start, Vec3 end) {
        int segments = segmentCount(start, end);
        double length = start.distanceTo(end);
        double sag = Math.min(2.5, length * 0.08);
        List<Vec3> points = new ArrayList<>(segments + 1);
        for (int i = 0; i <= segments; i++) {
            double t = i / (double) segments;
            points.add(new Vec3(
                    start.x + (end.x - start.x) * t,
                    start.y + (end.y - start.y) * t - sag * 4.0 * t * (1.0 - t),
                    start.z + (end.z - start.z) * t));
        }
        return List.copyOf(points);
    }

    public static AABB bounds(List<Vec3> points, double radius) {
        Vec3 first = points.get(0);
        double minX = first.x;
        double minY = first.y;
        double minZ = first.z;
        double maxX = first.x;
        double maxY = first.y;
        double maxZ = first.z;
        for (int i = 1; i < points.size(); i++) {
            Vec3 point = points.get(i);
            minX = Math.min(minX, point.x);
            minY = Math.min(minY, point.y);
            minZ = Math.min(minZ, point.z);
            maxX = Math.max(maxX, point.x);
            maxY = Math.max(maxY, point.y);
            maxZ = Math.max(maxZ, point.z);
        }
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(radius);
    }
}
