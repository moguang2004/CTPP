package com.mo_guang.ctpp;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import com.mo_guang.ctpp.api.CTPPMultiblockBuilder;
import com.simibubi.create.foundation.data.CreateEntityBuilder;
import tech.vixhentx.mcmod.ctnhlib.registrate.CNRegistrate;

import java.util.function.Function;

public class CTPPRegistrate extends CNRegistrate {

    protected CTPPRegistrate(String modId) {
        super(modId);
    }

    @Override
    public CTPPMultiblockBuilder multiblock(String name,
                                            Function<IMachineBlockEntity, ? extends MultiblockControllerMachine> metaMachine) {
        return CTPPMultiblockBuilder.createMulti(this, name, metaMachine, MetaMachineBlock::new, MetaMachineItem::new,
                MetaMachineBlockEntity::new);
    }

    public static CTPPRegistrate create(String modId) {
        return new CTPPRegistrate(modId);
    }

    public <T extends Entity> CreateEntityBuilder<T, GTRegistrate> movingEntity(String name,
                                                                                EntityType.EntityFactory<T> factory,
                                                                                MobCategory classification) {
        return this.movingEntity(self(), name, factory, classification);
    }

    public <T extends Entity, P> CreateEntityBuilder<T, P> movingEntity(P parent, String name,
                                                                        EntityType.EntityFactory<T> factory,
                                                                        MobCategory classification) {
        return (CreateEntityBuilder<T, P>) this.entry(name, (callback) -> {
            return CreateEntityBuilder.create(this, parent, name, callback, factory, classification);
        });
    }
}
