package com.mo_guang.ctpp.common.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;

import com.mo_guang.ctpp.common.beam.IBeamRedirector;
import org.jetbrains.annotations.Nullable;

/**
 * A passive optical mirror: reflects emitter beams around corners. The mirror plane bisects the
 * front facing and the in-plane bend direction (45°): a beam entering the front face leaves in
 * the bend direction, and vice versa. Beams arriving from other sides are absorbed. Orientation
 * lives entirely in the block state (6 facings x 4 rotations); the block has no block entity and
 * never ticks — the emitter recomputes the whole beam path every tick.
 */
public class MirrorBlock extends Block implements IBeamRedirector {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 3);

    public MirrorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ROTATION, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ROTATION);
    }

    /** The mirror plane's normal: the bisector of the front facing and the bend direction. */
    public static Vec3 mirrorNormal(BlockState state) {
        Direction facing = state.getValue(FACING);
        Direction bend = perpendiculars(facing)[state.getValue(ROTATION)];
        return new Vec3(
                facing.getStepX() + bend.getStepX(),
                facing.getStepY() + bend.getStepY(),
                facing.getStepZ() + bend.getStepZ()).normalize();
    }

    @Nullable
    @Override
    public Vec3 redirect(Vec3 incoming, BlockState state) {
        Vec3 normal = mirrorNormal(state);
        double dot = incoming.dot(normal);
        // only beams traveling against the normal (entering the reflective side) are reflected
        return dot < 0 ? incoming.subtract(normal.scale(2 * dot)) : null;
    }

    /** The four in-plane bend directions for a facing, in the fixed order used by ROTATION 0..3. */
    public static Direction[] perpendiculars(Direction facing) {
        return switch (facing) {
            case UP, DOWN -> new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
            default -> new Direction[] { Direction.UP, facing.getClockWise(), Direction.DOWN,
                    facing.getCounterClockWise() };
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        // front faces the player; the bend direction follows where the player is looking,
        // biased toward UP on horizontal facings so a head-on click gives a sensible default
        Direction facing = ctx.getNearestLookingDirection().getOpposite();
        Direction[] perps = perpendiculars(facing);
        Vec3 look = ctx.getPlayer() != null ? ctx.getPlayer().getLookAngle() :
                new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());
        int best = 0;
        double bestDot = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < perps.length; i++) {
            double dot = look.x * perps[i].getStepX() + look.y * perps[i].getStepY() + look.z * perps[i].getStepZ();
            if (facing.getAxis().isHorizontal() && perps[i] == Direction.UP) dot += 0.35;
            if (dot > bestDot) {
                bestDot = dot;
                best = i;
            }
        }
        return defaultBlockState().setValue(FACING, facing).setValue(ROTATION, best);
    }
}
