package com.mo_guang.ctpp.common.beam;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

/**
 * A block that changes the direction of an emitter beam hitting it (mirrors, prisms...).
 * The beam path tracer checks the hit block for this interface after every block collision.
 */
public interface IBeamRedirector {

    /**
     * Computes the beam's new direction after interacting with this block.
     *
     * @param incoming normalized beam travel direction
     * @param state    the block state that was hit
     * @return the new normalized direction, or null if the beam is absorbed
     */
    @Nullable
    Vec3 redirect(Vec3 incoming, BlockState state);
}
