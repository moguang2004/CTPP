package com.mo_guang.ctpp.common.blockentity;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import com.mo_guang.ctpp.common.block.MagnetBlock;
import com.mo_guang.ctpp.config.MainConfig;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

import java.util.ArrayList;
import java.util.List;

public class GeneratorCoilBlockEntity extends KineticBlockEntity {

    private final List<BlockPos> magnetPositions = new ArrayList<>();
    private float plainStress;
    private float lastStress;
    private int generatedEnergy;
    private float efficiency;

    public GeneratorCoilBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        magnetPositions.addAll(getMagnetPositions(pos, state.getValue(RotatedPillarKineticBlock.AXIS)));
        setLazyTickRate(20);
    }

    @CN("发电效率：%d%%")
    @EN("Generator Efficiency: %d%%")
    static Lang efficiency_tooltip;

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.text(efficiency_tooltip.translate(String.format("%.2f", efficiency * 100)).getString()).style(ChatFormatting.AQUA).forGoggles(tooltip);
        return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(worldPosition).inflate(1);
    }

    @Override
    public float calculateStressApplied() {
        plainStress = 12;
        float stress = plainStress;

        if (level != null) {
            for (BlockPos magnetPosition : magnetPositions) {
                stress += MagnetBlock.getStrength(level.getBlockState(magnetPosition));
            }
        }

        lastStressApplied = stress;
        return stress;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;

        efficiency = (lastStressApplied - plainStress) / lastStressApplied;
        generatedEnergy = (int) ((lastStressApplied - plainStress) * Math.abs(getSpeed()) *
                MainConfig.INSTANCE.ctnhConfig.carbonBrushesSuToEnergy);
    }

    @Override
    public void lazyTick() {
        if (level == null || level.isClientSide) return;

        float stress = calculateStressApplied();
        var network = getOrCreateNetwork();
        if (network != null && lastStress != stress) {
            network.updateStressFor(this, stress);
            network.updateStress();
            sendData();
            lastStress = stress;
        }
    }

    public int takeGeneratedEnergy() {
        int energy = generatedEnergy;
        generatedEnergy = 0;
        return energy;
    }

    public List<BlockPos> getMagnetPositions() {
        return magnetPositions;
    }

    public static List<BlockPos> getMagnetPositions(BlockPos coilPos, Direction.Axis axis) {
        List<BlockPos> positions = new ArrayList<>(12);
        Direction up = axis == Direction.Axis.Y ? Direction.SOUTH : Direction.UP;
        Direction right = axis == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;
        Direction left = right.getOpposite();
        Direction down = up.getOpposite();

        addMagnetPosition(positions, coilPos, up, 2, right, 1);
        addMagnetPosition(positions, coilPos, up, 2);
        addMagnetPosition(positions, coilPos, up, 2, left, 1);
        addMagnetPosition(positions, coilPos, left, 2, up, 1);
        addMagnetPosition(positions, coilPos, left, 2);
        addMagnetPosition(positions, coilPos, left, 2, down, 1);
        addMagnetPosition(positions, coilPos, down, 2, left, 1);
        addMagnetPosition(positions, coilPos, down, 2);
        addMagnetPosition(positions, coilPos, down, 2, right, 1);
        addMagnetPosition(positions, coilPos, right, 2, down, 1);
        addMagnetPosition(positions, coilPos, right, 2);
        addMagnetPosition(positions, coilPos, right, 2, up, 1);
        return positions;
    }

    private static void addMagnetPosition(List<BlockPos> positions, BlockPos coilPos,
                                          Direction direction, int distance) {
        positions.add(coilPos.relative(direction, distance));
    }

    private static void addMagnetPosition(List<BlockPos> positions, BlockPos coilPos,
                                          Direction first, int firstDistance,
                                          Direction second, int secondDistance) {
        positions.add(coilPos.relative(first, firstDistance).relative(second, secondDistance));
    }
}
