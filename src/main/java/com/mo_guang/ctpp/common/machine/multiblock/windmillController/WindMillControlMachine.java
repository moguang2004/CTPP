package com.mo_guang.ctpp.common.machine.multiblock.windmillController;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.handler.RecipeHandlerGroup;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;

import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.mo_guang.ctpp.api.StressRecipeCapability;
import com.mo_guang.ctpp.common.machine.multiblock.KineticOutputMachine;
import com.mo_guang.ctpp.dynamicPart.rotation.IContraptionMultiblock;
import com.mo_guang.ctpp.dynamicPart.rotation.SimpleRotatingContraptionEntity;
import com.mo_guang.ctpp.util.MathUtil;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.infrastructure.config.AllConfigs;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.vixhentx.mcmod.ctnhlib.client.render.ColorData;
import tech.vixhentx.mcmod.ctnhlib.client.render.highlight.HighlightHandler;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;
import tech.vixhentx.mcmod.ctnhlib.utils.MachineUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WindMillControlMachine extends KineticOutputMachine
                                    implements IContraptionMultiblock<SimpleRotatingContraptionEntity> {

    @CN("附近存在其他风车控制中心")
    @EN("There are other Windmill Controllers Around")
    static Lang conflict;

    @CN({ "控制的风车数量：%d(最大：%d)", "控制的风车总应力：%dsu", "总产能效率：%d%%", "总应力输出：§a%dsu§r" })
    @EN({ "Number of controlled windmills: %d(Max: %d)", "Total stress of controlled windmills: %dsu",
            "Total energy efficiency: %d%%", "Total stress output: §a%dsu§r" })
    static Lang[] info;

    @CN("高亮显示")
    @EN("Highlight Info")
    static Lang highlightInfo;

    public static int LEGAL_DISTANCE = 64;
    public List<BlockPos> windmillAround = new ArrayList<>();
    public int efficiency = 0;
    public float TotalOutput = 0;
    @Getter
    @Setter
    List<SimpleRotatingContraptionEntity> contraptionEntity = new ArrayList<>();
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
            // 重启/区块重载后 isFormed 等持久化字段已从 NBT 恢复，但效率/转速/实体列表这类
            // 运行时状态不会自动重建（onStructureFormed 不保证再次触发），这里一次性恢复
            if (isFormed()) {
                calculateWindmillAround();
                float currentSpeed = getOutputSpeed();
                if (this.speed != currentSpeed) {
                    this.speed = currentSpeed;
                }
                findAndReattachEntities();
                if (getRecipeLogic().isWorking()) {
                    updateRotateBlocks(true);
                }
            }
            // 向WindmillManager提交扫描任务：参数（世界，控制中心位置，扫描半径32，冲突检测距离64）
            WindmillManager.getInstance().submitScanTask(
                    serverLevel,
                    controllerPos,
                    32,
                    LEGAL_DISTANCE);
        }
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        calculateWindmillAround();
        // 结构刚恢复时尽快同步真实转速，避免沿用默认值 64
        float currentSpeed = getOutputSpeed();
        if (this.speed != currentSpeed) {
            this.speed = currentSpeed;
        }
        if (!getLevel().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) getLevel();
            WindmillSavedData windmillData = WindmillSavedData.get(serverLevel);
            windmillData.registerFormedController(this.getPos());
        }
        // assemble rotating entities (use interface helper)
        createAndAttachRotatingEntities(MachineUtils.getOffset(this, 0, 5, 5), Direction.Axis.Y);
        // 先装配再下发转速，确保新装配/已重挂的实体能立即拿到转速
        if (getRecipeLogic().isWorking()) {
            updateRotateBlocks(true);
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
        // clear and disassemble rotating entities (use interface helper)
        clearAndDisassembleRotatingEntities();
    }

    @Override
    public void attach(SimpleRotatingContraptionEntity contraption) {
        IContraptionMultiblock.super.attach(contraption);
        // 实体在区块重载后通过自身 tick 重新挂接时，立即补发一次转速，
        // 否则 onStructureFormed 下发转速时实体列表可能还是空的，导致配方运行但结构不转
        if (isFormed() && getRecipeLogic().isWorking()) {
            updateRotateBlocks(true);
        }
    }

    @Override
    public boolean onWorking() {
        if (willTick) {
            calculateWindmillAround();
        }
        return super.onWorking();
    }

    @Override
    public @Nullable Component beforeWorking(@NotNull GTRecipe recipe) {
        Component result = super.beforeWorking(recipe);
        var previousSpeed = speed;
        speed = getOutputSpeed();
        if (speed != previousSpeed) {
            updateRotateBlocks(result == null);
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
        return assembleFromPattern(pivot, Direction.Axis.Y);
    }

    //////////////////////////////////////
    // *** Rotation Control ***//
    //////////////////////////////////////
    @Override
    public void updateRotateBlocks(boolean active) {
        super.updateRotateBlocks(active);
        if (active) {
            float speed = MathUtil.rpm2rads(this.speed);
            if (contraptionEntity != null)
                contraptionEntity.forEach(entity -> entity.setRotationSpeedRPM(new Vec3(0, -1, 0), speed));
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) {
            if (hasConflictingController) {
                textList.add(conflict.translate()
                        .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                return;
            }
            var button = ComponentPanelWidget.withButton(highlightInfo.translate().withStyle(ChatFormatting.RED),
                    "Highlight");
            textList.add(
                    info[0].translate(efficiency, getMaxControlledSize()).append(button));
            textList.add(info[1].translate(String.format("%.1f", TotalOutput)));
            textList.add(info[2].translate(String.format("%d", efficiency * 100)));
            // textList.add(Component.translatable("ctpp.multiblock.windmill_control_center.info.3",String.format("%.1f",(TotalOutput
            // + 512) * efficiency)));
        }
    }

    @Override
    public void handleDisplayClick(String componentData, ClickData clickData) {
        if (!clickData.isRemote) {
            if (componentData.equals("Highlight")) {
                windmillAround.forEach(blockPos -> HighlightHandler.highlight(blockPos, this.getLevel().dimension(),
                        System.currentTimeMillis() + 10000, ColorData.RED));
            }
        }
    }

    public static @Nullable Component recipeModifier(MetaMachine machine, RecipeHandlerGroup group, GTRecipe recipe) {
        if (machine instanceof WindMillControlMachine wmachine) {
            if (wmachine.hasConflictingController) {
                recipe.outputs.put(StressRecipeCapability.CAP, List.of(0.0f));
                return null;
            }
            float output = wmachine.TotalOutput * wmachine.efficiency;
            recipe.outputs.put(StressRecipeCapability.CAP, List.of(output));
            return null;
        }
        return RecipeModifier.nullWrongType(WindMillControlMachine.class, machine);
    }

    public void refreshControllerState() {
        // 强制重新计算风车和冲突状态
        calculateWindmillAround();
    }

    public float getOutputSpeed() {
        if (hasConflictingController) {
            return 0.0f;
        }
        return (float) Math.min(Math.sqrt((512 + TotalOutput) * efficiency / 512),
                AllConfigs.server().kinetics.maxRotationSpeed.get());
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
            for (var windmill : workingWindmill) {
                if (Mth.sqrt((float) windmill.distToCenterSqr(this.getPos().getX(), this.getPos().getY(),
                        this.getPos().getZ())) <= 32) {
                    var kineticBlockEntity = getLevel().getBlockEntity(windmill);
                    if (kineticBlockEntity instanceof WindmillBearingBlockEntity windmillBearingBlockEntity) {
                        var speed = Math.abs(windmillBearingBlockEntity.getGeneratedSpeed());
                        if (speed != 0 && windmillAround.size() < getMaxControlledSize()) {
                            windmillAround.add(windmill);
                            TotalOutput += speed * 512;
                        }
                    }
                }
            }
            efficiency = Math.min(windmillAround.size(), getMaxControlledSize());
        }
    }

    public int getMaxControlledSize() {
        return tier * 4 + 4;
    }

    @Override
    public void saveCustomPersistedData(@NotNull CompoundTag tag, boolean forDrop) {
        super.saveCustomPersistedData(tag, forDrop);
    }

    @Override
    public BlockPos getAssemblyPivot() {
        return MachineUtils.getOffset(this, 0, 5, 5);
    }

    @Override
    public void onDebugAssembled() {
        updateRotateBlocks(getRecipeLogic().isWorking());
    }
}
