package com.mo_guang.ctpp.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.mo_guang.ctpp.api.CTPPPartAbility;
import com.mo_guang.ctpp.common.machine.multiblock.KineticWorkableMultiblockMachine;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;

public class CTPPMultiblockMachines {
    public static void init() {}

    public static MultiblockMachineDefinition SMASHING_FACTORY = REGISTRATE.multiblock("smashing_factory", KineticWorkableMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(AllBlocks.ANDESITE_CASING::get)
            .recipeType(CTPPRecipeTypes.SMASHING_FACTORY_RECIPES)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("AAAAA", "ABBBA", "ABBBA")
                    .aisle("AAAAA", "A   A", "AC CA")
                    .aisle("AAAAA", "A   A", "AC CA")
                    .aisle("AAAAA", "A   A", "AC CA")
                    .aisle("AAAAA", "AB@BA", "ABBBA")
                    .where('A',Predicates.blocks(AllBlocks.ANDESITE_CASING.get()))
                    .where('B',Predicates.blocks(AllBlocks.ANDESITE_CASING.get())
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.abilities(CTPPPartAbility.INPUT_KINETIC)))
                    .where('C',Predicates.blocks(AllBlocks.CRUSHING_WHEEL.get()))
                    .where('@',Predicates.controller(blocks(definition.getBlock())))
                    .where(' ',Predicates.any())
                    .build())
            .workableCasingRenderer(Create.asResource("block/andesite_casing"),GTCEu.id("block/multiblock/large_chemical_reactor"))
            .register();
}
