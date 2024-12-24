package com.mo_guang.ctpp.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterialBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.mo_guang.ctpp.api.CTPPPartAbility;
import com.mo_guang.ctpp.common.machine.multiblock.KineticGeneratorMachine;
import com.mo_guang.ctpp.common.machine.multiblock.KineticTurbineMachine;
import com.mo_guang.ctpp.common.machine.multiblock.KineticWorkableMultiblockMachine;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_STAINLESS_CLEAN;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_STEEL_SOLID;
import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;

public class CTPPMultiblockMachines {
    public static void init() {}

    public static MultiblockMachineDefinition SMASHING_FACTORY = REGISTRATE.multiblock("smashing_factory", KineticWorkableMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(AllBlocks.ANDESITE_CASING)
            .recipeType(CTPPRecipeTypes.SMASHING_FACTORY_RECIPES)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("AAAAA", "ABBBA", "ABBBA")
                    .aisle("AAAAA", "A   A", "AC CA")
                    .aisle("AAAAA", "A   A", "AC CA")
                    .aisle("AAAAA", "A   A", "AC CA")
                    .aisle("AAAAA", "AB@BA", "ABBBA")
                    .where("A",Predicates.blocks(AllBlocks.ANDESITE_CASING.get()))
                    .where("B",Predicates.blocks(AllBlocks.ANDESITE_CASING.get())
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.abilities(CTPPPartAbility.INPUT_KINETIC)))
                    .where("C",Predicates.blocks(AllBlocks.CRUSHING_WHEEL.get()))
                    .where("@",Predicates.controller(blocks(definition.getBlock())))
                    .where(" ",Predicates.any())
                    .build())
            .workableCasingRenderer(Create.asResource("block/andesite_casing"),GTCEu.id("block/multiblock/large_chemical_reactor"))
            .register();
    public static MultiblockMachineDefinition KINETIC_GENERATOR = REGISTRATE.multiblock("kinetic_generator", KineticGeneratorMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(CTPPRecipeTypes.KINETIC_GENERATOR_RECIPES)
            .appearanceBlock(CASING_STEEL_SOLID)
            .tooltips(Component.translatable("kinetic_generator").withStyle(ChatFormatting.GRAY),
                    Component.translatable("ctpp.kinetic_generator.basic"),
                    Component.translatable("ctpp.kinetic_generator.extrict"),
                    Component.translatable("ctpp.kinetic_generator.upgrade"),
                    Component.translatable("ctpp.kinetic_generator.core"))
            .recipeModifier(KineticGeneratorMachine::recipeModifier)
            .pattern(definition -> FactoryBlockPattern.start()
                .aisle("CCTP", "CCTP", "  TP")
                .aisle("CCTP", "EAGK", "CCTP")
                .aisle("CCTP", "CSTP", "  TP")
                .where("S", Predicates.controller(Predicates.blocks(definition.get())))
                .where("G", Predicates.blocks(GTBlocks.CASING_STEEL_GEARBOX.get()))
                .where("A", Predicates.blocks(GTMaterialBlocks.MATERIAL_BLOCKS.get(TagPrefix.block,GTMaterials.Coke).get())
                                .or(Predicates.blocks(GTMaterialBlocks.MATERIAL_BLOCKS.get(TagPrefix.block,GTMaterials.Graphene).get())))
                .where("C", Predicates.blocks(CASING_STEEL_SOLID.get())
                        .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
                .where("P", Predicates.blocks(CASING_STEEL_SOLID.get())
                        .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setExactLimit(1)))
                .where("K", Predicates.abilities(CTPPPartAbility.INPUT_KINETIC).setExactLimit(1))
                .where("E", Predicates.abilities(PartAbility.OUTPUT_ENERGY).setExactLimit(1))
                .where("T", Predicates.heatingCoils())
                .build())

            .workableCasingRenderer(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),GTCEu.id("block/multiblock/generator/large_steam_turbine"), false)
            .register();
    public static MultiblockMachineDefinition KINETIC_STEAM_TURBINE = REGISTRATE.multiblock("kinetic_steam_turbine", KineticTurbineMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(CTPPRecipeTypes.KINETIC_STEAM_TURBINE_RECIPES)
            .appearanceBlock(GTBlocks.CASING_BRONZE_BRICKS)
            .tooltips(Component.translatable("kinetic_output"),
                    Component.translatable("rotor_holder_upgrade"),
                    Component.translatable("steam_up_hv_loss").withStyle(ChatFormatting.RED))
            .recipeModifier(KineticTurbineMachine::recipeModifier)
            .pattern(definition -> FactoryBlockPattern.start()
                .aisle("CCCC", "CSSC", "CCCC")
                .aisle("CSSC", "TGGT", "CSSC")
                .aisle("CCCC", "CKSC", "CCCC")
                .where("C", Predicates.blocks(GTBlocks.CASING_BRONZE_BRICKS.get()))
                .where("S", Predicates.blocks(GTBlocks.CASING_BRONZE_BRICKS.get())
                    .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                .or(Predicates.abilities(PartAbility.MUFFLER).setExactLimit(1)))
                .where("K", Predicates.controller(Predicates.blocks(definition.get())))
                .where("T", Predicates.abilities(CTPPPartAbility.OUTPUT_KINETIC).setExactLimit(1)
                    .or(Predicates.abilities(PartAbility.ROTOR_HOLDER).setExactLimit(1)))
                .where("G", Predicates.blocks(GTBlocks.CASING_BRONZE_GEARBOX.get()))
                .build())
            .workableCasingRenderer(GTCEu.id("block/casings/solid/machine_casing_bronze_plated_bricks"), GTCEu.id("block/multiblock/generator/large_steam_turbine"), false)
            .register();
}
