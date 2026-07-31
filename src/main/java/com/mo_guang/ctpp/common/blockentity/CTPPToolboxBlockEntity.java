package com.mo_guang.ctpp.common.blockentity;

import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import com.mo_guang.ctpp.common.block.CTPPToolboxBlock;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxBlockRegistry;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxSavedData;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxSounds;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxStackData;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class CTPPToolboxBlockEntity extends SmartBlockEntity implements Nameable {

    public final LerpedFloat lid = LerpedFloat.linear().startWithValue(0);
    public final LerpedFloat drawers = LerpedFloat.linear().startWithValue(0);
    private @Nullable UUID toolboxId;
    private @Nullable Component customName;
    private int openCount;
    private LazyOptional<IItemHandler> inventoryCapability = LazyOptional.empty();

    public CTPPToolboxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(10);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    @Override
    public void initialize() {
        super.initialize();
        CTPPToolboxBlockRegistry.add(this);
        inventoryCapability = LazyOptional.of(this::resolveInventory);
    }

    @Override
    public void invalidate() {
        CTPPToolboxBlockRegistry.remove(this);
        super.invalidate();
    }

    @Override
    public void lazyTick() {
        CTPPToolboxBlockRegistry.add(this);
        super.lazyTick();
    }

    @Override
    public void tick() {
        super.tick();
        if (level != null && level.isClientSide) tickAudio();
        lid.chase(openCount > 0 ? 1 : 0, 0.2f, LerpedFloat.Chaser.LINEAR);
        drawers.chase(openCount > 0 ? 1 : 0, 0.2f, LerpedFloat.Chaser.EXP);
        lid.tickChaser();
        drawers.tickChaser();
    }

    private void tickAudio() {
        if (lid.settled() && openCount > 0 && lid.getChaseTarget() == 0) {
            CTPPToolboxSounds.playOpenLocally(level, worldPosition);
        }
        if (lid.settled() && openCount == 0 && lid.getChaseTarget() == 1) {
            CTPPToolboxSounds.playCloseLocally(level, worldPosition);
        }
    }

    public UUID ensureToolboxId() {
        if (toolboxId == null && level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            toolboxId = CTPPToolboxSavedData.get(serverLevel).create(getColor()).id();
            setChanged();
        }
        return toolboxId;
    }

    public @Nullable UUID getToolboxId() {
        return toolboxId;
    }

    public void setToolboxId(UUID toolboxId) {
        this.toolboxId = toolboxId;
        setChanged();
        sendData();
    }

    public void startOpen() {
        openCount++;
        sendData();
    }

    public void stopOpen() {
        openCount = Math.max(0, openCount - 1);
        sendData();
    }

    public DyeColor getColor() {
        return getBlockState().getBlock() instanceof CTPPToolboxBlock block ? block.getColor() : DyeColor.BROWN;
    }

    public ItemStack getDisplayStack() {
        ItemStack stack = getBlockState().getBlock().asItem().getDefaultInstance();
        if (toolboxId != null) {
            stack.getOrCreateTag().putUUID(CTPPToolboxStackData.ID, toolboxId);
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                CTPPToolboxSavedData.Record record = CTPPToolboxSavedData.get(serverLevel).find(toolboxId);
                if (record != null) CTPPToolboxStackData.update(stack, record);
            }
        }
        if (customName != null) stack.setHoverName(customName);
        return stack;
    }

    public void setCustomName(@Nullable Component customName) {
        this.customName = customName;
        setChanged();
    }

    @Override
    public Component getName() {
        return getDisplayName();
    }

    @Override
    public Component getDisplayName() {
        return customName != null ? customName : getBlockState().getBlock().getName();
    }

    @Override
    public @Nullable Component getCustomName() {
        return customName;
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        toolboxId = tag.hasUUID("ToolboxId") ? tag.getUUID("ToolboxId") : null;
        if (clientPacket) openCount = tag.getInt("OpenCount");
        if (tag.contains("CustomName")) customName = Component.Serializer.fromJson(tag.getString("CustomName"));
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        if (toolboxId != null) tag.putUUID("ToolboxId", toolboxId);
        if (clientPacket) tag.putInt("OpenCount", openCount);
        if (customName != null) tag.putString("CustomName", Component.Serializer.toJson(customName));
        super.write(tag, clientPacket);
    }

    private IItemHandler resolveInventory() {
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            UUID id = ensureToolboxId();
            CTPPToolboxSavedData.Record record = id == null ? null : CTPPToolboxSavedData.get(serverLevel).find(id);
            if (record != null) return record.inventory();
        }
        return new net.minecraftforge.items.ItemStackHandler(0);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable net.minecraft.core.Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) return inventoryCapability.cast();
        return super.getCapability(capability, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryCapability.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        inventoryCapability = LazyOptional.of(this::resolveInventory);
    }
}
