package com.mo_guang.ctpp.dynamicPart.rotation;

import com.simibubi.create.AllContraptionTypes;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class SimpleRotatingContraption extends Contraption {
    public List<BlockPos> partPos;

    public SimpleRotatingContraption(List<BlockPos> partPos, BlockPos anchor) {
        super();
        this.partPos = partPos;
        this.anchor = anchor;
        this.bounds = new AABB(BlockPos.ZERO);
    }
    @Override
    public boolean assemble(Level world, BlockPos pos_) {
        for (BlockPos pos : partPos) {
            BlockEntity be = world.getBlockEntity(pos);
            BlockState state = world.getBlockState(pos);
            StructureTemplate.StructureBlockInfo info = new StructureTemplate.StructureBlockInfo(pos, state, be != null ? be.saveWithFullMetadata() : null);
            addBlock(world, pos, Pair.of(info, be));
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
