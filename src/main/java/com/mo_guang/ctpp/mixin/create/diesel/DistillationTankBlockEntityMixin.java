package com.mo_guang.ctpp.mixin.create.diesel;

import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import com.jesz.createdieselgenerators.content.distillation.DistillationRecipe;
import com.jesz.createdieselgenerators.content.distillation.DistillationTankBlockEntity;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DistillationTankBlockEntity.class, remap = false)
public abstract class DistillationTankBlockEntityMixin extends SmartBlockEntity {

    @Shadow
    int processingTime;
    @Shadow
    DistillationRecipe currentRecipe;

    @Shadow
    public abstract boolean isBottom();

    @Shadow
    public float progress;

    @Shadow
    public abstract void sendData();

    @Shadow
    public abstract boolean isController();

    @Shadow
    protected abstract boolean isSameMultiBlock(DistillationTankBlockEntity be);

    @Shadow
    public FluidTank tankInventory;

    @Shadow
    abstract void checkForRecipes();

    @Shadow
    BlazeBurnerBlock.HeatLevel highestHeatLevel;
    @Shadow
    protected int syncCooldown;
    @Shadow
    protected boolean queuedSync;
    @Shadow
    protected BlockPos lastKnownPos;

    @Shadow
    protected abstract void onPositionChanged();

    @Shadow
    protected boolean updateConnectivity;

    @Shadow
    public abstract void updateConnectivity();

    @Shadow
    private LerpedFloat fluidLevel;
    @Unique
    private int ctpp$totalHeatLevel = 0;
    @Unique
    private boolean ctpp$canWork = false;

    public DistillationTankBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "getHeat", at = @At("HEAD"))
    void resetTotalHeatLevel(CallbackInfoReturnable<BlazeBurnerBlock.HeatLevel> cir) {
        ctpp$totalHeatLevel = 0;
    }

    @Inject(method = "getHeat",
            at = @At(value = "INVOKE",
                     target = "Lcom/simibubi/create/content/processing/burner/BlazeBurnerBlock$HeatLevel;isAtLeast(Lcom/simibubi/create/content/processing/burner/BlazeBurnerBlock$HeatLevel;)Z"))
    void updateTotalHeatLevel(CallbackInfoReturnable<BlazeBurnerBlock.HeatLevel> cir,
                              @Local(name = "heat") BlazeBurnerBlock.HeatLevel heat) {
        if (heat == BlazeBurnerBlock.HeatLevel.SEETHING) {
            ctpp$totalHeatLevel += 2;
        } else if (heat == BlazeBurnerBlock.HeatLevel.FADING || heat == BlazeBurnerBlock.HeatLevel.KINDLED) {
            ctpp$totalHeatLevel += 1;
        }
    }

    /**
     * @author luckyblock
     * @reason optimize
     */
    @Overwrite
    public void tick() {
        if (isController() && isBottom()) {
            if (processingTime >= 0 && currentRecipe != null) {
                if (level.getGameTime() % 20 == 0) {
                    if (!ctpp$canDrain()) {
                        ctpp$resetProcessing();
                    } else {
                        ctpp$canWork = ctpp$canFill();
                    }
                }
                if (ctpp$canWork) {
                    ctpp$updateProgress();
                }
            }

            if (!level.isClientSide) {
                if (processingTime < 0 && currentRecipe != null) {
                    if (ctpp$canDrain() && ctpp$canFill()) {
                        tankInventory.drain(currentRecipe.getFluidIngredients().get(0).getRequiredAmount(),
                                IFluidHandler.FluidAction.EXECUTE);
                        if (currentRecipe != null)
                            for (int i = 0; i < currentRecipe.getFluidResults().size(); i++) {
                                if (level.getBlockEntity(
                                        getBlockPos().above(i + 1)) instanceof DistillationTankBlockEntity be) {
                                    be.tankInventory.fill(currentRecipe.getFluidResults().get(i),
                                            IFluidHandler.FluidAction.EXECUTE);
                                } else {
                                    break;
                                }
                            }
                    }

                    ctpp$resetProcessing();
                    if (!tankInventory.isEmpty()) {
                        checkForRecipes();
                    }
                }
            }
            progress = currentRecipe != null ? (float) processingTime / (currentRecipe.getProcessingDuration()) : 0;

        }
        super.tick();
        if (syncCooldown > 0) {
            syncCooldown--;
        } else {
            sendData();
        }

        if (lastKnownPos == null)
            lastKnownPos = getBlockPos();
        else if (!lastKnownPos.equals(worldPosition)) {
            onPositionChanged();
            return;
        }

        if (updateConnectivity)
            updateConnectivity();
        if (fluidLevel != null)
            fluidLevel.tickChaser();
    }

    @Unique
    private void ctpp$updateProgress() {
        DistillationRecipe recipe = currentRecipe;
        if (recipe == null) {
            ctpp$canWork = false;
            return;
        }
        if (recipe.getRequiredHeat() == HeatCondition.HEATED) {
            processingTime -= ctpp$totalHeatLevel;
        } else if (recipe.getRequiredHeat() == HeatCondition.SUPERHEATED) {
            processingTime -= ctpp$totalHeatLevel / 2;
        }
    }

    @Unique
    private void ctpp$resetProcessing() {
        currentRecipe = null;
        processingTime = -1;
        ctpp$canWork = false;
    }

    @Unique
    private boolean ctpp$canFill() {
        for (int i = 0; i < currentRecipe.getFluidResults().size(); i++) {
            if (level.getBlockEntity(getBlockPos().above(i + 1)) instanceof DistillationTankBlockEntity be) {
                if (!isSameMultiBlock(be)) {
                    return false;
                }
                if (be.tankInventory.getSpace() < (currentRecipe.getFluidResults().get(i).getAmount())) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    @Unique
    private boolean ctpp$canDrain() {
        return tankInventory.getFluid().getAmount() >= currentRecipe.getFluidIngredients().get(0).getRequiredAmount() &&
                currentRecipe.getRequiredHeat().testBlazeBurner(highestHeatLevel);
    }

    @Inject(method = "read", at = @At("TAIL"))
    void readTotalHeatLevel(CompoundTag compound, boolean clientPacket, CallbackInfo ci) {
        if (clientPacket) {
            ctpp$totalHeatLevel = compound.getInt("totalHeatLevel");
        }
    }

    @Inject(method = "write", at = @At("TAIL"))
    void writeTotalHeatLevel(CompoundTag compound, boolean clientPacket, CallbackInfo ci) {
        if (clientPacket) {
            compound.putInt("totalHeatLevel", ctpp$totalHeatLevel);
        }
    }
}
