package com.mo_guang.ctpp.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class CTPPValues {
    public static String[] MT = {
            Component.translatable("ctpp.mechanical_tier.0").getString(),
            ChatFormatting.GRAY + Component.translatable("ctpp.mechanical_tier.1").getString() + ChatFormatting.RESET,
            ChatFormatting.YELLOW + Component.translatable("ctpp.mechanical_tier.2").getString() + ChatFormatting.RESET,
            ChatFormatting.DARK_GRAY+ Component.translatable("ctpp.mechanical_tier.3").getString() + ChatFormatting.RESET,
            ChatFormatting.AQUA + Component.translatable("ctpp.mechanical_tier.4").getString() + ChatFormatting.RESET,
            ChatFormatting.GOLD + Component.translatable("ctpp.mechanical_tier.5").getString() + ChatFormatting.RESET,
    };
}
