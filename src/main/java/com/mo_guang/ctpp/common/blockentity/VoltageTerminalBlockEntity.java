package com.mo_guang.ctpp.common.blockentity;

import com.gregtechceu.gtceu.api.blockentity.PipeBlockEntity;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.WireProperties;
import com.mo_guang.ctpp.api.terminal.VoltageTerminal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class VoltageTerminalBlockEntity extends PipeBlockEntity<VoltageTerminal, WireProperties> {
    public VoltageTerminalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public boolean canAttachTo(Direction side) {
        return false;
    }
}
