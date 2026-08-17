package com.mo_guang.ctpp.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import com.mo_guang.ctpp.common.blockentity.KineticMachineBlockEntity;
import com.mo_guang.ctpp.common.machine.IKineticMachine;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author KilaBash
 * @date 2023/4/1
 * @implNote RotationPropagatorMixin
 */
@Mixin(RotationPropagator.class)
public abstract class RotationPropagatorMixin {

    /**
     * 重载/未成型窗口期的网络重建竞态会让 Create 原版的动能冲突保护
     * （方向相反、同网络速度跳变等）直接 destroyBlock 掉应力输入/输出箱。
     * 这些机器箱的动能状态由 GT 配方驱动，不应被该保护误杀。
     */
    @Redirect(method = "propagateNewSource",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/world/level/Level;destroyBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private static boolean ctpp$protectKineticMachines(Level level, BlockPos pos, boolean dropBlock) {
        if (level.getBlockEntity(pos) instanceof KineticMachineBlockEntity) {
            return false;
        }
        return level.destroyBlock(pos, dropBlock);
    }

    @Inject(method = "getAxisModifier", at = @At(value = "RETURN"), remap = false, cancellable = true)
    private static void injectAxisModifier(KineticBlockEntity te, Direction direction,
                                           CallbackInfoReturnable<Float> cir) {
        if ((te.hasSource() || te.isSource()) && te instanceof KineticMachineBlockEntity kineticMachineBlockEntity) {
            if (kineticMachineBlockEntity.getMetaMachine() instanceof IKineticMachine kineticMachine) {
                cir.setReturnValue(kineticMachine.getRotationSpeedModifier(direction));
            }
        }
    }
}
