package com.mo_guang.ctpp.client.terminal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

public final class TerminalClientSelection {

    private static @Nullable BlockPos wireTarget;
    private static ItemStack wireItem = ItemStack.EMPTY;
    private static int wireMultiplier = 1;
    private static @Nullable BlockPos cutterTarget;

    private TerminalClientSelection() {}

    public static void selectWire(BlockPos pos, ItemStack stack) {
        wireTarget = pos.immutable();
        wireItem = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        wireMultiplier = 1;
        cutterTarget = null;
    }

    public static void setWireTarget(@Nullable BlockPos pos, ItemStack stack, int multiplier) {
        wireTarget = pos == null ? null : pos.immutable();
        wireItem = pos == null || stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        wireMultiplier = pos == null ? 1 : Math.max(1, multiplier);
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
        wireMultiplier = 1;
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

    public static int wireMultiplier() {
        return wireMultiplier;
    }

    public static @Nullable BlockPos cutterTarget() {
        return cutterTarget;
    }
}
