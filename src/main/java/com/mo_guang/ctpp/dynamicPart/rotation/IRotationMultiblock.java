package com.mo_guang.ctpp.dynamicPart.rotation;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.lowdragmc.lowdraglib.utils.TrackedDummyWorld;
import com.mo_guang.ctpp.api.pattern.StaticBlockPattern;
import com.mo_guang.ctpp.dynamicPart.rotation.SimpleRotatingContraption;
import com.mo_guang.ctpp.dynamicPart.rotation.SimpleRotatingContraptionEntity;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import net.minecraft.core.BlockPos;

import java.util.*;

public interface IRotationMultiblock<T extends SimpleRotatingContraptionEntity> extends IMultiController {
    Map<Integer, T> assemble(BlockPos pivot);
    List<T> getRotatingEntity();
    void setRotatingEntity(List<T> entities);
    default boolean isAttachedTo(AbstractContraptionEntity contraption) {
        return getRotatingEntity() != null && getRotatingEntity().contains(contraption);
    }

    default void attach(T contraption) {
        if (getRotatingEntity().isEmpty()) {
            setRotatingEntity(List.of(contraption));
        }
        else {
            List<T> rotatingEntity = new ArrayList<>(getRotatingEntity());
            rotatingEntity.add(contraption);
            setRotatingEntity(rotatingEntity);
        }
        self().holder.notifyBlockUpdate();
    }

    default BlockPos getBlockPosition() {
        return self().getPos();
    }
}
