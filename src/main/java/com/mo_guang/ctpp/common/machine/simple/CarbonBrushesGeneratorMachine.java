package com.mo_guang.ctpp.common.machine.simple;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.WorkableTieredMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.mo_guang.ctpp.common.machine.IKineticMachine;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.utility.CreateLang;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.antarcticgardens.newage.content.generation.generatorcoil.GeneratorCoilBlock;
import org.antarcticgardens.newage.content.generation.generatorcoil.GeneratorCoilBlockEntity;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;

public class CarbonBrushesGeneratorMachine extends WorkableTieredMachine implements IKineticMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            CarbonBrushesGeneratorMachine.class,
            WorkableTieredMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    int lastOutput = 0;

    protected TickableSubscription generatorSub;

    public CarbonBrushesGeneratorMachine(IMachineBlockEntity holder, int tier, Int2IntFunction tankScalingFunction,
                                         Object... args) {
        super(holder, tier, tankScalingFunction, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        generatorSub = subscribeServerTick(this::tick);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (generatorSub != null) {
            generatorSub.unsubscribe();
            generatorSub = null;
        }
    }

    @Override
    protected NotifiableEnergyContainer createEnergyContainer(Object... args) {
        var energyContainer = super.createEnergyContainer(args);
        energyContainer
                .setSideOutputCondition(side -> side != getFrontFacing() && side != getFrontFacing().getOpposite());
        return energyContainer;
    }

    @Override
    protected boolean isEnergyEmitter() {
        return true;
    }

    @Override
    protected long getMaxInputOutputAmperage() {
        return 16;
    }

    public void tick() {
        if (getLevel() == null || getLevel().isClientSide)
            return;

        Direction facing = getBlockState().getValue(DirectionalKineticBlock.FACING);

        MutableInt coilsLeft = new MutableInt(18);

        int output = 0;
        output += processCoil(getPos(), facing, coilsLeft);
        output += processCoil(getPos(), facing.getOpposite(), coilsLeft);

        this.lastOutput = output;

        energyContainer.changeEnergy(output);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.translate("tooltip.create_new_age.energy_stats").style(ChatFormatting.WHITE).forGoggles(tooltip);
        CreateLang.translate("tooltip.create_new_age.energy_output").style(ChatFormatting.GRAY).forGoggles(tooltip);
        tooltip.add(Component.literal("    %s EU/t".formatted(lastOutput)).withStyle(ChatFormatting.YELLOW));
        return true;
    }

    private int processCoil(BlockPos startPos, Direction dir, MutableInt coilsLeft) {
        int generated = 0;
        BlockPos currentPos = startPos;

        while (coilsLeft.intValue() > 0) {
            currentPos = currentPos.relative(dir);
            BlockEntity be = getLevel().getBlockEntity(currentPos);

            if (!(be instanceof GeneratorCoilBlockEntity coil))
                break;

            Direction.Axis axis = coil.getBlockState().getValue(GeneratorCoilBlock.AXIS);
            if (!axis.test(dir))
                break;

            int energy = coil.takeGeneratedEnergy() / 4;
            generated += energy;

            coilsLeft.subtract(1);
        }

        return generated;
    }
}
