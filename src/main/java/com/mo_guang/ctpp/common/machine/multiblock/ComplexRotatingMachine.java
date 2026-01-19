package com.mo_guang.ctpp.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.mo_guang.ctpp.dynamicPart.rotation.IRubiksRotationMultiblock;
import com.mo_guang.ctpp.dynamicPart.rotation.RubiksCubeContraptionEntity;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public class ComplexRotatingMachine extends WorkableElectricMultiblockMachine implements IRubiksRotationMultiblock {
    public List<RubiksCubeContraptionEntity> rotatingEntities;
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
        if (rotatingEntities == null) {
            var rotatingEntities = assemble(MachineUtils.getOffset(this, 0, 0, 1), getFrontFacing());
            if (rotatingEntities != null) {
                this.rotatingEntities = new ArrayList<>(rotatingEntities.values());
            }
            this.rotatingSubs = this.subscribeServerTick(this::rotatingTick);
        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (rotatingEntities != null) {
            this.rotatingEntities.forEach(AbstractContraptionEntity::disassemble);
        }
        this.rotatingEntities = null;
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
    @Override
    public void onUnload() {
        super.onUnload();
        if (rotatingEntities != null) {
            this.rotatingEntities.forEach(AbstractContraptionEntity::disassemble);
        }
        this.rotatingEntities = null;
    }
    public void rotatingTick() {
        if (isFormed && rotatingEntities != null) {
            var halfTick = 90 / RubiksCubeContraptionEntity.ROTATE_SPEED;
            if (getOffsetTimer() % (2 * halfTick) == 0) {
                rotatingEntities.forEach(entity -> entity.performStandardMove(rotation));
                rotation = "STOP";
//                count += 1;
//                int index = count % avalibleMoving.size();
//                rotatingEntities.forEach(entity -> entity.performStandardMove(avalibleMoving.get(index)));
            }
            if (getOffsetTimer() % (2 * halfTick) == halfTick) {
                rotatingEntities.forEach(entity -> entity.performStandardMove("STOP"));
            }
        }
    }
}
