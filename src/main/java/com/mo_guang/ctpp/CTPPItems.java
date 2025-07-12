package com.mo_guang.ctpp;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;

import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;

public class CTPPItems {
    public static void init() {}
    public static ItemEntry<Item> BASIC_MECHANISM = REGISTRATE.item("basic_mechanism", Item::new).register();
    public static ItemEntry<Item> STEEL_MECHANISM = REGISTRATE.item("steel_mechanism", Item::new).register();
}
