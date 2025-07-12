package com.mo_guang.ctpp;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.mo_guang.ctpp.api.CTPPMultiblockBuilder;

import java.util.function.Function;

public class CTPPRegistrate extends GTRegistrate {
    protected CTPPRegistrate(String modId) {
        super(modId);
    }

    @Override
    public CTPPMultiblockBuilder multiblock(String name, Function<IMachineBlockEntity, ? extends MultiblockControllerMachine> metaMachine) {
        return CTPPMultiblockBuilder.createMulti(this, name, metaMachine, MetaMachineBlock::new, MetaMachineItem::new, MetaMachineBlockEntity::createBlockEntity);
    }

    public static CTPPRegistrate create(String modId) {
        return new CTPPRegistrate(modId);
    }
}
