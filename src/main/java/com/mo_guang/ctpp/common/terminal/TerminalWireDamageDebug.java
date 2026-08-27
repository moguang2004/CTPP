package com.mo_guang.ctpp.common.terminal;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import com.mo_guang.ctpp.CTPP;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

/** Per-player aggregation of theoretical terminal-wire damage for debugging. */
public final class TerminalWireDamageDebug {

    private static final Map<UUID, State> STATES = new HashMap<>();

    private TerminalWireDamageDebug() {}

    public static void enable(ServerPlayer player) {
        int nextLogTick = player.getServer().getTickCount() + 20;
        STATES.put(player.getUUID(), new State(nextLogTick));
    }

    public static boolean disable(ServerPlayer player) {
        return STATES.remove(player.getUUID()) != null;
    }

    public static void remove(UUID playerId) {
        STATES.remove(playerId);
    }

    public static void recordExpectedDamage(ServerPlayer player, float damage) {
        State state = STATES.get(player.getUUID());
        if (state != null && damage > 0 && Float.isFinite(damage)) {
            state.damageCounts.merge(damage, 1, Integer::sum);
        }
    }

    public static void tick(MinecraftServer server) {
        int currentTick = server.getTickCount();
        STATES.entrySet().removeIf(entry -> {
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player == null) return true;
            State state = entry.getValue();
            if (currentTick < state.nextLogTick) return false;

            String summary = state.damageCounts.isEmpty() ? "无细线伤害" : state.damageCounts.entrySet().stream()
                    .map(count -> formatDamage(count.getKey()) + "点伤害 x " + count.getValue() + "次")
                    .collect(Collectors.joining("，"));
            CTPP.LOGGER.info("[Wire Damage Debug] {}: {}", player.getGameProfile().getName(), summary);
            state.damageCounts.clear();
            state.nextLogTick = currentTick + 20;
            return false;
        });
    }

    private static String formatDamage(float damage) {
        if (damage == Math.rint(damage)) return Long.toString((long) damage);
        String formatted = String.format(Locale.ROOT, "%.2f", damage);
        return formatted.replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private static final class State {

        private int nextLogTick;
        private final Map<Float, Integer> damageCounts = new TreeMap<>(Comparator.reverseOrder());

        private State(int nextLogTick) {
            this.nextLogTick = nextLogTick;
        }
    }
}
