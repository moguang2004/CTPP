package com.mo_guang.ctpp.common.beam;

import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Immutable description of one placeable-emitter beam, shared by server tracking and client
 * rendering. The beam is a polyline: it starts at the emitter face, and every interior vertex is
 * a mirror reflection point.
 */
public record EmitterBeam(int id, List<Vec3> points, long voltage, long amps, int tier) {

    public EmitterBeam {
        points = List.copyOf(points);
    }

    public Vec3 origin() {
        return points.get(0);
    }

    public Vec3 end() {
        return points.get(points.size() - 1);
    }

    public int segmentCount() {
        return points.size() - 1;
    }

    /** Total geometric length of the polyline. */
    public double length() {
        double length = 0;
        for (int i = 0; i + 1 < points.size(); i++) {
            length += points.get(i + 1).subtract(points.get(i)).length();
        }
        return length;
    }
}
