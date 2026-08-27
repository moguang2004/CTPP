package com.mo_guang.ctpp.common.command;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import com.mo_guang.ctpp.common.terminal.TerminalWireDamageDebug;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import static net.minecraft.commands.Commands.literal;

public final class CTPPTerminalCommands {

    private CTPPTerminalCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("ctpp")
                .requires(source -> source.hasPermission(2))
                .then(literal("wire_damage_debug")
                        .then(literal("on").executes(context -> enable(context.getSource())))
                        .then(literal("off").executes(context -> disable(context.getSource())))));
    }

    private static int enable(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        TerminalWireDamageDebug.enable(player);
        source.sendSuccess(() -> Component.literal("已启用细线理论伤害统计"), false);
        return 1;
    }

    private static int disable(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        boolean enabled = TerminalWireDamageDebug.disable(player);
        source.sendSuccess(() -> Component.literal(enabled ? "已关闭细线理论伤害统计" :
                "细线理论伤害统计未启用"), false);
        return enabled ? 1 : 0;
    }
}
