package com.mo_guang.ctpp.common.block;

import com.gregtechceu.gtceu.api.block.PipeBlock;
import com.gregtechceu.gtceu.api.blockentity.PipeBlockEntity;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.WireProperties;
import com.gregtechceu.gtceu.api.pipenet.IPipeNode;
import com.gregtechceu.gtceu.api.registry.registrate.provider.GTBlockstateProvider;
import com.gregtechceu.gtceu.client.model.pipe.PipeModel;
import com.gregtechceu.gtceu.common.pipelike.cable.LevelEnergyNet;
import com.mo_guang.ctpp.api.terminal.TerminalProperties;
import com.mo_guang.ctpp.api.terminal.VoltageTerminal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class VoltageTerminalBlock extends PipeBlock<VoltageTerminal, WireProperties, LevelEnergyNet> {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private static final VoxelShape SHAPE_UP = Shapes.or(
            Block.box(2, 0, 2, 14, 2, 14),
            Block.box(5, 2, 5, 11, 10, 11));

    private static final VoxelShape SHAPE_DOWN = Shapes.or(
            Block.box(2, 14, 2, 14, 16, 14),
            Block.box(5, 6, 5, 11, 14, 11));

    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(2, 2, 14, 14, 14, 16),
            Block.box(5, 5, 6, 11, 11, 14));

    private static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Block.box(2, 2, 0, 14, 14, 2),
            Block.box(5, 5, 2, 11, 11, 10));

    private static final VoxelShape SHAPE_EAST = Shapes.or(
            Block.box(0, 2, 2, 2, 14, 14),
            Block.box(2, 5, 5, 10, 11, 11));

    private static final VoxelShape SHAPE_WEST = Shapes.or(
            Block.box(14, 2, 2, 16, 14, 14),
            Block.box(6, 5, 5, 14, 11, 11));

    public VoltageTerminalBlock(Properties properties) {
        super(properties.strength(2.0F, 1.0F), VoltageTerminal.TERMINAL);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public LevelEnergyNet getWorldPipeNet(ServerLevel level) {
        return LevelEnergyNet.getOrCreate(level);
    }

    @Override
    public BlockEntityType<? extends PipeBlockEntity<VoltageTerminal, WireProperties>> getBlockEntityType() {
        return null;
    }

    @Override
    public WireProperties createRawData(BlockState pState, @Nullable ItemStack pStack) {
        return null;
    }

    @Override
    public WireProperties createProperties(IPipeNode<VoltageTerminal, WireProperties> pipeTile) {
        return null;
    }

    @Override
    public WireProperties getFallbackType() {
        return null;
    }

    @Override
    public PipeModel createPipeModel(GTBlockstateProvider provider) {
        return null;
    }

    @Override
    public boolean canPipesConnect(IPipeNode<VoltageTerminal, WireProperties> selfTile, Direction side, IPipeNode<VoltageTerminal, WireProperties> sideTile) {
        return false;
    }

    @Override
    public boolean canPipeConnectToBlock(IPipeNode<VoltageTerminal, WireProperties> selfTile, Direction side, @Nullable BlockEntity tile) {
        return false;
    }

}
