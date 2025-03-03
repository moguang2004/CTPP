package com.mo_guang.ctpp.mixin.fix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(IRotorHolderMachineMixin.class)
public interface IRotorHolderMachineMixin {
    @Shadow
    int getTierDifference();
    /**
     * @author mo_guang
     * @reason fix
     */
    @Overwrite(remap = false)
    default int getHolderEfficiency() {
        int tierDifference = this.getTierDifference();
        return tierDifference == -20 ? -1 : 100 + 10 * tierDifference;
    }
}
