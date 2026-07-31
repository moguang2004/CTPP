package com.mo_guang.ctpp.client.toolbox;

import net.createmod.catnip.gui.ScreenOpener;

import com.mo_guang.ctpp.common.toolbox.CTPPToolboxBinding;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxSnapshot;

import java.util.Map;

public final class CTPPToolboxClientState {

    private static Map<Integer, CTPPToolboxBinding> bindings = Map.of();

    private CTPPToolboxClientState() {}

    public static void setBindings(Map<Integer, CTPPToolboxBinding> value) {
        bindings = Map.copyOf(value);
    }

    public static Map<Integer, CTPPToolboxBinding> bindings() {
        return bindings;
    }

    public static void openRadial(java.util.List<CTPPToolboxSnapshot> snapshots,
                                  Map<Integer, CTPPToolboxBinding> value, int hotbarSlot) {
        setBindings(value);
        if ((!snapshots.isEmpty() || bindings.containsKey(hotbarSlot)) &&
                net.minecraft.client.Minecraft.getInstance().screen == null) {
            ScreenOpener.open(new CTPPToolboxRadialScreen(snapshots, bindings.get(hotbarSlot), hotbarSlot));
        }
    }
}
