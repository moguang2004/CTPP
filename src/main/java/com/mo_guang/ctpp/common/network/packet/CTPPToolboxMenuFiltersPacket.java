package com.mo_guang.ctpp.common.network.packet;

import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import com.mo_guang.ctpp.common.menu.CTPPToolboxMenu;

import java.util.ArrayList;
import java.util.List;

public final class CTPPToolboxMenuFiltersPacket implements GTNetwork.INetPacket {

    private final int containerId;
    private final List<ItemStack> filters;

    public CTPPToolboxMenuFiltersPacket(int containerId, List<ItemStack> filters) {
        this.containerId = containerId;
        this.filters = filters.stream().map(ItemStack::copy).toList();
    }

    public CTPPToolboxMenuFiltersPacket(FriendlyByteBuf buffer) {
        containerId = buffer.readVarInt();
        List<ItemStack> decoded = new ArrayList<>(8);
        for (int i = 0; i < 8; i++) decoded.add(buffer.readItem());
        filters = List.copyOf(decoded);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(containerId);
        filters.forEach(buffer::writeItem);
    }

    @Override
    public void execute(net.minecraftforge.network.NetworkEvent.Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if (Minecraft.getInstance().player != null &&
                    Minecraft.getInstance().player.containerMenu instanceof CTPPToolboxMenu menu &&
                    menu.containerId == containerId) {
                menu.applyFilters(filters);
            }
        });
    }
}
