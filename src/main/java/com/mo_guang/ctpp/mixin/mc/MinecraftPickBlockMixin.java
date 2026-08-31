package com.mo_guang.ctpp.mixin.mc;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import com.mo_guang.ctpp.common.machine.simple.PlaceableEmitterMachine;
import com.mo_guang.ctpp.network.packet.PickEmitterPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Middle-click (pick block) on a placed emitter gives the vanilla GT emitter item, not the machine block. */
@Mixin(Minecraft.class)
public class MinecraftPickBlockMixin {

    @Inject(method = "pickBlock", at = @At("HEAD"), cancellable = true)
    private void ctnhcore$pickEmitter(CallbackInfo ci) {
        Minecraft mc = (Minecraft) (Object) this;
        if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.BLOCK || mc.level == null ||
                mc.player == null)
            return;
        var pos = ((BlockHitResult) mc.hitResult).getBlockPos();
        if (MetaMachine.getMachine(mc.level, pos) instanceof PlaceableEmitterMachine machine) {
            ci.cancel();
            if (mc.player.getAbilities().instabuild) {
                GTNetwork.sendToServer(new PickEmitterPacket(machine.getTier()));
            }
        }
    }
}
