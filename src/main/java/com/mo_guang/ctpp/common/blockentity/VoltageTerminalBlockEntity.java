package com.mo_guang.ctpp.common.blockentity;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import com.gregtechceu.gtceu.utils.ManagedFieldHolderMap;

import com.lowdragmc.lowdraglib.misc.SyncableMap;
import com.lowdragmc.lowdraglib.syncdata.IEnhancedManaged;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender;
import com.lowdragmc.lowdraglib.syncdata.blockentity.IAsyncAutoSyncBlockEntity;
import com.lowdragmc.lowdraglib.syncdata.blockentity.IAutoPersistBlockEntity;
import com.lowdragmc.lowdraglib.syncdata.field.FieldManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import com.mo_guang.ctpp.api.terminal.TerminalLinkState;
import com.mo_guang.ctpp.api.terminal.TerminalProperties;
import com.mo_guang.ctpp.common.block.VoltageTerminalBlock;
import com.mo_guang.ctpp.common.terminal.TerminalNetwork;
import com.mo_guang.ctpp.common.terminal.TerminalWireHazardManager;
import com.mo_guang.ctpp.config.MainConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VoltageTerminalBlockEntity extends BlockEntity implements IEnhancedManaged,
                                        IAsyncAutoSyncBlockEntity, IAutoPersistBlockEntity {

    static {
        ManagedFieldHolderMap.createManagedFieldHolder(VoltageTerminalBlockEntity.class);
    }

    @DescSynced
    @Persisted(key = "links")
    @RequireRerender
    private final SyncableMap<BlockPos, TerminalLinkState> links = new SyncableMap<>() {};
    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);
    private final IEnergyContainer energyContainer = new TerminalEnergyContainer();
    private final LazyOptional<IEnergyContainer> energyCapability = LazyOptional.of(() -> energyContainer);
    private boolean terminalWiresRegistered;

    public VoltageTerminalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return ManagedFieldHolderMap.getManagedFieldHolder(getClass());
    }

    @Override
    public FieldManagedStorage getSyncStorage() {
        return syncStorage;
    }

    @Override
    public FieldManagedStorage getRootStorage() {
        return syncStorage;
    }

    @Override
    public void onChanged() {
        if (level instanceof ServerLevel server) {
            server.getServer().execute(this::setChanged);
        }
    }

    @Override
    public void scheduleRenderUpdate() {
        if (level != null && level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
            requestModelDataUpdate();
        }
    }

    public Map<BlockPos, TerminalProperties.Link> getLinks() {
        Map<BlockPos, TerminalProperties.Link> result = new HashMap<>();
        links.forEach((pos, state) -> result.put(pos, state.toLink()));
        return Collections.unmodifiableMap(result);
    }

    public @Nullable TerminalProperties.Link getLink(BlockPos other) {
        TerminalLinkState state = links.get(other);
        return state == null ? null : state.toLink();
    }

    public boolean addLink(BlockPos other, TerminalProperties.FineWireSpec wire,
                           ItemStack wireItem, TerminalProperties.ConnectionType connectionType) {
        if (other.equals(worldPosition) || links.containsKey(other)) return false;
        TerminalLinkState state = new TerminalLinkState(other, wire, wireItem, connectionType);
        links.put(other.immutable(), state);
        if (level instanceof ServerLevel server) {
            TerminalWireHazardManager.get(server).register(worldPosition, other, state.toLink());
        }
        setChanged();
        return true;
    }

    public void removeLink(BlockPos other) {
        if (links.remove(other) != null) {
            if (level instanceof ServerLevel server) {
                TerminalWireHazardManager.get(server).remove(worldPosition, other);
            }
            setChanged();
        }
    }

    public void applyLinkHeat(BlockPos other, int amount) {
        TerminalLinkState state = links.get(other);
        if (state != null) {
            TerminalProperties.Link link = state.toLink();
            link.applyHeat(amount);
            links.put(other, state.withHeat(link.getTemperature(), link.getHeatQueue()));
            setChanged();
        }
    }

    public void setLinkHeat(BlockPos other, int temperature, int heatQueue) {
        TerminalLinkState state = links.get(other);
        if (state != null && (state.temperature() != temperature || state.heatQueue() != heatQueue)) {
            links.put(other, state.withHeat(temperature, heatQueue));
            setChanged();
        }
    }

    public void serverTick() {
        if (!(level instanceof ServerLevel server)) return;
        if (!terminalWiresRegistered) {
            links.forEach((other, state) -> TerminalWireHazardManager.get(server).register(worldPosition, other,
                    state.toLink()));
            terminalWiresRegistered = true;
        }
        for (Map.Entry<BlockPos, TerminalLinkState> entry : new HashMap<>(links).entrySet()) {
            TerminalLinkState state = entry.getValue();
            BlockPos other = entry.getKey();
            VoltageTerminalBlockEntity peer = server.getBlockEntity(other) instanceof VoltageTerminalBlockEntity value ?
                    value : null;
            // Both endpoints mirror the same link state. Tick it once using
            // the canonical (lexicographically smaller) endpoint.
            if (worldPosition.compareTo(other) >= 0) continue;
            TerminalProperties.Link link = state.toLink();
            if (link.tick()) {
                TerminalNetwork.disconnectLink(server, worldPosition, other);
                break;
            }
            setLinkHeat(other, link.getTemperature(), link.getHeatQueue());
            if (peer != null) {
                if (peer.getLink(worldPosition) != null) {
                    peer.setLinkHeat(worldPosition, link.getTemperature(), link.getHeatQueue());
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
        int range = MainConfig.INSTANCE == null ? 32 :
                MainConfig.INSTANCE.terminalConfig.terminalMaxConnectionRange;
        return new AABB(worldPosition).inflate(range);
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

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (energyCapability.isPresent()) {
            energyCapability.invalidate();
        }
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
