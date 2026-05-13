package com.mo_guang.ctpp.dynamicPart.rotation;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;

import com.lowdragmc.lowdraglib.utils.TrackedDummyWorld;

import net.minecraft.core.BlockPos;

import com.mo_guang.ctpp.api.pattern.StaticBlockPattern;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;

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
        } else {
            List<T> rotatingEntity = new ArrayList<>(getRotatingEntity());
            rotatingEntity.add(contraption);
            setRotatingEntity(rotatingEntity);
        }
        self().holder.notifyBlockUpdate();
    }

    default BlockPos getBlockPosition() {
        return self().getPos();
    }

    /**
     * Default helper to assemble rotating contraptions from a StaticBlockPattern dynamic part.
     * Implementations can call this to avoid duplicating assembly logic.
     */
    @SuppressWarnings("unchecked")
    default Map<Integer, T> assembleFromPattern(BlockPos pivot) {
        if (self().getLevel() instanceof TrackedDummyWorld) return null;
        if (self().getLevel().isClientSide) return null;
        Map<Integer, T> ce = new HashMap<>();
        var pattern = self().getDefinition().getPatternFactory().get();
        if (pattern instanceof StaticBlockPattern staticBlockPattern) {
            Map<Integer, List<BlockPos>> dynamicPart = staticBlockPattern.getDynamicPart(self().getMultiblockState());
            for (var entry : dynamicPart.entrySet()) {
                int group = entry.getKey();
                var part = entry.getValue();
                SimpleRotatingContraption contraption = new SimpleRotatingContraption(part, pivot);
                contraption.assemble(this.self().getLevel(), self().getPos());
                contraption.removeBlocksFromWorld(this.self().getLevel(), BlockPos.ZERO);
                SimpleRotatingContraptionEntity contraptionEntity = SimpleRotatingContraptionEntity
                        .create(self().getLevel(), contraption, this, pivot.getCenter());
                contraptionEntity.setPos(pivot.getX(), pivot.getY(), pivot.getZ());
                this.self().getLevel().addFreshEntity(contraptionEntity);
                ce.put(group, (T) contraptionEntity);
            }
            return ce;
        }
        return null;
    }

    /**
     * Helper: assemble rotating contraptions and attach them to this controller.
     * Will only create entities on server and non-dummy worlds, and only if no rotating entities exist yet.
     */
    default void createAndAttachRotatingEntities(BlockPos pivot) {
        if (self().getLevel() instanceof TrackedDummyWorld) return;
        if (self().getLevel().isClientSide) return;
        if (getRotatingEntity() == null) setRotatingEntity(new ArrayList<>());
        if (!getRotatingEntity().isEmpty()) return;
        Map<Integer, T> map = assembleFromPattern(pivot);
        if (map != null && !map.isEmpty()) {
            setRotatingEntity(new ArrayList<>(map.values()));
        }
    }

    /**
     * Helper: disassemble and clear all attached rotating entities.
     */
    default void clearAndDisassembleRotatingEntities() {
        if (getRotatingEntity() != null && !getRotatingEntity().isEmpty()) {
            getRotatingEntity().forEach(entity -> {
                if (entity != null) entity.disassemble();
            });
        }
        setRotatingEntity(new ArrayList<>());
    }
}
