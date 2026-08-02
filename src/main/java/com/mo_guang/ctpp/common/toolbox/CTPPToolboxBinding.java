package com.mo_guang.ctpp.common.toolbox;

import net.minecraft.nbt.CompoundTag;

import org.jetbrains.annotations.Nullable;

public record CTPPToolboxBinding(CTPPToolboxSourceId source, int compartment) {

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.put("Source", source.serialize());
        tag.putInt("Compartment", compartment);
        return tag;
    }

    public static @Nullable CTPPToolboxBinding deserialize(CompoundTag tag) {
        CTPPToolboxSourceId source = CTPPToolboxSourceId.deserialize(tag.getCompound("Source"));
        int compartment = tag.getInt("Compartment");
        return source == null || compartment < 0 || compartment >= CTPPToolboxInventory.COMPARTMENTS ? null :
                new CTPPToolboxBinding(source, compartment);
    }
}
