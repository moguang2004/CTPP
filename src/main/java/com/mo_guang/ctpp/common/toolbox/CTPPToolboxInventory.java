package com.mo_guang.ctpp.common.toolbox;

import com.gregtechceu.gtceu.api.item.tool.ToolHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public final class CTPPToolboxInventory extends ItemStackHandler {

    public static final int COMPARTMENTS = 8;
    public static final int STACKS_PER_COMPARTMENT = 4;

    private final List<ItemStack> filters = new ArrayList<>(COMPARTMENTS);
    private Runnable changed = () -> {};
    private boolean settling;
    private boolean loading;

    public CTPPToolboxInventory() {
        super(COMPARTMENTS * STACKS_PER_COMPARTMENT);
        for (int i = 0; i < COMPARTMENTS; i++) filters.add(ItemStack.EMPTY);
    }

    public void setChanged(Runnable changed) {
        this.changed = changed;
    }

    public ItemStack getFilter(int compartment) {
        return filters.get(compartment);
    }

    public void setFilter(int compartment, ItemStack filter) {
        ItemStack normalized = filter.isEmpty() ? ItemStack.EMPTY : ItemHandlerHelper.copyStackWithSize(filter, 1);
        if (ItemStack.matches(filters.get(compartment), normalized)) return;
        filters.set(compartment, normalized);
        changed.run();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (!stack.getItem().canFitInsideContainerItems() ||
                stack.getItem() instanceof com.mo_guang.ctpp.common.item.CTPPToolboxItem)
            return false;
        int compartment = slot / STACKS_PER_COMPARTMENT;
        ItemStack filter = filters.get(compartment);
        return filter.isEmpty() || canShareCompartment(filter, stack);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        super.setStackInSlot(slot, stack);
        int compartment = slot / STACKS_PER_COMPARTMENT;
        if (!stack.isEmpty() && filters.get(compartment).isEmpty()) {
            filters.set(compartment, ItemHandlerHelper.copyStackWithSize(stack, 1));
        }
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (loading) return;
        int compartment = slot / STACKS_PER_COMPARTMENT;
        ItemStack changedStack = getStackInSlot(slot);
        if (!changedStack.isEmpty() && filters.get(compartment).isEmpty()) {
            filters.set(compartment, ItemHandlerHelper.copyStackWithSize(changedStack, 1));
        }
        if (!settling) settle(compartment);
        changed.run();
    }

    public void sourceSlotChanged(int slot) {
        onContentsChanged(slot);
    }

    private void settle(int compartment) {
        int total = 0;
        ItemStack sample = ItemStack.EMPTY;
        for (int i = 0; i < STACKS_PER_COMPARTMENT; i++) {
            ItemStack stack = getStackInSlot(compartment * STACKS_PER_COMPARTMENT + i);
            total += stack.getCount();
            if (!stack.isEmpty()) sample = stack;
        }
        if (sample.isEmpty()) return;

        settling = true;
        loading = true;
        try {
            if (!sample.isStackable()) {
                int write = 0;
                for (int read = 0; read < STACKS_PER_COMPARTMENT; read++) {
                    int readSlot = compartment * STACKS_PER_COMPARTMENT + read;
                    ItemStack stack = getStackInSlot(readSlot);
                    if (stack.isEmpty()) continue;
                    int writeSlot = compartment * STACKS_PER_COMPARTMENT + write++;
                    if (writeSlot == readSlot) continue;
                    super.setStackInSlot(writeSlot, stack);
                    super.setStackInSlot(readSlot, ItemStack.EMPTY);
                }
                return;
            }
            for (int i = 0; i < STACKS_PER_COMPARTMENT; i++) {
                int slot = compartment * STACKS_PER_COMPARTMENT + i;
                ItemStack replacement = total <= 0 ? ItemStack.EMPTY :
                        ItemHandlerHelper.copyStackWithSize(sample, Math.min(total, sample.getMaxStackSize()));
                total -= replacement.getCount();
                super.setStackInSlot(slot, replacement);
            }
        } finally {
            loading = false;
            settling = false;
        }
    }

    public ItemStack distribute(ItemStack stack, int compartment, boolean simulate) {
        if (stack.isEmpty() || filters.get(compartment).isEmpty()) return stack;
        ItemStack remainder = stack;
        for (int i = STACKS_PER_COMPARTMENT - 1; i >= 0 && !remainder.isEmpty(); i--) {
            remainder = insertItem(compartment * STACKS_PER_COMPARTMENT + i, remainder, simulate);
        }
        return remainder;
    }

    public ItemStack take(int amount, int compartment, boolean simulate) {
        if (amount <= 0) return ItemStack.EMPTY;
        ItemStack result = ItemStack.EMPTY;
        int remaining = amount;
        for (int i = STACKS_PER_COMPARTMENT - 1; i >= 0 && remaining > 0; i--) {
            ItemStack extracted = extractItem(compartment * STACKS_PER_COMPARTMENT + i, remaining, simulate);
            if (!extracted.isEmpty()) result = extracted;
            remaining -= extracted.getCount();
        }
        return result.isEmpty() ? ItemStack.EMPTY : ItemHandlerHelper.copyStackWithSize(result, amount - remaining);
    }

    public int count(int compartment) {
        int total = 0;
        for (int i = 0; i < STACKS_PER_COMPARTMENT; i++) {
            total += getStackInSlot(compartment * STACKS_PER_COMPARTMENT + i).getCount();
        }
        return total;
    }

    public String toolTypeSummary() {
        TreeSet<String> names = new TreeSet<>();
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) ToolHelper.getToolTypes(stack).forEach(type -> names.add(type.name));
        }
        return names.isEmpty() ? "" : " " + String.join(" ", names) + " ";
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        ListTag list = new ListTag();
        filters.forEach(filter -> list.add(filter.save(new CompoundTag())));
        tag.put("Compartments", list);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        loading = true;
        try {
            super.deserializeNBT(tag);
            filters.clear();
            ListTag list = tag.getList("Compartments", Tag.TAG_COMPOUND);
            for (int i = 0; i < COMPARTMENTS; i++) {
                filters.add(i < list.size() ? ItemStack.of(list.getCompound(i)) : ItemStack.EMPTY);
            }
        } finally {
            loading = false;
        }
    }

    public static boolean canShareCompartment(ItemStack first, ItemStack second) {
        if (!first.isStackable() && !second.isStackable() && first.isDamageableItem() && second.isDamageableItem()) {
            return first.getItem() == second.getItem();
        }
        return ItemHandlerHelper.canItemStacksStack(first, second);
    }
}
