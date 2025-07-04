package com.mo_guang.ctpp.common.blockentity;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.common.registry.GTRegistration;
import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.managed.MultiManagedStorage;
import com.mo_guang.ctpp.api.IBlockStressValues;
import com.mo_guang.ctpp.common.machine.KineticMachineDefinition;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticEffectHandler;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

public class KineticMachineBlockEntity extends KineticBlockEntity implements IMachineBlockEntity {

    public final MultiManagedStorage managedStorage = new MultiManagedStorage();
    @Getter
    public final MetaMachine metaMachine;
    private final long offset = GTValues.RNG.nextInt(20);
    public float workingSpeed;
    public boolean reActivateSource;
    @DescSynced
    private UUID owner;
    @Getter
    @DescSynced
    private String ownerName;
    private Class<?> ownerType;

    protected KineticMachineBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        this.metaMachine = getDefinition().createMetaMachine(this);
    }

    public static KineticMachineBlockEntity create(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        return new KineticMachineBlockEntity(typeIn, pos, state);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        var result = MetaMachineBlockEntity.getCapability(getMetaMachine(), cap, side);
        return result == null ? super.getCapability(cap, side) : result;
    }

    public static void onBlockEntityRegister(BlockEntityType blockEntityType,
                                             NonNullSupplier<SimpleBlockEntityVisualizer.Factory<? extends KineticBlockEntity>> visualFactory,
                                             boolean renderNormally) {
        if (visualFactory != null && LDLib.isClient()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> OneTimeEventReceiver.addModListener(GTRegistration.REGISTRATE,
                            FMLClientSetupEvent.class,
                            ($) -> SimpleBlockEntityVisualizer.builder(blockEntityType)
                                    .factory(visualFactory.get())
                                    .skipVanillaRender((be) -> !renderNormally)
                                    .apply()));
        }
    }

    @Override
    public KineticMachineDefinition getDefinition() {
        return (KineticMachineDefinition) IMachineBlockEntity.super.getDefinition();
    }

    @Override
    public KineticMachineBlockEntity self() {
        return this;
    }

    @Override
    public boolean triggerEvent(int id, int para) {
        if (id == 1) { // chunk re render
            if (level != null && level.isClientSide) {
                scheduleRenderUpdate();
            }
            return true;
        }
        return false;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public MultiManagedStorage getRootStorage() {
        return managedStorage;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        metaMachine.onUnload();
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        metaMachine.onLoad();
    }

    @Override
    public boolean shouldRenderGrid(Player player, BlockPos pos, BlockState state, ItemStack held,
                                    Set<GTToolType> toolTypes) {
        return metaMachine.shouldRenderGrid(player, pos, state, held, toolTypes);
    }

    @Override
    public ResourceTexture sideTips(Player player, BlockPos pos, BlockState state, Set<GTToolType> toolTypes,
                                    Direction side) {
        return metaMachine.sideTips(player, pos, state, toolTypes, side);
    }

    //////////////////////////////////////
    // ********* Create *********//
    //////////////////////////////////////


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return super.getCapability(cap);
    }

    public KineticEffectHandler getEffects() {
        return effects;
    }

    public float scheduleWorking(float su, boolean simulate) {
        if (getDefinition().isSource()) {
            float speed = Math.min(AllConfigs.server().kinetics.maxRotationSpeed.get(), su / getDefinition().getTorque());
            if (!simulate) {
                workingSpeed = speed;
                updateGeneratedRotation();
            }
            return speed * getDefinition().getTorque();
        }
        return 0;
    }

    public void scheduleWorking(float su) {
        scheduleWorking(su, false);
    }

    public void stopWorking() {
        if (getDefinition().isSource() && getGeneratedSpeed() != 0) {
            workingSpeed = 0;
            updateGeneratedRotation();
        }
    }

    @Override
    public float getGeneratedSpeed() {
        return workingSpeed;
    }

    protected void notifyStressCapacityChange(float capacity) {
        this.getOrCreateNetwork().updateCapacityFor(this, capacity);
    }

    public void removeSource() {
        if (getDefinition().isSource() && this.hasSource() && this.isSource()) {
            this.reActivateSource = true;
        }
        super.removeSource();
    }

    public void setSource(BlockPos source) {
        super.setSource(source);
        if (!getDefinition().isSource()) return;
        BlockEntity tileEntity = this.level.getBlockEntity(source);
        if (tileEntity instanceof KineticBlockEntity sourceTe) {
            if (this.reActivateSource && Math.abs(sourceTe.getSpeed()) >= Math.abs(this.getGeneratedSpeed())) {
                this.reActivateSource = false;
            }
        }
    }

    public void tick() {
        super.tick();
        if (getDefinition().isSource() && this.reActivateSource) {
            this.updateGeneratedRotation();
            this.reActivateSource = false;
        }
    }

    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        float stressBase = this.calculateAddedStressCapacity();
        if (stressBase != 0.0F && IRotate.StressImpact.isEnabled()) {
            CreateLang.translate("gui.goggles.kinetic_stats").forGoggles(tooltip);
            CreateLang.translate("tooltip.stressImpact").style(ChatFormatting.GRAY).forGoggles(tooltip);
            float speed = this.getTheoreticalSpeed();
            if (speed != this.getGeneratedSpeed() && speed != 0.0F) {
                stressBase *= this.getGeneratedSpeed() / speed;
            }

            speed = Math.abs(speed);
            float stressTotal = stressBase * speed;
            CreateLang.number(stressTotal).translate("generic.unit.stress").style(ChatFormatting.AQUA).space()
                    .add(CreateLang.translate("gui.goggles.at_current_speed").style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);
            added = true;
        }

        return added;
    }

    public void updateGeneratedRotation() {
        if (!getDefinition().isSource()) return;
        float speed = this.getGeneratedSpeed();
        float prevSpeed = this.speed;
        if (!this.level.isClientSide) {
            if (prevSpeed != speed) {
                if (!this.hasSource()) {
                    IRotate.SpeedLevel levelBefore = IRotate.SpeedLevel.of(this.speed);
                    IRotate.SpeedLevel levelafter = IRotate.SpeedLevel.of(speed);
                    if (levelBefore != levelafter) {
                        this.effects.queueRotationIndicators();
                    }
                }

                this.applyNewSpeed(prevSpeed, speed);
            }

            if (this.hasNetwork() && speed != 0.0F) {
                KineticNetwork network = this.getOrCreateNetwork();
                this.notifyStressCapacityChange(this.calculateAddedStressCapacity());
                this.getOrCreateNetwork().updateStressFor(this, this.calculateStressApplied());
                network.updateStress();
            }

            this.onSpeedChanged(prevSpeed);
            this.sendData();
        }
    }
    @Override
    public float calculateStressApplied() {
        float impact = (float) IBlockStressValues.getImpact(this.getStressConfigKey());
        this.lastStressApplied = impact;
        return impact;
    }
    @Override
    public float calculateAddedStressCapacity() {
        float capacity = (float)IBlockStressValues.getCapacity(this.getStressConfigKey());
        this.lastCapacityProvided = capacity;
        return capacity;
    }

    public void applyNewSpeed(float prevSpeed, float speed) {
        if (speed == 0.0F) {
            if (this.hasSource()) {
                this.notifyStressCapacityChange(0.0F);
                this.getOrCreateNetwork().updateStressFor(this, this.calculateStressApplied());
            } else {
                this.detachKinetics();
                this.setSpeed(0.0F);
                this.setNetwork(null);
            }
        } else if (prevSpeed == 0.0F) {
            this.setSpeed(speed);
            this.setNetwork(this.createNetworkId());
            this.attachKinetics();
        } else if (this.hasSource()) {
            if (Math.abs(prevSpeed) >= Math.abs(speed)) {
                if (Math.signum(prevSpeed) != Math.signum(speed)) {
                    this.level.destroyBlock(this.worldPosition, true);
                }
            } else {
                this.detachKinetics();
                this.setSpeed(speed);
                this.source = null;
                this.setNetwork(this.createNetworkId());
                this.attachKinetics();
            }
        } else {
            this.detachKinetics();
            this.setSpeed(speed);
            this.attachKinetics();
        }
    }

    public Long createNetworkId() {
        return this.worldPosition.asLong();
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putFloat("workingSpeed", workingSpeed);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        workingSpeed = compound.contains("workingSpeed") ? compound.getFloat("workingSpeed") : 0;
    }
}
