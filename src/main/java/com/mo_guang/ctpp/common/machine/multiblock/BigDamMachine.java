package com.mo_guang.ctpp.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import com.mo_guang.ctpp.dynamicPart.rotation.IContraptionMultiblock;
import com.mo_guang.ctpp.dynamicPart.rotation.SimpleRotatingContraptionEntity;
import com.mo_guang.ctpp.util.MathUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BigDamMachine extends KineticOutputMachine
                           implements IContraptionMultiblock<SimpleRotatingContraptionEntity> {

    @Getter
    @Setter
    List<SimpleRotatingContraptionEntity> contraptionEntity = new ArrayList<>();

    public BigDamMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        // assemble rotating entities using interface helper
        createAndAttachRotatingEntities(MachineUtils.getOffset(this, 0, 6, 9));
        contraptionEntity.forEach(entity -> {
            var facing = getFrontFacing().getNormal();
            Vec3 newF = new Vec3(facing.getX(), facing.getY(), facing.getZ());
            entity.setRotationSpeed(MathUtil.rotateByVec(newF, 90, new Vec3(0, -1, 0)), 2);
        });
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (!getLevel().isClientSide) {
            // disassemble and clear using helper
            clearAndDisassembleRotatingEntities();
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        // After reload, find existing entities and reapply rotation
        if (!getLevel().isClientSide) {
            findAndReattachEntities();
            contraptionEntity.forEach(entity -> {
                var facing = getFrontFacing().getNormal();
                Vec3 newF = new Vec3(facing.getX(), facing.getY(), facing.getZ());
                entity.setRotationSpeed(MathUtil.rotateByVec(newF, 90, new Vec3(0, -1, 0)), 2);
            });
        }
    }

    @Override
    public Map<Integer, SimpleRotatingContraptionEntity> assemble(BlockPos pivot) {
        return assembleFromPattern(pivot);
    }

    @Override
    public BlockPos getAssemblyPivot() {
        return MachineUtils.getOffset(this, 0, 6, 9);
    }

    @Override
    public void onDebugAssembled() {
        contraptionEntity.forEach(entity -> {
            var facing = getFrontFacing().getNormal();
            Vec3 newF = new Vec3(facing.getX(), facing.getY(), facing.getZ());
            entity.setRotationSpeed(MathUtil.rotateByVec(newF, 90, new Vec3(0, -1, 0)), 2);
        });
    }
}
