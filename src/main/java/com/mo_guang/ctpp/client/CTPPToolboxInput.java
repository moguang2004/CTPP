package com.mo_guang.ctpp.client;

import net.minecraft.client.Minecraft;

import com.mo_guang.ctpp.common.item.CTPPToolboxItem;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxBlockRegistry;

public final class CTPPToolboxInput {

    private CTPPToolboxInput() {}

    public static boolean shouldReplaceCreateToolboxInput() {
        var player = Minecraft.getInstance().player;
        if (player == null) return false;
        if (!CTPPToolboxClientState.bindings().isEmpty() || CTPPToolboxBlockRegistry.hasNearby(player)) return true;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            if (player.getInventory().getItem(slot).getItem() instanceof CTPPToolboxItem) return true;
        }
        return false;
    }
}
