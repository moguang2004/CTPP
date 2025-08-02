package com.mo_guang.ctpp.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.block.IMachineBlock;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.client.renderer.machine.OverlayTieredActiveMachineRenderer;
import com.gregtechceu.gtceu.client.renderer.machine.OverlayTieredMachineRenderer;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.registry.GTRegistration;
import com.mo_guang.ctpp.CTPPRegistration;
import com.mo_guang.ctpp.api.CTPPPartAbility;
import com.mo_guang.ctpp.client.SplitShaftVisual;
import com.mo_guang.ctpp.common.blockentity.KineticMachineBlockEntity;
import com.mo_guang.ctpp.common.machine.ElectricGearBoxMachine;
import com.mo_guang.ctpp.common.machine.KineticMachineDefinition;
import com.mo_guang.ctpp.common.machine.KineticPartMachine;
import com.mo_guang.ctpp.common.machine.SimpleKineticElectricWorkableMachine;
import com.mo_guang.ctpp.common.block.KineticMachineBlock;
import com.mo_guang.ctpp.common.machine.multiblock.part.MechanicalUpgradePartMachine;
import com.mo_guang.ctpp.config.MainConfig;
import com.mo_guang.ctpp.render.KineticWorkableTieredHullMachineRenderer;
import com.mo_guang.ctpp.render.SplitShaftTieredHullMachineRenderer;
import com.mo_guang.ctpp.util.CommonTooltips;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.GTValues.ALL_TIERS;
import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.*;
import static com.gregtechceu.gtceu.utils.FormattingUtil.toEnglishName;
import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;
import static com.mo_guang.ctpp.core.CTPPCreativeModeTabs.MACHINE;
import static com.mo_guang.ctpp.config.ConfigUtils.*;

public class CTPPMachines {
    static {
        REGISTRATE.creativeModeTab(() -> MACHINE);
    }
    public static final MachineDefinition MECHANICAL_UPGRADE_BUS = REGISTRATE.machine("mechanical_upgrade_bus", MechanicalUpgradePartMachine::new)
            .langValue("Mechanical Upgrade Bus")
            .tooltips(CommonTooltips.MECHANICAL_TIER)
            .tier(ULV)
            .rotationState(RotationState.ALL)
            .abilities(CTPPPartAbility.MECHANICAL_UPGRADE)
            .workableTieredHullModel(GTCEu.id("block/machine/part/item_bus.import"))
            .register();

    public static final KineticMachineDefinition[] ELECTRIC_GEAR_BOX_2A = registerElectricGearBox(2, LOW_TIERS);
    public static final KineticMachineDefinition[] ELECTRIC_GEAR_BOX_8A = registerElectricGearBox(8, LOW_TIERS);
    public static final KineticMachineDefinition[] ELECTRIC_GEAR_BOX_16A = registerElectricGearBox(16, LOW_TIERS);
    public static final KineticMachineDefinition[] ELECTRIC_GEAR_BOX_32A = registerElectricGearBox(32, LOW_TIERS);
    public static final KineticMachineDefinition[] KINETIC_MIXER = CTPPRegistration.conditionalRegistration(gtmEnabled("GTMKineticCreateMixer"),() -> 
        registerSimpleKineticElectricMachine("kinetic_mixer",CTPPRecipeTypes.KINETIC_MIXER_RECIPES, LOW_TIERS));
    
    public static final KineticMachineDefinition[] KINETIC_INPUT_BOX = registerKineticTieredMachines("kinetic_input_box",
            (tier, id) -> new KineticMachineDefinition(id, false, GTValues.V[tier]*MainConfig.INSTANCE.gtmConfig.kineticInputBoxTorqueMultiplier).setFrontRotation(true),
            (holder, tier) -> new KineticPartMachine(holder, tier, IO.IN), (tier, builder) -> builder
                    .langValue("%s %s %s".formatted(VLVH[tier], toEnglishName("kinetic_input_box"), VLVT[tier]))
                    .rotationState(RotationState.ALL)
                    .blockProp(BlockBehaviour.Properties::dynamicShape)
                    .blockProp(BlockBehaviour.Properties::noOcclusion)
                    .abilities(CTPPPartAbility.INPUT_KINETIC)
                    .model()
                    .renderer(() -> new SplitShaftTieredHullMachineRenderer(tier,
                            GTCEu.id("block/machine/part/kinetic_input_box")))
                    .register(),
            () -> (VisualizationContext var1, KineticMachineBlockEntity var2, float var3) -> new SplitShaftVisual(var1, var2, var3), false, ALL_TIERS);
    public static final KineticMachineDefinition[] KINETIC_OUTPUT_BOX = CTPPRegistration.conditionalRegistration(gtmEnabled("GTMKineticOutputBox"),() ->
            registerKineticTieredMachines("kinetic_output_box",
                (tier, id) -> new KineticMachineDefinition(id, true, GTValues.V[tier] * MainConfig.INSTANCE.gtmConfig.kineticOutputBoxTorqueMultiplier).setFrontRotation(true),
                (holder, tier) -> new KineticPartMachine(holder, tier, IO.OUT), (tier, builder) -> builder
                    .langValue("%s %s %s".formatted(VLVH[tier], toEnglishName("kinetic_output_box"), VLVT[tier]))
                    .rotationState(RotationState.ALL)
                    .blockProp(BlockBehaviour.Properties::dynamicShape)
                    .blockProp(BlockBehaviour.Properties::noOcclusion)
                    .abilities(CTPPPartAbility.OUTPUT_KINETIC)
                    .renderer(() -> new SplitShaftTieredHullMachineRenderer(tier,
                            GTCEu.id("block/machine/part/kinetic_output_box")))
                    .register(),
                    () -> (VisualizationContext var1, KineticMachineBlockEntity var2, float var3) -> new SplitShaftVisual(var1, var2, var3), false, ALL_TIERS));

    @SuppressWarnings("unchecked")
    public static KineticMachineDefinition[] registerElectricGearBox(int maxAmps, int... tiers) {
        return CTPPRegistration.conditionalRegistration(gtmEnabled("GTMElectricGearBox"),() -> 
                registerKineticTieredMachines("electric_gear_box_%sa".formatted(maxAmps),
                (tier, id) -> new KineticMachineDefinition(id, true, GTValues.V[tier]).setFrontRotation(true),
                (holder, tier) -> new ElectricGearBoxMachine(holder, tier, maxAmps), (tier, builder) -> builder
                        .langValue(
                                "%s %s %s".formatted(VLVH[tier], "Electric Gearbox %dA".formatted(maxAmps), VLVT[tier]))
                        .rotationState(RotationState.ALL)
                        .blockProp(BlockBehaviour.Properties::dynamicShape)
                        .blockProp(BlockBehaviour.Properties::noOcclusion)
                        .renderer(() -> new SplitShaftTieredHullMachineRenderer(tier,
                                GTCEu.id("block/machine/electric_gear_box_%sa".formatted(maxAmps))))
                        .tooltips(explosion())
                        .register(),
                        () -> (VisualizationContext var1, KineticMachineBlockEntity var2, float var3) -> new SplitShaftVisual(var1, var2, var3), false, tiers));
    }

    public static KineticMachineDefinition[] registerSimpleKineticElectricMachine(String name, GTRecipeType recipeType,
                                                                                  int... tiers) {
        return registerKineticTieredMachines(name, (tier, id) -> new KineticMachineDefinition(id, false, GTValues.V[tier]),
                (holder, tier) -> new SimpleKineticElectricWorkableMachine(holder, tier, defaultTankSizeFunction),
                (tier, builder) -> builder
                        .langValue("%s %s %s".formatted(VLVH[tier], toEnglishName(name), VLVT[tier]))
                        .rotationState(RotationState.NON_Y_AXIS)
                        .editableUI(SimpleTieredMachine.EDITABLE_UI_CREATOR.apply(GTCEu.id(name), recipeType))
                        .blockProp(BlockBehaviour.Properties::dynamicShape)
                        .blockProp(BlockBehaviour.Properties::noOcclusion)
                        .recipeType(recipeType)
                        .recipeModifier(
                                GTRecipeModifiers.ELECTRIC_OVERCLOCK.apply(OverclockingLogic.NON_PERFECT_OVERCLOCK))
                        .renderer(() -> new KineticWorkableTieredHullMachineRenderer(tier,
                                GTCEu.id("block/machine/kinetic_electric_machine"), GTCEu.id("block/machines/" + name)))
                        .tooltips(explosion())
                        .tooltips(workableTiered(tier, GTValues.V[tier], GTValues.V[tier] * 64, recipeType,
                                defaultTankSizeFunction.apply(tier), true))
                        .register(),
                () -> (VisualizationContext var1, KineticMachineBlockEntity var2, float var3) -> new SplitShaftVisual(var1, var2, var3), false, tiers);
    }

    public static MachineBuilder<KineticMachineDefinition> registerMachines(String name,
                                                                            Function<ResourceLocation, KineticMachineDefinition> definitionFactory,
                                                                            Function<IMachineBlockEntity, MetaMachine> factory,
                                                                            @Nullable NonNullSupplier<SimpleBlockEntityVisualizer.Factory<? extends KineticBlockEntity>> visualFactory,
                                                                            boolean renderNormally) {
        return REGISTRATE
                .machine(name, definitionFactory, factory, KineticMachineBlock::new, MetaMachineItem::new,
                        KineticMachineBlockEntity::create)
                .hasTESR(visualFactory != null)
                .onBlockEntityRegister(
                        type -> KineticMachineBlockEntity.onBlockEntityRegister(type, visualFactory, renderNormally));
    }

    public static KineticMachineDefinition[] registerKineticTieredMachines(String name,
                                                                    BiFunction<Integer, ResourceLocation, KineticMachineDefinition> definitionFactory,
                                                                    BiFunction<IMachineBlockEntity, Integer, MetaMachine> factory,
                                                                    BiFunction<Integer, MachineBuilder<KineticMachineDefinition>, KineticMachineDefinition> builder,
                                                                    @Nullable NonNullSupplier<SimpleBlockEntityVisualizer.Factory<? extends KineticBlockEntity>> visualFactory,
                                                                    boolean renderNormally,
                                                                    int... tiers) {
        KineticMachineDefinition[] definitions = new KineticMachineDefinition[GTValues.TIER_COUNT];
        for (int tier : tiers) {
            var register = REGISTRATE.machine(GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_" + name,
                            id -> definitionFactory.apply(tier, id),
                            holder -> factory.apply(holder, tier),
                            KineticMachineBlock::new,
                            MetaMachineItem::new,
                            KineticMachineBlockEntity::create)
                    .tier(tier)
                    .hasTESR(visualFactory != null)
                    .onBlockEntityRegister(type -> KineticMachineBlockEntity.onBlockEntityRegister(type,
                            visualFactory, renderNormally));
            definitions[tier] = builder.apply(tier, register);
        }
        return definitions;
    }

    public static void init() {
    }
}
