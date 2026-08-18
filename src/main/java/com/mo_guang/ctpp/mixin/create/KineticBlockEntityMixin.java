package com.mo_guang.ctpp.mixin.create;

import net.minecraft.nbt.CompoundTag;

import com.mo_guang.ctpp.common.blockentity.IKineticBlockEntityExtension;
import com.mo_guang.ctpp.common.blockentity.KineticMachineBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KineticBlockEntity.class)
public class KineticBlockEntityMixin implements IKineticBlockEntityExtension {

    @Unique
    private boolean CTNH$inMultiblock;

    @Unique
    private float CTNH$visualSpeed;

    @Inject(method = "write", at = @At("TAIL"), remap = false)
    private void ctpp$writeMultiblockMarker(CompoundTag compound, boolean clientPacket, CallbackInfo ci) {
        if (CTNH$inMultiblock) {
            compound.putBoolean("CTPPInMultiblock", true);
        }
        if (clientPacket && CTNH$visualSpeed != 0) {
            compound.putFloat("CTNHVisualSpeed", CTNH$visualSpeed);
        }
    }

    @Inject(method = "read", at = @At("TAIL"), remap = false)
    private void ctpp$readMultiblockMarker(CompoundTag compound, boolean clientPacket, CallbackInfo ci) {
        CTNH$inMultiblock = compound.getBoolean("CTPPInMultiblock");
        if (clientPacket) {
            CTNH$visualSpeed = compound.getFloat("CTNHVisualSpeed");
        }
    }

    @Inject(method = "getSpeed", at = @At("RETURN"), remap = false, cancellable = true)
    private void ctpp$provideVisualSpeed(CallbackInfoReturnable<Float> cir) {
        KineticBlockEntity blockEntity = (KineticBlockEntity) (Object) this;
        if (blockEntity.getLevel() != null && blockEntity.getLevel().isClientSide && CTNH$inMultiblock &&
                !(blockEntity instanceof KineticMachineBlockEntity)) {
            cir.setReturnValue(CTNH$visualSpeed);
        }
    }

    @Inject(method = "needsSpeedUpdate", at = @At("RETURN"), remap = false, cancellable = true)
    private void ctpp$disableKineticReattachment(CallbackInfoReturnable<Boolean> cir) {
        KineticBlockEntity blockEntity = (KineticBlockEntity) (Object) this;
        if (CTNH$inMultiblock && !(blockEntity instanceof KineticMachineBlockEntity)) {
            cir.setReturnValue(false);
        }
    }

    @Override
    public void setCTNHInMultiblock(boolean inMultiblock) {
        if (CTNH$inMultiblock == inMultiblock) {
            return;
        }
        CTNH$inMultiblock = inMultiblock;
        KineticBlockEntity blockEntity = (KineticBlockEntity) (Object) this;
        if (blockEntity.getLevel() != null && !blockEntity.getLevel().isClientSide) {
            if (inMultiblock && !(blockEntity instanceof KineticMachineBlockEntity)) {
                ctpp$detachFromCreateNetwork(blockEntity);
            } else if (!inMultiblock) {
                // Allow ordinary Create propagation again after the multiblock is gone.
                blockEntity.updateSpeed = true;
            }
            blockEntity.setChanged();
        }
        if (blockEntity.getLevel() != null && !blockEntity.getLevel().isClientSide) {
            blockEntity.sendData();
        }
    }

    @Unique
    private void ctpp$detachFromCreateNetwork(KineticBlockEntity blockEntity) {
        // Old versions wrote the visual speed into Create's real kinetic fields. Remove
        // that state when the block is first claimed by the new render-only path.
        if (blockEntity.hasNetwork() || blockEntity.hasSource() || blockEntity.getTheoreticalSpeed() != 0) {
            blockEntity.detachKinetics();
            blockEntity.setSpeed(0);
            blockEntity.source = null;
            blockEntity.sequenceContext = null;
            blockEntity.setNetwork(null);
        }
        blockEntity.updateSpeed = false;
    }

    @Override
    public void setCTNHVisualSpeed(float speed) {
        if (Float.compare(CTNH$visualSpeed, speed) == 0) {
            return;
        }
        CTNH$visualSpeed = speed;
        KineticBlockEntity blockEntity = (KineticBlockEntity) (Object) this;
        if (blockEntity.getLevel() != null && !blockEntity.getLevel().isClientSide) {
            blockEntity.sendData();
        }
    }
}
