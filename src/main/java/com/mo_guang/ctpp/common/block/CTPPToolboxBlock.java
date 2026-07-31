package com.mo_guang.ctpp.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.mo_guang.ctpp.common.blockentity.CTPPToolboxBlockEntity;
import com.mo_guang.ctpp.common.item.CTPPToolboxItem;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxSavedData;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxService;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxSourceId;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxStackData;
import com.mo_guang.ctpp.registry.CTPPBlockEntities;
import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.block.IBE;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class CTPPToolboxBlock extends HorizontalDirectionalBlock
                              implements SimpleWaterloggedBlock, IBE<CTPPToolboxBlockEntity> {

    private final DyeColor color;

    public CTPPToolboxBlock(Properties properties, DyeColor color) {
        super(properties);
        this.color = color;
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    public DyeColor getColor() {
        return color;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, FACING);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighbourPos) {
        if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return state;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
                            ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!(level instanceof net.minecraft.server.level.ServerLevel serverLevel)) return;
        withBlockEntityDo(level, pos, toolbox -> {
            CTPPToolboxSavedData.Record record = CTPPToolboxService.ensure(stack, serverLevel);
            toolbox.setToolboxId(record.id());
            if (stack.hasCustomHoverName()) toolbox.setCustomName(stack.getHoverName());
        });
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(this);
        getBlockEntityOptional(level, pos).ifPresent(toolbox -> {
            UUID id = toolbox.getToolboxId();
            if (id != null) stack.getOrCreateTag().putUUID(CTPPToolboxStackData.ID, id);
            if (toolbox.hasCustomName()) stack.setHoverName(toolbox.getCustomName());
        });
        return stack;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit) {
        if (player.isCrouching()) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;
        withBlockEntityDo(level, pos, toolbox -> {
            UUID id = toolbox.ensureToolboxId();
            if (id == null) return;
            toolbox.startOpen();
            CTPPToolboxItem.open(serverPlayer,
                    new CTPPToolboxSourceId(CTPPToolboxSourceId.Type.BLOCK, -1, id, pos));
        });
        return InteractionResult.SUCCESS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AllShapes.TOOLBOX.get(state.getValue(FACING));
    }

    @Override
    public Class<CTPPToolboxBlockEntity> getBlockEntityClass() {
        return CTPPToolboxBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CTPPToolboxBlockEntity> getBlockEntityType() {
        return CTPPBlockEntities.TOOLBOX.get();
    }
}
