package com.mo_guang.ctpp.network.packet;

import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import com.mo_guang.ctpp.common.machine.simple.PlaceableEmitterMachine;

/** C2S: creative middle-click on a placed emitter; give the vanilla GT emitter item of that tier. */
public class PickEmitterPacket implements GTNetwork.INetPacket {

    private final int tier;

    public PickEmitterPacket(int tier) {
        this.tier = tier;
    }

    public PickEmitterPacket(FriendlyByteBuf buffer) {
        tier = buffer.readVarInt();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(tier);
    }

    @Override
    public void execute(net.minecraftforge.network.NetworkEvent.Context context) {
        var player = context.getSender();
        if (player == null || !player.getAbilities().instabuild) return;
        ItemStack stack = PlaceableEmitterMachine.emitterItem(tier);
        if (!stack.isEmpty()) {
            player.getInventory().setItem(player.getInventory().selected, stack);
        }
    }
}
