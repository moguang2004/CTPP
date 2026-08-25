package com.mo_guang.ctpp.common.terminal;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.IEnergyTransferHandler;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import com.gregtechceu.gtceu.common.blockentity.CableBlockEntity;
import com.mo_guang.ctpp.api.terminal.TerminalProperties;
import com.mo_guang.ctpp.common.blockentity.VoltageTerminalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Server-side connection state and bridge forwarding for voltage terminals. */
public final class TerminalNetwork {

    private static final Map<UUID, BlockPos> selections = new HashMap<>();
    private static final ThreadLocal<Set<BlockPos>> TRANSFER_CONTEXT =
            ThreadLocal.withInitial(HashSet::new);

    private TerminalNetwork() {}

    public static Set<BlockPos> currentVisited() {
        return new HashSet<>(TRANSFER_CONTEXT.get());
    }

    public static boolean handleUse(Level level, BlockPos pos, Player player, ItemStack stack) {
        if (!(level.getBlockEntity(pos) instanceof VoltageTerminalBlockEntity)) {
            return false;
        }
        TerminalProperties.FineWireSpec wire = TerminalProperties.FineWireSpec.from(stack);
        // The server owns selection, linking and item consumption. Returning
        // success on the client still produces the normal hand-swing/success
        // feedback while avoiding a client-side duplicate mutation.
        if (level.isClientSide) {
            return wire != null || (player.isShiftKeyDown() && stack.isEmpty());
        }
        ServerLevel server = (ServerLevel) level;
        if (wire == null) {
            if (player.isShiftKeyDown() && stack.isEmpty()) {
                disconnectAll(server, pos);
                return true;
            }
            return false;
        }

        BlockPos selected = selections.get(player.getUUID());
        if (selected == null) {
            selections.put(player.getUUID(), pos.immutable());
            return true;
        }
        selections.remove(player.getUUID());
        if (selected.equals(pos)) return true;
        if (!(server.getBlockEntity(selected) instanceof VoltageTerminalBlockEntity first) ||
                !(server.getBlockEntity(pos) instanceof VoltageTerminalBlockEntity second)) {
            return true;
        }
        // A pair of endpoints can have only one physical link. Do not
        // disturb an existing link when the player clicks the pair again.
        if (first.getLinks().containsKey(pos) || second.getLinks().containsKey(selected)) {
            return true;
        }
        boolean firstAdded = first.addLink(pos, wire);
        boolean secondAdded = firstAdded && second.addLink(selected, wire);
        if (!firstAdded || !secondAdded) {
            if (firstAdded) first.removeLink(pos);
            return true;
        }
        if (!player.getAbilities().instabuild) stack.shrink(1);
        return true;
    }

    public static void disconnectAll(ServerLevel level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof VoltageTerminalBlockEntity terminal)) return;
        for (BlockPos other : terminal.getLinks().keySet().toArray(BlockPos[]::new)) {
            if (level.getBlockEntity(other) instanceof VoltageTerminalBlockEntity peer) {
                peer.removeLink(pos);
            }
            terminal.removeLink(other);
        }
    }

    public static long forward(Level level, VoltageTerminalBlockEntity source, long voltage, long amperage,
                               Set<BlockPos> visited) {
        if (!(level instanceof ServerLevel server) || voltage <= 0 || amperage <= 0 ||
                !visited.add(source.getBlockPos())) return 0;
        long remaining = amperage;
        for (Map.Entry<BlockPos, TerminalProperties.Link> entry : new HashMap<>(source.getLinks()).entrySet()) {
            if (remaining <= 0 || visited.contains(entry.getKey())) continue;
            TerminalProperties.Link link = entry.getValue();
            long linkVoltage = voltage - link.wire().loss(source.getBlockPos(), entry.getKey());
            if (linkVoltage <= 0) {
                continue;
            }
            long linkAmperage = remaining;
            if (!(server.getBlockEntity(entry.getKey()) instanceof VoltageTerminalBlockEntity peer)) continue;
            long transferVoltage = Math.min(linkVoltage, link.wire().voltage());
            long accepted = peer.acceptLinkedEnergy(level, transferVoltage, linkAmperage,
                    new HashSet<>(visited));
            if (accepted > 0) {
                applyLinkHeat(server, source.getBlockPos(), entry.getKey(), link.wire(), accepted, linkVoltage);
            }
            remaining -= accepted;
        }
        return amperage - remaining;
    }

    private static void disconnectPair(ServerLevel level, BlockPos firstPos, BlockPos secondPos) {
        if (level.getBlockEntity(firstPos) instanceof VoltageTerminalBlockEntity first) {
            first.removeLink(secondPos);
        }
        if (level.getBlockEntity(secondPos) instanceof VoltageTerminalBlockEntity second) {
            second.removeLink(firstPos);
        }
    }

    public static void disconnectLink(ServerLevel level, BlockPos firstPos, BlockPos secondPos) {
        disconnectPair(level, firstPos, secondPos);
    }

    private static void applyLinkHeat(ServerLevel level, BlockPos firstPos, BlockPos secondPos,
                                      TerminalProperties.FineWireSpec wire, long amperage, long voltage) {
        VoltageTerminalBlockEntity first = level.getBlockEntity(firstPos) instanceof VoltageTerminalBlockEntity value
                ? value : null;
        VoltageTerminalBlockEntity second = level.getBlockEntity(secondPos) instanceof VoltageTerminalBlockEntity value
                ? value : null;
        if (first == null || second == null) return;

        int heat = 0;
        int amperageDifference = (int) Math.min(Integer.MAX_VALUE,
                Math.max(0L, amperage - wire.amperage()));
        if (amperageDifference > 0) heat += amperageDifference * 40;

        int sourceTier = com.gregtechceu.gtceu.utils.GTUtil.getTierByVoltage(voltage);
        int wireTier = com.gregtechceu.gtceu.utils.GTUtil.getTierByVoltage(wire.voltage());
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
