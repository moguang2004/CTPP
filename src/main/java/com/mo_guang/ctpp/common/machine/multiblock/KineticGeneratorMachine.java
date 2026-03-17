package com.mo_guang.ctpp.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.block.ICoilType;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.ingredient.EnergyStack;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.block.CoilBlock;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.TrackedDummyWorld;
import com.mo_guang.ctpp.api.pattern.StaticBlockPattern;
import com.mo_guang.ctpp.common.machine.multiblock.part.KineticPartMachine;
import com.mo_guang.ctpp.config.MainConfig;
import com.mo_guang.ctpp.dynamicPart.rotation.IRotationMultiblock;
import com.mo_guang.ctpp.dynamicPart.rotation.SimpleRotatingContraption;
import com.mo_guang.ctpp.dynamicPart.rotation.SimpleRotatingContraptionEntity;
import com.mo_guang.ctpp.util.MathUtil;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.newage.content.generation.magnets.ImplementedMagnetBlock;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KineticGeneratorMachine extends KineticMultiblockMachine
        implements IRotationMultiblock<SimpleRotatingContraptionEntity> {

    public static final float GENERATING_BOOST = MainConfig.INSTANCE.ctnhConfig.kineticGeneratorGeneratingBoost;
    @Getter
    @Setter
    List<SimpleRotatingContraptionEntity> rotatingEntity = new ArrayList<>();
    private ICoilType coilType = CoilBlock.CoilType.CUPRONICKEL;
    public float previousSpeed;
    public float speed;
    public double b;
    public double efficiency;
    public double outputEnergy = 0;

    public KineticGeneratorMachine(IMachineBlockEntity holder) {
        super(holder);
        b = 0;
        efficiency = (getCoilTier() * 0.1 + 1) * (b / (b + 36));
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        var type = getMultiblockState().getMatchContext().get("CoilType");
        if (type instanceof ICoilType coil) {
            this.coilType = coil;
        }
//        var type = getMultiblockState().getMatchContext().get("ImplementedMagnetBlock");
//        if (type instanceof IMagneticBlock magnetite) {
//            this.b = magnetite.getStrength();
//        }
        for (BlockPos pos:BlockPos.betweenClosed(MachineUtils.getOffset(this, 1, 2, 3),MachineUtils.getOffset(this, 3, -2, -1))) {
            Object block = getLevel().getBlockState(pos).getBlock();
                    if (block instanceof ImplementedMagnetBlock magnetBlock) {
                        b += magnetBlock.getStrength();
                    }
        }
        efficiency = (getCoilTier() * 0.1 + 1) * (b / (b + 36));

        if (rotatingEntity.isEmpty() && !getLevel().isClientSide) {
            var rotatingEntities = assemble(MachineUtils.getOffset(this, 0, 0, 1));
            if (rotatingEntities != null) {
                this.rotatingEntity.addAll(rotatingEntities.values());
            }
        }
        rotatingEntity.forEach(entity -> {
            var facing = getFrontFacing().getNormal();
            Vec3 newF = new Vec3(facing.getX(), facing.getY(), facing.getZ());
            entity.setRotationSpeed(MathUtil.rotateByVec(newF, 90, new Vec3(0, -1, 0)), getInputSpeed());
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
            entity.setRotationSpeed(MathUtil.rotateByVec(newF, 90, new Vec3(0, -1, 0)), getInputSpeed());
        });
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        boolean result = super.beforeWorking(recipe);
        previousSpeed = speed;
        speed = getInputSpeed();
        if (speed != previousSpeed) {
            updateRotateBlocks(result);
        }
        return result;
    }
    @Override
    public void updateRotateBlocks(boolean active) {
        super.updateRotateBlocks(active);
        if (active) {
            float speed = MathUtil.rpm2rads(this.speed);
            if (rotatingEntity != null) rotatingEntity.forEach(entity -> entity.setRotationSpeed(0, -speed, 0));
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) {
            var voltageName = GTValues.VNF[GTUtil.getTierByVoltage((long) outputEnergy)];
            textList.add(textList.size(), Component.translatable("ctpp.multiblock.kinetic_generator.info.0",
                    FormattingUtil.formatNumbers(outputEnergy), voltageName));
            textList.add(textList.size(), Component.translatable("ctpp.multiblock.kinetic_generator.info.1",
                    FormattingUtil.formatNumbers(b)));
            textList.add(textList.size(), Component.translatable("ctpp.multiblock.kinetic_generator.info.2",
                    String.format("%.1f", efficiency * 100)));
        }
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (machine instanceof KineticGeneratorMachine kmachine) {
            var kinetic = (KineticPartMachine) kmachine.getParts().stream()
                    .filter(part -> part instanceof KineticPartMachine).toList().get(0);
            kmachine.outputEnergy = Math.abs(kinetic.getKineticHolder().getSpeed()) *
                    kinetic.getKineticDefinition().torque * kmachine.efficiency * GENERATING_BOOST / 128;
            var modifiedRecipe = recipe.copy();
            modifiedRecipe.tickOutputs.put(EURecipeCapability.CAP,
                    EURecipeCapability.makeEUContent(new EnergyStack((long) kmachine.outputEnergy)));
            return recipe1 -> modifiedRecipe;
        }
        return ModifierFunction.NULL;
    }

    @Override
    public boolean regressWhenWaiting() {
        return false;
    }

    public float getInputSpeed() {
            var kinetic = (KineticPartMachine) this.getParts().stream()
                    .filter(part -> part instanceof KineticPartMachine).toList().get(0);
                    return kinetic.getKineticHolder().getSpeed();
    }

    public int getCoilTier() {
        return coilType.getTier();
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
                SimpleRotatingContraptionEntity contraptionEntity = SimpleRotatingContraptionEntity
                        .create(self().getLevel(), contraption, this, pivot.getCenter());
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
