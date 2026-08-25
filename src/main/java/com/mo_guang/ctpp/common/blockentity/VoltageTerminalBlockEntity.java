package com.mo_guang.ctpp.common.blockentity;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import com.mo_guang.ctpp.api.terminal.TerminalProperties;
import com.mo_guang.ctpp.common.block.VoltageTerminalBlock;
import com.mo_guang.ctpp.common.terminal.TerminalNetwork;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VoltageTerminalBlockEntity extends BlockEntity {

    private final Map<BlockPos, TerminalProperties.Link> links = new HashMap<>();
    private final IEnergyContainer energyContainer = new TerminalEnergyContainer();
    private final LazyOptional<IEnergyContainer> energyCapability = LazyOptional.of(() -> energyContainer);

    public VoltageTerminalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public Map<BlockPos, TerminalProperties.Link> getLinks() {
        return Collections.unmodifiableMap(links);
    }

    public @Nullable TerminalProperties.Link getLink(BlockPos other) {
        return links.get(other);
    }

    public boolean addLink(BlockPos other, TerminalProperties.FineWireSpec wire) {
        return addLink(other, wire, net.minecraft.world.item.ItemStack.EMPTY,
                TerminalProperties.ConnectionType.ONE);
    }

    public boolean addLink(BlockPos other, TerminalProperties.FineWireSpec wire,
                           net.minecraft.world.item.ItemStack wireItem,
                           TerminalProperties.ConnectionType connectionType) {
        if (other.equals(worldPosition) || links.containsKey(other)) return false;
        links.put(other.immutable(), new TerminalProperties.Link(other.immutable(), wire, wireItem, connectionType));
        setChanged();
        syncClients();
        return true;
    }

    public void removeLink(BlockPos other) {
        if (links.remove(other) != null) {
            setChanged();
            syncClients();
        }
    }

    public void applyLinkHeat(BlockPos other, int amount) {
        TerminalProperties.Link link = links.get(other);
        if (link != null) {
            link.applyHeat(amount);
            setChanged();
        }
    }

    public void serverTick() {
        if (!(level instanceof ServerLevel server)) return;
        for (Map.Entry<BlockPos, TerminalProperties.Link> entry : new HashMap<>(links).entrySet()) {
            // Both endpoints mirror the same link state. Tick it once using
            // the canonical (lexicographically smaller) endpoint.
            if (worldPosition.compareTo(entry.getKey()) >= 0) continue;
            if (entry.getValue().tick()) {
                TerminalNetwork.disconnectLink(server, worldPosition, entry.getKey());
                break;
            }
            VoltageTerminalBlockEntity peer = server
                    .getBlockEntity(entry.getKey()) instanceof VoltageTerminalBlockEntity value ? value : null;
            if (peer != null) {
                TerminalProperties.Link peerLink = peer.links.get(worldPosition);
                if (peerLink != null) {
                    peerLink.loadHeat(entry.getValue().getTemperature(), entry.getValue().getHeatQueue());
                    peer.setChanged();
                }
            }
        }
    }

    public long getVoltageLimit() {
        return ((VoltageTerminalBlock) getBlockState().getBlock()).getVoltage();
    }

    public Direction getElectricalSide() {
        // FACING points away from the supporting block. The electrical face
        // is the opposite side where a cable or machine is attached.
        return getBlockState().getValue(VoltageTerminalBlock.FACING).getOpposite();
    }

    @Override
    public AABB getRenderBoundingBox() {
        int range = com.mo_guang.ctpp.config.MainConfig.INSTANCE == null ? 32 :
                com.mo_guang.ctpp.config.MainConfig.INSTANCE.terminalConfig.terminalMaxConnectionRange;
        return new AABB(worldPosition).inflate(range);
    }

    public long forwardIntoAttached(net.minecraft.world.level.Level level, long voltage, long amperage,
                                    Set<BlockPos> visited) {
        return TerminalNetwork.forwardToAttached(level, this, voltage, amperage, visited);
    }

    public long acceptLinkedEnergy(net.minecraft.world.level.Level level, long voltage, long amperage,
                                   Set<BlockPos> visited) {
        if (voltage > getVoltageLimit()) {
            burn();
            return 0;
        }
        long linked = TerminalNetwork.forward(level, this, voltage, amperage, visited);
        long attached = linked >= amperage ? 0 : TerminalNetwork.forwardToAttached(level, this, voltage,
                amperage - linked, visited);
        return linked + attached;
    }

    public void burn() {
        if (!(level instanceof ServerLevel server)) return;
        TerminalNetwork.disconnectAllNoDrop(server, worldPosition);
        server.setBlockAndUpdate(worldPosition, Blocks.FIRE.defaultBlockState());
    }

    private void syncClients() {
        if (level instanceof ServerLevel server) {
            server.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void setRemoved() {
        if (level instanceof ServerLevel server) TerminalNetwork.disconnectAllNoDrop(server, worldPosition);
        energyCapability.invalidate();
        super.setRemoved();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        links.clear();
        ListTag savedLinks = tag.getList("Links", Tag.TAG_COMPOUND);
        for (int i = 0; i < savedLinks.size(); i++) {
            CompoundTag link = savedLinks.getCompound(i);
            BlockPos other = BlockPos.of(link.getLong("pos"));
            net.minecraft.world.item.ItemStack wireItem = link.contains("wireItem", Tag.TAG_COMPOUND) ?
                    net.minecraft.world.item.ItemStack.of(link.getCompound("wireItem")) :
                    net.minecraft.world.item.ItemStack.EMPTY;
            TerminalProperties.ConnectionType connectionType = TerminalProperties.ConnectionType
                    .fromMultiplier(link.getInt("multiplier"));
            links.put(other, new TerminalProperties.Link(other,
                    new TerminalProperties.FineWireSpec(link.getLong("voltage"), link.getLong("amperage"),
                            link.getInt("loss")),
                    wireItem, connectionType));
            links.get(other).loadHeat(link.getInt("temperature"), link.getInt("heatQueue"));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet) {
        super.onDataPacket(connection, packet);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag savedLinks = new ListTag();
        for (TerminalProperties.Link value : links.values()) {
            CompoundTag link = new CompoundTag();
            link.putLong("pos", value.other().asLong());
            link.putLong("voltage", value.wire().voltage());
            link.putLong("amperage", value.wire().amperage());
            link.putInt("loss", value.wire().lossPerBlock());
            if (!value.wireItem().isEmpty()) {
                CompoundTag wireItem = new CompoundTag();
                value.wireItem().save(wireItem);
                link.put("wireItem", wireItem);
            }
            link.putInt("multiplier", value.connectionType().multiplier());
            link.putInt("temperature", value.getTemperature());
            link.putInt("heatQueue", value.getHeatQueue());
            savedLinks.add(link);
        }
        tag.put("Links", savedLinks);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (capability == GTCapability.CAPABILITY_ENERGY_CONTAINER &&
                side == getElectricalSide()) {
            return GTCapability.CAPABILITY_ENERGY_CONTAINER.orEmpty(capability, energyCapability);
        }
        return super.getCapability(capability, side);
    }

    private final class TerminalEnergyContainer implements IEnergyContainer {

        @Override
        public long acceptEnergyFromNetwork(Direction side, long voltage, long amperage) {
            if (side != null && side != getElectricalSide()) return 0;
            if (voltage > getVoltageLimit()) {
                burn();
                return 0;
            }
            return TerminalNetwork.forward(level, VoltageTerminalBlockEntity.this, voltage, amperage,
                    TerminalNetwork.currentVisited());
        }

        @Override
        public boolean inputsEnergy(Direction side) {
            return side == getElectricalSide();
        }

        @Override
        public long changeEnergy(long amount) {
            return 0;
        }

        @Override
        public long getEnergyStored() {
            return 0;
        }

        @Override
        public long getEnergyCapacity() {
            return Long.MAX_VALUE;
        }

        @Override
        public long getInputAmperage() {
            return Long.MAX_VALUE;
        }

        @Override
        public long getInputVoltage() {
            return getVoltageLimit();
        }
    }
}
