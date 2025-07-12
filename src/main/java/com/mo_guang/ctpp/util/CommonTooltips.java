package com.mo_guang.ctpp.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.KeybindContents;

public class CommonTooltips {
    public static MutableComponent[] KINETIC_OVERCLOCK = new MutableComponent[]{Component.translatable("ctpp.common_tooltip.kinetic_overclock"),
    Component.translatable("ctpp.common_tooltip.input_speed").withStyle(ChatFormatting.YELLOW)};
    public static MutableComponent MECHANICAL_TIER = Component.translatable("ctpp.common_tooltip.mechanical_tier");
}
