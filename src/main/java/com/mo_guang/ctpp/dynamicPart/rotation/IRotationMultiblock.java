package com.mo_guang.ctpp.dynamicPart.rotation;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.lowdragmc.lowdraglib.utils.TrackedDummyWorld;
import com.mo_guang.ctpp.api.pattern.StaticBlockPattern;
import com.mo_guang.ctpp.dynamicPart.rotation.SimpleRotatingContraption;
import com.mo_guang.ctpp.dynamicPart.rotation.SimpleRotatingContraptionEntity;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IRotationMultiblock extends IMultiController {
    default Map<Integer, SimpleRotatingContraptionEntity> assemble(BlockPos pivot) {
        if (self().getLevel() instanceof TrackedDummyWorld) return null;
        if (self().getLevel().isClientSide) return null;
        Map<Integer, SimpleRotatingContraptionEntity> ce = new HashMap<>();
        var pattern = self().getDefinition().getPatternFactory().get();
        if (pattern instanceof StaticBlockPattern staticBlockPattern) {
            Map<Integer, List<BlockPos>> dymanicPart = staticBlockPattern.getDynamicPart(self().getMultiblockState());
            for (var entry : dymanicPart.entrySet()) {
                int group = entry.getKey();
                var part = entry.getValue();
                SimpleRotatingContraption contraption = new SimpleRotatingContraption(part, pivot);
                contraption.assemble(this.self().getLevel(), self().getPos()); // 第二个参数无用
                contraption.removeBlocksFromWorld(this.self().getLevel(), BlockPos.ZERO);
                SimpleRotatingContraptionEntity contraptionEntity = SimpleRotatingContraptionEntity.create(self().getLevel(), contraption, this, pivot.getCenter());
                contraptionEntity.setPos(pivot.getX(), pivot.getY(), pivot.getZ());
                this.self().getLevel().addFreshEntity(contraptionEntity);
                ce.put(group, contraptionEntity);
            }
            return ce;
        }
        return null;
    }
    List<SimpleRotatingContraptionEntity> getRotatingEntity();
    void setRotatingEntity(List<SimpleRotatingContraptionEntity> entities);
    default boolean isAttachedTo(AbstractContraptionEntity contraption) {
        return getRotatingEntity() != null && getRotatingEntity().contains(contraption);
    }

    default void attach(SimpleRotatingContraptionEntity contraption) {
        if (getRotatingEntity() == null) {
            setRotatingEntity(List.of(contraption));
        }
        else {
            getRotatingEntity().add(contraption);
        }
        self().holder.notifyBlockUpdate();
    }

    default BlockPos getBlockPosition() {
        return self().getPos();
    }
}
