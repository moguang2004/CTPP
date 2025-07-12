package com.mo_guang.ctpp.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class CTPPValues {
    public static String[] MT = {
            Component.translatable("ctpp.mechanical_tier.0").getString(),
            Component.translatable("ctpp.mechanical_tier.1").withStyle(ChatFormatting.GRAY).getString(),
            Component.translatable("ctpp.mechanical_tier.2").withStyle(ChatFormatting.YELLOW).getString(),
            Component.translatable("ctpp.mechanical_tier.3").withStyle(ChatFormatting.DARK_GRAY).getString(),
            Component.translatable("ctpp.mechanical_tier.4").withStyle(ChatFormatting.AQUA).getString(),
            Component.translatable("ctpp.mechanical_tier.5").withStyle(ChatFormatting.BLUE).getString(),
    };
}
