package com.mo_guang.ctpp.registry;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.*;
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.api.CTPPPartAbility;
import com.mo_guang.ctpp.common.data.recipe.CTPPRecipeModifiers;
import com.mo_guang.ctpp.api.pattern.FactoryStaticBlockPattern;
import com.mo_guang.ctpp.common.machine.multiblock.*;
import com.mo_guang.ctpp.common.machine.multiblock.windmillController.WindMillControlMachine;
import com.mo_guang.ctpp.util.CommonTooltips;
import com.simibubi.create.AllBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import com.mo_guang.ctpp.CTPPRegistration;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.GTBlocks.*;
import static com.gregtechceu.gtceu.common.data.GTMaterialBlocks.MATERIAL_BLOCKS;
import static com.gregtechceu.gtceu.common.data.GTMaterials.TreatedWood;
import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;
import static com.mo_guang.ctpp.config.ConfigUtils.*;
import static net.minecraft.world.level.block.Blocks.*;
import static net.minecraft.world.level.block.Blocks.STONE_BRICKS;

public class CTPPMultiblockMachines {
    public static void init() {}

    public static MultiblockMachineDefinition SMASHING_FACTORY = CTPPRegistration.conditionalRegistration(
        ctnhEnabled("SmashingFactory"),
        () -> REGISTRATE.multiblock("smashing_factory", KineticWorkableMultiblockMachine::new)
                .cnLangValue("粉碎工厂")
            .rotationState(RotationState.NON_Y_AXIS)
            .appearanceBlock(AllBlocks.ANDESITE_CASING)
            .recipeType(CTPPRecipeTypes.SMASHING_FACTORY_RECIPES)
                .tooltips(CommonTooltips.KINETIC_OVERCLOCK)
            .recipeModifier(CTPPRecipeModifiers.KINETIC_PARALLEL)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("AAAAA", "ABBBA", "ABBBA")
                    .aisle("AAAAA", "A   A", "AC CA")
                    .aisle("AAAAA", "A   A", "AC CA")
                    .aisle("AAAAA", "A   A", "AC CA")
                    .aisle("AAAAA", "AB@BA", "ABBBA")
                    .where("A", blocks(AllBlocks.ANDESITE_CASING.get()))
                    .where("B", blocks(AllBlocks.ANDESITE_CASING.get())
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.abilities(CTPPPartAbility.INPUT_KINETIC))
                            .or(Predicates.abilities(CTPPPartAbility.MECHANICAL_UPGRADE).setMaxGlobalLimited(1)))
                    .where("C", blocks(AllBlocks.CRUSHING_WHEEL.get()))
                    .where("@",Predicates.controller(blocks(definition.getBlock())))
                    .where(" ",Predicates.any())
                    .build())
            .shapeInfo(definition -> MultiblockShapeInfo.builder()
                    .aisle("AAAAA", "AE@FA", "AAGAA")
                    .aisle("AAAAA", "A   A", "AC CA")
                    .aisle("AAAAA", "A   A", "AC CA")
                    .aisle("AAAAA", "A   A", "AC CA")
                    .aisle("AAAAA", "AADAA", "AAAAA")
                    .where('A', AllBlocks.ANDESITE_CASING.get())
                    .where('C', AllBlocks.CRUSHING_WHEEL.get().defaultBlockState().setValue(BlockStateProperties.AXIS, Direction.Axis.Z))
                    .where('D', CTPPMachines.KINETIC_INPUT_BOX[GTValues.LV], Direction.SOUTH)
                    .where('E', GTMachines.ITEM_IMPORT_BUS[GTValues.LV], Direction.NORTH)
                    .where('F', GTMachines.ITEM_EXPORT_BUS[GTValues.LV], Direction.NORTH)
                    .where('G', CTPPMachines.MECHANICAL_UPGRADE_BUS, Direction.NORTH)
                    .where('@', CTPPMultiblockMachines.SMASHING_FACTORY, Direction.NORTH)
                    .build())
            .workableCasingModel(CTPP.id("block/create/andesite_casing"),GTCEu.id("block/multiblock/large_chemical_reactor"))
            .register());
    public static MultiblockMachineDefinition KINETIC_GENERATOR = CTPPRegistration.conditionalRegistration(
        ctnhEnabled("KineticGenerator"),
        () -> REGISTRATE.multiblock("kinetic_generator", KineticGeneratorMachine::new)
                .cnLangValue("应力发电机")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(CTPPRecipeTypes.KINETIC_GENERATOR_RECIPES)
            .appearanceBlock(CASING_STEEL_SOLID)
                .generator(true)
            .tooltips(Component.translatable("ctpp.multiblock.kinetic_generator.tooltip.0").withStyle(ChatFormatting.GRAY),
                    Component.translatable("ctpp.multiblock.kinetic_generator.tooltip.1"),
                    Component.translatable("ctpp.multiblock.kinetic_generator.tooltip.2"),
                    Component.translatable("ctpp.multiblock.kinetic_generator.tooltip.3"),
                    Component.translatable("ctpp.multiblock.kinetic_generator.tooltip.4"))
            .recipeModifier(KineticGeneratorMachine::recipeModifier,true)
            .pattern(definition -> FactoryBlockPattern.start()
                .aisle("CCTP", "CCTP", "  TP")
                .aisle("CCTP", "EAGK", "CCTP")
                .aisle("CCTP", "CSTP", "  TP")
                .where("S", Predicates.controller(Predicates.blocks(definition.get())))
                .where("G", Predicates.blocks(GTBlocks.CASING_STEEL_GEARBOX.get()))
                .where("A", Predicates.blocks(GTMaterialBlocks.MATERIAL_BLOCKS.get(TagPrefix.block,GTMaterials.Coke).get())
                                .or(Predicates.blocks(GTMaterialBlocks.MATERIAL_BLOCKS.get(TagPrefix.block,GTMaterials.Graphene).get())))
                .where("C", Predicates.blocks(CASING_STEEL_SOLID.get())
                        .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                        .or(Predicates.abilities(CTPPPartAbility.MECHANICAL_UPGRADE).setMaxGlobalLimited(1)))
                .where("P", Predicates.blocks(CASING_STEEL_SOLID.get())
                        .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setExactLimit(1)))
                .where("K", Predicates.abilities(CTPPPartAbility.INPUT_KINETIC).setExactLimit(1))
                .where("E", Predicates.abilities(PartAbility.OUTPUT_ENERGY).setExactLimit(1))
                .where("T", Predicates.heatingCoils())
                .build())

            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),GTCEu.id("block/multiblock/generator/large_steam_turbine"))
            .register());
    public static MultiblockMachineDefinition KINETIC_STEAM_TURBINE = CTPPRegistration.conditionalRegistration(
        ctnhEnabled("KineticSteamTurbine"),
        () -> REGISTRATE.multiblock("kinetic_steam_turbine", KineticTurbineMachine::new)
                .cnLangValue("机械蒸汽涡轮")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(CTPPRecipeTypes.KINETIC_STEAM_TURBINE_RECIPES)
            .appearanceBlock(GTBlocks.CASING_BRONZE_BRICKS)
            .tooltips(Component.translatable("ctpp.multiblock.kinetic_steam_turbine.tooltip.0"),
                    Component.translatable("ctpp.multiblock.kinetic_steam_turbine.tooltip.1"),
                    Component.translatable("ctpp.multiblock.kinetic_steam_turbine.tooltip.2").withStyle(ChatFormatting.RED),
                    Component.translatable("ctpp.multiblock.kinetic_steam_turbine.tooltip.3"),
                    CommonTooltips.MECHANICAL_TIER_MACHINE)
            .recipeModifiers(KineticTurbineMachine::recipeModifier)
            .pattern(definition -> FactoryBlockPattern.start()
                .aisle("CCCC", "CSSC", "CCCC")
                .aisle("CSSC", "TGGT", "CSSC")
                .aisle("CCCC", "CKSC", "CCCC")
                .where("C", Predicates.blocks(GTBlocks.CASING_BRONZE_BRICKS.get()))
                .where("S", Predicates.blocks(GTBlocks.CASING_BRONZE_BRICKS.get())
                    .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                .or(Predicates.abilities(PartAbility.MUFFLER).setExactLimit(1))
                        .or(Predicates.abilities(CTPPPartAbility.MECHANICAL_UPGRADE).setMaxGlobalLimited(1)))
                .where("K", Predicates.controller(Predicates.blocks(definition.get())))
                .where("T", Predicates.abilities(CTPPPartAbility.OUTPUT_KINETIC).setExactLimit(1)
                    .or(Predicates.abilities(PartAbility.ROTOR_HOLDER).setExactLimit(1)))
                .where("G", Predicates.blocks(GTBlocks.CASING_BRONZE_GEARBOX.get()))
                .build())
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_bronze_plated_bricks"), GTCEu.id("block/multiblock/generator/large_steam_turbine"))
            .register());
    public static MultiblockMachineDefinition SEAWEED_FARM = CTPPRegistration.conditionalRegistration(
        ctnhEnabled("SeaweedFarm"),
        () -> REGISTRATE.multiblock("seaweed_farm", KineticWorkableMultiblockMachine::new)
                .cnLangValue("海草养殖农场")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(CTPPRecipeTypes.SEAWEED_FARM)
            .recipeModifier(CTPPRecipeModifiers.KINETIC_PARALLEL)
            .tooltips(CommonTooltips.KINETIC_OVERCLOCK)
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
                .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                    .or(Predicates.abilities(CTPPPartAbility.MECHANICAL_UPGRADE).setMaxGlobalLimited(1)))
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
            .workableCasingModel(CTPP.id("block/create/andesite_casing"), GTCEu.id("block/multiblock/coke_oven"))
            .register());
    public static MultiblockMachineDefinition WINDMILL_CONTROL_CENTER = CTPPRegistration.conditionalRegistration(
        ctnhEnabled("WindmillControlCenter"),
        () -> REGISTRATE.multiblock("windmill_control_center", WindMillControlMachine::new)
                .cnLangValue("风车控制中心")
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .recipeType(CTPPRecipeTypes.WINDMILL_CONTROL)
            .appearanceBlock(AllBlocks.BRASS_CASING)
            .recipeModifiers(WindMillControlMachine::recipeModifier)
            .tooltips(Component.translatable("ctpp.multiblock.windmill_control_center.tooltip.0").withStyle(ChatFormatting.GRAY),
                    Component.translatable("ctpp.multiblock.windmill_control_center.tooltip.1"),
                    CommonTooltips.MECHANICAL_TIER_MACHINE,
                    Component.translatable("ctpp.multiblock.windmill_control_center.tooltip.2").withStyle(ChatFormatting.RED))
                .pattern(definition -> FactoryStaticBlockPattern.start()
                        .aisle("AABPPPPPBAA", "###CDDDC###", "###CDDDC###", "###CDDDC###", "AAACCCCCAAA", "###########", "#####EE####", "####EE#####", "####E######", "###########", "###########", "######E####", "#####EE####", "####EE#####", "####E######", "###########", "###########", "######E####", "#####EE####", "####EE#####", "####E######", "###########", "###########", "######E####", "#####EE####", "###EEE#####")
                        .aisle("AFFCCCCCFFA", "#GFF###FFG#", "#HFF###FFH#", "#GFF###FFG#", "AFFCCICCFFA", "#####J#####", "#####KE####", "###########", "###E#######", "###E#######", "#######E###", "#######E###", "###########", "###########", "###E#######", "###E#######", "#######E###", "#######E###", "###########", "###########", "###E#######", "###E#######", "#######E###", "#######E###", "###########", "###########")
                        .aisle("BFCCCCCCCFB", "#F#######F#", "#F#######F#", "#F#######F#", "AFCCCICCCFA", "#####J#####", "####KKE####", "###########", "###########", "##E#####E##", "##E#####E##", "###########", "###########", "###########", "###########", "##E#####E##", "##E#####E##", "###########", "###########", "###########", "###########", "##E#####E##", "##E#####E##", "###########", "###########", "###########")
                        .aisle("PCCCCCCCCCP", "CF#######FC", "CF#######FC", "CF#######FC", "CCCCCICCCCC", "#####J#####", "####KE#####", "###########", "#########E#", "#########E#", "#E#########", "#E#########", "###########", "###########", "#########E#", "#########E#", "#E#########", "#E#########", "###########", "###########", "#########E#", "#########E#", "#E#########", "#E#########", "###########", "##########E")
                        .aisle("PCCCLMLCCCP", "D#########D", "D#########D", "D#########D", "CCCCLMLCCCC", "####JJJ####", "EEE#KEKKK##", "##########E", "##########E", "###########", "###########", "E##########", "E##########", "##########E", "##########E", "###########", "###########", "E##########", "E##########", "##########E", "##########E", "###########", "###########", "E##########", "E##########", "##########E")
                        .aisle("PCCCMCMCCCP", "D####N####D", "D####N####D", "D####N####D", "CIIIMOMIIIC", "#JJJJJJJJJ#", "EKKEEEEEKKE", "E#########E", "###########", "###########", "###########", "###########", "E#########E", "E#########E", "###########", "###########", "###########", "###########", "E#########E", "E#########E", "###########", "###########", "###########", "###########", "E#########E", "E#########E")
                        .aisle("PCCCLMLCCCP", "D#########D", "D#########D", "D#########D", "CCCCLMLCCCC", "####JJJ####", "##KKKEK#EEE", "E##########", "E##########", "###########", "###########", "##########E", "##########E", "E##########", "E##########", "###########", "###########", "##########E", "##########E", "E##########", "E##########", "###########", "###########", "##########E", "##########E", "E##########")
                        .aisle("PCCCCCCCCCP", "CF#######FC", "CF#######FC", "CF#######FC", "CCCCCICCCCC", "#####J#####", "#####EK####", "###########", "#E#########", "#E#########", "#########E#", "#########E#", "###########", "###########", "#E#########", "#E#########", "#########E#", "#########E#", "###########", "###########", "#E#########", "#E#########", "#########E#", "#########E#", "###########", "E##########")
                        .aisle("BFCCCCCCCFB", "#F#######F#", "#F#######F#", "#F#######F#", "AFCCCICCCFA", "#####J#####", "####EKK####", "###########", "###########", "##E#####E##", "##E#####E##", "###########", "###########", "###########", "###########", "##E#####E##", "##E#####E##", "###########", "###########", "###########", "###########", "##E#####E##", "##E#####E##", "###########", "###########", "###########")
                        .aisle("AFFCCCCCFFA", "#GFF###FFG#", "#HFF###FFH#", "#GFF###FFG#", "AFFCCICCFFA", "#####J#####", "####EK#####", "###########", "#######E###", "#######E###", "###E#######", "###E#######", "###########", "###########", "#######E###", "#######E###", "###E#######", "###E#######", "###########", "###########", "#######E###", "#######E###", "###E#######", "###E#######", "###########", "###########")
                        .aisle("AABPP@PPBAA", "###PPPPP###", "###CDDDC###", "###CDDDC###", "AAACCCCCAAA", "###########", "####EE#####", "#####EE####", "######E####", "###########", "###########", "####E######", "####EE#####", "#####EE####", "######E####", "###########", "###########", "####E######", "####EE#####", "#####EE####", "######E####", "###########", "###########", "####E######", "####EE#####", "#####EEE###")
                        .where("A", Predicates.blocks(CASING_BRONZE_BRICKS.get()))
                        .where("B", Predicates.blocks(BRONZE_BRICKS_HULL.get()))
                        .where("C", Predicates.blocks(AllBlocks.BRASS_CASING.get()))
                        .where("#", Predicates.any())
                        .where("D", Predicates.blocks(CASING_TEMPERED_GLASS.get()))
                        .where("E", Predicates.blocks(Blocks.YELLOW_WOOL), false)
                        .where("F", Predicates.blocks(AllBlocks.RAILWAY_CASING.get()))
                        .where("G", Predicates.blocks(AllBlocks.METAL_GIRDER.get()))
                        .where("H", Predicates.blocks(AllBlocks.METAL_GIRDER.get()))
                        .where("I", Predicates.blocks(AllBlocks.ROSE_QUARTZ_LAMP.get()))
                        .where("J", Predicates.blocks(AllBlocks.LINEAR_CHASSIS.get()), false)
                        .where("K", Predicates.blocks(Blocks.WHITE_WOOL), false)
                        .where("L", Predicates.blocks(CASING_BRONZE_GEARBOX.get()))
                        .where("M", Predicates.blocks(CASING_BRONZE_PIPE.get()))
                        .where("N", Predicates.blocks(GCYMBlocks.CASING_INDUSTRIAL_STEAM.get()))
                        .where("O", Predicates.blocks(AllBlocks.WINDMILL_BEARING.get()))
                        .where("P", Predicates.blocks(AllBlocks.BRASS_CASING.get())
                                .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                                .or(Predicates.abilities(CTPPPartAbility.OUTPUT_KINETIC))
                                .or(Predicates.abilities(CTPPPartAbility.MECHANICAL_UPGRADE)))
                        .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                        .build()
                )
            .workableCasingModel(CTPP.id("block/create/brass_casing"), GTCEu.id("block/machines/miner"))
            .register());
    public static MultiblockMachineDefinition BOOM_OF_CREATE = CTPPRegistration.conditionalRegistration(
        ctnhEnabled("BoomOfCreate"),
        () -> REGISTRATE.multiblock("boom_of_create", KineticOutputMachine::new)
                .cnLangValue("大型聚爆应力厂")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(CTPPRecipeTypes.BOOM_OF_CREATE)
            .appearanceBlock(CASING_STEEL_SOLID)
            .noRecipeModifier()
            .tooltips(Component.translatable("ctpp.multiblock.boom_of_create.tooltip.0").withStyle(ChatFormatting.GRAY),
                    Component.translatable("ctpp.multiblock.boom_of_create.tooltip.1"),
                    Component.translatable("ctpp.multiblock.boom_of_create.tooltip.2"),
                    Component.translatable("ctpp.multiblock.boom_of_create.tooltip.3"))
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
                    .or(Predicates.abilities(PartAbility.MAINTENANCE)).setMinGlobalLimited(1)
                        .or(Predicates.abilities(CTPPPartAbility.MECHANICAL_UPGRADE).setMaxGlobalLimited(1)))
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
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"), GTCEu.id("block/multiblock/generator/large_steam_turbine"))
            .register());
    public static final MultiblockMachineDefinition BIG_DAM = REGISTRATE.multiblock("big_dam",
                    BigDamMachine::new)
            .cnLangValue("三峡大坝")
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .recipeType(CTPPRecipeTypes.BIG_DAM)
            .noRecipeModifier()
            .appearanceBlock(() -> Blocks.STONE_BRICKS)
            .pattern(definition -> FactoryStaticBlockPattern.start()
                    .aisle("###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "##A##A##A##A##A##A##A##A##A##A##A##A##A####", "#AB##B##B##B##B##B##B##B##B##B##B##B##B####", "#DBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "DDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE####")
                    .aisle("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "FFBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BGGGGGBGGGGGBGGGGGBGGGGGBGGGGGBGGGGGB####", "FFE#####E#####E#####E#####E#####E#####E####")
                    .aisle("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "#DBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "#DBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "#DBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "#DBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "DDB##B##B##B##B##B##B##B##B##B##B##B##B####", "#DB##B##B##B##B##B##B##B##B##B##B##B##B####", "#DB##B##B##B##B##B##B##B##B##B##B##B##B####", "#DB##B##B##B##B##B##B##B##B##B##B##B##B####", "#DB##B##B##B##B##B##B##B##B##B##B##B##B####", "#DB##B##B##B##B##B##B##B##B##B##B##B##B####", "#DB##B##B##B##B##B##B##B##B##B##B##B##B####", "#DB##B##B##B##B##B##B##B##B##B##B##B##B####", "#DB##B##B##B##B##B##B##B##B##B##B##B##B####", "#DB#####B#####B#####B#####B#####B#####B####", "#DE#####E#####E#####E#####E#####E#####E####", "DD#########################################")
                    .aisle("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##B##B##B##B##B##B##B##B##B##B##B##B##B####", "FFB##B##B##B##B##B##B##B##B##B##B##B##B####", "##B##B##B##B##B##B##B##B##B##B##B##B##B####", "##H##A##H##A##H##A##H##A##H##A##H##A##H####", "##H##A##H##A##H##A##H##A##H##A##H##A##H####", "##A##A##A##A##A##A##A##A##A##A##A##A##A####", "##A##A##A##A##A##A##A##A##A##A##A##A##A####", "##A##A##A##A##A##A##A##A##A##A##A##A##A####", "##A##A##A##A##A##A##A##A##A##A##A##A##A####", "##A##A##A##A##A##A##A##A##A##A##A##A##A####", "##EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE####", "###########################################", "###########################################")
                    .aisle("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##B##B##B##B##B##B##B##B##B##B##B##B##B####", "##B##B##B##B##B##B##B##B##B##B##B##B##B####", "FFB##B##B##B##B##B##B##B##B##B##B##B##B####", "##H#####H#####H#####H#####H#####H#####H####", "##H#####H#####H#####H#####H#####H#####H####", "##I#####I#####I#####I#####I#####I#####I####", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################")
                    .aisle("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##B##B##B##B##B##B##B##B##B##B##B##B##B####", "##B##B##B##B##B##B##B##B##B##B##B##B##B####", "##B##B##B##B##B##B##B##B##B##B##B##B##B####", "FFB#####B#####B#####B#####B#####B#####B####", "##H#####H#####H#####H#####H#####H#####H####", "###########################################", "##I#####I#####I#####I#####I#####I#####I####", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################")
                    .aisle("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##B##B##B##B##B##B##B##B##B##B##B##B##B####", "##B##B##B##B##B##B##B##B##B##B##B##B##B####", "###########################################", "FF#########################################", "##I#####I#####I#####I#####I#####I#####I####", "###########################################", "##I#####I#####I#####I#####I#####I#####I####", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################")
                    .aisle("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##B##B##B##B##B##B##B##B##B##B##B##B##B####", "###########################################", "###########################################", "FF#########################################", "##ICC#CCICC#CCICC#CCICC#CCICC#CCICC#CCI####", "###CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#####", "##ICC#CCICC#CCICC#CCICC#CCICC#CCICC#CCI####", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################")
                    .aisle("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##B##B##B##B##B##B##B##B##B##B##B##B##B####", "###########################################", "###CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#####", "FF#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#####", "##IJJ#JJIJJ#JJIJJ#JJIJJ#JJIJJ#JJIJJ#JJI####", "###JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#####", "##IJJ#JJIJJ#JJIJJ#JJIJJ#JJIJJ#JJIJJ#JJI####", "###CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#####", "###CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#####", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################")
                    .aisle("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBD##D", "##B##B##B##B##B##B##B##B##B##B##B##B##BD##D", "###CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#D##D", "###JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#DDDD", "FF#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#####", "##I#####I#####I#####I#####I#####I#####I####", "###C###C#C###C#C###C#C###C#C###C#C###C#####", "##I#####I#####I#####I#####I#####I#####I####", "###JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#####", "###JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#####", "###CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#####", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################")
                    .aisle("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "#DBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "#DB##B##B##B##B##B##B##B##B##B##B##B##B####", "#D#CC#CC#CC#CC#CC#CC#CC#CC#CC#C##CC#CC#####", "#D#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#FFFF", "DD##C#C###C#C###C#C###C#C###C#C###C#C######", "##I#####I#####I#####I#####I#####I#####I####", "###C###C#C###C#C###C#C###C#C###C#C###C#####", "##I#####I#####I#####I#####I#####I#####I####", "####C#C###C#C###C#C###C#C###C#C###C#C######", "###JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#####", "###CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#####", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################")
                    .aisle("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##HCCBCCHCCBCCHCCBCCHCCBCCHCCBCCHCCBCCH####", "###JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#####", "#######################################FDDF", "FF#####################################I###", "##HCCHCCHCCHCCHCCHCCHCCHCCHCCHCCHCCHCCHH###", "##HCCHCCHCCHCCHCCHCCHCCHCCHCCHCCHCCHCCHH###", "##HCCHCCHCCHCCHCCHCCHCCHCCHCCHCCHCCHCCHH###", "###########################################", "###########################################", "###JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#####", "###CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#####", "###########################################", "###########################################", "###########################################", "###########################################")
                    .aisle("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "##B##B##B##B##B##B##B##B##B##B##B##B##B####", "##HCCBCCHCCBCCHCCBCCHCCBCCHCCBCCHCCBCCH####", "##EJJ#JJEJJ#JJEJJ#JJEJJ#JJEJJ#JJEJJ#JJE####", "##EC###CEC###CEC###CEC###CEC###CEC###CEFDDF", "FFEC###CEC###CEC###CEC###CEC###CEC###CE####", "##HCCHCJHCCHCJHCCHCJHCCHCJHCCHCJHCCHCJHH###", "##KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKL###", "##HCCHCCHCCHCCHCCHCCHCCHCCHCCHCCHCCHCCHH###", "###C###C#C###C#C###C#C###C#C###C#C###C#####", "###C###C#C###C#C###C#C###C#C###C#C###C#####", "###JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#####", "###CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#####", "###########################################", "###########################################", "###########################################", "###########################################")
                    .aisle("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "##B##B##B##B##B##B##B##B##B##B##B##B##B####", "##HCCBCCHCCBCCHCCBCCHCCBCCHCCBCCHCCBCCH####", "##EJJ#JJEJJ#JJEJJ#JJEJJ#JJEJJ#JJEJJ#JJE####", "##E#####E#####E#####E#####E#####E#####EFDDF", "FFE#####E#####E#####E#####E#####E#####EI###", "##HCCHCCHCCHCCHCCHCCHCCHCCHCCHCCHCCHCCHH###", "##HCCHCCHCCHCCHCCHCCHCCHCCHCCHCCHCCHCCHH###", "##HCCHCCHCCHCCHCCHCCHCCHCCHCCHCCHCCHCCHH###", "###########################################", "###########################################", "###JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#####", "###CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#####", "###########################################", "###########################################", "###########################################", "###########################################")
                    .aisle("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "#DB##B##B##B##B##B##B##B##B##B##B##B##B####", "#DH##B##H##B##H##B##H##B##H##B##H##B##H####", "#D#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#####", "#D#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#FFFF", "DD##C#C###C#C###C#C###C#C###C#C###C#C######", "##E#####E#####E#####E#####E#####E#####E####", "##EC###CEC###CEC###CEC###CEC###CEC###CE####", "##E#####E#####E#####E#####E#####E#####E####", "####C#C###C#C###C#C###C#C###C#C###C#C######", "###JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#####", "###CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#####", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################")
                    .aisle("##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "##B##B##B##B##B##B##B##B##B##B##B##B##BD##D", "##B##B##B##B##B##B##B##B##B##B##B##B##BD##D", "###CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#DDDD", "###JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#DDDD", "##EJJ#JJEJJ#JJEJJ#JJEJJ#JJEJJ#JJEJJ#JJE####", "##E#####E#####E#####E#####E#####E#####E####", "##EC###CEC###CEC###CEC###CEC###CEC###CE####", "###########################################", "###JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#####", "###JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#####", "###CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#####", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################")
                    .aisle("##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "##B##B##B##B##B##B##B##B##B##B##B##B##B####", "##B##B##B##B##B##B##B##B##B##B##B##B##B####", "#######################################FFFF", "##ECC#CCECC#CCECC#CCECC#CCECC#CCECC#CCE####", "##ECC#CCECC#CCECC#CCECC#CCECC#CCECC#CCE####", "##EJJ#JJEJJ#JJEJJ#JJEJJ#JJEJJ#JJEJJ#JJE####", "###JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#####", "###JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#JJ#####", "###CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#####", "###CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#####", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################")
                    .aisle("##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "##B##B##B##B##B##B##B##B##B##B##B##B##B####", "##H##B##H##B##H##B##H##B##H##B##H##B##H####", "##E#####E#####E#####E#####E#####E#####EFDDF", "##E#####E#####E#####E#####E#####E#####E####", "##E#####E#####E#####E#####E#####E#####E####", "###CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#####", "###CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#####", "###CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#CC#####", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################")
                    .aisle("##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "##B##B##B##B##B##B##B##B##B##B##B##B##B####", "##H##B##H##B##H##B##H##B##H##B##H##B##H####", "##H#####H#####H#####H#####H#####H#####HFDDF", "##E#####E#####E#####E#####E#####E#####E####", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################")
                    .aisle("##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "##B##B##B##B##B##B##B##B##B##B##B##B##B####", "##B##B##B##B##B##B##B##B##B##B##B##B##B####", "##H#####H#####H#####H#####H#####H#####HFDDF", "##H#####H#####H#####H#####H#####H#####H####", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################")
                    .aisle("##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB####", "##B##B##B##B##B##B##B##B##B##B##B##B##B####", "##B##B##B##B##B##B##B##B##B##B##B##B##BFFFF", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################")
                    .aisle("##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB", "##BBBBBBMMMMMMMMMMMM@MMMMMMMMMMMMBBBBBBD##D", "##BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBD##D", "##B##B##B##B##B##B##B##B##B##B##B##B##BDDDD", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################", "###########################################")
                    .where("D", Predicates.frames(TreatedWood))
                    .where("J", Predicates.blocks(TREATED_WOOD_PLANK.get()), false)
                    .where("G", Predicates.blocks(WATER))
                    .where("I", Predicates.blocks(AllBlocks.METAL_GIRDER.get()))
                    .where("#", Predicates.any())
                    .where("E", Predicates.blocks(AllBlocks.ANDESITE_CASING.get()))
                    .where("A", Predicates.blocks(STONE_BRICK_WALL))
                    .where("K", Predicates.blocks(MATERIAL_BLOCKS.get(TagPrefix.block, CTPPMaterials.AndesiteAlloy).get()))
                    .where("H", Predicates.blocks(AllBlocks.INDUSTRIAL_IRON_BLOCK.get()))
                    .where("L", Predicates.blocks(AllBlocks.ITEM_VAULT.get()))
                    .where("C", Predicates.frames(TreatedWood), false)
                    .where("B", Predicates.blocks(STONE_BRICKS))
                    .where("M", Predicates.blocks(STONE_BRICKS)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.abilities(CTPPPartAbility.OUTPUT_KINETIC)).setMinGlobalLimited(1))
                    .where("F", Predicates.blocks(TREATED_WOOD_PLANK.get()))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .build())
            .workableCasingModel(ResourceLocation.tryParse("minecraft:block/stone_bricks"), GTCEu.id("block/multiblock/implosion_compressor"))
            .register();
    public static MultiblockMachineDefinition TEST = REGISTRATE.multiblock("test", ComplexRotatingMachine::new)
            .rotationState(RotationState.ALL)
            .recipeType(CTPPRecipeTypes.BOOM_OF_CREATE)
            .appearanceBlock(CASING_STEEL_SOLID)
            .pattern(definition -> FactoryStaticBlockPattern.start()
                    .aisle("A###B", "#####", "#####", "#####", "E###F")
                    .aisle("#####", "#III#", "#III#", "#III#", "#####")
                    .aisle("#####", "#III#", "#III#", "#III#", "#####")
                    .aisle("#####", "#III#", "#I@I#", "#III#", "#####")
                    .aisle("C###D", "#####", "#####", "#####", "G###H")
                    .where("A",Predicates.blocks(CASING_STEEL_SOLID.get()), false, 0)
                    .where("B",Predicates.blocks(CASING_STEEL_SOLID.get()), false, 1)
                    .where("C",Predicates.blocks(CASING_STEEL_SOLID.get()), false, 2)
                    .where("D",Predicates.blocks(CASING_STEEL_SOLID.get()), false, 3)
                    .where("E",Predicates.blocks(CASING_STEEL_SOLID.get()), false, 4)
                    .where("F",Predicates.blocks(CASING_STEEL_SOLID.get()), false, 5)
                    .where("G",Predicates.blocks(CASING_STEEL_SOLID.get()), false, 6)
                    .where("H",Predicates.blocks(CASING_STEEL_SOLID.get()), false, 7)
                    .where("I",Predicates.blocks(CASING_STEEL_SOLID.get())
                            .or(Predicates.autoAbilities(definition.getRecipeTypes())))
                    .where("@",Predicates.controller(Predicates.blocks(definition.get())))
                    .where("#",Predicates.any())
                    .build()
            )
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"), GTCEu.id("block/multiblock/generator/large_steam_turbine"))
            .register();
}
