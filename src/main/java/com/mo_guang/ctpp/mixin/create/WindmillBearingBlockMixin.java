package com.mo_guang.ctpp.mixin.create;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.mo_guang.ctpp.common.machine.multiblock.WindMillControlMachine;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WindmillBearingBlock.class)
public class WindmillBearingBlockMixin extends Block {

    public WindmillBearingBlockMixin(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
        notifyWindmillController(pLevel, pPos);
    }
    @Unique
    public void notifyWindmillController(Level level, BlockPos pos) {
        BlockPos.betweenClosed(pos.offset(-10, -10, -10), pos.offset(10, 10, 10))
                .forEach(checkPos -> {
                    MetaMachine machine = MetaMachine.getMachine(level, pos);
                    if (machine instanceof WindMillControlMachine wmachine) {
                        wmachine.calculateWindmillAround();
                    }
                });
    }
}
