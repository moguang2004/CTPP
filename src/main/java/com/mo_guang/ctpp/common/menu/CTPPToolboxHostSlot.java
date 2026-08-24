package com.mo_guang.ctpp.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class CTPPToolboxHostSlot extends Slot {

    public CTPPToolboxHostSlot(Inventory inventory, int slot, int x, int y) {
        super(inventory, slot, x, y);
    }

    @Override
    public boolean mayPickup(net.minecraft.world.entity.player.Player player) {
        return false;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }
}
