package com.mo_guang.ctpp.common.machine.multiblock.windmillController;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.mo_guang.ctpp.common.machine.multiblock.KineticOutputMachine;
import com.mo_guang.ctpp.common.machine.multiblock.MachineUtils;
import com.mo_guang.ctpp.dynamicPart.rotation.IRotationMultiblock;
import com.mo_guang.ctpp.dynamicPart.rotation.SimpleRotatingContraptionEntity;
import com.mo_guang.ctpp.util.MathUtil;
import com.mojang.datafixers.util.Pair;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.infrastructure.config.AllConfigs;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.vixhentx.mcmod.ctnhlib.client.render.ColorData;
import tech.vixhentx.mcmod.ctnhlib.client.render.highlight.HighlightHandler;

import java.util.ArrayList;
import java.util.List;

public class WindMillControlMachine extends KineticOutputMachine implements IRotationMultiblock {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            WindMillControlMachine.class, KineticOutputMachine.MANAGED_FIELD_HOLDER);

    public static List<Pair<Level, BlockPos>> formedWindmillController = new ArrayList<>();
    public static int LEGAL_DISTANCE = 64;
    public List<BlockPos> windmillAround = new ArrayList<>();
    public int efficiency = 0;
    public float TotalOutput = 0;
    @Getter
    @Setter
    List<SimpleRotatingContraptionEntity> rotatingEntity = new ArrayList<>();
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
//        boolean islegal = true;
//        for (Pair<Level, BlockPos> levelBlockPosPair : formedWindmillController) {
//            if (levelBlockPosPair.getFirst().equals(this.getLevel()) &&
//                    levelBlockPosPair.getSecond().closerToCenterThan(this.getPos().getCenter(), LEGAL_DISTANCE)) {
//                islegal = false;
//            }
//        }
//        if (!islegal) {
//            onStructureInvalid();
//            return;
//        }
        calculateWindmillAround();
        formedWindmillController.add(Pair.of(this.getLevel(), this.getPos()));
        if (rotatingEntity.isEmpty() && !getLevel().isClientSide) {
            var rotatingEntities = assemble(MachineUtils.getOffset(this, 0, 5, 5));
            if (rotatingEntities != null) {
                this.rotatingEntity.addAll(rotatingEntities.values());
            }
        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        formedWindmillController.removeIf(levelBlockPosPair ->
                levelBlockPosPair.getFirst().equals(this.getLevel()) && levelBlockPosPair.getSecond().equals(this.getPos()));
        if (!rotatingEntity.isEmpty() && !getLevel().isClientSide) {
            this.rotatingEntity.forEach(AbstractContraptionEntity::disassemble);
        }
        this.rotatingEntity.clear();
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
        boolean result = super.beforeWorking(recipe);
        previousSpeed = speed;
        speed = getOutputSpeed();
        if(speed != previousSpeed){
            updateRotateBlocks(result);
        }
        return result;
    }

    @Override
    public void onTierChanged() {
        super.onTierChanged();
        calculateWindmillAround();
    }
    //////////////////////////////////////
    // *** Rotation Control ***//
    //////////////////////////////////////
    @Override
    public void updateRotateBlocks(boolean active){
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
            var button = ComponentPanelWidget.withButton(Component.translatable("ctpp.multiblock.windmill_control_center.button").withStyle(ChatFormatting.RED), "Highlight");
            textList.add(Component.translatable("ctpp.multiblock.windmill_control_center.info.0", efficiency, 6 + 2 * tier).append(button));
            textList.add(Component.translatable("ctpp.multiblock.windmill_control_center.info.1", String.format("%.1f",TotalOutput)));
            textList.add(Component.translatable("ctpp.multiblock.windmill_control_center.info.2", String.format("%d",efficiency*100)));
            //textList.add(Component.translatable("ctpp.multiblock.windmill_control_center.info.3",String.format("%.1f",(TotalOutput + 512) * efficiency)));
        }
    }
    @Override
    public void handleDisplayClick(String componentData, ClickData clickData) {
        if (!clickData.isRemote) {
            if (componentData.equals("Highlight")) {
                windmillAround.forEach(blockPos -> HighlightHandler.highlight(blockPos, this.getLevel().dimension(), System.currentTimeMillis() + 5000, ColorData.RED));
            }
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
        windmillAround.clear();
        TotalOutput = 0;
        var workingWindmill = WindmillSavedData.get((ServerLevel) getLevel()).getAllWindmills();
        for (var windmill: workingWindmill) {
            if (Mth.sqrt((float) windmill.distToCenterSqr(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ())) <= 32) {
                var kineticBlockEntity = getLevel().getBlockEntity(windmill);
                if (kineticBlockEntity instanceof WindmillBearingBlockEntity windmillBearingBlockEntity) {
                    var speed = windmillBearingBlockEntity.getGeneratedSpeed();
                    if (speed != 0 && windmillAround.size() <= 6 + tier * 6) {
                        windmillAround.add(windmill);
                        TotalOutput += speed * 512;
                    }
                }
            }
        }
        efficiency = Math.min(windmillAround.size(),6 + tier * 6);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void saveCustomPersistedData(@NotNull CompoundTag tag, boolean forDrop) {
        super.saveCustomPersistedData(tag, forDrop);
    }
}
