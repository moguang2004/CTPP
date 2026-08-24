package com.mo_guang.ctpp.common.menu;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public final class CTPPToolboxSlot extends SlotItemHandler {

    private final CTPPToolboxMenu menu;

    public CTPPToolboxSlot(CTPPToolboxMenu menu, IItemHandler handler, int index, int x, int y) {
        super(handler, index, x, y);
        this.menu = menu;
    }

    @Override
    public boolean isActive() {
        return !menu.renderPass && super.isActive();
    }
}
