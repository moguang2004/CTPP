package com.mo_guang.ctpp.network.packet;

import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

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
        ItemStack stack = switch (tier) {
            case 1 -> GTItems.EMITTER_LV.asStack();
            case 2 -> GTItems.EMITTER_MV.asStack();
            case 3 -> GTItems.EMITTER_HV.asStack();
            case 4 -> GTItems.EMITTER_EV.asStack();
            case 5 -> GTItems.EMITTER_IV.asStack();
            case 6 -> GTItems.EMITTER_LuV.asStack();
            case 7 -> GTItems.EMITTER_ZPM.asStack();
            case 8 -> GTItems.EMITTER_UV.asStack();
            default -> ItemStack.EMPTY;
        };
        if (!stack.isEmpty()) {
            player.getInventory().setItem(player.getInventory().selected, stack);
        }
    }
}
