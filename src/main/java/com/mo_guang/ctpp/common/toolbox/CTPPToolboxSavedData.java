package com.mo_guang.ctpp.common.toolbox;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.saveddata.SavedData;

import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class CTPPToolboxSavedData extends SavedData {

    private static final String DATA_NAME = "ctpp_toolboxes";
    private final Map<UUID, Record> records = new LinkedHashMap<>();

    public CTPPToolboxSavedData() {}

    public CTPPToolboxSavedData(CompoundTag tag) {
        for (Tag value : tag.getList("Toolboxes", Tag.TAG_COMPOUND)) {
            Record record = new Record((CompoundTag) value, this::setDirty);
            records.put(record.id(), record);
        }
    }

    public static CTPPToolboxSavedData get(ServerLevel level) {
        return get(level.getServer());
    }

    public static CTPPToolboxSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
                CTPPToolboxSavedData::new, CTPPToolboxSavedData::new, DATA_NAME);
    }

    public Record create(DyeColor color) {
        UUID id;
        do {
            id = UUID.randomUUID();
        } while (records.containsKey(id));
        Record record = new Record(id, color, this::setDirty);
        records.put(id, record);
        setDirty();
        return record;
    }

    public @Nullable Record find(UUID id) {
        return records.get(id);
    }

    public Record getOrCreate(@Nullable UUID id, DyeColor color) {
        Record existing = id == null ? null : records.get(id);
        return existing != null ? existing : create(color);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        records.values().forEach(record -> list.add(record.serialize()));
        tag.put("Toolboxes", list);
        return tag;
    }

    public static final class Record {

        private final UUID id;
        private final CTPPToolboxInventory inventory;
        private DyeColor color;
        private String toolTypes;
        private final Runnable changed;

        private Record(UUID id, DyeColor color, Runnable changed) {
            this.id = id;
            this.color = color;
            this.changed = changed;
            this.inventory = new CTPPToolboxInventory();
            this.toolTypes = "";
            inventory.setChanged(this::inventoryChanged);
        }

        private Record(CompoundTag tag, Runnable changed) {
            this(tag.getUUID("Id"), DyeColor.byId(tag.getInt("Color")), changed);
            inventory.deserializeNBT(tag.getCompound("Inventory"));
            toolTypes = inventory.toolTypeSummary();
        }

        public UUID id() {
            return id;
        }

        public CTPPToolboxInventory inventory() {
            return inventory;
        }

        public DyeColor color() {
            return color;
        }

        public void setColor(DyeColor color) {
            if (this.color == color) return;
            this.color = color;
            changed.run();
        }

        public String toolTypes() {
            return toolTypes;
        }

        private void inventoryChanged() {
            toolTypes = inventory.toolTypeSummary();
            changed.run();
        }

        private CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("Id", id);
            tag.putInt("Color", color.getId());
            tag.put("Inventory", inventory.serializeNBT());
            return tag;
        }
    }
}
