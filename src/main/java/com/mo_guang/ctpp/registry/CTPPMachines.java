package com.mo_guang.ctpp.registry;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;

import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.CTPPRegistration;
import com.mo_guang.ctpp.api.CTPPPartAbility;
import com.mo_guang.ctpp.common.blockentity.KineticMachineBlockEntity;
import com.mo_guang.ctpp.common.machine.simple.CarbonBrushesGeneratorMachine;
import com.mo_guang.ctpp.common.machine.simple.ElectricGearBoxMachine;
import com.mo_guang.ctpp.api.KineticMachineDefinition;
import com.mo_guang.ctpp.common.machine.multiblock.part.KineticPartMachine;
import com.mo_guang.ctpp.common.block.KineticMachineBlock;
import com.mo_guang.ctpp.common.machine.multiblock.part.MechanicalUpgradePartMachine;
import com.mo_guang.ctpp.config.MainConfig;
import com.mo_guang.ctpp.util.CommonTooltips;
import com.mo_guang.ctpp.util.ItemAxisBuilder;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.CN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.EN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.Prefix;

import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.GTValues.ALL_TIERS;
import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.*;
import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;
import static com.mo_guang.ctpp.common.data.model.CTPPMachineModels.createTieredCustomModel;
import static com.mo_guang.ctpp.registry.CTPPCreativeModeTabs.MACHINE;
import static com.mo_guang.ctpp.config.ConfigUtils.*;

@Prefix("machine")
public class CTPPMachines {
    static {
        REGISTRATE.creativeModeTab(() -> MACHINE);
    }
    public static MachineDefinition MECHANICAL_UPGRADE_BUS;

    public static KineticMachineDefinition[] ELECTRIC_GEAR_BOX_2A;
    public static KineticMachineDefinition[] ELECTRIC_GEAR_BOX_8A;
    public static KineticMachineDefinition[] ELECTRIC_GEAR_BOX_16A;
    public static KineticMachineDefinition[] ELECTRIC_GEAR_BOX_32A;
//    public static final KineticMachineDefinition[] KINETIC_MIXER = CTPPRegistration.conditionalRegistration(gtmEnabled("GTMKineticCreateMixer"),() ->
//        registerSimpleKineticElectricMachine("kinetic_mixer",CTPPRecipeTypes.KINETIC_MIXER_RECIPES, LOW_TIERS));
    public static KineticMachineDefinition[] KINETIC_INPUT_BOX;
    public static KineticMachineDefinition[] KINETIC_OUTPUT_BOX;
    public static KineticMachineDefinition CARBON_BRUSHES;

    @SuppressWarnings("unchecked")
    public static KineticMachineDefinition[] registerElectricGearBox(int maxAmps, int... tiers) {
        return CTPPRegistration.conditionalRegistration(gtmEnabled("GTMElectricGearBox"),() -> 
                registerKineticTieredMachines("electric_gear_box_%sa".formatted(maxAmps),
                "%sA电力齿轮箱".formatted(maxAmps),
                (tier, id) -> new KineticMachineDefinition(id, true, GTValues.V[tier]).setFrontRotation(true),
                (holder, tier) -> new ElectricGearBoxMachine(holder, tier, maxAmps), (tier, builder) -> builder
                                .langValue(VNF[tier] + " %sA Electric Gear Box".formatted(maxAmps))
                        .rotationState(RotationState.ALL)
                        .model(createTieredCustomModel(
                                CTPP.id("block/machine/electric_gear_box")))
                        .tier(tier)
                        .register(),
                         tiers));
    }

//    public static KineticMachineDefinition[] registerSimpleKineticElectricMachine(String name, GTRecipeType recipeType,
//                                                                                  int... tiers) {
//        return registerKineticTieredMachines(name, (tier, id) -> new KineticMachineDefinition(id, false, GTValues.V[tier]),
//                (holder, tier) -> new SimpleKineticElectricWorkableMachine(holder, tier, defaultTankSizeFunction),
//                (tier, builder) -> builder
//                        .langValue("%s %s %s".formatted(VLVH[tier], toEnglishName(name), VLVT[tier]))
//                        .rotationState(RotationState.NON_Y_AXIS)
//                        .editableUI(SimpleTieredMachine.EDITABLE_UI_CREATOR.apply(GTCEu.id(name), recipeType))
//                        .recipeType(recipeType)
//                        .recipeModifier(
//                                GTRecipeModifiers.ELECTRIC_OVERCLOCK.apply(OverclockingLogic.NON_PERFECT_OVERCLOCK))
//                        .model(createWorkableTieredCustomMachineModel(CTPP.id("block/machine/kinetic_electric_machine"),GTCEu.id("block/machines/"+name))
//                        )
//                        .tier(tier)
//                        .tooltips(explosion())
//                        .tooltips(workableTiered(tier, GTValues.V[tier], GTValues.V[tier] * 64, recipeType,
//                                defaultTankSizeFunction.apply(tier), true))
//                        .register(),
//                 tiers);
//    }


    public static KineticMachineDefinition[] registerKineticTieredMachines(String name,
                                                                    String cnname,
                                                                    BiFunction<Integer, ResourceLocation, KineticMachineDefinition> definitionFactory,
                                                                    BiFunction<IMachineBlockEntity, Integer, MetaMachine> factory,
                                                                    BiFunction<Integer, MachineBuilder<KineticMachineDefinition>, KineticMachineDefinition> builder,
                                                                    int... tiers) {
        KineticMachineDefinition[] definitions = new KineticMachineDefinition[GTValues.TIER_COUNT];
        for (int tier : tiers) {
            var register = REGISTRATE.machine(VN[tier].toLowerCase(Locale.ROOT) + "_" + name,
                            VNF[tier] + cnname,
                            id -> definitionFactory.apply(tier, id),
                            holder -> factory.apply(holder, tier),
                            KineticMachineBlock::new,
                            MetaMachineItem::new,
                            KineticMachineBlockEntity::create)
                    .tier(tier)
                    .blockProp(BlockBehaviour.Properties::noOcclusion)
                    .onBlockEntityRegister(KineticMachineBlockEntity::onBlockEntityRegister)
                    .itemBuilder(ItemAxisBuilder::addShaft)
                    ;
            definitions[tier] = builder.apply(tier, register);
        }
        return definitions;
    }

    @CN("输出发电机线圈产生的能量")
    @EN("Energy Output for Generator Coil")
    static Lang carbon_brushes;

    @CN("§e最大输出电流§r %sA")
    @EN("§eMax Output Amperage:§r %sA")
    static Lang max_output_amperage;

    public static void init() {
        KINETIC_INPUT_BOX = registerKineticTieredMachines(
                "kinetic_input_box",
                "应力输入箱",
                (tier, id) -> new KineticMachineDefinition(id, false, GTValues.V[tier]*MainConfig.INSTANCE.gtmConfig.kineticInputBoxTorqueMultiplier).setFrontRotation(true),
                (holder, tier) -> new KineticPartMachine(holder, tier, IO.IN), (tier, builder) -> builder
                        .langValue(VNF[tier] + " Kinetic Input Box")
                        .tooltips(
                                Component.translatable("ctpp.machine.kinetic_input_box.tooltip",
                                        FormattingUtil.formatNumbers(GTValues.V[tier]*MainConfig.INSTANCE.gtmConfig.kineticInputBoxTorqueMultiplier)
                                ),
                                Component.translatable("gtceu.part_sharing.disabled")
                        )
                        .rotationState(RotationState.ALL)
                        .abilities(CTPPPartAbility.INPUT_KINETIC)
                        .modelProperty(GTMachineModelProperties.IS_FORMED, false)
                        .model(createTieredCustomModel(CTPP.id("block/machine/part/kinetic_input_box"))
                                .andThen((ctx, prov, model) ->
                                        model.addReplaceableTextures("bottom", "top", "side")))
                        .tier(tier)
                        .register(),
                ALL_TIERS);

        KINETIC_OUTPUT_BOX = registerKineticTieredMachines("kinetic_output_box",
                "应力输出箱",
                        (tier, id) -> new KineticMachineDefinition(id, true, GTValues.V[tier] * MainConfig.INSTANCE.gtmConfig.kineticOutputBoxTorqueMultiplier).setFrontRotation(true),
                        (holder, tier) -> new KineticPartMachine(holder, tier, IO.OUT), (tier, builder) -> builder
                        .langValue(VNF[tier] + " Kinetic Output Box")
                                .tooltips(
                                        Component.translatable("ctpp.machine.kinetic_output_box.tooltip",
                                                FormattingUtil.formatNumbers(GTValues.V[tier]*MainConfig.INSTANCE.gtmConfig.kineticInputBoxTorqueMultiplier)
                                        ),
                                        Component.translatable("gtceu.part_sharing.disabled")
                                )
                                .rotationState(RotationState.ALL)
                                .abilities(CTPPPartAbility.OUTPUT_KINETIC)
                                .modelProperty(GTMachineModelProperties.IS_FORMED, false)
                                .model(createTieredCustomModel(CTPP.id("block/machine/part/kinetic_output_box"))
                                        .andThen((ctx, prov, model) ->
                                                model.addReplaceableTextures("bottom", "top", "side"))
                                )
                                .tier(tier)
                                .register(),
                        ALL_TIERS);

        CARBON_BRUSHES = REGISTRATE.machine("carbon_brushes",
                "碳刷",
                id -> new KineticMachineDefinition(id, false, GTValues.V[LV] * MainConfig.INSTANCE.gtmConfig.kineticOutputBoxTorqueMultiplier).setFrontRotation(true),
                holder -> new CarbonBrushesGeneratorMachine(holder, LV, genericGeneratorTankSizeFunction),
                KineticMachineBlock::new,
                MetaMachineItem::new,
                KineticMachineBlockEntity::create)
                .model((ctx, prov, builder) -> {
                    ModelFile parentModel = prov.models().getExistingFile(ResourceLocation.tryParse("create_new_age:block/generation/carbon_brushes"));
                    BlockModelBuilder model = prov.models().nested().parent(parentModel);
                    builder.forAllStatesModels(state -> model);
                })
                .rotationState(RotationState.ALL)
                .recipeType(GTRecipeTypes.DUMMY_RECIPES)
                .tier(LV)
                .itemBuilder(ItemAxisBuilder::addShaft)
                .tooltips(List.of(
                        Component.translatable("gtceu.universal.tooltip.voltage_out", FormattingUtil.formatNumbers(V[LV]), VNF[LV]),
                        max_output_amperage.translate("16"),
                        Component.translatable("gtceu.universal.tooltip.energy_storage_capacity", FormattingUtil.formatNumbers(2048)),
                        carbon_brushes.translate()
                ))
                .hasBER(false)
                .blockProp(BlockBehaviour.Properties::noOcclusion)
                .onBlockEntityRegister(KineticMachineBlockEntity::onBlockEntityRegister)
                .register();

        ELECTRIC_GEAR_BOX_2A = registerElectricGearBox(2, LOW_TIERS);
        ELECTRIC_GEAR_BOX_8A = registerElectricGearBox(8, LOW_TIERS);
        ELECTRIC_GEAR_BOX_16A = registerElectricGearBox(16, LOW_TIERS);
        ELECTRIC_GEAR_BOX_32A = registerElectricGearBox(32, LOW_TIERS);

        MECHANICAL_UPGRADE_BUS = REGISTRATE.machine("mechanical_upgrade_bus", MechanicalUpgradePartMachine::new)
                .cnLangValue("机械升级仓")
                .langValue("Mechanical Upgrade Bus")
                .tooltips(CommonTooltips.MECHANICAL_TIER)
                .tier(LV)
                .rotationState(RotationState.ALL)
                .abilities(CTPPPartAbility.MECHANICAL_UPGRADE)
                .modelProperty(GTMachineModelProperties.IS_FORMED, false)
                .overlayTieredHullModel(GTCEu.id("block/machine/part/item_passthrough_hatch"))
                .register();
    }
}
