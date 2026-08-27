package com.mo_guang.ctpp.common.terminal;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.IEnergyTransferHandler;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import com.gregtechceu.gtceu.common.blockentity.CableBlockEntity;
import com.gregtechceu.gtceu.common.network.GTNetwork;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import com.ctnhlang.*;
import com.mo_guang.ctpp.api.terminal.TerminalProperties;
import com.mo_guang.ctpp.common.blockentity.VoltageTerminalBlockEntity;
import com.mo_guang.ctpp.config.MainConfig;
import com.mo_guang.ctpp.network.packet.CTPPTerminalWireSelectionPacket;
import org.jetbrains.annotations.Nullable;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Prefix("message")
@Category("terminal")
/** Server-side connection state and bridge forwarding for voltage terminals. */
public final class TerminalNetwork {

    @CN("已选中接线柱，右键另一个接线柱来完成连接")
    @EN("Terminal selected. Right-click another terminal to connect")
    private static Lang bound;

    @CN("已取消接线柱选择")
    @EN("Terminal selection cancelled")
    private static Lang selectionCancelled;

    @CN("连接类型：%s")
    @EN("Connection type: %s")
    private static Lang connectionType;

    @CN("已建立 %s 连接")
    @EN("Established %s connection")
    private static Lang connected;

    @CN("接线柱距离超过最大范围：%s 格")
    @EN("Terminals are beyond the maximum range of %s blocks")
    private static Lang tooFar;

    @CN("距离接线柱过远，已取消绑定")
    @EN("You moved too far from the selected terminal; binding cancelled")
    private static Lang bindingLost;

    @CN("必须使用相同类型的细线")
    @EN("The same fine wire type must be used")
    private static Lang differentWire;

    @CN("已选中接线柱，右键另一个接线柱来断开连接")
    @EN("Terminal selected. Right-click another terminal to disconnect")
    private static Lang cutterSelected;

    @CN("已取消剪线钳选择")
    @EN("Wire-cutter selection cancelled")
    private static Lang cutterCancelled;

    @CN("连接已断开")
    @EN("Connection removed")
    private static Lang cutterDisconnected;

    @CN("这两个接线柱之间没有连接")
    @EN("These terminals are not connected")
    private static Lang cutterNoConnection;

    @CN("该连接已存在，请先断开后再重新连接")
    @EN("That connection already exists; disconnect it before reconnecting")
    private static Lang alreadyConnected;

    @CN("细导线数量不足，需要 %s")
    @EN("Not enough fine wire; requires %s")
    private static Lang notEnoughWire;

    @CN("该细导线没有可用的线缆属性，不能用于接线")
    @EN("This fine wire has no usable cable properties and cannot be connected")
    private static Lang unusableWire;

    private record Selection(ResourceKey<Level> dimension, BlockPos pos,
                             TerminalProperties.FineWireSpec wire, ItemStack wireItem,
                             TerminalProperties.ConnectionType connectionType) {}

    private record CutterSelection(ResourceKey<Level> dimension, BlockPos pos) {}

    private static final Map<UUID, Selection> selections = new HashMap<>();
    private static final Map<UUID, CutterSelection> cutterSelections = new HashMap<>();
    private static final ThreadLocal<Set<BlockPos>> TRANSFER_CONTEXT = ThreadLocal.withInitial(HashSet::new);

    private TerminalNetwork() {}

    public static Set<BlockPos> currentVisited() {
        return new HashSet<>(TRANSFER_CONTEXT.get());
    }

    public static boolean handleUse(Level level, BlockPos pos, Player player, ItemStack stack) {
        if (!(level.getBlockEntity(pos) instanceof VoltageTerminalBlockEntity)) {
            return false;
        }
        if (stack.is(CustomTags.WIRE_CUTTERS)) {
            if (level.isClientSide) return true;
            ServerLevel server = (ServerLevel) level;
            if (player.isShiftKeyDown()) {
                cutterSelections.remove(player.getUUID());
                disconnectAllToInventory(server, pos, player);
                return true;
            }
            CutterSelection selected = cutterSelections.get(player.getUUID());
            if (selected == null || !selected.dimension().equals(level.dimension())) {
                cutterSelections.put(player.getUUID(), new CutterSelection(level.dimension(), pos.immutable()));
                show(player, cutterSelected.translate());
                return true;
            }
            if (selected.pos().equals(pos)) {
                cutterSelections.remove(player.getUUID());
                show(player, cutterCancelled.translate());
                return true;
            }
            if (server.getBlockEntity(selected.pos()) instanceof VoltageTerminalBlockEntity first &&
                    first.getLink(pos) != null) {
                disconnectAndStore(server, selected.pos(), pos, player);
            } else {
                show(player, cutterNoConnection.translate());
            }
            cutterSelections.remove(player.getUUID());
            return true;
        }
        TerminalProperties.FineWireSpec wire = TerminalProperties.FineWireSpec.from(stack);
        boolean fineWireItem = TerminalProperties.isFineWire(stack);
        // The server owns selection, linking and item consumption. Returning
        // success on the client still produces the normal hand-swing/success
        // feedback while avoiding a client-side duplicate mutation.
        if (level.isClientSide) return wire != null || fineWireItem;
        ServerLevel server = (ServerLevel) level;
        Selection selection = selections.get(player.getUUID());

        if (player.isShiftKeyDown()) {
            if (selection != null && selection.dimension().equals(level.dimension()) && selection.pos().equals(pos)) {
                selections.remove(player.getUUID());
                syncWireSelection(player, null);
                show(player, selectionCancelled.translate());
                return true;
            }
            return false;
        }

        if (wire == null) {
            if (fineWireItem) show(player, unusableWire.translate());
            return fineWireItem;
        }
        if (selection == null) {
            Selection created = new Selection(level.dimension(), pos.immutable(), wire,
                    stack.copyWithCount(1), TerminalProperties.ConnectionType.ONE);
            selections.put(player.getUUID(), created);
            syncWireSelection(player, created);
            show(player, bound.translate());
            return true;
        }
        if (!selection.dimension().equals(level.dimension())) {
            selections.remove(player.getUUID());
            syncWireSelection(player, null);
            show(player, bindingLost.translate());
            return true;
        }
        if (selection.pos().equals(pos)) {
            if (!sameWire(selection.wireItem(), stack)) {
                show(player, differentWire.translate());
                return true;
            }
            TerminalProperties.ConnectionType next = selection.connectionType().next();
            selections.put(player.getUUID(), new Selection(selection.dimension(), selection.pos(), selection.wire(),
                    selection.wireItem(), next));
            syncWireSelection(player, selections.get(player.getUUID()));
            show(player, connectionType.translate(next.display()));
            return true;
        }
        if (!(server.getBlockEntity(selection.pos()) instanceof VoltageTerminalBlockEntity first) ||
                !(server.getBlockEntity(pos) instanceof VoltageTerminalBlockEntity second)) {
            selections.remove(player.getUUID());
            syncWireSelection(player, null);
            show(player, bindingLost.translate());
            return true;
        }
        if (!sameWire(selection.wireItem(), stack)) {
            show(player, differentWire.translate());
            return true;
        }
        int maxRange = MainConfig.INSTANCE.terminalConfig.terminalMaxConnectionRange;
        if (selection.pos().distSqr(pos) > (double) maxRange * maxRange) {
            show(player, tooFar.translate(maxRange));
            return true;
        }
        if (first.getLinks().containsKey(pos) || second.getLinks().containsKey(selection.pos())) {
            selections.remove(player.getUUID());
            syncWireSelection(player, null);
            show(player, alreadyConnected.translate());
            return true;
        }
        long requiredWire = TerminalProperties.requiredWireCount(selection.pos(), pos, selection.connectionType());
        TerminalWirePayment.Plan payment = player.getAbilities().instabuild ? null :
                TerminalWirePayment.prepare(player, selection.wireItem(), requiredWire);
        if (!player.getAbilities().instabuild && payment == null) {
            show(player, notEnoughWire.translate(requiredWire));
            return true;
        }
        boolean firstAdded = first.addLink(pos, selection.wire(), selection.wireItem(), selection.connectionType());
        boolean secondAdded = firstAdded &&
                second.addLink(selection.pos(), selection.wire(), selection.wireItem(), selection.connectionType());
        if (!firstAdded || !secondAdded) {
            if (firstAdded) first.removeLink(pos);
            selections.remove(player.getUUID());
            syncWireSelection(player, null);
            show(player, bindingLost.translate());
            return true;
        }
        if (payment != null && !payment.commit()) {
            first.removeLink(pos);
            second.removeLink(selection.pos());
            show(player, notEnoughWire.translate(requiredWire));
            return true;
        }
        selections.remove(player.getUUID());
        syncWireSelection(player, null);
        show(player, connected.translate(selection.connectionType().display()));
        return true;
    }

    public static void disconnectAllNoDrop(ServerLevel level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof VoltageTerminalBlockEntity terminal)) return;
        for (BlockPos other : terminal.getLinks().keySet()) {
            disconnectPair(level, pos, other);
        }
    }

    public static void disconnectAll(ServerLevel level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof VoltageTerminalBlockEntity terminal)) return;
        for (BlockPos other : terminal.getLinks().keySet()) {
            disconnectAndDrop(level, pos, other);
        }
    }

    private static void disconnectAllToInventory(ServerLevel level, BlockPos pos, Player player) {
        if (!(level.getBlockEntity(pos) instanceof VoltageTerminalBlockEntity terminal)) return;
        for (BlockPos other : terminal.getLinks().keySet()) {
            disconnectAndStore(level, pos, other, player);
        }
    }

    private static void disconnectAndDrop(ServerLevel level, BlockPos firstPos, BlockPos secondPos) {
        TerminalProperties.Link link = disconnectPair(level, firstPos, secondPos);
        if (link != null) {
            ItemStack drop = link.getDropStack(firstPos);
            if (!drop.isEmpty()) Containers.dropItemStack(level, firstPos.getX() + 0.5, firstPos.getY() + 0.5,
                    firstPos.getZ() + 0.5, drop);
        }
    }

    private static void disconnectAndStore(ServerLevel level, BlockPos firstPos, BlockPos secondPos, Player player) {
        TerminalProperties.Link link = disconnectPair(level, firstPos, secondPos);
        if (link != null) {
            ItemStack drop = link.getDropStack(firstPos);
            if (!drop.isEmpty()) player.getInventory().placeItemBackInInventory(drop);
            show(player, cutterDisconnected.translate());
        }
    }

    public static void tickPlayer(ServerPlayer player) {
        Selection selection = selections.get(player.getUUID());
        if (selection != null) {
            ItemStack heldWire = heldFineWire(player);
            if (player.level().dimension() != selection.dimension() || heldWire.isEmpty() ||
                    !sameWire(selection.wireItem(), heldWire) ||
                    !(player.level().getBlockEntity(selection.pos()) instanceof VoltageTerminalBlockEntity)) {
                selections.remove(player.getUUID());
                syncWireSelection(player, null);
                show(player, selectionCancelled.translate());
            } else {
                int range = MainConfig.INSTANCE.terminalConfig.terminalMaxConnectionRange;
                if (player.blockPosition().distSqr(selection.pos()) > (double) range * range * 4.0) {
                    selections.remove(player.getUUID());
                    syncWireSelection(player, null);
                    show(player, bindingLost.translate());
                }
            }
        }
        CutterSelection cutter = cutterSelections.get(player.getUUID());
        if (cutter != null) {
            boolean cutterHeld = player.getMainHandItem().is(CustomTags.WIRE_CUTTERS) ||
                    player.getOffhandItem().is(CustomTags.WIRE_CUTTERS);
            boolean targetValid = player.level().dimension().equals(cutter.dimension()) &&
                    player.level().getBlockEntity(cutter.pos()) instanceof VoltageTerminalBlockEntity &&
                    player.blockPosition().distSqr(cutter.pos()) <= 64 * 64;
            if (!cutterHeld || !targetValid) cutterSelections.remove(player.getUUID());
        }
    }

    public static void clearSelection(UUID playerId) {
        selections.remove(playerId);
        cutterSelections.remove(playerId);
    }

    public static void cancelWireSelection(ServerPlayer player, BlockPos pos) {
        Selection selection = selections.get(player.getUUID());
        if (selection == null || !selection.dimension().equals(player.level().dimension()) ||
                !selection.pos().equals(pos)) {
            return;
        }
        selections.remove(player.getUUID());
        syncWireSelection(player, null);
        show(player, selectionCancelled.translate());
    }

    private static void syncWireSelection(Player player, @Nullable Selection selection) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (selection == null) {
            GTNetwork.sendToPlayer(serverPlayer,
                    CTPPTerminalWireSelectionPacket.cleared());
        } else {
            GTNetwork.sendToPlayer(serverPlayer,
                    new CTPPTerminalWireSelectionPacket(selection.pos(), selection.wireItem(),
                            selection.connectionType().multiplier()));
        }
    }

    private static boolean sameWire(ItemStack first, ItemStack second) {
        return !first.isEmpty() && !second.isEmpty() &&
                ItemStack.isSameItemSameTags(first, second);
    }

    private static ItemStack heldFineWire(Player player) {
        if (TerminalProperties.FineWireSpec.from(player.getMainHandItem()) != null) {
            return player.getMainHandItem();
        }
        if (TerminalProperties.FineWireSpec.from(player.getOffhandItem()) != null) {
            return player.getOffhandItem();
        }
        return ItemStack.EMPTY;
    }

    private static void show(Player player, Component message) {
        player.displayClientMessage(message, true);
    }

    public static long forward(Level level, VoltageTerminalBlockEntity source, long voltage, long amperage,
                               Set<BlockPos> visited) {
        if (!(level instanceof ServerLevel server) || voltage <= 0 || amperage <= 0 ||
                !visited.add(source.getBlockPos()))
            return 0;
        long remaining = amperage;
        for (Map.Entry<BlockPos, TerminalProperties.Link> entry : source.getLinks().entrySet()) {
            if (remaining <= 0 || visited.contains(entry.getKey())) continue;
            TerminalProperties.Link link = entry.getValue();
            long linkVoltage = voltage - link.wire().loss(source.getBlockPos(), entry.getKey());
            if (linkVoltage <= 0) {
                continue;
            }
            long linkAmperage = Math.min(remaining, link.amperageLimit());
            if (!(server.getBlockEntity(entry.getKey()) instanceof VoltageTerminalBlockEntity peer)) continue;
            long transferVoltage = Math.min(linkVoltage, link.wire().voltage());
            long accepted = peer.acceptLinkedEnergy(level, transferVoltage, linkAmperage,
                    new HashSet<>(visited));
            if (accepted > 0) {
                applyLinkHeat(server, source.getBlockPos(), entry.getKey(), link, accepted, linkVoltage);
            }
            remaining -= accepted;
        }
        return amperage - remaining;
    }

    private static @Nullable TerminalProperties.Link disconnectPair(ServerLevel level, BlockPos firstPos,
                                                                    BlockPos secondPos) {
        VoltageTerminalBlockEntity first = level.getBlockEntity(firstPos) instanceof VoltageTerminalBlockEntity value ?
                value : null;
        VoltageTerminalBlockEntity second = level
                .getBlockEntity(secondPos) instanceof VoltageTerminalBlockEntity value ? value : null;
        TerminalProperties.Link link = first == null ? null : first.getLink(secondPos);
        if (first != null) first.removeLink(secondPos);
        if (second != null) second.removeLink(firstPos);
        return link;
    }

    public static void disconnectLink(ServerLevel level, BlockPos firstPos, BlockPos secondPos) {
        disconnectPair(level, firstPos, secondPos);
    }

    private static void applyLinkHeat(ServerLevel level, BlockPos firstPos, BlockPos secondPos,
                                      TerminalProperties.Link link, long amperage, long voltage) {
        VoltageTerminalBlockEntity first = level.getBlockEntity(firstPos) instanceof VoltageTerminalBlockEntity value ?
                value : null;
        VoltageTerminalBlockEntity second = level
                .getBlockEntity(secondPos) instanceof VoltageTerminalBlockEntity value ? value : null;
        if (first == null || second == null) return;

        int heat = 0;
        int amperageDifference = (int) Math.min(Integer.MAX_VALUE,
                Math.max(0L, amperage - link.amperageLimit()));
        if (amperageDifference > 0) heat += amperageDifference * 40;

        int sourceTier = com.gregtechceu.gtceu.utils.GTUtil.getTierByVoltage(voltage);
        int wireTier = com.gregtechceu.gtceu.utils.GTUtil.getTierByVoltage(link.wire().voltage());
        if (sourceTier > wireTier) {
            int tierDifference = sourceTier - wireTier;
            heat += (int) (Math.log(tierDifference) * 45 + 36.5);
        }
        if (heat > 0) {
            first.applyLinkHeat(secondPos, heat);
            second.applyLinkHeat(firstPos, heat);
        }
    }

    public static long forwardToAttached(Level level, VoltageTerminalBlockEntity terminal, long voltage,
                                         long amperage, Set<BlockPos> visited) {
        if (!(level instanceof ServerLevel server) || voltage > terminal.getVoltageLimit()) {
            terminal.burn();
            return 0;
        }
        Direction side = terminal.getElectricalSide();
        BlockPos attached = terminal.getBlockPos().relative(side);
        BlockEntity blockEntity = server.getBlockEntity(attached);
        if (blockEntity instanceof CableBlockEntity cable) {
            IEnergyContainer container = cable.getEnergyContainer(side.getOpposite());
            if (container instanceof IEnergyTransferHandler handler) {
                Set<BlockPos> excluded = new HashSet<>(visited);
                excluded.add(terminal.getBlockPos());
                Set<BlockPos> previous = TRANSFER_CONTEXT.get();
                TRANSFER_CONTEXT.set(excluded);
                try {
                    return handler.acceptEnergyFromNetwork(side.getOpposite(), voltage, amperage, excluded);
                } finally {
                    TRANSFER_CONTEXT.set(previous);
                }
            }
        }
        IEnergyContainer container = blockEntity == null ? null :
                blockEntity.getCapability(GTCapability.CAPABILITY_ENERGY_CONTAINER, side.getOpposite())
                        .resolve().orElse(null);
        if (container == null || !container.inputsEnergy(side.getOpposite())) return 0;
        return container.acceptEnergyFromNetwork(side.getOpposite(), voltage, amperage);
    }
}
