package com.mo_guang.ctpp.dynamicPart.rotation;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.lowdragmc.lowdraglib.utils.TrackedDummyWorld;
import com.mo_guang.ctpp.api.pattern.StaticBlockPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IRubiksRotationMultiblock extends IMultiController {
    default Map<Integer, RubiksCubeContraptionEntity> assemble(BlockPos pivot, Direction frontFacing) {
        if (self().getLevel() instanceof TrackedDummyWorld) return null;
        Map<Integer, RubiksCubeContraptionEntity> ce = new HashMap<>();
        var pattern = self().getDefinition().getPatternFactory().get();
        if (pattern instanceof StaticBlockPattern staticBlockPattern) {
            Map<Integer, List<BlockPos>> dymanicPart = staticBlockPattern.getDynamicPart(self().getMultiblockState());
            for (var entry : dymanicPart.entrySet()) {
                int group = entry.getKey();
                var part = entry.getValue();
                BlockPos pos = pivot;
                BlockPos randomPos = part.get(0);
                pos = pos.offset((int) Math.signum(randomPos.getX() - pivot.getX()),
                        (int) Math.signum(randomPos.getY() - pivot.getY()),
                        (int) Math.signum(randomPos.getZ() - pivot.getZ()));
                SimpleRotatingContraption contraption = new SimpleRotatingContraption(part, pivot);
                contraption.assemble(this.self().getLevel(), self().getPos());
                contraption.removeBlocksFromWorld(this.self().getLevel(), BlockPos.ZERO);
                RubiksCubeContraptionEntity contraptionEntity = RubiksCubeContraptionEntity.create(self().getLevel(), contraption, pivot.getCenter(), frontFacing, pos);
                contraptionEntity.setPos(pivot.getX(), pivot.getY(), pivot.getZ());
                this.self().getLevel().addFreshEntity(contraptionEntity);
                ce.put(group, contraptionEntity);
            }
            return ce;
        }
        return null;
    }
}
