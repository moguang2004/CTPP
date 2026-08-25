package com.mo_guang.ctpp.client.terminal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

public final class TerminalClientSelection {

    private static @Nullable BlockPos wireTarget;
    private static ItemStack wireItem = ItemStack.EMPTY;
    private static @Nullable BlockPos cutterTarget;

    private TerminalClientSelection() {}

    public static void selectWire(BlockPos pos, ItemStack stack) {
        wireTarget = pos.immutable();
        wireItem = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        cutterTarget = null;
    }

    public static void setWireTarget(@Nullable BlockPos pos, ItemStack stack) {
        wireTarget = pos == null ? null : pos.immutable();
        wireItem = pos == null || stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        if (pos != null) cutterTarget = null;
    }

    public static void selectCutter(BlockPos pos) {
        cutterTarget = pos.immutable();
        clearWire();
    }

    public static void clear() {
        clearWire();
        cutterTarget = null;
    }

    public static void clearWire() {
        wireTarget = null;
        wireItem = ItemStack.EMPTY;
    }

    public static void clearCutter() {
        cutterTarget = null;
    }

    public static @Nullable BlockPos wireTarget() {
        return wireTarget;
    }

    public static ItemStack wireItem() {
        return wireItem.copy();
    }

    public static @Nullable BlockPos cutterTarget() {
        return cutterTarget;
    }
}
