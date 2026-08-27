package com.mo_guang.ctpp.common.terminal;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import de.mari_023.ae2wtlib.terminal.IUniversalWirelessTerminalItem;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

/** Builds and executes an all-or-nothing fine-wire extraction plan. */
final class TerminalWirePayment {

    private static final int MAX_CONTAINER_DEPTH = 32;

    private TerminalWirePayment() {}

    static @Nullable Plan prepare(Player player, ItemStack wire, long required) {
        if (required < 0 || wire.isEmpty()) return null;
        Builder builder = new Builder(player, wire.copyWithCount(1), required);
        if (required == 0) return builder.build();

        int reservedSlot = findReservedHandSlot(player, wire);
        builder.collectPlayerInventory(reservedSlot, true);

        IItemHandler curios = CuriosApi.getCuriosInventory(player)
                .resolve()
                .map(ICuriosItemHandler::getEquippedCurios)
                .orElse(null);
        builder.collectDirect(curios);
        builder.collectNestedContainers(curios);
        builder.collectWirelessNetworks(curios);

        if (builder.remaining > 0 && reservedSlot >= 0) {
            builder.collectPlayerSlot(reservedSlot, false);
        }
        return builder.remaining == 0 ? builder.build() : null;
    }

    private static int findReservedHandSlot(Player player, ItemStack wire) {
        Inventory inventory = player.getInventory();
        int selected = inventory.selected;
        if (selected >= 0 && selected < inventory.getContainerSize() &&
                sameWire(inventory.getItem(selected), wire)) {
            return selected;
        }

        ItemStack offhand = player.getOffhandItem();
        if (!sameWire(offhand, wire)) return -1;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (inventory.getItem(slot) == offhand) return slot;
        }
        return -1;
    }

    private static boolean sameWire(ItemStack first, ItemStack second) {
        return !first.isEmpty() && !second.isEmpty() && ItemStack.isSameItemSameTags(first, second);
    }

    static final class Plan {

        private final Player player;
        private final ItemStack wire;
        private final List<Extraction> extractions;

        private Plan(Player player, ItemStack wire, List<Extraction> extractions) {
            this.player = player;
            this.wire = wire;
            this.extractions = List.copyOf(extractions);
        }

        boolean commit() {
            List<CompletedExtraction> completed = new ArrayList<>(extractions.size());
            for (Extraction extraction : extractions) {
                long extracted = extraction.extract();
                if (extracted == extraction.amount()) {
                    completed.add(new CompletedExtraction(extraction, extracted));
                    continue;
                }

                long unreturned = extracted > 0 ? extraction.restore(extracted) : 0;
                for (int i = completed.size() - 1; i >= 0; i--) {
                    CompletedExtraction previous = completed.get(i);
                    unreturned += previous.extraction().restore(previous.amount());
                }
                returnToPlayer(unreturned);
                return false;
            }
            return true;
        }

        private void returnToPlayer(long amount) {
            int stackSize = Math.max(1, wire.getMaxStackSize());
            while (amount > 0) {
                int count = (int) Math.min(amount, stackSize);
                ItemStack returned = wire.copyWithCount(count);
                player.getInventory().placeItemBackInInventory(returned);
                if (!returned.isEmpty()) player.drop(returned, false);
                amount -= count;
            }
        }
    }

    private static final class Builder {

        private final Player player;
        private final ItemStack wire;
        private final List<Extraction> extractions = new ArrayList<>();
        private final Set<IItemHandler> visitedHandlers = Collections.newSetFromMap(new IdentityHashMap<>());
        private final Set<IGrid> visitedGrids = Collections.newSetFromMap(new IdentityHashMap<>());
        private long remaining;

        private Builder(Player player, ItemStack wire, long required) {
            this.player = player;
            this.wire = wire;
            this.remaining = required;
        }

        private Plan build() {
            return new Plan(player, wire, extractions);
        }

        private void collectPlayerInventory(int reservedSlot, boolean keepReservedItem) {
            Inventory inventory = player.getInventory();
            for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
                collectPlayerSlot(slot, keepReservedItem && slot == reservedSlot);
            }
        }

        private void collectPlayerSlot(int slot, boolean keepOne) {
            Inventory inventory = player.getInventory();
            ItemStack candidate = inventory.getItem(slot);
            if (!sameWire(candidate, wire)) return;
            int available = candidate.getCount() - (keepOne ? 1 : 0);
            int amount = (int) Math.min(remaining, Math.max(0, available));
            if (amount <= 0) return;
            extractions.add(new InventoryExtraction(player, slot, wire, amount));
            remaining -= amount;
        }

        private void collectDirect(@Nullable IItemHandler handler) {
            if (handler == null) return;
            for (int slot = 0; slot < handler.getSlots() && remaining > 0; slot++) {
                ItemStack candidate = handler.getStackInSlot(slot);
                if (!sameWire(candidate, wire)) continue;
                int requested = (int) Math.min(remaining, Integer.MAX_VALUE);
                ItemStack simulated = handler.extractItem(slot, requested, true);
                if (!sameWire(simulated, wire)) continue;
                int amount = simulated.getCount();
                if (amount <= 0) continue;
                extractions.add(new HandlerExtraction(handler, slot, wire, amount));
                remaining -= amount;
            }
        }

        private void collectNestedContainers(@Nullable IItemHandler curios) {
            Inventory inventory = player.getInventory();
            for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
                collectNested(inventory.getItem(slot), 0);
            }
            if (curios == null) return;
            for (int slot = 0; slot < curios.getSlots() && remaining > 0; slot++) {
                collectNested(curios.getStackInSlot(slot), 0);
            }
        }

        private void collectNested(ItemStack container, int depth) {
            if (container.isEmpty() || depth >= MAX_CONTAINER_DEPTH || remaining <= 0) return;
            container.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().ifPresent(handler -> {
                if (!visitedHandlers.add(handler)) return;
                collectDirect(handler);
                for (int slot = 0; slot < handler.getSlots() && remaining > 0; slot++) {
                    collectNested(handler.getStackInSlot(slot), depth + 1);
                }
            });
        }

        private void collectWirelessNetworks(@Nullable IItemHandler curios) {
            if (remaining <= 0) return;
            Inventory inventory = player.getInventory();
            for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
                collectWirelessNetwork(inventory.getItem(slot));
            }
            if (curios == null) return;
            for (int slot = 0; slot < curios.getSlots() && remaining > 0; slot++) {
                collectWirelessNetwork(curios.getStackInSlot(slot));
            }
        }

        private void collectWirelessNetwork(ItemStack terminalStack) {
            if (!(terminalStack.getItem() instanceof IUniversalWirelessTerminalItem terminal)) return;
            IGrid grid = terminal.getLinkedGrid(terminalStack, player.level(), player);
            if (grid == null || !visitedGrids.add(grid)) return;
            MEStorage storage = grid.getStorageService().getInventory();
            AEItemKey key = AEItemKey.of(wire);
            IActionSource source = IActionSource.ofPlayer(player);
            long amount = Math.min(remaining, storage.extract(key, remaining, Actionable.SIMULATE, source));
            if (amount <= 0) return;
            extractions.add(new AEExtraction(storage, key, source, amount));
            remaining -= amount;
        }
    }

    private interface Extraction {

        long amount();

        long extract();

        /** Returns the amount that could not be restored to this source. */
        long restore(long amount);
    }

    private record CompletedExtraction(Extraction extraction, long amount) {}

    private record InventoryExtraction(Player player, int slot, ItemStack wire, int count) implements Extraction {

        @Override
        public long amount() {
            return count;
        }

        @Override
        public long extract() {
            ItemStack current = player.getInventory().getItem(slot);
            if (!sameWire(current, wire) || current.getCount() < count) return 0;
            current.shrink(count);
            player.getInventory().setChanged();
            return count;
        }

        @Override
        public long restore(long amount) {
            ItemStack current = player.getInventory().getItem(slot);
            int restored = (int) amount;
            if (current.isEmpty()) {
                player.getInventory().setItem(slot, wire.copyWithCount(restored));
                return 0;
            }
            if (!sameWire(current, wire) || current.getCount() + restored > current.getMaxStackSize()) {
                return amount;
            }
            current.grow(restored);
            player.getInventory().setChanged();
            return 0;
        }
    }

    private record HandlerExtraction(IItemHandler handler, int slot, ItemStack wire, int count)
            implements Extraction {

        @Override
        public long amount() {
            return count;
        }

        @Override
        public long extract() {
            ItemStack current = handler.getStackInSlot(slot);
            if (!sameWire(current, wire) || current.getCount() < count) return 0;
            ItemStack extracted = handler.extractItem(slot, count, false);
            if (sameWire(extracted, wire)) return extracted.getCount();
            if (!extracted.isEmpty()) handler.insertItem(slot, extracted, false);
            return 0;
        }

        @Override
        public long restore(long amount) {
            ItemStack remainder = handler.insertItem(slot, wire.copyWithCount((int) amount), false);
            return remainder.getCount();
        }
    }

    private record AEExtraction(MEStorage storage, AEItemKey key, IActionSource source, long amount)
            implements Extraction {

        @Override
        public long extract() {
            return storage.extract(key, amount, Actionable.MODULATE, source);
        }

        @Override
        public long restore(long amount) {
            return amount - storage.insert(key, amount, Actionable.MODULATE, source);
        }
    }
}
