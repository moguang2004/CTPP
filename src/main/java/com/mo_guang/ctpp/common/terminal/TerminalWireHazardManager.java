package com.mo_guang.ctpp.common.terminal;

import com.gregtechceu.gtceu.common.data.GTDamageTypes;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.api.terminal.TerminalProperties;
import com.mo_guang.ctpp.api.terminal.TerminalWireGeometry;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Server-only runtime index and electrical hazard state for terminal wires. */
public final class TerminalWireHazardManager {

    private static final int MAX_INDEXED_SECTIONS = 8192;
    private static final Map<ServerLevel, TerminalWireHazardManager> MANAGERS = new IdentityHashMap<>();

    private final ServerLevel level;
    private final Map<WireId, WireEntry> wires = new HashMap<>();
    private final Long2ObjectMap<Set<WireId>> wiresBySection = new Long2ObjectOpenHashMap<>();
    private final Map<WireId, ElectricalState> energized = new HashMap<>();
    private final Set<WireId> unindexedWires = new HashSet<>();

    private TerminalWireHazardManager(ServerLevel level) {
        this.level = level;
    }

    public static TerminalWireHazardManager get(ServerLevel level) {
        return MANAGERS.computeIfAbsent(level, TerminalWireHazardManager::new);
    }

    public static void unload(ServerLevel level) {
        MANAGERS.remove(level);
    }

    public static void tick(ServerLevel level) {
        TerminalWireHazardManager manager = MANAGERS.get(level);
        if (manager != null) manager.tick();
    }

    public void register(BlockPos first, BlockPos second, TerminalProperties.Link link) {
        WireId id = WireId.of(first, second);
        WireEntry existing = wires.get(id);
        if (existing != null && existing.connectionType() == link.connectionType() &&
                existing.amperageLimit() == link.amperageLimit()) {
            return;
        }
        if (existing != null) remove(id);

        List<Vec3> points = TerminalWireGeometry.points(
                TerminalWireGeometry.connectionPoint(id.first()),
                TerminalWireGeometry.connectionPoint(id.second()));
        double radius = TerminalWireGeometry.radius(link.connectionType());
        LongSet sections = collectSections(points, radius);
        WireEntry entry = new WireEntry(id, link.connectionType(), link.amperageLimit(), points,
                TerminalWireGeometry.bounds(points, radius), radius, sections);
        wires.put(id, entry);
        if (sections.isEmpty()) {
            unindexedWires.add(id);
            CTPP.LOGGER.warn("Terminal wire {} -> {} crosses too many sections; using fallback collision lookup",
                    id.first(), id.second());
            return;
        }
        for (long section : sections) {
            Set<WireId> sectionWires = wiresBySection.get(section);
            if (sectionWires == null) {
                sectionWires = new HashSet<>();
                wiresBySection.put(section, sectionWires);
            }
            sectionWires.add(id);
        }
    }

    public void remove(BlockPos first, BlockPos second) {
        remove(WireId.of(first, second));
    }

    private void remove(WireId id) {
        WireEntry removed = wires.remove(id);
        energized.remove(id);
        unindexedWires.remove(id);
        if (removed == null) return;
        for (long section : removed.sections()) {
            Set<WireId> sectionWires = wiresBySection.get(section);
            if (sectionWires == null) continue;
            sectionWires.remove(id);
            if (sectionWires.isEmpty()) wiresBySection.remove(section);
        }
    }

    public void recordTransfer(BlockPos first, BlockPos second, TerminalProperties.Link link,
                               long voltage, long amperage) {
        if (voltage <= 0 || amperage <= 0) return;
        WireId id = WireId.of(first, second);
        WireEntry wire = wires.get(id);
        if (wire == null || wire.connectionType() != link.connectionType() ||
                wire.amperageLimit() != link.amperageLimit()) {
            register(first, second, link);
            wire = wires.get(id);
        }
        WireEntry activeWire = wire;
        if (activeWire == null) return;
        long tick = level.getServer().getTickCount();
        energized.compute(id, (ignored, previous) -> {
            long accepted = Math.min(amperage, activeWire.amperageLimit());
            if (previous == null || previous.serverTick() != tick || voltage > previous.voltage()) {
                return new ElectricalState(tick, voltage, accepted);
            }
            if (voltage < previous.voltage()) return previous;
            long combined = previous.amperage() > Long.MAX_VALUE - accepted ? Long.MAX_VALUE :
                    previous.amperage() + accepted;
            return new ElectricalState(tick, voltage, Math.min(combined, activeWire.amperageLimit()));
        });
    }

    public void tick() {
        long tick = level.getServer().getTickCount();
        energized.entrySet().removeIf(entry -> entry.getValue().serverTick() != tick ||
                !wires.containsKey(entry.getKey()));
        if (energized.isEmpty()) return;

        Map<Integer, LivingEntity> candidates = collectCandidates();
        for (LivingEntity entity : candidates.values()) {
            calculateDamage(entity, tick);
        }
        energized.clear();
    }

    private Map<Integer, LivingEntity> collectCandidates() {
        LongSet energizedSections = new LongOpenHashSet();
        List<WireEntry> fallback = new ArrayList<>();
        for (WireId id : energized.keySet()) {
            WireEntry wire = wires.get(id);
            if (wire == null) continue;
            if (wire.sections().isEmpty()) fallback.add(wire);
            else energizedSections.addAll(wire.sections());
        }

        Map<Integer, LivingEntity> candidates = new LinkedHashMap<>();
        for (long packed : energizedSections) {
            int x = SectionPos.x(packed) << 4;
            int y = SectionPos.y(packed) << 4;
            int z = SectionPos.z(packed) << 4;
            AABB sectionBounds = new AABB(x, y, z, x + 16, y + 16, z + 16);
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, sectionBounds,
                    LivingEntity::isAlive)) {
                candidates.put(entity.getId(), entity);
            }
        }
        for (WireEntry wire : fallback) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof LivingEntity living && living.isAlive() &&
                        wire.bounds().intersects(living.getBoundingBox())) {
                    candidates.put(living.getId(), living);
                }
            }
        }
        return candidates;
    }

    private void calculateDamage(LivingEntity entity, long tick) {
        Set<WireId> nearby = nearbyWires(entity.getBoundingBox().inflate(TerminalWireGeometry.MAX_RADIUS));
        if (!unindexedWires.isEmpty()) nearby.addAll(unindexedWires);

        boolean contactedWire = false;
        boolean contactedEnergizedWire = false;
        long minVoltage = entity.onGround() || entity.isInWater() ? 0 : Long.MAX_VALUE;
        long maxVoltage = Long.MIN_VALUE;
        long maxVoltageAmperage = 0;
        for (WireId id : nearby) {
            WireEntry wire = wires.get(id);
            if (wire == null || !wire.touches(entity.getBoundingBox())) continue;
            contactedWire = true;
            ElectricalState state = energized.get(id);
            boolean active = state != null && state.serverTick() == tick;
            long voltage = active ? state.voltage() : 0;
            long amperage = active ? state.amperage() : 0;
            contactedEnergizedWire |= active;
            minVoltage = Math.min(minVoltage, voltage);
            if (voltage > maxVoltage) {
                maxVoltage = voltage;
                maxVoltageAmperage = amperage;
            } else if (voltage == maxVoltage) {
                maxVoltageAmperage = Math.max(maxVoltageAmperage, amperage);
            }
        }
        if (!contactedWire || !contactedEnergizedWire || minVoltage == Long.MAX_VALUE) return;
        long voltageDifference = maxVoltage - minVoltage;
        if (voltageDifference <= 0 || maxVoltageAmperage <= 0) return;

        int tier = GTUtil.getTierByVoltage(voltageDifference);
        double rawDamage = (double) tier * maxVoltageAmperage * 2.0;
        float damage = (float) Math.min(Float.MAX_VALUE, rawDamage);
        if (!(damage > 0)) return;
        if (entity instanceof ServerPlayer player) {
            TerminalWireDamageDebug.recordExpectedDamage(player, damage);
        }
        entity.hurt(GTDamageTypes.ELECTRIC.source(level), damage);
    }

    private Set<WireId> nearbyWires(AABB bounds) {
        Set<WireId> result = new HashSet<>();
        int minX = SectionPos.blockToSectionCoord(Mth.floor(bounds.minX));
        int minY = SectionPos.blockToSectionCoord(Mth.floor(bounds.minY));
        int minZ = SectionPos.blockToSectionCoord(Mth.floor(bounds.minZ));
        int maxX = SectionPos.blockToSectionCoord(Mth.floor(bounds.maxX));
        int maxY = SectionPos.blockToSectionCoord(Mth.floor(bounds.maxY));
        int maxZ = SectionPos.blockToSectionCoord(Mth.floor(bounds.maxZ));
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Set<WireId> section = wiresBySection.get(SectionPos.asLong(x, y, z));
                    if (section != null) result.addAll(section);
                }
            }
        }
        return result;
    }

    private static LongSet collectSections(List<Vec3> points, double radius) {
        LongSet result = new LongOpenHashSet();
        for (int i = 0; i < points.size() - 1; i++) {
            AABB bounds = new AABB(points.get(i), points.get(i + 1)).inflate(radius);
            int minX = SectionPos.blockToSectionCoord(Mth.floor(bounds.minX));
            int minY = SectionPos.blockToSectionCoord(Mth.floor(bounds.minY));
            int minZ = SectionPos.blockToSectionCoord(Mth.floor(bounds.minZ));
            int maxX = SectionPos.blockToSectionCoord(Mth.floor(bounds.maxX));
            int maxY = SectionPos.blockToSectionCoord(Mth.floor(bounds.maxY));
            int maxZ = SectionPos.blockToSectionCoord(Mth.floor(bounds.maxZ));
            long sectionCount = (long) (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
            if (sectionCount > MAX_INDEXED_SECTIONS || result.size() + sectionCount > MAX_INDEXED_SECTIONS) {
                return new LongOpenHashSet();
            }
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        result.add(SectionPos.asLong(x, y, z));
                    }
                }
            }
        }
        return result;
    }

    private record WireId(BlockPos first, BlockPos second) {

        private static WireId of(BlockPos first, BlockPos second) {
            return first.compareTo(second) <= 0 ? new WireId(first.immutable(), second.immutable()) :
                    new WireId(second.immutable(), first.immutable());
        }
    }

    private record WireEntry(WireId id, TerminalProperties.ConnectionType connectionType, long amperageLimit,
                             List<Vec3> points, AABB bounds, double radius, LongSet sections) {

        private boolean touches(AABB entityBounds) {
            if (!bounds.intersects(entityBounds)) return false;
            AABB expanded = entityBounds.inflate(radius);
            for (int i = 0; i < points.size() - 1; i++) {
                Vec3 first = points.get(i);
                Vec3 second = points.get(i + 1);
                if (expanded.contains(first) || expanded.contains(second) || expanded.clip(first, second).isPresent()) {
                    return true;
                }
            }
            return false;
        }
    }

    private record ElectricalState(long serverTick, long voltage, long amperage) {}
}
