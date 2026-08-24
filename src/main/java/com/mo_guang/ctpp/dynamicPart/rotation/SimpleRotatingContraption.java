package com.mo_guang.ctpp.dynamicPart.rotation;

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

import java.util.List;

public class SimpleRotatingContraption extends Contraption {

    public List<BlockPos> partPos;
    private final Direction.Axis rotationAxis;

    public SimpleRotatingContraption(List<BlockPos> partPos, BlockPos anchor) {
        this(partPos, anchor, null);
    }

    /**
     * @param rotationAxis the axis this contraption rotates around; null keeps the
     *                     conservative all-axis bounds for contraptions that can
     *                     change axes at runtime
     */
    public SimpleRotatingContraption(List<BlockPos> partPos, BlockPos anchor, Direction.Axis rotationAxis) {
        super();
        this.partPos = partPos;
        this.anchor = anchor;
        this.rotationAxis = rotationAxis;
        this.bounds = new AABB(BlockPos.ZERO);
    }

    @Override
    public boolean assemble(Level world, BlockPos pos_) {
        for (BlockPos pos : partPos) {
            BlockEntity be = world.getBlockEntity(pos);
            BlockState state = world.getBlockState(pos);
            StructureTemplate.StructureBlockInfo info = new StructureTemplate.StructureBlockInfo(pos, state,
                    be != null ? be.saveWithFullMetadata() : null);
            addBlock(world, pos, Pair.of(info, be));
        }
        if (rotationAxis == null) {
            expandBoundsAroundAxis(Direction.Axis.X);
            expandBoundsAroundAxis(Direction.Axis.Y);
            expandBoundsAroundAxis(Direction.Axis.Z);
        } else {
            expandBoundsAroundAxis(rotationAxis);
        }
        return true;
    }

    @Override
    public boolean canBeStabilized(Direction facing, BlockPos localPos) {
        return false;
    }

    @Override
    public ContraptionType getType() {
        return AllContraptionTypes.BEARING.value();
    }
}
