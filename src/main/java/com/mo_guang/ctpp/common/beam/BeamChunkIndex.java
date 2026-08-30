package com.mo_guang.ctpp.common.beam;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.mo_guang.ctpp.CTPP;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Index of currently loaded chunks per dimension, kept for beam path tracing. Collision along a
 * beam must never touch unloaded chunks: {@code getBlockState}/{@code clip} on a server would
 * synchronously load (or even generate) them. This index is fed by Forge chunk events; every
 * candidate is re-verified with {@link Level#hasChunk(int, int)} at use time, so a missed event
 * can never cause an accidental chunk load.
 * <p>
 * {@link #loadedRuns} returns the beam's intersection with the loaded area as ordered distance
 * intervals, choosing the cheaper of two strategies: walk the ray's chunk columns (short beams)
 * or iterate the loaded-chunk set (long beams — cost stays independent of beam length).
 */
@Mod.EventBusSubscriber(modid = CTPP.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class BeamChunkIndex {

    private static final Map<ResourceKey<Level>, LongOpenHashSet> LOADED = new HashMap<>();

    private BeamChunkIndex() {}

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level && event.getChunk() instanceof LevelChunk chunk) {
            LOADED.computeIfAbsent(level.dimension(), d -> new LongOpenHashSet())
                    .add(chunk.getPos().toLong());
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level && event.getChunk() instanceof LevelChunk chunk) {
            var loaded = LOADED.get(level.dimension());
            if (loaded != null) loaded.remove(chunk.getPos().toLong());
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        LOADED.clear();
    }

    /**
     * Intervals {@code [tEnter, tExit]} along the ray {@code from + dir * t, t in [0, maxDist]}
     * that lie in loaded chunks, ordered by tEnter and merged where adjacent. Loadedness is
     * column-based (a loaded chunk spans the full world height), so all math is 2D in XZ.
     */
    public static List<double[]> loadedRuns(ServerLevel level, Vec3 from, Vec3 dir, double maxDist) {
        var loaded = LOADED.get(level.dimension());
        int loadedCount = loaded == null ? 0 : loaded.size();
        // whichever set is smaller wins: columns the ray crosses vs all loaded chunks
        int rayColumns = (int) (maxDist / 12) + 2;
        if (rayColumns < loadedCount) return walkRuns(level, from, dir, maxDist);
        if (loaded == null || loaded.isEmpty()) return List.of();
        return scanRuns(level, loaded, from, dir, maxDist);
    }

    /** Loaded chunk columns a beam polyline passes through (for watcher matching). */
    public static Set<ChunkPos> coveredLoadedChunks(ServerLevel level, EmitterBeam beam) {
        Set<ChunkPos> result = new HashSet<>();
        var points = beam.points();
        for (int i = 0; i + 1 < points.size(); i++) {
            Vec3 a = points.get(i);
            Vec3 delta = points.get(i + 1).subtract(a);
            double len = delta.length();
            if (len < 1.0E-9) continue;
            Vec3 dir = delta.normalize();
            for (double[] run : loadedRuns(level, a, dir, len)) {
                // sample densely along the run; granularity only affects corner-grazing chunks
                for (double t = run[0]; t <= run[1] + 1.0E-6; t += 2) {
                    Vec3 p = a.add(dir.scale(Math.min(t, run[1])));
                    result.add(new ChunkPos(Mth.floor(p.x) >> 4, Mth.floor(p.z) >> 4));
                }
            }
        }
        return result;
    }

    /** O(segments) test: does the beam polyline pass through this chunk column? */
    public static boolean chunkCovered(EmitterBeam beam, ChunkPos pos) {
        var points = beam.points();
        for (int i = 0; i + 1 < points.size(); i++) {
            Vec3 a = points.get(i);
            Vec3 delta = points.get(i + 1).subtract(a);
            double len = delta.length();
            if (len < 1.0E-9) continue;
            double[] t = rayColumn(a, delta.normalize(), pos.x, pos.z);
            if (t != null && t[1] > 0 && t[0] < len) return true;
        }
        return false;
    }

    /** DDA over the ray's chunk columns, checking loadedness per column. O(columns crossed). */
    private static List<double[]> walkRuns(ServerLevel level, Vec3 from, Vec3 dir, double maxDist) {
        List<double[]> runs = new ArrayList<>();
        double px = from.x, pz = from.z;
        double dx = dir.x, dz = dir.z;
        int cx = Mth.floor(px) >> 4, cz = Mth.floor(pz) >> 4;
        int stepX = dx > 0 ? 1 : (dx < 0 ? -1 : 0);
        int stepZ = dz > 0 ? 1 : (dz < 0 ? -1 : 0);
        double tMaxX = dx != 0 ? ((dx > 0 ? ((cx + 1) << 4) - px : (cx << 4) - px)) / dx : Double.POSITIVE_INFINITY;
        double tMaxZ = dz != 0 ? ((dz > 0 ? ((cz + 1) << 4) - pz : (cz << 4) - pz)) / dz : Double.POSITIVE_INFINITY;
        double tDeltaX = dx != 0 ? Math.abs(16.0 / dx) : Double.POSITIVE_INFINITY;
        double tDeltaZ = dz != 0 ? Math.abs(16.0 / dz) : Double.POSITIVE_INFINITY;
        double t = 0, runStart = -1;
        while (t < maxDist) {
            if (level.hasChunk(cx, cz)) {
                if (runStart < 0) runStart = t;
            } else if (runStart >= 0) {
                runs.add(new double[] { runStart, t });
                runStart = -1;
            }
            double tNext = Math.min(Math.min(tMaxX, tMaxZ), maxDist);
            t = tNext;
            if (tMaxX < tMaxZ) {
                tMaxX += tDeltaX;
                cx += stepX;
            } else {
                tMaxZ += tDeltaZ;
                cz += stepZ;
            }
        }
        if (runStart >= 0) runs.add(new double[] { runStart, maxDist });
        return runs;
    }

    /** Intersect the ray with every loaded chunk column. O(loaded chunk count), beam-length independent. */
    private static List<double[]> scanRuns(ServerLevel level, LongOpenHashSet loaded, Vec3 from, Vec3 dir,
                                           double maxDist) {
        List<double[]> intervals = new ArrayList<>();
        for (long key : loaded) {
            int cx = ChunkPos.getX(key), cz = ChunkPos.getZ(key);
            if (!level.hasChunk(cx, cz)) continue; // guard against stale index entries
            double[] t = rayColumn(from, dir, cx, cz);
            if (t == null || t[1] <= 0 || t[0] >= maxDist) continue;
            intervals.add(new double[] { Math.max(0, t[0]), Math.min(maxDist, t[1]) });
        }
        intervals.sort(Comparator.comparingDouble(a -> a[0]));
        List<double[]> runs = new ArrayList<>(intervals.size());
        for (double[] iv : intervals) {
            if (!runs.isEmpty() && iv[0] <= runs.get(runs.size() - 1)[1] + 1.0E-6) {
                runs.get(runs.size() - 1)[1] = Math.max(runs.get(runs.size() - 1)[1], iv[1]);
            } else {
                runs.add(iv);
            }
        }
        return runs;
    }

    /** 2D slab intersection of the ray with a chunk column's XZ rectangle; null when no hit. */
    private static double[] rayColumn(Vec3 from, Vec3 dir, int cx, int cz) {
        double x0 = cx << 4, x1 = x0 + 16, z0 = cz << 4, z1 = z0 + 16;
        double tEnter = Double.NEGATIVE_INFINITY, tExit = Double.POSITIVE_INFINITY;
        if (dir.x == 0) {
            if (from.x < x0 || from.x > x1) return null;
        } else {
            double ta = (x0 - from.x) / dir.x, tb = (x1 - from.x) / dir.x;
            tEnter = Math.max(tEnter, Math.min(ta, tb));
            tExit = Math.min(tExit, Math.max(ta, tb));
        }
        if (dir.z == 0) {
            if (from.z < z0 || from.z > z1) return null;
        } else {
            double ta = (z0 - from.z) / dir.z, tb = (z1 - from.z) / dir.z;
            tEnter = Math.max(tEnter, Math.min(ta, tb));
            tExit = Math.min(tExit, Math.max(ta, tb));
        }
        return tEnter <= tExit ? new double[] { tEnter, tExit } : null;
    }
}
