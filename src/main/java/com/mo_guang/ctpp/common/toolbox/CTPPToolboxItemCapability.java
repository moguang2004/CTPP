package com.mo_guang.ctpp.common.toolbox;

import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.server.ServerLifecycleHooks;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/** Exposes the server-owned toolbox inventory without duplicating it into the item stack. */
public final class CTPPToolboxItemCapability implements ICapabilityProvider {

    private final LazyOptional<IItemHandler> inventory;

    public CTPPToolboxItemCapability(ItemStack toolbox) {
        inventory = LazyOptional.of(() -> new Handler(toolbox));
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
        return ForgeCapabilities.ITEM_HANDLER.orEmpty(capability, inventory);
    }

    private static final class Handler implements IItemHandler {

        private final ItemStack toolbox;

        private Handler(ItemStack toolbox) {
            this.toolbox = toolbox;
        }

        @Override
        public int getSlots() {
            CTPPToolboxSavedData.Record record = resolve();
            return record == null ? 0 : record.inventory().getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            CTPPToolboxSavedData.Record record = resolve();
            return record == null ? ItemStack.EMPTY : record.inventory().getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            CTPPToolboxSavedData.Record record = resolve();
            if (record == null) return stack;
            ItemStack remainder = record.inventory().insertItem(slot, stack, simulate);
            if (!simulate) CTPPToolboxStackData.update(toolbox, record);
            return remainder;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            CTPPToolboxSavedData.Record record = resolve();
            if (record == null) return ItemStack.EMPTY;
            ItemStack extracted = record.inventory().extractItem(slot, amount, simulate);
            if (!simulate) CTPPToolboxStackData.update(toolbox, record);
            return extracted;
        }

        @Override
        public int getSlotLimit(int slot) {
            CTPPToolboxSavedData.Record record = resolve();
            return record == null ? 0 : record.inventory().getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            CTPPToolboxSavedData.Record record = resolve();
            return record != null && record.inventory().isItemValid(slot, stack);
        }

        private @Nullable CTPPToolboxSavedData.Record resolve() {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null || !server.isSameThread()) return null;
            UUID id = CTPPToolboxStackData.getId(toolbox);
            CTPPToolboxSavedData.Record record = id == null ? null :
                    CTPPToolboxSavedData.get(server).find(id);
            return record != null ? record : CTPPToolboxService.ensure(toolbox, server.overworld());
        }
    }
}
