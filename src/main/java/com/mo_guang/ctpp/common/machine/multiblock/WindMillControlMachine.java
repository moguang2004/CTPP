package com.mo_guang.ctpp.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.mo_guang.ctpp.common.machine.IKineticMachine;
import com.mo_guang.ctpp.rotate.SimpleRotatingContraptionEntity;
import com.mo_guang.ctpp.util.MathUtil;
import com.mojang.datafixers.util.Pair;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WindMillControlMachine extends KineticOutputMachine implements IRotationMultiblock {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            WindMillControlMachine.class, KineticOutputMachine.MANAGED_FIELD_HOLDER);
    @Persisted
    public static List<Pair<Level, BlockPos>> workingWindmill = new ArrayList<>();
    public static List<Pair<Level, BlockPos>> workingWindmillController = new ArrayList<>();
    public SimpleRotatingContraptionEntity rotatingEntity;
    public int efficiency = 0;
    public float TotalOutput = 0;

    public boolean willTick = false;
    public WindMillControlMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    //////////////////////////////////////
    // *** Multiblock LifeCycle ***//
    //////////////////////////////////////
    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        calculateWindmillAround();
        if (rotatingEntity == null) {
            var rotatingEntities = assemble(MachineUtils.getOffset(this, 0, 5, 5));
            if (rotatingEntities != null) {
                this.rotatingEntity = rotatingEntities.get(0);
            }
        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (rotatingEntity != null) {
            this.rotatingEntity.disassemble();
        }
        this.rotatingEntity = null;
    }

    @Override
    public boolean onWorking() {
        if (willTick) {
            calculateWindmillAround();
        }
        return super.onWorking();
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        workingWindmillController.add(Pair.of(this.getLevel(), this.getPos()));
        boolean result = super.beforeWorking(recipe);
        previousSpeed = speed;
        speed = getOutputSpeed();
        if(speed != previousSpeed){
            updateRotateBlocks(result);
        }
        return result;
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        workingWindmillController.removeIf(levelBlockPosPair ->
                levelBlockPosPair.getFirst().equals(this.getLevel()) && levelBlockPosPair.getSecond().equals(this.getPos()));
    }

    //////////////////////////////////////
    // *** Rotation Control ***//
    //////////////////////////////////////
    @Override
    public void updateRotateBlocks(boolean active){
                   super.updateRotateBlocks(active);
        if (active) {
            float speed = MathUtil.rpm2rads(this.speed);
            if (rotatingEntity != null) rotatingEntity.setRotationSpeed(0, -speed, 0);
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) {
            textList.add(Component.translatable("ctpp.multiblock.windmill_control_center.info.0", efficiency, 4 + 2 * tier));
            textList.add(Component.translatable("ctpp.multiblock.windmill_control_center.info.1", String.format("%.1f",TotalOutput)));
            textList.add(Component.translatable("ctpp.multiblock.windmill_control_center.info.2", String.format("%d",efficiency*100)));
            textList.add(Component.translatable("ctpp.multiblock.windmill_control_center.info.3",String.format("%.1f",(TotalOutput + 512) * efficiency)));
        }
    }
    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (machine instanceof WindMillControlMachine wmachine) {
            var add = ModifierFunction.builder().outputModifier(ContentModifier.addition(wmachine.TotalOutput)).build();
            return add.andThen(ModifierFunction.builder().outputModifier(ContentModifier.multiplier(wmachine.efficiency)).build());
        }
        return ModifierFunction.NULL;
    }
    public float getOutputSpeed() {
        return Math.min((512 + TotalOutput) * efficiency / 512, AllConfigs.server().kinetics.maxRotationSpeed.get());
    }
    public void calculateWindmillAround() {
        var WindMillAround = new ArrayList<>();
        TotalOutput = 0;
        for (var windmill: workingWindmill) {
            if (Mth.sqrt((float) windmill.getSecond().distToCenterSqr(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ())) <= 32) {
                var kineticBlockEntity = getLevel().getBlockEntity(windmill.getSecond());
                if (kineticBlockEntity instanceof WindmillBearingBlockEntity windmillBearingBlockEntity) {
                    var speed = windmillBearingBlockEntity.getGeneratedSpeed();
                    if (speed != 0 && WindMillAround.size() <= 16) {
                        WindMillAround.add(speed);
                        TotalOutput += speed * 512;
                    }
                }
            }
        }
        efficiency = Math.min(WindMillAround.size(),6 + tier * 2);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}
