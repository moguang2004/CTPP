package com.mo_guang.ctpp.common.command;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import com.mo_guang.ctpp.common.toolbox.CTPPToolboxSavedData;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxStackData;
import com.mo_guang.ctpp.registry.CTPPBlocks;
import com.mojang.brigadier.CommandDispatcher;

import java.util.Comparator;
import java.util.UUID;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class CTPPToolboxCommands {

    private CTPPToolboxCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("ctpp")
                .requires(source -> source.hasPermission(2))
                .then(literal("toolbox")
                        .then(literal("list").executes(context -> list(context.getSource())))
                        .then(literal("give")
                                .then(argument("uuid", UuidArgument.uuid())
                                        .executes(context -> give(context.getSource(),
                                                UuidArgument.getUuid(context, "uuid")))))
                        .then(literal("delete")
                                .then(argument("uuid", UuidArgument.uuid())
                                        .executes(context -> delete(context.getSource(),
                                                UuidArgument.getUuid(context, "uuid")))))));
    }

    private static int list(CommandSourceStack source) {
        var records = CTPPToolboxSavedData.get(source.getLevel()).records().stream()
                .sorted(Comparator.comparing(record -> record.id().toString()))
                .toList();
        source.sendSuccess(() -> Component.literal("Toolboxes: " + records.size()).withStyle(ChatFormatting.GOLD),
                false);
        for (CTPPToolboxSavedData.Record record : records) {
            String id = record.id().toString();
            Component line = Component.literal(id + "  " + record.color().getName())
                    .withStyle(Style.EMPTY
                            .withColor(ChatFormatting.AQUA)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, id))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    Component.literal("Click to copy UUID"))));
            source.sendSuccess(() -> line, false);
        }
        return records.size();
    }

    private static int give(CommandSourceStack source, UUID id)
                                                                throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        CTPPToolboxSavedData.Record record = CTPPToolboxSavedData.get(source.getLevel()).find(id);
        if (record == null) {
            source.sendFailure(Component.literal("Unknown toolbox UUID: " + id));
            return 0;
        }
        ServerPlayer player = source.getPlayerOrException();
        ItemStack stack = CTPPBlocks.TOOLBOXES[record.color().getId()].asStack();
        stack.getOrCreateTag().putUUID(CTPPToolboxStackData.ID, id);
        CTPPToolboxStackData.update(stack, record);
        if (!player.getInventory().add(stack) && !stack.isEmpty()) player.drop(stack, false);
        return 1;
    }

    private static int delete(CommandSourceStack source, UUID id) {
        if (!CTPPToolboxSavedData.get(source.getLevel()).delete(id)) {
            source.sendFailure(Component.literal("Unknown toolbox UUID: " + id));
            return 0;
        }
        source.sendSuccess(() -> Component.literal("Deleted toolbox " + id), true);
        return 1;
    }
}
