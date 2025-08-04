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
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.api.CTPPPartAbility;
import com.mo_guang.ctpp.common.machine.multiblock.*;
import com.mo_guang.ctpp.util.CommonTooltips;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import com.mo_guang.ctpp.CTPPRegistration;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.common.data.GTBlocks.*;
import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;
import static com.mo_guang.ctpp.config.ConfigUtils.*;

public class CTPPMultiblockMachines {
    public static void init() {}

    public static MultiblockMachineDefinition SMASHING_FACTORY = CTPPRegistration.conditionalRegistration(
        ctnhEnabled("SmashingFactory"),
        () -> REGISTRATE.multiblock("smashing_factory", KineticWorkableMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
                .tooltips(CommonTooltips.KINETIC_OVERCLOCK)
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
                            .or(Predicates.abilities(CTPPPartAbility.INPUT_KINETIC))
                            .or(Predicates.abilities(CTPPPartAbility.MECHANICAL_UPGRADE).setMaxGlobalLimited(1)))
                    .where("C",Predicates.blocks(AllBlocks.CRUSHING_WHEEL.get()))
                    .where("@",Predicates.controller(blocks(definition.getBlock())))
                    .where(" ",Predicates.any())
                    .build())
            .workableCasingModel(CTPP.id("block/create/andesite_casing"),GTCEu.id("block/multiblock/large_chemical_reactor"))
            .register());
    public static MultiblockMachineDefinition KINETIC_GENERATOR = CTPPRegistration.conditionalRegistration(
        ctnhEnabled("KineticGenerator"),
        () -> REGISTRATE.multiblock("kinetic_generator", KineticGeneratorMachine::new)
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
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(CTPPRecipeTypes.KINETIC_STEAM_TURBINE_RECIPES)
            .appearanceBlock(GTBlocks.CASING_BRONZE_BRICKS)
            .tooltips(Component.translatable("ctpp.multiblock.kinetic_steam_turbine.tooltip.0"),
                    Component.translatable("ctpp.multiblock.kinetic_steam_turbine.tooltip.1"),
                    Component.translatable("ctpp.multiblock.kinetic_steam_turbine.tooltip.2").withStyle(ChatFormatting.RED),
                    Component.translatable("ctpp.multiblock.kinetic_steam_turbine.tooltip.3"),
                    CommonTooltips.MECHANICAL_TIER_MACHINE)
            .recipeModifiers(KineticTurbineMachine::recipeModifier,CTPPRecipeModifiers.KINETIC_ADJUST)
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
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(CTPPRecipeTypes.SEAWEED_FARM)
            .recipeModifier(CTPPRecipeModifiers.KINETIC_OVERCLOCK)
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
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(CTPPRecipeTypes.WINDMILL_CONTROL)
            .appearanceBlock(AllBlocks.BRASS_CASING)
            .recipeModifiers(WindMillControlMachine::recipeModifier,CTPPRecipeModifiers.KINETIC_ADJUST)
            .tooltips(Component.translatable("ctpp.multiblock.windmill_control_center.tooltip.0").withStyle(ChatFormatting.GRAY),
                    Component.translatable("ctpp.multiblock.windmill_control_center.tooltip.1"),
                    CommonTooltips.MECHANICAL_TIER_MACHINE,
                    Component.translatable("ctpp.multiblock.windmill_control_center.tooltip.2").withStyle(ChatFormatting.RED))
            .pattern(definition -> FactoryBlockPattern.start()
                .aisle("BBBBB", "CBBBC", "CBBBC", "#CCC#")
                .aisle("BBBBB", "BDBDB", "BDBDB", "#CEC#")
                .aisle("BBBBB", "CB@BC", "CBBBC", "#CCC#")
                .where("A", Predicates.blocks(Blocks.STONE))
                .where("B", Predicates.blocks(AllBlocks.BRASS_CASING.get())
                    .or(Predicates.abilities(CTPPPartAbility.OUTPUT_KINETIC)
                            .or(Predicates.abilities(CTPPPartAbility.MECHANICAL_UPGRADE).setMaxGlobalLimited(1)))
                .or(Predicates.autoAbilities(definition.getRecipeTypes())))
                .where("C", Predicates.frames(GTMaterials.TreatedWood))
                .where("#", Predicates.any())
                .where("D", Predicates.blocks(CASING_STEEL_SOLID.get()))
                    .where("E", Predicates.blocks(AllBlocks.WATER_WHEEL.get()))
                .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                .build())
            .workableCasingModel(CTPP.id("block/create/brass_casing"), GTCEu.id("block/machines/miner"))
            .register());
    public static MultiblockMachineDefinition BOOM_OF_CREATE = CTPPRegistration.conditionalRegistration(
        ctnhEnabled("BoomOfCreate"),
        () -> REGISTRATE.multiblock("boom_of_create", KineticOutputMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(CTPPRecipeTypes.BOOM_OF_CREATE)
            .appearanceBlock(CASING_STEEL_SOLID)
            .recipeModifier(CTPPRecipeModifiers.KINETIC_ADJUST)
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
    public static MultiblockMachineDefinition TEST = REGISTRATE.multiblock("test", KineticWorkableMultiblockMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.MIXER_RECIPES)
            .appearanceBlock(AllBlocks.ANDESITE_CASING::get)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("AA","BB","BB")
                    .aisle("AA","KB","BB")
                    .where("A", Predicates.blocks(AllBlocks.BLAZE_BURNER.get()))
                    .where("K", Predicates.controller(Predicates.blocks(definition.get())))
                    .where("B", Predicates.blocks(AllBlocks.ANDESITE_CASING.get())
                            .or(Predicates.autoAbilities(definition.getRecipeTypes())))
                    .build()
            )
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"), GTCEu.id("block/multiblock/generator/large_steam_turbine"))
            .register();
}
