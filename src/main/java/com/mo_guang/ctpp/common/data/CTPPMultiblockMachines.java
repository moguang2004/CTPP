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
import com.mo_guang.ctpp.common.machine.multiblock.*;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.GTBlocks.*;
import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;

public class CTPPMultiblockMachines {
    public static void init() {}

    public static MultiblockMachineDefinition SMASHING_FACTORY = REGISTRATE.multiblock("smashing_factory", KineticWorkableMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(AllBlocks.ANDESITE_CASING)
            .recipeType(CTPPRecipeTypes.SMASHING_FACTORY_RECIPES)
            .recipeModifier(CTPPRecipeModifiers.KINETIC_OVERCLOCK)
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
            .recipeModifiers(KineticTurbineMachine::recipeModifier,CTPPRecipeModifiers.KINETIC_ADJUST)
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
    public static MultiblockMachineDefinition SEAWEED_FARM = REGISTRATE.multiblock("seaweed_farm", KineticWorkableMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(CTPPRecipeTypes.SEAWEED_FARM)
            .recipeModifier(CTPPRecipeModifiers.KINETIC_OVERCLOCK)
            .tooltips(Component.translatable("kinetic_overclock"),
                    Component.translatable("actual_speed").withStyle(ChatFormatting.YELLOW))
            .appearanceBlock(AllBlocks.ANDESITE_CASING)
            .pattern(definition -> FactoryBlockPattern.start()
            .aisle("CNNNNNC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CNNNNNC")
            .aisle("DSSSSSD", "G#####G", "G#####G", "GFFFFFG", "DBBBBBD")
            .aisle("DSSSSSD", "G#####G", "G#####G", "GLLLLLG", "EBBBBBE")
            .aisle("DSSSSSD", "G#####G", "G#####G", "GFFFFFG", "DBBBBBD")
            .aisle("CNNKNNC", "CGGGGGC", "CGGGGGC", "CGGGGGC", "CNNNNNC")
            .where("C", Predicates.blocks(AllBlocks.ANDESITE_CASING.get()))
            .where("N", Predicates.blocks(AllBlocks.ANDESITE_CASING.get())
                .or(Predicates.autoAbilities(definition.getRecipeTypes()))
            .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
            .where("K", Predicates.controller(Predicates.blocks(definition.get())))
            .where("D", Predicates.blocks(AllBlocks.ANDESITE_CASING.get()))
            .where("E", Predicates.abilities(CTPPPartAbility.INPUT_KINETIC).setExactLimit(1)
                .or(Predicates.blocks(AllBlocks.ANDESITE_CASING.get())))
            .where("F", Predicates.blocks(AllBlocks.MECHANICAL_HARVESTER.get()))
            .where("L", Predicates.blocks(AllBlocks.ANDESITE_CASING.get()))
            .where("B", Predicates.blocks(Blocks.OAK_PLANKS))
            .where("S", Predicates.blocks(Blocks.SAND))
            .where("G", Predicates.blocks(GTBlocks.CASING_TEMPERED_GLASS.get()))
            .where("#", Predicates.blocks(Blocks.WATER))
            .build())
            .workableCasingRenderer(Create.asResource("block/andesite_casing"), GTCEu.id("block/multiblock/coke_oven"), false)
            .register();
    public static MultiblockMachineDefinition WINDMILL_CONTROL_CENTER = REGISTRATE.multiblock("windmill_control_center", WindMillControlMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(CTPPRecipeTypes.WINDMILL_CONTROL)
            .appearanceBlock(AllBlocks.BRASS_CASING)
            .recipeModifiers(WindMillControlMachine::recipeModifier,CTPPRecipeModifiers.KINETIC_ADJUST)
            .tooltips(Component.translatable("windmill_control_center").withStyle(ChatFormatting.GRAY),
                    Component.translatable("ctpp.windmill_control_center.mechanism"),
                    Component.translatable("ctpp.windmill_control_center.output").withStyle(ChatFormatting.RED))
            .pattern(definition -> FactoryBlockPattern.start()
                .aisle("BBBBB", "CBBBC", "CBBBC", "#CCC#")
                .aisle("BBBBB", "BDBDB", "BDBDB", "#CEC#")
                .aisle("BBBBB", "CB@BC", "CBBBC", "#CCC#")
                .where("A", Predicates.blocks(Blocks.STONE))
                .where("B", Predicates.blocks(AllBlocks.BRASS_CASING.get())
                    .or(Predicates.abilities(CTPPPartAbility.OUTPUT_KINETIC))
                .or(Predicates.autoAbilities(definition.getRecipeTypes())))
                .where("C", Predicates.frames(GTMaterials.TreatedWood))
                .where("#", Predicates.any())
                .where("D", Predicates.blocks(CASING_STEEL_SOLID.get()))
                    .where("E", Predicates.blocks(AllBlocks.WATER_WHEEL.get()))
                .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                .build())
            .workableCasingRenderer(Create.asResource("block/brass_casing"), GTCEu.id("block/machines/miner"), false)
            .register();
    public static MultiblockMachineDefinition BOOM_OF_CREATE = REGISTRATE.multiblock("boom_of_create", KineticOutputMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(CTPPRecipeTypes.BOOM_OF_CREATE)
            .appearanceBlock(CASING_STEEL_SOLID)
            .recipeModifier(CTPPRecipeModifiers.KINETIC_ADJUST)
            .tooltips(Component.translatable("boom_of_create").withStyle(ChatFormatting.GRAY),
                    Component.translatable("ctpp.boom_of_create.basic"),
                    Component.translatable("ctpp.boom_of_create.coolant"),
                    Component.translatable("ctpp.boom_of_create.safe"))
            .pattern(definition -> FactoryBlockPattern.start()
                .aisle("######AAA######", "######AAA######", "######AAA######", "######AAA######", "######AAA######", "######AAA######", "######AAA######", "######AAA######", "######AAA######")
                .aisle("#AAA##AAA##AAA#", "#AAA##AAA##AAA#", "#AAA##AAA##AAA#", "#AAA##AAA##AAA#", "#AAA##AAA##AAA#", "#AAA##AAA##AAA#", "#AAA##AAA##AAA#", "#AAA##AAA##AAA#", "#AAA##AAA##AAA#")
                .aisle("#AAA##AAA##AAA#", "#AAABBBBBBBAAA#", "#AAABBCCCBBAAA#", "#AAABBCCCBBAAA#", "#AAABBCCCBBAAA#", "#AAABBCCCBBAAA#", "#AAABBCCCBBAAA#", "#AAABBBBBBBAAA#", "#AAA##AAA##AAA#")
                .aisle("#AAA#######AAA#", "#AABBBBBBBBBAA#", "#AABCCCCCCCBAA#", "#AABCC   CCBAA#", "#AABCC   CCBAA#", "#AABCC   CCBAA#", "#AABCCCCCCCBAA#", "#AABBBBBBBBBAA#", "#AAA#######AAA#")
                .aisle("###############", "##BBBBBBBBBBB##", "##BCCCCCCCCCB##", "##BC       CB##", "##BC   D   CB##", "##BC       CB##", "##BCCCCCCCCCB##", "##BBBBBBBBBBB##", "###############")
                .aisle("###############", "##BBBEEEEEBBB##", "##BCCEEEEECCB##", "##BC       CB##", "##BC   D   CB##", "##BC       CB##", "##BCCEEEEECCB##", "##BBBEEEEEBBB##", "###############")
                .aisle("AAA#########AAA", "AABBBEGGGEBBBAA", "AACCCEDDDECCCAA", "AAC         CAA", "AAC    D    CAA", "AAC         CAA", "AACCCEDDDECCCAA", "AABBBEGGGEBBBAA", "AAA#########AAA")
                .aisle("AAA#########AAA", "AABBBEGGGEBBBAA", "AACCCEDFDECCCAA", "AAC    F    CAA", "AAC DDDFDDD CAA", "AAC    F    CAA", "AACCCEDFDECCCAA", "AABBBEGGGEBBBAA", "AAA#########AAA")
                .aisle("AAA#########AAA", "AABBBEGGGEBBBAA", "AACCCEDDDECCCAA", "AAC         CAA", "AAC    D    CAA", "AAC         CAA", "AACCCEDDDECCCAA", "AABBBEGGGEBBBAA", "AAA#########AAA")
                .aisle("###############", "##BBBEEEEEBBB##", "##BCCEEEEECCB##", "##BC       CB##", "##BC   D   CB##", "##BC       CB##", "##BCCEEEEECCB##", "##BBBEEEEEBBB##", "###############")
                .aisle("###############", "##BBBBBBBBBBB##", "##BCCCCCCCCCB##", "##BC       CB##", "##BC   D   CB##", "##BC       CB##", "##BCCCCCCCCCB##", "##BBBBBBBBBBB##", "###############")
                .aisle("#AAA#######AAA#", "#AABBBBBBBBBAA#", "#AABCCCCCCCBAA#", "#AABCC   CCBAA#", "#AABCC   CCBAA#", "#AABCC   CCBAA#", "#AABCCCCCCCBAA#", "#AABBBBBBBBBAA#", "#AAA#######AAA#")
                .aisle("#AAA##AAA##AAA#", "#AAABBBBBBBAAA#", "#AAABBCCCBBAAA#", "#AAABBCCCBBAAA#", "#AAABBCCCBBAAA#", "#AAABBCCCBBAAA#", "#AAABBCCCBBAAA#", "#AAABBBBBBBAAA#", "#AAA##AAA##AAA#")
                .aisle("#AAA##AAA##AAA#", "#AAA##AAA##AAA#", "#AAA##AAA##AAA#", "#AAA##AAA##AAA#", "#AAA##AAA##AAA#", "#AAA##AAA##AAA#", "#AAA##AAA##AAA#", "#AAA##AAA##AAA#", "#AAA##AAA##AAA#")
                .aisle("######AAA######", "######A@A######", "######AAA######", "######AAA######", "######AAA######", "######AAA######", "######AAA######", "######AAA######", "######AAA######")
                .where("A", Predicates.blocks(CASING_STEEL_SOLID.get())
                    .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                .or(Predicates.abilities(PartAbility.MAINTENANCE)).setMinGlobalLimited(1))
                .where("#", Predicates.any())
                .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                .where("B", Predicates.blocks(CASING_STAINLESS_CLEAN.get()))
                .where("C", Predicates.blocks(CASING_TITANIUM_STABLE.get()))
                .where("D", Predicates.blocks(CASING_TUNGSTENSTEEL_ROBUST.get()))
                .where("E", Predicates.blocks(CASING_LAMINATED_GLASS.get()))
                .where("F", Predicates.blocks(CASING_TUNGSTENSTEEL_GEARBOX.get()))
                .where("G", Predicates.abilities(CTPPPartAbility.OUTPUT_KINETIC)
                    .or(Predicates.blocks(CASING_TUNGSTENSTEEL_ROBUST.get())))
                .build())
            .workableCasingRenderer(GTCEu.id("block/casings/solid/machine_casing_solid_steel"), GTCEu.id("block/multiblock/generator/large_steam_turbine"), false)
            .register();
}
