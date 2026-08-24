package com.mo_guang.ctpp.common.toolbox;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import com.simibubi.create.content.equipment.toolbox.ItemReturnInvWrapper;

public final class CTPPToolboxOperations {

    private CTPPToolboxOperations() {}

    public static boolean equip(ServerPlayer player, CTPPToolboxSourceId sourceId, int compartment, int hotbarSlot) {
        if (hotbarSlot < 0 || hotbarSlot >= 9 || compartment < 0 ||
                compartment >= CTPPToolboxInventory.COMPARTMENTS)
            return false;
        CTPPToolboxService.Resolved resolved = CTPPToolboxService.resolve(player, sourceId);
        if (resolved == null || resolved.inventory().take(1, compartment, true).isEmpty()) return false;
        if (resolved.source().type() == CTPPToolboxSourceId.Type.PLAYER_INVENTORY &&
                resolved.source().slot() == hotbarSlot)
            return false;

        unequip(player, hotbarSlot, false);
        resolved = CTPPToolboxService.resolve(player, resolved.source());
        if (resolved == null) return false;
        ItemStack filter = resolved.inventory().getFilter(compartment);
        ItemStack current = player.getInventory().getItem(hotbarSlot);
        if (!current.isEmpty() && !CTPPToolboxInventory.canShareCompartment(current, filter)) {
            ItemStack remainder = returnToInventory(player, current.copy());
            player.getInventory().setItem(hotbarSlot, remainder);
            current = remainder;
        }
        if (!current.isEmpty() && !CTPPToolboxInventory.canShareCompartment(current, filter)) return false;

        int target = Math.max(1, (filter.getMaxStackSize() + 1) / 2);
        ItemStack extracted = resolved.inventory().take(target - current.getCount(), compartment, false);
        if (!extracted.isEmpty()) {
            if (current.isEmpty()) player.getInventory().setItem(hotbarSlot, extracted);
            else current.grow(extracted.getCount());
        }
        resolved.syncProjection();
        CTPPToolboxBindings.put(player, hotbarSlot, new CTPPToolboxBinding(resolved.source(), compartment));
        return true;
    }

    public static void unequip(ServerPlayer player, int hotbarSlot, boolean keepItem) {
        if (hotbarSlot < 0 || hotbarSlot >= 9) return;
        CTPPToolboxBinding binding = CTPPToolboxBindings.get(player, hotbarSlot);
        if (binding == null) return;
        CTPPToolboxService.Resolved resolved = CTPPToolboxService.resolve(player, binding.source());
        ItemStack current = player.getInventory().getItem(hotbarSlot);
        if (!keepItem && resolved != null && !current.isEmpty()) {
            ItemStack remainder = resolved.inventory().distribute(current.copy(), binding.compartment(), false);
            if (remainder.getCount() != current.getCount()) player.getInventory().setItem(hotbarSlot, remainder);
            resolved.syncProjection();
        }
        CTPPToolboxBindings.put(player, hotbarSlot, null);
    }

    public static void refill(ServerPlayer player, int hotbarSlot, CTPPToolboxBinding binding) {
        CTPPToolboxService.Resolved resolved = CTPPToolboxService.resolve(player, binding.source());
        if (resolved == null) {
            CTPPToolboxBindings.put(player, hotbarSlot, null);
            return;
        }
        ItemStack filter = resolved.inventory().getFilter(binding.compartment());
        ItemStack current = player.getInventory().getItem(hotbarSlot);
        if (filter.isEmpty() || !current.isEmpty() && !CTPPToolboxInventory.canShareCompartment(current, filter)) {
            CTPPToolboxBindings.put(player, hotbarSlot, null);
            return;
        }
        int target = Math.max(1, (filter.getMaxStackSize() + 1) / 2);
        if (current.getCount() >= target) return;
        ItemStack extracted = resolved.inventory().take(target - current.getCount(), binding.compartment(), false);
        if (extracted.isEmpty()) return;
        if (current.isEmpty()) player.getInventory().setItem(hotbarSlot, extracted);
        else current.grow(extracted.getCount());
        resolved.syncProjection();
    }

    public static void depositAll(ServerPlayer player, CTPPToolboxSourceId sourceId) {
        if (sourceId == null) {
            java.util.List<CTPPToolboxService.Resolved> sources = CTPPToolboxService.collect(player);
            for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
                if (CTPPToolboxBindings.get(player, hotbarSlot) != null) unequip(player, hotbarSlot, true);
            }
            for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
                ItemStack stack = player.getInventory().getItem(slot);
                if (stack.isEmpty() || stack.getItem() instanceof com.mo_guang.ctpp.common.item.CTPPToolboxItem)
                    continue;
                ItemStack remainder = stack.copy();
                for (CTPPToolboxService.Resolved resolved : sources) {
                    for (int compartment = 0; compartment < CTPPToolboxInventory.COMPARTMENTS &&
                            !remainder.isEmpty(); compartment++) {
                        remainder = resolved.inventory().distribute(remainder, compartment, false);
                    }
                    resolved.syncProjection();
                }
                if (remainder.getCount() != stack.getCount()) player.getInventory().setItem(slot, remainder);
            }
            return;
        }
        CTPPToolboxService.Resolved resolved = CTPPToolboxService.resolve(player, sourceId);
        if (resolved == null) return;
        depositInto(player, resolved);
    }

    public static void detachSource(ServerLevel level, java.util.UUID toolboxId) {
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            if (player.level() != level) continue;
            for (int slot = 0; slot < 9; slot++) {
                CTPPToolboxBinding binding = CTPPToolboxBindings.get(player, slot);
                if (binding != null && binding.source().toolboxId().equals(toolboxId)) {
                    unequip(player, slot, false);
                }
            }
        }
    }

    private static void depositInto(ServerPlayer player, CTPPToolboxService.Resolved initial) {
        CTPPToolboxService.Resolved resolved = initial;
        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            if (CTPPToolboxBindings.get(player, hotbarSlot) != null) unequip(player, hotbarSlot, true);
        }
        resolved = CTPPToolboxService.resolve(player, resolved.source());
        if (resolved == null) return;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.isEmpty() || stack.getItem() instanceof com.mo_guang.ctpp.common.item.CTPPToolboxItem) continue;
            ItemStack remainder = stack.copy();
            for (int compartment = 0; compartment < CTPPToolboxInventory.COMPARTMENTS &&
                    !remainder.isEmpty(); compartment++) {
                remainder = resolved.inventory().distribute(remainder, compartment, false);
            }
            if (remainder.getCount() != stack.getCount()) player.getInventory().setItem(slot, remainder);
        }
        resolved.syncProjection();
    }

    private static ItemStack returnToInventory(ServerPlayer player, ItemStack stack) {
        return ItemHandlerHelper.insertItemStacked(new ItemReturnInvWrapper(player.getInventory()), stack, false);
    }
}
