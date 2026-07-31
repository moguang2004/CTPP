package com.mo_guang.ctpp.common.toolbox;

import net.createmod.catnip.data.WorldAttached;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import com.mo_guang.ctpp.common.blockentity.CTPPToolboxBlockEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CTPPToolboxBlockRegistry {

    private static final WorldAttached<Map<BlockPos, CTPPToolboxBlockEntity>> TOOLBOXES = new WorldAttached<>(
            level -> new HashMap<>());

    private CTPPToolboxBlockRegistry() {}

    public static void add(CTPPToolboxBlockEntity toolbox) {
        if (toolbox.getLevel() != null) TOOLBOXES.get(toolbox.getLevel()).put(toolbox.getBlockPos(), toolbox);
    }

    public static void remove(CTPPToolboxBlockEntity toolbox) {
        if (toolbox.getLevel() != null) TOOLBOXES.get(toolbox.getLevel()).remove(toolbox.getBlockPos());
    }

    public static List<CTPPToolboxBlockEntity> nearby(ServerPlayer player) {
        return TOOLBOXES.get(player.level()).values().stream()
                .filter(toolbox -> !toolbox.isRemoved())
                .filter(toolbox -> toolbox.getBlockPos().distSqr(player.blockPosition()) <= 64 * 64)
                .sorted(java.util.Comparator.comparingDouble(
                        toolbox -> toolbox.getBlockPos().distSqr(player.blockPosition())))
                .toList();
    }

    public static boolean hasNearby(Player player) {
        return TOOLBOXES.get(player.level()).values().stream()
                .anyMatch(toolbox -> !toolbox.isRemoved() &&
                        toolbox.getBlockPos().distSqr(player.blockPosition()) <= 64 * 64);
    }
}
