package com.mo_guang.ctpp.common.toolbox;

import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import com.mo_guang.ctpp.common.network.packet.CTPPToolboxBindingsPacket;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class CTPPToolboxBindings {

    public static final String KEY = "CTPPToolboxBindings";

    private CTPPToolboxBindings() {}

    public static Map<Integer, CTPPToolboxBinding> get(net.minecraft.world.entity.player.Player player) {
        CompoundTag root = player.getPersistentData().getCompound(KEY);
        Map<Integer, CTPPToolboxBinding> bindings = new LinkedHashMap<>();
        for (int slot = 0; slot < 9; slot++) {
            CTPPToolboxBinding binding = CTPPToolboxBinding.deserialize(root.getCompound(String.valueOf(slot)));
            if (binding != null) bindings.put(slot, binding);
        }
        return bindings;
    }

    public static @Nullable CTPPToolboxBinding get(ServerPlayer player, int slot) {
        return get(player).get(slot);
    }

    public static void put(ServerPlayer player, int slot, @Nullable CTPPToolboxBinding binding) {
        CompoundTag root = player.getPersistentData().getCompound(KEY);
        String key = String.valueOf(slot);
        if (binding == null) root.remove(key);
        else root.put(key, binding.serialize());
        player.getPersistentData().put(KEY, root);
        sync(player);
    }

    public static void copy(ServerPlayer from, ServerPlayer to) {
        to.getPersistentData().put(KEY, from.getPersistentData().getCompound(KEY).copy());
        sync(to);
    }

    public static void sync(ServerPlayer player) {
        GTNetwork.sendToPlayer(player, new CTPPToolboxBindingsPacket(get(player)));
    }
}
