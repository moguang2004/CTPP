package com.mo_guang.ctpp.mixin.create;

import com.mo_guang.ctpp.common.blockentity.IKineticBlockEntityExtension;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KineticBlockEntity.class)
public class KineticBlockEntityMixin implements IKineticBlockEntityExtension {
    @Unique
    private boolean CTNH$inMultiblock;

    @Inject(method = "validateKinetics",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;hasSource()Z", shift = At.Shift.AFTER),
            remap = false,
            cancellable = true)
    public void validateKinetics(CallbackInfo ci) {
        if (CTNH$inMultiblock) {
            ci.cancel();
        }
    }
    @Inject(method = "write",
            at = @At(value = "TAIL"),
            remap = false)
    public void write(CompoundTag compound, boolean clientPacket, CallbackInfo ci) {
        compound.putBoolean("inMultiblock", CTNH$inMultiblock);
    }

    @Inject(method = "read",
            at = @At(value = "TAIL"),
            remap = false)
    public void read(CompoundTag compound, boolean clientPacket, CallbackInfo ci) {
        CTNH$inMultiblock = compound.getBoolean("inMultiblock");
    }

    @Override
    public boolean isCTNHInMultiblock() {
        return CTNH$inMultiblock;
    }

    @Override
    public void setCTNHInMultiblock(boolean inMultiblock) {
        CTNH$inMultiblock = inMultiblock;
    }
}
