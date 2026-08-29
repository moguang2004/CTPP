package com.mo_guang.ctpp.common.beam;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/** Immutable description of one placeable-emitter beam, shared by server tracking and client rendering. */
public record EmitterBeam(int id, BlockPos src, Vec3 dir, long voltage, long amps, int tier, double distance) {

    public Vec3 origin() {
        return src.getCenter().add(dir.scale(0.51));
    }

    public Vec3 end() {
        return origin().add(dir.scale(distance));
    }
}
