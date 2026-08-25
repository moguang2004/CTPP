package com.mo_guang.ctpp.common.block;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.ICoverable;
import com.gregtechceu.gtceu.common.item.tool.rotation.ICustomRotationBehavior;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.mo_guang.ctpp.common.blockentity.VoltageTerminalBlockEntity;
import com.mo_guang.ctpp.common.terminal.TerminalNetwork;
import com.mo_guang.ctpp.registry.CTPPBlockEntities;
import org.jetbrains.annotations.Nullable;

/** A tiered EU endpoint. Only FACING exposes the EU capability. */
public class VoltageTerminalBlock extends Block implements EntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final ICustomRotationBehavior ROTATION_BEHAVIOR = new ICustomRotationBehavior() {

        @Override
        public boolean customRotate(BlockState state, Level level, BlockPos pos, BlockHitResult hitResult) {
            Direction gridSide = ICoverable.determineGridSideHit(hitResult);
            if (gridSide == null) return false;
            // The grid side is the terminal's base/electrical side, while
            // FACING points away from that side (the stem direction).
            Direction targetFacing = gridSide.getOpposite();
            if (targetFacing == state.getValue(FACING)) return false;
            level.setBlockAndUpdate(pos, state.setValue(FACING, targetFacing));
            return true;
        }

        @Override
        public boolean showSideTip(BlockState state, Direction side) {
            return state.getValue(FACING).getOpposite() != side;
        }
    };

    private static final VoxelShape SHAPE_UP = Shapes.or(Block.box(2, 0, 2, 14, 2, 14), Block.box(5, 2, 5, 11, 10, 11));
    private static final VoxelShape SHAPE_DOWN = Shapes.or(Block.box(2, 14, 2, 14, 16, 14),
            Block.box(5, 6, 5, 11, 14, 11));
    private static final VoxelShape SHAPE_NORTH = Shapes.or(Block.box(2, 2, 14, 14, 14, 16),
            Block.box(5, 5, 6, 11, 11, 14));
    private static final VoxelShape SHAPE_SOUTH = Shapes.or(Block.box(2, 2, 0, 14, 14, 2),
            Block.box(5, 5, 2, 11, 11, 10));
    private static final VoxelShape SHAPE_EAST = Shapes.or(Block.box(0, 2, 2, 2, 14, 14),
            Block.box(2, 5, 5, 10, 11, 11));
    private static final VoxelShape SHAPE_WEST = Shapes.or(Block.box(14, 2, 2, 16, 14, 14),
            Block.box(6, 5, 5, 14, 11, 11));

    private final int tier;

    public VoltageTerminalBlock(Properties properties, int tier) {
        super(properties.strength(2.0F, 1.0F));
        this.tier = tier;
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP));
    }

    public long getVoltage() {
        return GTValues.V[tier];
    }

    public int getTier() {
        return tier;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityCtx && entityCtx.getEntity() instanceof Player player) {
            var held = player.getMainHandItem();
            if (held.is(CustomTags.WRENCH)) {
                return Shapes.block();
            }
        }
        return getCollisionShape(state, level, pos, context);
    }

    private static VoxelShape terminalShape(BlockState state) {
        return switch (state.getValue(FACING)) {
            case DOWN -> SHAPE_DOWN;
            case UP -> SHAPE_UP;
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
                                        CollisionContext context) {
        // GT pipes keep their physical collision shape independent from the
        // wrench interaction hitbox. Do the same for terminals.
        return terminalShape(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return CTPPBlockEntities.VOLTAGE_TERMINAL.get().create(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
                                                                            Level level, BlockState state,
                                                                            BlockEntityType<T> type) {
        if (level.isClientSide || type != CTPPBlockEntities.VOLTAGE_TERMINAL.get()) return null;
        return (ignoredLevel, ignoredPos, ignoredState, blockEntity) -> {
            if (blockEntity instanceof VoltageTerminalBlockEntity terminal) {
                terminal.serverTick();
            }
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        return TerminalNetwork.handleUse(level, pos, player, stack) ?
                InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) &&
                level.getBlockEntity(pos) instanceof VoltageTerminalBlockEntity terminal &&
                level instanceof net.minecraft.server.level.ServerLevel server) {
            TerminalNetwork.disconnectAll(server, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
