package com.mo_guang.ctpp.common.toolbox;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record CTPPToolboxSnapshot(CTPPToolboxSourceId source, ItemStack displayStack, Component displayName,
                                  List<ItemStack> filters, int[] counts) {

    public static CTPPToolboxSnapshot create(CTPPToolboxService.Resolved resolved) {
        List<ItemStack> filters = new ArrayList<>(CTPPToolboxInventory.COMPARTMENTS);
        int[] counts = new int[CTPPToolboxInventory.COMPARTMENTS];
        for (int i = 0; i < CTPPToolboxInventory.COMPARTMENTS; i++) {
            filters.add(resolved.inventory().getFilter(i).copy());
            counts[i] = resolved.inventory().count(i);
        }
        return new CTPPToolboxSnapshot(resolved.source(), resolved.displayStack().copy(), resolved.displayName(),
                List.copyOf(filters), counts);
    }

    public void write(FriendlyByteBuf buffer) {
        source.write(buffer);
        buffer.writeItem(displayStack);
        buffer.writeComponent(displayName);
        filters.forEach(buffer::writeItem);
        for (int count : counts) buffer.writeVarInt(count);
    }

    public static CTPPToolboxSnapshot read(FriendlyByteBuf buffer) {
        CTPPToolboxSourceId source = CTPPToolboxSourceId.read(buffer);
        ItemStack displayStack = buffer.readItem();
        Component displayName = buffer.readComponent();
        List<ItemStack> filters = new ArrayList<>(CTPPToolboxInventory.COMPARTMENTS);
        int[] counts = new int[CTPPToolboxInventory.COMPARTMENTS];
        for (int i = 0; i < CTPPToolboxInventory.COMPARTMENTS; i++) filters.add(buffer.readItem());
        for (int i = 0; i < CTPPToolboxInventory.COMPARTMENTS; i++) counts[i] = buffer.readVarInt();
        return new CTPPToolboxSnapshot(source, displayStack, displayName, List.copyOf(filters), counts);
    }
}
