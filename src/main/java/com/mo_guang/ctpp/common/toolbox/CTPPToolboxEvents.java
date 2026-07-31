package com.mo_guang.ctpp.common.toolbox;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class CTPPToolboxEvents {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide ||
                !(event.player instanceof ServerPlayer player) || player.tickCount % 5 != 0)
            return;
        CTPPToolboxBindings.get(player).forEach((slot, binding) -> {
            if (binding.source().type() == CTPPToolboxSourceId.Type.BLOCK &&
                    (binding.source().blockPos() == null ||
                            binding.source().blockPos().distSqr(player.blockPosition()) > 64 * 64)) {
                CTPPToolboxOperations.unequip(player, slot, true);
            } else {
                CTPPToolboxOperations.refill(player, slot, binding);
            }
        });
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) CTPPToolboxBindings.sync(player);
    }

    @SubscribeEvent
    public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) CTPPToolboxBindings.sync(player);
    }

    @SubscribeEvent
    public void onClone(PlayerEvent.Clone event) {
        if (event.getOriginal() instanceof ServerPlayer original && event.getEntity() instanceof ServerPlayer player) {
            CTPPToolboxBindings.copy(original, player);
        }
    }
}
