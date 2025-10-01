package com.mo_guang.ctpp;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.mo_guang.ctpp.api.CTPPMultiblockBuilder;
import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.tterrag.registrate.builders.EntityBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Function;

public class CTPPRegistrate extends GTRegistrate {
    protected CTPPRegistrate(String modId) {
        super(modId);
    }

    @Override
    public CTPPMultiblockBuilder multiblock(String name, Function<IMachineBlockEntity, ? extends MultiblockControllerMachine> metaMachine) {
        return CTPPMultiblockBuilder.createMulti(this, name, metaMachine, MetaMachineBlock::new, MetaMachineItem::new, MetaMachineBlockEntity::new);
    }

    public static CTPPRegistrate create(String modId) {
        return new CTPPRegistrate(modId);
    }

    @Override
    public <T extends Entity> CreateEntityBuilder<T, GTRegistrate> entity(String name,
                                                                    EntityType.EntityFactory<T> factory, MobCategory classification) {
        return this.entity(self(), name, factory, classification);
    }

    @Override
    public <T extends Entity, P> CreateEntityBuilder<T, P> entity(P parent, String name,
                                                                  EntityType.EntityFactory<T> factory, MobCategory classification) {
        return (CreateEntityBuilder<T, P>) this.entry(name, (callback) -> {
            return CreateEntityBuilder.create(this, parent, name, callback, factory, classification);
        });
    }
}
