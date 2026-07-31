package com.mo_guang.ctpp.common.toolbox;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record CTPPToolboxSourceId(Type type, int slot, UUID toolboxId, @Nullable BlockPos blockPos) {

    public enum Type {
        PLAYER_INVENTORY,
        CURIOS,
        BLOCK
    }

    public boolean identifies(CTPPToolboxSourceId other) {
        return toolboxId.equals(other.toolboxId);
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeEnum(type);
        buffer.writeVarInt(slot);
        buffer.writeUUID(toolboxId);
        buffer.writeBoolean(blockPos != null);
        if (blockPos != null) buffer.writeBlockPos(blockPos);
    }

    public static CTPPToolboxSourceId read(FriendlyByteBuf buffer) {
        Type type = buffer.readEnum(Type.class);
        int slot = buffer.readVarInt();
        UUID id = buffer.readUUID();
        BlockPos pos = buffer.readBoolean() ? buffer.readBlockPos() : null;
        return new CTPPToolboxSourceId(type, slot, id, pos);
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Type", type.name());
        tag.putInt("Slot", slot);
        tag.putUUID("Id", toolboxId);
        if (blockPos != null) tag.putLong("Pos", blockPos.asLong());
        return tag;
    }

    public static @Nullable CTPPToolboxSourceId deserialize(CompoundTag tag) {
        if (!tag.hasUUID("Id")) return null;
        try {
            Type type = Type.valueOf(tag.getString("Type"));
            BlockPos pos = tag.contains("Pos") ? BlockPos.of(tag.getLong("Pos")) : null;
            return new CTPPToolboxSourceId(type, tag.getInt("Slot"), tag.getUUID("Id"), pos);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
