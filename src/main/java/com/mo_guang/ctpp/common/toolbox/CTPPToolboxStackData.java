package com.mo_guang.ctpp.common.toolbox;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class CTPPToolboxStackData {

    public static final String ID = "CTPPToolboxId";
    public static final String TOOL_TYPES = "tool_types";
    public static final String LAST_USED_TOOL = "last_used_tool";

    private CTPPToolboxStackData() {}

    public static @Nullable UUID getId(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.hasUUID(ID) ? tag.getUUID(ID) : null;
    }

    public static void update(ItemStack stack, CTPPToolboxSavedData.Record record) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.hasUUID(ID) || !record.id().equals(tag.getUUID(ID))) tag.putUUID(ID, record.id());
        if (record.toolTypes().isEmpty()) {
            if (tag.contains(TOOL_TYPES)) tag.remove(TOOL_TYPES);
        } else if (!record.toolTypes().equals(tag.getString(TOOL_TYPES))) {
            tag.putString(TOOL_TYPES, record.toolTypes());
        }
        if (tag.getInt("ToolboxColor") != record.color().getId()) tag.putInt("ToolboxColor", record.color().getId());
    }

    public static boolean containsTool(ItemStack stack, String toolType) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return false;
        String summary = tag.getString(TOOL_TYPES);
        int from = 0;
        while (true) {
            int index = summary.indexOf(toolType, from);
            if (index < 0) return false;
            int end = index + toolType.length();
            if (index > 0 && end < summary.length() && summary.charAt(index - 1) == ' ' &&
                    summary.charAt(end) == ' ')
                return true;
            from = index + 1;
        }
    }
}
