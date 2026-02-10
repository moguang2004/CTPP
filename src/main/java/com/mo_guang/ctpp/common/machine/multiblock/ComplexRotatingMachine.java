package com.mo_guang.ctpp.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.utils.TrackedDummyWorld;
import com.mo_guang.ctpp.api.pattern.StaticBlockPattern;
import com.mo_guang.ctpp.dynamicPart.rotation.*;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComplexRotatingMachine extends WorkableElectricMultiblockMachine implements IRotationMultiblock<RubiksCubeContraptionEntity> {
    @Getter
    @Setter
    public List<RubiksCubeContraptionEntity> rotatingEntity = new ArrayList<>();
    public List<String> avalibleMoving = List.of("U", "U'", "D", "D'", "L", "L'", "R", "R'", "F", "F'", "B", "B'");
    protected TickableSubscription rotatingSubs;
    public int count = 0;
    public String rotation = "STOP";
    public ComplexRotatingMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        if (rotatingEntity.isEmpty()) {
            var rotatingEntities = assemble(MachineUtils.getOffset(this, 0, 0, 1));
            if (rotatingEntities != null) {
                this.rotatingEntity.addAll(rotatingEntities.values());
            }
        }
        this.rotatingSubs = this.subscribeServerTick(this::rotatingTick);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (!rotatingEntity.isEmpty()) {
            this.rotatingEntity.forEach(AbstractContraptionEntity::disassemble);
        }
        this.rotatingEntity.clear();
        if (rotatingSubs != null) {
            unsubscribe(rotatingSubs);
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        var button = Component.literal("旋转：");
        button.append(" ");
        button.append(ComponentPanelWidget.withButton(Component.literal("U"), "U"));
        button.append(" ");
        button.append(ComponentPanelWidget.withButton(Component.literal("L"), "L"));
        button.append(" ");
        button.append(ComponentPanelWidget.withButton(Component.literal("R"), "R"));
        button.append(" ");
        button.append(ComponentPanelWidget.withButton(Component.literal("F"), "F"));
        textList.add(button);
    }
    @Override
    public void handleDisplayClick(String componentData, ClickData clickData) {
        if (!clickData.isRemote) {
            rotation = componentData;
        }
    }
    public void rotatingTick() {
//        if (isFormed && rotatingEntity != null) {
//            var halfTick = 90 / RubiksCubeContraptionEntity.ROTATE_SPEED;
//            if (getOffsetTimer() % (2 * halfTick) == 0) {
//                rotatingEntity.forEach(entity -> {
//                    if(!(entity instanceof RubiksCubeContraptionEntity)) return;
//                    entity.performStandardMove(rotation);
//                });
//                rotation = "STOP";
//            }
//            if (getOffsetTimer() % (2 * halfTick) == halfTick) {
//                rotatingEntity.forEach(entity -> {
//                    if(!(entity instanceof RubiksCubeContraptionEntity)) return;
//                    entity.performStandardMove("STOP");
//                });
//            }
//        }
        if (isFormed && rotatingEntity != null) {
            var halfTick = 90 / RubiksCubeContraptionEntity.ROTATE_SPEED;
            if (getOffsetTimer() % (2 * halfTick) == 0) {
                int index = RandomSource.create().nextInt(avalibleMoving.size());
                rotatingEntity.forEach(entity -> entity.performStandardMove(avalibleMoving.get(index)));
            }
            if (getOffsetTimer() % (2 * halfTick) == halfTick) {
                rotatingEntity.forEach(entity -> entity.performStandardMove("STOP"));
            }
        }
    }

    @Override
    public Map<Integer, RubiksCubeContraptionEntity> assemble(BlockPos pivot) {
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
                RubiksCubeContraptionEntity contraptionEntity = RubiksCubeContraptionEntity.create(self().getLevel(), contraption, pivot.getCenter(), getFrontFacing(), pos, this);
                contraptionEntity.setPos(pivot.getX(), pivot.getY(), pivot.getZ());
                this.self().getLevel().addFreshEntity(contraptionEntity);
                ce.put(group, contraptionEntity);
            }
            return ce;
        }
        return null;
    }
}
