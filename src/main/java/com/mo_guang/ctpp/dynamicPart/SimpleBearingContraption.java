package com.mo_guang.ctpp.dynamicPart;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;

import com.simibubi.create.AllContraptionTypes;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.Contraption;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;

public class SimpleBearingContraption extends Contraption {

    protected Direction facing;

    public SimpleBearingContraption(Direction facing) {
        this.facing = facing;
    }

    public boolean assemble(Level world, BlockPos pos, BlockPos pivot) {
        System.out.println("[SimpleBearingContraption] anchor=" + pivot);
        this.anchor = pivot;
        this.bounds = null;
        return assemble(world, pos);
    }

    @Override
    public boolean assemble(Level world, BlockPos pos) {
        // anchor 就是点击位置

        Set<BlockPos> visited = new HashSet<>();
        dfsCollect(world, pos, visited);
        // bounds.setMinY(bounds.minY-10);
        if (blocks.isEmpty())
            return false;

        startMoving(world);
        expandBoundsAroundAxis(facing.getAxis());
        return true;
    }

    @Override
    public boolean canBeStabilized(Direction facing, BlockPos localPos) {
        return false;
    }

    private void dfsCollect(Level world, BlockPos pos, Set<BlockPos> visited) {
        if (visited.contains(pos))
            return;
        visited.add(pos);

        BlockState state = world.getBlockState(pos);
        if (state.isAir())
            return;
        if (this.bounds == null) {
            this.bounds = new AABB(pos.subtract(anchor));
        }
        // 捕获方块和 BE
        BlockEntity be = world.getBlockEntity(pos);
        StructureTemplate.StructureBlockInfo info = new StructureTemplate.StructureBlockInfo(pos, state,
                be != null ? be.saveWithFullMetadata() : null);
        addBlock(world, pos, Pair.of(info, be));

        // 递归遍历 6 个方向相邻方块
        for (Direction dir : Direction.values()) {
            BlockPos next = pos.relative(dir);
            if (!visited.contains(next)) {
                dfsCollect(world, next, visited);
            }
        }
    }

    @Override
    public ContraptionType getType() {
        // 可以自定义一个 type，也可以直接沿用 BEARING
        return AllContraptionTypes.MOUNTED.value();
    }

    @Override
    protected boolean isAnchoringBlockAt(BlockPos pos) {
        return pos.equals(anchor.relative(facing.getOpposite()));
    }
}
