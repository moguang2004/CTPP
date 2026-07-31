package com.mo_guang.ctpp.common.network.packet;

import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import com.mo_guang.ctpp.common.item.CTPPToolboxItem;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxService;

public final class CTPPToolboxOpenNearestPacket implements GTNetwork.INetPacket {

    public CTPPToolboxOpenNearestPacket() {}

    public CTPPToolboxOpenNearestPacket(FriendlyByteBuf buffer) {}

    @Override
    public void encode(FriendlyByteBuf buffer) {}

    @Override
    public void execute(net.minecraftforge.network.NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player == null) return;
        CTPPToolboxService.findNearest(player).ifPresent(resolved -> {
            CTPPToolboxItem.open(player, resolved.source());
            player.level().playSound(null, player.blockPosition(), SoundEvents.BARREL_OPEN, SoundSource.BLOCKS,
                    0.5f, 1.0f);
        });
    }
}
