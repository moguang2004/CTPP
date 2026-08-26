package com.mo_guang.ctpp.api.terminal;

import com.gregtechceu.gtceu.utils.ManagedFieldHolderMap;

import com.lowdragmc.lowdraglib.syncdata.IManaged;
import com.lowdragmc.lowdraglib.syncdata.IManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.FieldManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

/** Immutable managed representation of one terminal connection. */
public final class TerminalLinkState implements IManaged {

    static {
        ManagedFieldHolderMap.createManagedFieldHolder(TerminalLinkState.class);
    }

    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @DescSynced
    @Persisted
    private BlockPos other;
    @DescSynced
    @Persisted
    private long voltage;
    @DescSynced
    @Persisted
    private long amperage;
    @DescSynced
    @Persisted
    private int lossPerBlock;
    @DescSynced
    @Persisted
    private ItemStack wireItem;
    @DescSynced
    @Persisted
    private TerminalProperties.ConnectionType connectionType;
    @DescSynced
    @Persisted
    private int temperature;
    @DescSynced
    @Persisted
    private int heatQueue;

    public TerminalLinkState() {
        this.other = BlockPos.ZERO;
        this.wireItem = ItemStack.EMPTY;
        this.connectionType = TerminalProperties.ConnectionType.ONE;
        this.temperature = 293;
    }

    public TerminalLinkState(BlockPos other, TerminalProperties.FineWireSpec wire, ItemStack wireItem,
                             TerminalProperties.ConnectionType connectionType, int temperature, int heatQueue) {
        this.other = other.immutable();
        this.voltage = wire.voltage();
        this.amperage = wire.amperage();
        this.lossPerBlock = wire.lossPerBlock();
        this.wireItem = wireItem.isEmpty() ? ItemStack.EMPTY : wireItem.copyWithCount(1);
        this.connectionType = connectionType == null ? TerminalProperties.ConnectionType.ONE : connectionType;
        this.temperature = temperature;
        this.heatQueue = heatQueue;
    }

    public TerminalLinkState(BlockPos other, TerminalProperties.FineWireSpec wire, ItemStack wireItem,
                             TerminalProperties.ConnectionType connectionType) {
        this(other, wire, wireItem, connectionType, 293, 0);
    }

    public BlockPos other() {
        return other;
    }

    public TerminalProperties.FineWireSpec wire() {
        return new TerminalProperties.FineWireSpec(voltage, amperage, lossPerBlock);
    }

    public ItemStack wireItem() {
        return wireItem.copy();
    }

    public TerminalProperties.ConnectionType connectionType() {
        return connectionType;
    }

    public int temperature() {
        return temperature;
    }

    public int heatQueue() {
        return heatQueue;
    }

    public TerminalLinkState withHeat(int newTemperature, int newHeatQueue) {
        return new TerminalLinkState(other, wire(), wireItem, connectionType, newTemperature, newHeatQueue);
    }

    public TerminalProperties.Link toLink() {
        TerminalProperties.Link link = new TerminalProperties.Link(other, wire(), wireItem, connectionType);
        link.loadHeat(temperature, heatQueue);
        return link;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return ManagedFieldHolderMap.getManagedFieldHolder(getClass());
    }

    @Override
    public IManagedStorage getSyncStorage() {
        return syncStorage;
    }

    @Override
    public void onChanged() {
        // Child state is replaced atomically by its owning block entity.
    }
}
