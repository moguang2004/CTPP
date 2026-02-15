package com.mo_guang.ctpp.common.machine.multiblock.windmillController;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.TrackedDummyWorld;
import com.mo_guang.ctpp.api.pattern.StaticBlockPattern;
import com.mo_guang.ctpp.common.machine.multiblock.KineticOutputMachine;
import com.mo_guang.ctpp.common.machine.multiblock.MachineUtils;
import com.mo_guang.ctpp.dynamicPart.rotation.IRotationMultiblock;
import com.mo_guang.ctpp.dynamicPart.rotation.SimpleRotatingContraption;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WindMillControlMachine extends KineticOutputMachine implements IRotationMultiblock<SimpleRotatingContraptionEntity> {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            WindMillControlMachine.class, KineticOutputMachine.MANAGED_FIELD_HOLDER);
    public static int LEGAL_DISTANCE = 64;
    public List<BlockPos> windmillAround = new ArrayList<>();
    public int efficiency = 0;
    public float TotalOutput = 0;
    @Getter
    @Setter
    List<SimpleRotatingContraptionEntity> rotatingEntity = new ArrayList<>();
    public boolean willTick = false;
    public boolean hasConflictingController = false;
    public WindMillControlMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    //////////////////////////////////////
    // *** Multiblock LifeCycle ***//
    //////////////////////////////////////

    @Override
    public void onLoad() {
        super.onLoad();
        if (!getLevel().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) getLevel();
            BlockPos controllerPos = this.getPos();
            // 向WindmillManager提交扫描任务：参数（世界，控制中心位置，扫描半径32，冲突检测距离64）
            WindmillManager.getInstance().submitScanTask(
                    serverLevel,
                    controllerPos,
                    32, // 你的原代码中扫描半径是32
                    LEGAL_DISTANCE // 64，控制器冲突检测距离
            );
        }
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        calculateWindmillAround();
        if (!getLevel().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) getLevel();
            WindmillSavedData windmillData = WindmillSavedData.get(serverLevel);
            windmillData.registerFormedController(this.getPos());
        }
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
        if (!getLevel().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) getLevel();
            WindmillSavedData windmillData = WindmillSavedData.get(serverLevel);
            windmillData.unregisterFormedController(this.getPos());
            windmillData.notifyAllControllersRefresh(serverLevel, getPos());
        }
        if (!rotatingEntity.isEmpty() && !getLevel().isClientSide) {
            this.rotatingEntity.forEach(AbstractContraptionEntity::disassemble);
        }
        this.rotatingEntity = new ArrayList<>();
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
            if (hasConflictingController) {
                textList.add(Component.translatable("ctpp.multiblock.windmill_control_center.conflict")
                        .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                // 冲突时直接返回，不显示正常数据（或按需保留，标记为无效）
                return;
            }
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
                windmillAround.forEach(blockPos -> HighlightHandler.highlight(blockPos, this.getLevel().dimension(), System.currentTimeMillis() + 10000, ColorData.RED));
            }
        }
    }
    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (machine instanceof WindMillControlMachine wmachine) {
            if (wmachine.hasConflictingController) {
                return ModifierFunction.builder().outputModifier(ContentModifier.multiplier(0)).build();
            }
            var add = ModifierFunction.builder().outputModifier(ContentModifier.addition(wmachine.TotalOutput)).build();
            return add.andThen(ModifierFunction.builder().outputModifier(ContentModifier.multiplier(wmachine.efficiency)).build());
        }
        return ModifierFunction.NULL;
    }

    public void refreshControllerState() {
        // 强制重新计算风车和冲突状态
        calculateWindmillAround();
    }

    public float getOutputSpeed() {
        if (hasConflictingController) {
            return 0.0f;
        }
        return Math.min((512 + TotalOutput) * efficiency / 512, AllConfigs.server().kinetics.maxRotationSpeed.get());
    }
    public void calculateWindmillAround() {
        windmillAround.clear();
        TotalOutput = 0;
        hasConflictingController = false;
        if (!getLevel().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) getLevel();
            WindmillSavedData windmillData = WindmillSavedData.get(serverLevel);
            // 调用WindmillSavedData的冲突校验方法
            hasConflictingController = windmillData.hasConflictingController(this.getPos(), LEGAL_DISTANCE);

            // 存在冲突则直接返回（输出保持0），无冲突再计算风车数据
            if (hasConflictingController) {
                return;
            }
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
