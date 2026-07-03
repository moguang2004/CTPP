package com.mo_guang.ctpp.dynamicPart.rotation;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;

import com.lowdragmc.lowdraglib.utils.TrackedDummyWorld;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import com.mo_guang.ctpp.api.pattern.StaticBlockPattern;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;

import java.util.*;

public interface IContraptionMultiblock<T extends SimpleRotatingContraptionEntity> extends IMultiController {

    Map<Integer, T> assemble(BlockPos pivot);

    List<T> getContraptionEntity();

    void setContraptionEntity(List<T> entities);

    default boolean isAttachedTo(AbstractContraptionEntity contraption) {
        return getContraptionEntity() != null && getContraptionEntity().contains(contraption);
    }

    default void attach(T contraption) {
        if (getContraptionEntity().isEmpty()) {
            setContraptionEntity(List.of(contraption));
        } else {
            List<T> rotatingEntity = new ArrayList<>(getContraptionEntity());
            rotatingEntity.add(contraption);
            setContraptionEntity(rotatingEntity);
        }
        self().holder.notifyBlockUpdate();
    }

    default BlockPos getBlockPosition() {
        return self().getPos();
    }

    /**
     * After chunk reload, find existing entities that belong to this controller
     * and reattach them. This handles the case where the entity list was lost
     * during unload.
     */
    @SuppressWarnings("unchecked")
    default void findAndReattachEntities() {
        if (self().getLevel() == null || self().getLevel().isClientSide) return;
        if (getContraptionEntity() == null) setContraptionEntity(new ArrayList<>());
        // Only search if the list is empty — otherwise entities are already tracked
        if (!getContraptionEntity().isEmpty()) return;

        BlockPos pos = self().getPos();
        // Search in a 32-block radius for entities that reference this controller
        AABB searchBox = new AABB(pos).inflate(32);
        for (Entity entity : self().getLevel().getEntitiesOfClass(SimpleRotatingContraptionEntity.class, searchBox)) {
            T srEntity = (T) entity;
            if (srEntity.controllerPos != null && srEntity.controllerPos.equals(pos)) {
                // Found an entity that belongs to us — reattach
                getContraptionEntity().add(srEntity);
                if (!srEntity.isRunning()) {
                    srEntity.setRunning(true);
                }
            }
        }
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
                var part = entry.getValue().stream()
                        .filter(pos -> !self().getLevel().getBlockState(pos).isAir())
                        .toList();
                if (part.isEmpty()) continue;
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
        if (getContraptionEntity() == null) setContraptionEntity(new ArrayList<>());
        if (!getContraptionEntity().isEmpty()) return;
        Map<Integer, T> map = assembleFromPattern(pivot);
        if (map != null && !map.isEmpty()) {
            setContraptionEntity(new ArrayList<>(map.values()));
        }
    }

    /**
     * Helper: disassemble and clear all attached rotating entities.
     */
    default void clearAndDisassembleRotatingEntities() {
        if (getContraptionEntity() != null && !getContraptionEntity().isEmpty()) {
            getContraptionEntity().forEach(entity -> {
                if (entity != null) entity.disassemble();
            });
        }
        setContraptionEntity(new ArrayList<>());
    }
}
