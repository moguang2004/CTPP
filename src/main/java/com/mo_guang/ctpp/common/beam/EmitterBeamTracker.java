package com.mo_guang.ctpp.common.beam;

import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.network.packet.DelEmitterBeamPacket;
import com.mo_guang.ctpp.network.packet.SetEmitterBeamPacket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Server-side global beam tracking: beams live independently from their emitter's visibility.
 * Players watching any chunk the beam passes through receive beam data; updates are re-sent on change.
 */
@Mod.EventBusSubscriber(modid = CTPP.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EmitterBeamTracker {

    private static final Map<ResourceKey<Level>, Map<Integer, EmitterBeam>> BEAMS = new HashMap<>();
    private static final Map<ResourceKey<Level>, Map<ServerPlayer, Set<ChunkPos>>> WATCHERS = new HashMap<>();
    private static int nextId = 1;

    private EmitterBeamTracker() {}

    public static int newBeamId() {
        return nextId++;
    }

    /** Insert or update a beam; viewers of covered chunks get the new state. */
    public static void setBeam(ServerLevel level, int id, BlockPos src, Vec3 dir, long voltage, long amps, int tier,
                               double distance) {
        var beam = new EmitterBeam(id, src, dir, voltage, amps, tier, distance);
        BEAMS.computeIfAbsent(level.dimension(), d -> new HashMap<>()).put(id, beam);
        var packet = new SetEmitterBeamPacket(id, level.dimension(), beam);
        for (var player : level.players()) {
            if (watchesAny(player, beam)) {
                GTNetwork.sendToPlayer(player, packet);
            }
        }
    }

    public static void removeBeam(ServerLevel level, int id) {
        var beams = BEAMS.get(level.dimension());
        if (beams == null) return;
        var beam = beams.remove(id);
        if (beam == null) return;
        var packet = new DelEmitterBeamPacket(id, level.dimension());
        for (var player : level.players()) {
            if (watchesAny(player, beam)) {
                GTNetwork.sendToPlayer(player, packet);
            }
        }
        if (beams.isEmpty()) BEAMS.remove(level.dimension());
    }

    private static Set<ChunkPos> coveredChunks(EmitterBeam beam) {
        Set<ChunkPos> chunks = new HashSet<>();
        Vec3 from = beam.origin();
        Vec3 to = beam.end();
        double length = beam.distance();
        int steps = Math.max(1, (int) Math.ceil(length / 8));
        for (int i = 0; i <= steps; i++) {
            Vec3 p = from.lerp(to, i / (double) steps);
            chunks.add(new ChunkPos(BlockPos.containing(p)));
        }
        return chunks;
    }

    private static boolean watchesAny(ServerPlayer player, EmitterBeam beam) {
        var chunks = WATCHERS.get(player.level().dimension());
        if (chunks == null) return false;
        var watched = chunks.get(player);
        if (watched == null) return false;
        for (ChunkPos pos : coveredChunks(beam)) {
            if (watched.contains(pos)) return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onChunkWatch(ChunkWatchEvent.Watch event) {
        ServerLevel level = event.getLevel();
        ServerPlayer player = event.getPlayer();
        WATCHERS.computeIfAbsent(level.dimension(), d -> new HashMap<>())
                .computeIfAbsent(player, p -> new HashSet<>())
                .add(event.getPos());
        var beams = BEAMS.get(level.dimension());
        if (beams == null) return;
        for (var beam : beams.values()) {
            if (coveredChunks(beam).contains(event.getPos())) {
                GTNetwork.sendToPlayer(player,
                        new SetEmitterBeamPacket(beam.id(), level.dimension(), beam));
            }
        }
    }

    @SubscribeEvent
    public static void onChunkUnWatch(ChunkWatchEvent.UnWatch event) {
        ServerLevel level = event.getLevel();
        ServerPlayer player = event.getPlayer();
        var dimWatchers = WATCHERS.get(level.dimension());
        if (dimWatchers == null) return;
        var watched = dimWatchers.get(player);
        if (watched == null) return;
        watched.remove(event.getPos());
        var beams = BEAMS.get(level.dimension());
        if (beams == null) return;
        for (var beam : beams.values()) {
            if (coveredChunks(beam).contains(event.getPos()) && !watchesAny(player, beam)) {
                GTNetwork.sendToPlayer(player,
                        new DelEmitterBeamPacket(beam.id(), level.dimension()));
            }
        }
        if (watched.isEmpty()) dimWatchers.remove(player);
    }
}
