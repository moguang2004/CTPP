package com.mo_guang.ctpp.common.machine.simple;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.TieredEnergyMachine;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.IMachineLife;
import com.gregtechceu.gtceu.api.machine.feature.IMachineModifyDrops;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.gregtechceu.gtceu.common.data.GTItems;

import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.ProgressWidget;
import com.lowdragmc.lowdraglib.gui.widget.SwitchWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.ctnhlang.Key;
import com.mo_guang.ctpp.common.beam.EmitterBeamTracker;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

import java.util.List;

/**
 * A placeable GregTech emitter: accepts EU from its back face, and shoots a beam from its front face
 * that transfers energy to the block it hits. Port of greg-emitters semantics onto the GT machine API.
 * Beam rendering is global (see {@link EmitterBeamTracker}), independent of the emitter's visibility.
 */
public class PlaceableEmitterMachine extends TieredEnergyMachine
                                     implements IFancyUIMachine, IMachineLife, IMachineModifyDrops {

    /** Max emission current, in amps; the UI slider sets the actual value and caps everything the beam does. */
    public static final int MAX_CONSUMPTION = 4;
    /** Beam damage per voltage tier. Placeholder; adjust freely. */
    public static final float DAMAGE_PER_TIER = 2.0f;
    private static final double MAX_BEND = Math.PI / 2;

    /** Angle from the default direction, in radians. Limited to 90 degrees. */
    @Persisted
    @DescSynced
    @Getter
    public double zenith;

    /** Rotation around the default direction, in radians. */
    @Persisted
    @DescSynced
    @Getter
    public double azimuth;

    @Persisted
    @DescSynced
    @Getter
    public boolean transferDisabled;

    /** Emission current the beam fires with (UI-adjustable, 1..MAX_CONSUMPTION): the max output amperage. */
    @Persisted
    @DescSynced
    @Getter
    public int consumptionAmps = 1;

    private int beamId = -1;

    // last state sent to the tracker, to avoid re-sending identical packets
    private Vec3 sentDirection = Vec3.ZERO;
    private long sentVoltage = -1;
    private long sentAmps = -1;
    private double sentDistance = -1;

    @Nullable
    protected TickableSubscription beamSubs;

    public PlaceableEmitterMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
        energyContainer.setSideInputCondition(side -> side != getFrontFacing());
    }

    @Override
    protected NotifiableEnergyContainer createEnergyContainer(Object... args) {
        long voltage = GTValues.V[getTier()];
        return new NotifiableEnergyContainer(this, voltage * 64 * MAX_CONSUMPTION, voltage, MAX_CONSUMPTION, 0, 0);
    }

    //////////////////////////////////////
    // ******** Beam tick **********//
    //////////////////////////////////////
    @Override
    public void onLoad() {
        super.onLoad();
        if (!isRemote()) beamSubs = subscribeServerTick(this::beamTick);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (beamSubs != null) {
            beamSubs.unsubscribe();
            beamSubs = null;
        }
        removeBeam();
    }

    @Override
    public void onMachineRemoved() {
        removeBeam();
    }

    public void beamTick() {
        if (!(getLevel() instanceof ServerLevel level)) return;
        long stored = energyContainer.getEnergyStored();
        if (stored <= 0 || transferDisabled) {
            removeBeam(level);
            return;
        }

        // an emitter always fires at its own tier voltage, regardless of the feeding network's
        // voltage: beam color bands then reliably show this emitter's tier decaying with distance
        long voltage = GTValues.V[getTier()];
        // emission current: the UI setting IS the max output amperage by design; storage decides
        // how much of it can actually flow this tick. The buffer bursts like any GT machine, so a
        // starving supply thins the beam (via stored/voltage) and the slider thickens it directly.
        long amps = Math.min(consumptionAmps, stored / voltage);
        if (amps <= 0) {
            energyContainer.removeEnergy(stored); // not enough for one packet; drain the rest
            removeBeam(level);
            return;
        }

        Vec3 direction = beamDirection();
        Vec3 origin = getPos().getCenter().add(direction.scale(0.51));
        Vec3 rayEnd = origin.add(direction.scale(128));
        BlockHitResult hit = level.clip(new ClipContext(origin, rayEnd,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
        boolean hitBlock = hit.getType() == HitResult.Type.BLOCK;
        double beamDistance = hitBlock ? Math.max(1, origin.distanceTo(hit.getLocation())) : 128;
        Vec3 clipEnd = origin.add(direction.scale(beamDistance));

        // the beam also hits entities: nearest living entity along the ray gets damaged
        net.minecraft.world.entity.LivingEntity hitEntity = null;
        var searchBox = new net.minecraft.world.phys.AABB(origin, clipEnd).inflate(1.5);
        for (var entity : level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, searchBox)) {
            var clip = entity.getBoundingBox().inflate(0.25).clip(origin, clipEnd);
            if (clip.isPresent()) {
                double d = origin.distanceTo(clip.get());
                if (d < beamDistance) {
                    beamDistance = d;
                    hitEntity = entity;
                }
            }
        }

        if (hitEntity != null) {
            // burn the emission current; damage scales with voltage tier
            energyContainer.removeEnergy(amps * voltage);
            hitEntity.hurt(level.damageSources().magic(), DAMAGE_PER_TIER * getTier());
            updateBeam(level, direction, voltage, amps, beamDistance);
            return;
        }

        if (hitBlock) {
            BlockPos target = hit.getBlockPos();
            if (!target.equals(getPos())) {
                Direction side = hit.getDirection();
                // the beam's voltage decays with travel distance; the target only receives what arrives
                long effectiveVoltage = Math.max(1, Math.round(voltageAt(voltage, getTier(), beamDistance)));
                IEnergyContainer container = GTCapabilityHelper.getEnergyContainer(level, target, side);
                if (container != null && container.inputsEnergy(side)) {
                    long accepted = container.acceptEnergyFromNetwork(side, effectiveVoltage, amps);
                    if (accepted > 0) {
                        // voltage decay is a real loss in transit: the emitter pays the full input
                        // voltage for every accepted ampere; amperage itself is never lost.
                        energyContainer.removeEnergy(accepted * voltage);
                        updateBeam(level, direction, voltage, accepted, beamDistance);
                        return;
                    }
                }
            }
        }

        // no usable target: always burn the emission current anyway
        energyContainer.removeEnergy(amps * voltage);
        updateBeam(level, direction, voltage, amps, beamDistance);
    }

    private void updateBeam(ServerLevel level, Vec3 direction, long voltage, long amps, double distance) {
        if (beamId < 0) beamId = EmitterBeamTracker.newBeamId();
        distance = Math.max(1, Math.round(distance * 2) / 2.0);
        if (direction.equals(sentDirection) && voltage == sentVoltage && amps == sentAmps && distance == sentDistance)
            return;
        sentDirection = direction;
        sentVoltage = voltage;
        sentAmps = amps;
        sentDistance = distance;
        EmitterBeamTracker.setBeam(level, beamId, getPos(), direction, voltage, amps, getTier(), distance);
    }

    private void removeBeam(ServerLevel level) {
        if (beamId < 0) return;
        EmitterBeamTracker.removeBeam(level, beamId);
        beamId = -1;
        sentDirection = Vec3.ZERO;
        sentVoltage = -1;
        sentAmps = -1;
        sentDistance = -1;
    }

    private void removeBeam() {
        if (getLevel() instanceof ServerLevel serverLevel) removeBeam(serverLevel);
    }

    //////////////////////////////////////
    // ******** Direction **********//
    //////////////////////////////////////
    public Vec3 beamDirection() {
        Direction facing = getFrontFacing();
        Vec3 base = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());
        if (zenith == 0 && azimuth == 0) return base;
        Vec3 up = Math.abs(base.y) > 0.9 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        Vec3 side = base.cross(up).normalize();
        Vec3 vertical = side.cross(base).normalize();
        double sinZenith = Math.sin(zenith);
        return base.scale(Math.cos(zenith))
                .add(side.scale(Math.sin(azimuth) * sinZenith))
                .add(vertical.scale(Math.cos(azimuth) * sinZenith))
                .normalize();
    }

    public void adjustAngle(double zenithDelta, double azimuthDelta) {
        zenith = Mth.clamp(zenith + zenithDelta, -MAX_BEND, MAX_BEND);
        azimuth = (azimuth + azimuthDelta) % (Math.PI * 2);
        if (azimuth < 0) azimuth += Math.PI * 2;
        markDirty();
    }

    public void setAngles(double zenith, double azimuth) {
        this.zenith = Mth.clamp(zenith, -MAX_BEND, MAX_BEND);
        azimuth %= Math.PI * 2;
        this.azimuth = azimuth < 0 ? azimuth + Math.PI * 2 : azimuth;
        markDirty();
    }

    public void setTransferDisabled(boolean disabled) {
        transferDisabled = disabled;
        markDirty();
    }

    public void adjustConsumption(int delta) {
        consumptionAmps = Mth.clamp(consumptionAmps + delta, 1, MAX_CONSUMPTION);
        markDirty();
    }

    public boolean isBeamActive() {
        return !transferDisabled && energyContainer.getEnergyStored() > 0;
    }

    //////////////////////////////////////
    // ******** Beam appearance **********//
    //////////////////////////////////////
    /**
     * Percent voltage loss per block of beam travel. Lower tiers decay much faster (percent-wise)
     * than higher tiers. Placeholder numbers; tweak freely.
     */
    public static double lossPerBlock(int tier) {
        return 0.5 / Math.pow(2, tier - 1); // LV: 2% / block, halving every tier
    }

    /** Effective beam voltage after traveling {@code distance} blocks from {@code baseVoltage}. */
    public static double voltageAt(long baseVoltage, int tier, double distance) {
        return baseVoltage * Math.pow(1 - lossPerBlock(tier), distance);
    }

    /** Continuous spectrum position across GT tier main colors (VCM) for an arbitrary voltage. */
    public static int colorForVoltage(double voltage) {
        // GT voltages grow by 4x per tier (V[t] = 8 * 4^t), so the spectrum position is log4-based;
        // exact tier voltages land exactly on their tier color.
        double x = Math.log(Math.max(voltage, 1) / GTValues.V[0]) / Math.log(4);
        x = Mth.clamp(x, 0, GTValues.VCM.length - 1.001);
        int lo = (int) Math.floor(x);
        int hi = Math.min(lo + 1, GTValues.VCM.length - 1);
        float t = (float) (x - lo);
        int c1 = GTValues.VCM[lo];
        int c2 = GTValues.VCM[hi];
        int r = Mth.lerpInt(t, c1 >> 16 & 255, c2 >> 16 & 255);
        int g = Mth.lerpInt(t, c1 >> 8 & 255, c2 >> 8 & 255);
        int b = Mth.lerpInt(t, c1 & 255, c2 & 255);
        return r << 16 | g << 8 | b;
    }

    //////////////////////////////////////
    // ******** Drops **********//
    //////////////////////////////////////
    /** The vanilla GT emitter item corresponding to a tier; placed emitters drop this, not the machine item. */
    public static ItemStack emitterItem(int tier) {
        return switch (tier) {
            case 1 -> GTItems.EMITTER_LV.asStack();
            case 2 -> GTItems.EMITTER_MV.asStack();
            case 3 -> GTItems.EMITTER_HV.asStack();
            case 4 -> GTItems.EMITTER_EV.asStack();
            case 5 -> GTItems.EMITTER_IV.asStack();
            case 6 -> GTItems.EMITTER_LuV.asStack();
            case 7 -> GTItems.EMITTER_ZPM.asStack();
            case 8 -> GTItems.EMITTER_UV.asStack();
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public void onDrops(List<ItemStack> drops) {
        // breaking a placed emitter returns the vanilla GT emitter item used to place it
        drops.clear();
        var stack = emitterItem(getTier());
        if (!stack.isEmpty()) drops.add(stack);
    }

    //////////////////////////////////////
    // ******** GUI **********//
    //////////////////////////////////////
    @Key("ctnh.placeable_emitter.consumption")
    @CN("发射电流")
    @EN("Current")
    public static Lang consumptionLabel;

    @Key("ctnh.placeable_emitter.transfer_off")
    @CN("传输关")
    @EN("Off")
    public static Lang transferOffLabel;

    @Key("ctnh.placeable_emitter.transfer_on")
    @CN("传输开")
    @EN("On")
    public static Lang transferOnLabel;

    @Override
    public Widget createUIWidget() {
        var group = new WidgetGroup(0, 0, 140, 172);
        group.addWidget(new LabelWidget(5, 5, getBlockState().getBlock().getDescriptionId()));
        group.addWidget(new LabelWidget(5, 17,
                () -> String.format("%.1f°, %.1f°", Math.toDegrees(zenith), Math.toDegrees(azimuth))));
        group.addWidget(new ProgressWidget(
                () -> energyContainer.getEnergyStored() * 1d / energyContainer.getEnergyCapacity(), 5, 30, 18, 100,
                new ProgressTexture(IGuiTexture.EMPTY, GuiTextures.ENERGY_BAR_BASE))
                .setFillDirection(ProgressTexture.FillDirection.DOWN_TO_UP)
                .setBackground(GuiTextures.ENERGY_BAR_BACKGROUND));
        // polar angle dial (greg-emitters style): drag the red handle to aim
        group.addWidget(new com.mo_guang.ctpp.common.gui.widget.EmitterAngleDialWidget(this, 22, 30, 48));
        // cardinal labels around the dial (Minecraft yaw convention)
        group.addWidget(new LabelWidget(70, 32, "W"));
        group.addWidget(new LabelWidget(122, 78, "N"));
        group.addWidget(new LabelWidget(70, 124, "E"));
        group.addWidget(new LabelWidget(20, 78, "S"));
        // always-on consumption control (1..MAX_CONSUMPTION amps)
        group.addWidget(new LabelWidget(5, 140, consumptionLabel.translate()));
        group.addWidget(new ButtonWidget(5, 150, 20, 20, new TextTexture("-"),
                data -> adjustConsumption(-1)));
        group.addWidget(new LabelWidget(30, 156, () -> consumptionAmps + " A"));
        group.addWidget(new ButtonWidget(50, 150, 20, 20, new TextTexture("+"),
                data -> adjustConsumption(1)));
        group.addWidget(new SwitchWidget(85, 146, 52, 22, (clickData, value) -> setTransferDisabled(!value))
                .setTexture(
                        new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON,
                                new TextTexture(transferOffLabel.translate().getString())),
                        new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON,
                                new TextTexture(transferOnLabel.translate().getString())))
                .setPressed(!transferDisabled));
        return group;
    }
}
