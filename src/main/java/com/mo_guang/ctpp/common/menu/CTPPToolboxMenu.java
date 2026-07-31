package com.mo_guang.ctpp.common.menu;

import com.gregtechceu.gtceu.common.network.GTNetwork;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import com.mo_guang.ctpp.common.blockentity.CTPPToolboxBlockEntity;
import com.mo_guang.ctpp.common.network.packet.CTPPToolboxMenuFiltersPacket;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxBindings;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxInventory;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxOperations;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxService;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxSourceId;
import com.mo_guang.ctpp.registry.CTPPMenus;

import java.util.ArrayList;
import java.util.List;

public class CTPPToolboxMenu extends AbstractContainerMenu {

    private final Inventory playerInventory;
    private final CTPPToolboxInventory toolbox;
    private final CTPPToolboxService.Resolved resolved;
    private final CTPPToolboxSourceId source;
    private final ItemStack displayStack;
    private List<ItemStack> lastFilters = List.of();
    public boolean renderPass;

    public CTPPToolboxMenu(MenuType<CTPPToolboxMenu> type, int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(type, id);
        playerInventory = inventory;
        source = CTPPToolboxSourceId.read(buffer);
        displayStack = buffer.readItem();
        resolved = null;
        toolbox = new CTPPToolboxInventory();
        addSlots();
    }

    private CTPPToolboxMenu(MenuType<CTPPToolboxMenu> type, int id, Inventory inventory,
                            CTPPToolboxService.Resolved resolved) {
        super(type, id);
        playerInventory = inventory;
        this.resolved = resolved;
        source = resolved.source();
        displayStack = resolved.displayStack();
        toolbox = resolved.inventory();
        addSlots();
    }

    public static CTPPToolboxMenu create(int id, Inventory inventory, CTPPToolboxService.Resolved resolved) {
        return new CTPPToolboxMenu(CTPPMenus.TOOLBOX.get(), id, inventory, resolved);
    }

    private void addSlots() {
        int x = 79;
        int y = 37;
        int[] xOffsets = { x, x + 33, x + 66, x + 72, x + 66, x + 33, x, x - 6 };
        int[] yOffsets = { y, y - 6, y, y + 33, y + 66, y + 72, y + 66, y + 33 };
        for (int compartment = 0; compartment < CTPPToolboxInventory.COMPARTMENTS; compartment++) {
            int base = compartment * CTPPToolboxInventory.STACKS_PER_COMPARTMENT;
            addSlot(new CTPPToolboxSlot(this, toolbox, base, xOffsets[compartment], yOffsets[compartment]));
            for (int slot = 1; slot < CTPPToolboxInventory.STACKS_PER_COMPARTMENT; slot++) {
                addSlot(new SlotItemHandler(toolbox, base + slot, -10000, -10000));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                int slot = column + row * 9 + 9;
                addSlot(playerSlot(slot, 8 + column * 18, 165 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(playerSlot(column, 8 + column * 18, 223));
        }
    }

    private Slot playerSlot(int slot, int x, int y) {
        return source.type() == CTPPToolboxSourceId.Type.PLAYER_INVENTORY && source.slot() == slot ?
                new CTPPToolboxHostSlot(playerInventory, slot, x, y) : new Slot(playerInventory, slot, x, y);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot clicked = getSlot(index);
        if (!clicked.hasItem()) return ItemStack.EMPTY;
        if (index >= toolbox.getSlots() && player instanceof ServerPlayer serverPlayer) {
            detachBindingForSlot(serverPlayer, clicked.getContainerSlot());
        }
        ItemStack original = clicked.getItem().copy();
        ItemStack moving = clicked.getItem();
        int storageSlots = toolbox.getSlots();
        if (index < storageSlots) {
            if (!moveItemStackTo(moving, storageSlots, slots.size(), true)) return ItemStack.EMPTY;
            toolbox.sourceSlotChanged(index);
        } else if (!moveItemStackTo(moving, 0, storageSlots, false)) {
            return ItemStack.EMPTY;
        }
        if (resolved != null) resolved.syncProjection();
        return original;
    }

    @Override
    public void clicked(int index, int button, ClickType type, Player player) {
        if (index >= toolbox.getSlots() && player instanceof ServerPlayer serverPlayer) {
            detachBindingForSlot(serverPlayer, getSlot(index).getContainerSlot());
        }
        if (index >= 0 && index < toolbox.getSlots()) {
            ItemStack clicked = getSlot(index).getItem();
            ItemStack carried = getCarried();
            if (type == ClickType.PICKUP && !carried.isEmpty() && !clicked.isEmpty() &&
                    CTPPToolboxInventory.canShareCompartment(clicked, carried)) {
                int subIndex = index % CTPPToolboxInventory.STACKS_PER_COMPARTMENT;
                if (subIndex != CTPPToolboxInventory.STACKS_PER_COMPARTMENT - 1) {
                    clicked(index - subIndex + CTPPToolboxInventory.STACKS_PER_COMPARTMENT - 1,
                            button, type, player);
                    return;
                }
            }
            int compartment = index / CTPPToolboxInventory.STACKS_PER_COMPARTMENT;
            if (type == ClickType.PICKUP && carried.isEmpty() && clicked.isEmpty() && toolbox.count(compartment) == 0) {
                toolbox.setFilter(compartment, ItemStack.EMPTY);
            }
        }
        super.clicked(index, button, type, player);
    }

    private static void detachBindingForSlot(ServerPlayer player, int containerSlot) {
        if (containerSlot >= 0 && containerSlot < 9 &&
                CTPPToolboxBindings.get(player, containerSlot) != null) {
            CTPPToolboxOperations.unequip(player, containerSlot, true);
        }
    }

    @Override
    public boolean canDragTo(Slot slot) {
        return slot.index >= toolbox.getSlots() && super.canDragTo(slot);
    }

    @Override
    public boolean stillValid(Player player) {
        if (player.level().isClientSide) return true;
        return player instanceof ServerPlayer serverPlayer && CTPPToolboxService.resolve(serverPlayer, source) != null;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (resolved != null) resolved.syncProjection();
        if (!player.level().isClientSide && source.type() == CTPPToolboxSourceId.Type.BLOCK &&
                source.blockPos() != null &&
                player.level().getBlockEntity(source.blockPos()) instanceof CTPPToolboxBlockEntity block) {
            block.stopOpen();
        }
    }

    public ItemStack getFilter(int compartment) {
        return toolbox.getFilter(compartment);
    }

    public int totalCountInCompartment(int compartment) {
        return toolbox.count(compartment);
    }

    public ItemStack displayStack() {
        return displayStack;
    }

    public CTPPToolboxSourceId source() {
        return source;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (!(playerInventory.player instanceof ServerPlayer player)) return;
        if (resolved != null) resolved.syncProjection();
        List<ItemStack> filters = new ArrayList<>(CTPPToolboxInventory.COMPARTMENTS);
        for (int i = 0; i < CTPPToolboxInventory.COMPARTMENTS; i++) filters.add(toolbox.getFilter(i).copy());
        if (same(filters, lastFilters)) return;
        lastFilters = filters;
        GTNetwork.sendToPlayer(player, new CTPPToolboxMenuFiltersPacket(containerId, filters));
    }

    public void applyFilters(List<ItemStack> filters) {
        for (int i = 0; i < CTPPToolboxInventory.COMPARTMENTS; i++) toolbox.setFilter(i, filters.get(i));
    }

    private static boolean same(List<ItemStack> first, List<ItemStack> second) {
        if (first.size() != second.size()) return false;
        for (int i = 0; i < first.size(); i++) if (!ItemStack.matches(first.get(i), second.get(i))) return false;
        return true;
    }
}
