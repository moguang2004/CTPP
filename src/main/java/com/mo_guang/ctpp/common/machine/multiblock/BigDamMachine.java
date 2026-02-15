package com.mo_guang.ctpp.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.TrackedDummyWorld;
import com.mo_guang.ctpp.api.pattern.StaticBlockPattern;
import com.mo_guang.ctpp.dynamicPart.rotation.IRotationMultiblock;
import com.mo_guang.ctpp.dynamicPart.rotation.SimpleRotatingContraption;
import com.mo_guang.ctpp.dynamicPart.rotation.SimpleRotatingContraptionEntity;
import com.mo_guang.ctpp.util.MathUtil;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BigDamMachine extends KineticOutputMachine implements IRotationMultiblock<SimpleRotatingContraptionEntity> {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            BigDamMachine.class, KineticOutputMachine.MANAGED_FIELD_HOLDER);
    @Getter
    @Setter
    List<SimpleRotatingContraptionEntity> rotatingEntity = new ArrayList<>();
    public BigDamMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        if (rotatingEntity.isEmpty() && !getLevel().isClientSide) {
            var rotatingEntities = assemble(MachineUtils.getOffset(this, 0, 6, 9));
            if (rotatingEntities != null) {
                this.rotatingEntity.addAll(rotatingEntities.values());
            }
        }
        rotatingEntity.forEach(entity -> {
            var facing = getFrontFacing().getNormal();
            Vec3 newF = new Vec3(facing.getX(), facing.getY(), facing.getZ());
            entity.setRotationSpeed(MathUtil.rotateByVec(newF, 90, new Vec3(0, -1 ,0)), 2);
        });
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (!getLevel().isClientSide) {
            if (!rotatingEntity.isEmpty()) {
                this.rotatingEntity.forEach(AbstractContraptionEntity::disassemble);
            }
            this.rotatingEntity = new ArrayList<>();
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        rotatingEntity.forEach(entity -> {
            var facing = getFrontFacing().getNormal();
            Vec3 newF = new Vec3(facing.getX(), facing.getY(), facing.getZ());
            entity.setRotationSpeed(MathUtil.rotateByVec(newF, 90, new Vec3(0, -1 ,0)), 2);
        });
    }

    @Override
    public Map<Integer, SimpleRotatingContraptionEntity> assemble(BlockPos pivot) {
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

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}
