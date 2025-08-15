package com.mo_guang.ctpp.api;

import com.gregtechceu.gtceu.api.block.IMachineBlock;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.gui.editor.EditableMachineUI;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.api.registry.registrate.MultiblockMachineBuilder;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.function.*;

public class CTPPMultiblockBuilder extends MultiblockMachineBuilder {
    protected CTPPMultiblockBuilder(GTRegistrate registrate, String name, Function<IMachineBlockEntity, ? extends MultiblockControllerMachine> metaMachine, BiFunction<BlockBehaviour.Properties, MultiblockMachineDefinition, IMachineBlock> blockFactory, BiFunction<IMachineBlock, Item.Properties, MetaMachineItem> itemFactory, TriFunction<BlockEntityType<?>, BlockPos, BlockState, IMachineBlockEntity> blockEntityFactory) {
        super(registrate, name, metaMachine, blockFactory, itemFactory, blockEntityFactory);
    }
    public static CTPPMultiblockBuilder createMulti(GTRegistrate registrate, String name, Function<IMachineBlockEntity, ? extends MultiblockControllerMachine> metaMachine, BiFunction<BlockBehaviour.Properties, MultiblockMachineDefinition, IMachineBlock> blockFactory, BiFunction<IMachineBlock, Item.Properties, MetaMachineItem> itemFactory, TriFunction<BlockEntityType<?>, BlockPos, BlockState, IMachineBlockEntity> blockEntityFactory) {
        return new CTPPMultiblockBuilder(registrate, name, metaMachine, blockFactory, itemFactory, blockEntityFactory);
    }

    public CTPPMultiblockBuilder shapeInfo(Function<MultiblockMachineDefinition, MultiblockShapeInfo> shape) {
        super.shapeInfo(shape);
        return this;
    }

    public CTPPMultiblockBuilder shapeInfos(Function<MultiblockMachineDefinition, List<MultiblockShapeInfo>> shapes) {
        super.shapeInfos(shapes);
        return this;
    }

    public CTPPMultiblockBuilder recoveryItems(Supplier<ItemLike[]> items) {
        this.recoveryItems(items);
        return this;
    }

    public CTPPMultiblockBuilder recoveryStacks(Supplier<ItemStack[]> stacks) {
        super.recoveryStacks(stacks);
        return this;
    }

    public CTPPMultiblockBuilder machine(Function<IMachineBlockEntity, MetaMachine> machine) {
        return (CTPPMultiblockBuilder)super.machine(machine);
    }

    public CTPPMultiblockBuilder shape(VoxelShape shape) {
        return (CTPPMultiblockBuilder)super.shape(shape);
    }

    public CTPPMultiblockBuilder multiblockPreviewRenderer(boolean multiBlockWorldPreview, boolean multiBlockXEIPreview) {
        return (CTPPMultiblockBuilder)super.multiblockPreviewRenderer(multiBlockWorldPreview, multiBlockXEIPreview);
    }

    public CTPPMultiblockBuilder rotationState(RotationState rotationState) {
        return (CTPPMultiblockBuilder)super.rotationState(rotationState);
    }

    public CTPPMultiblockBuilder blockProp(NonNullUnaryOperator<BlockBehaviour.Properties> blockProp) {
        return (CTPPMultiblockBuilder)super.blockProp(blockProp);
    }

    public CTPPMultiblockBuilder itemProp(NonNullUnaryOperator<Item.Properties> itemProp) {
        return (CTPPMultiblockBuilder)super.itemProp(itemProp);
    }

    public CTPPMultiblockBuilder blockBuilder(Consumer<BlockBuilder<? extends Block, ?>> blockBuilder) {
        return (CTPPMultiblockBuilder)super.blockBuilder(blockBuilder);
    }

    public CTPPMultiblockBuilder itemBuilder(Consumer<ItemBuilder<? extends MetaMachineItem, ?>> itemBuilder) {
        return (CTPPMultiblockBuilder)super.itemBuilder(itemBuilder);
    }

    public CTPPMultiblockBuilder recipeTypes(GTRecipeType... recipeTypes) {
        return (CTPPMultiblockBuilder)super.recipeTypes(recipeTypes);
    }

    public CTPPMultiblockBuilder recipeType(GTRecipeType recipeTypes) {
        return (CTPPMultiblockBuilder)super.recipeType(recipeTypes);
    }

    public CTPPMultiblockBuilder tier(int tier) {
        return (CTPPMultiblockBuilder)super.tier(tier);
    }

    public CTPPMultiblockBuilder recipeOutputLimits(Object2IntMap<RecipeCapability<?>> map) {
        return (CTPPMultiblockBuilder)super.recipeOutputLimits(map);
    }

    public CTPPMultiblockBuilder addOutputLimit(RecipeCapability<?> capability, int limit) {
        return (CTPPMultiblockBuilder)super.addOutputLimit(capability, limit);
    }

    public CTPPMultiblockBuilder itemColor(BiFunction<ItemStack, Integer, Integer> itemColor) {
        return (CTPPMultiblockBuilder)super.itemColor(itemColor);
    }

    public MultiblockMachineBuilder simpleModel(ResourceLocation model) {
        return (MultiblockMachineBuilder)super.simpleModel(model);
    }

    public MultiblockMachineBuilder defaultModel() {
        return (MultiblockMachineBuilder)super.defaultModel();
    }

    public MultiblockMachineBuilder tieredHullModel(ResourceLocation model) {
        return (MultiblockMachineBuilder)super.tieredHullModel(model);
    }

    public MultiblockMachineBuilder overlayTieredHullModel(String name) {
        return (MultiblockMachineBuilder)super.overlayTieredHullModel(name);
    }

    public MultiblockMachineBuilder overlayTieredHullModel(ResourceLocation overlayModel) {
        return (MultiblockMachineBuilder)super.overlayTieredHullModel(overlayModel);
    }

    public MultiblockMachineBuilder colorOverlayTieredHullModel(String overlay) {
        return (MultiblockMachineBuilder)super.colorOverlayTieredHullModel(overlay);
    }

    public MultiblockMachineBuilder colorOverlayTieredHullModel(String overlay, @Nullable String pipeOverlay, @Nullable String emissiveOverlay) {
        return (MultiblockMachineBuilder)super.colorOverlayTieredHullModel(overlay, pipeOverlay, emissiveOverlay);
    }

    public MultiblockMachineBuilder colorOverlayTieredHullModel(ResourceLocation overlay) {
        return (MultiblockMachineBuilder)super.colorOverlayTieredHullModel(overlay);
    }

    public MultiblockMachineBuilder colorOverlayTieredHullModel(ResourceLocation overlay, @Nullable ResourceLocation pipeOverlay, @Nullable ResourceLocation emissiveOverlay) {
        return (MultiblockMachineBuilder)super.colorOverlayTieredHullModel(overlay, pipeOverlay, emissiveOverlay);
    }

    public MultiblockMachineBuilder workableTieredHullModel(ResourceLocation workableModel) {
        return (MultiblockMachineBuilder)super.workableTieredHullModel(workableModel);
    }

    public MultiblockMachineBuilder simpleGeneratorModel(ResourceLocation workableModel) {
        return (MultiblockMachineBuilder)super.simpleGeneratorModel(workableModel);
    }

    public MultiblockMachineBuilder workableCasingModel(ResourceLocation baseCasing, ResourceLocation overlayModel) {
        return (MultiblockMachineBuilder)super.workableCasingModel(baseCasing, overlayModel);
    }

    public MultiblockMachineBuilder sidedOverlayCasingModel(ResourceLocation baseCasing, ResourceLocation workableModel) {
        return (MultiblockMachineBuilder)super.sidedOverlayCasingModel(baseCasing, workableModel);
    }

    public MultiblockMachineBuilder sidedWorkableCasingModel(ResourceLocation baseCasing, ResourceLocation workableModel) {
        return (MultiblockMachineBuilder)super.sidedWorkableCasingModel(baseCasing, workableModel);
    }

    public CTPPMultiblockBuilder tooltipBuilder(BiConsumer<ItemStack, List<Component>> tooltipBuilder) {
        return (CTPPMultiblockBuilder)super.tooltipBuilder(tooltipBuilder);
    }

    public CTPPMultiblockBuilder appearance(Supplier<BlockState> state) {
        return (CTPPMultiblockBuilder)super.appearance(state);
    }

    public CTPPMultiblockBuilder appearanceBlock(Supplier<? extends Block> block) {
        return (CTPPMultiblockBuilder)super.appearanceBlock(block);
    }

    public CTPPMultiblockBuilder langValue(String langValue) {
        return (CTPPMultiblockBuilder)super.langValue(langValue);
    }

    public CTPPMultiblockBuilder tooltips(Component... components) {
        return (CTPPMultiblockBuilder)super.tooltips(components);
    }

    public CTPPMultiblockBuilder conditionalTooltip(Component component, Supplier<Boolean> condition) {
        return (CTPPMultiblockBuilder) super.conditionalTooltip(component, (Boolean)condition.get());
    }

    public CTPPMultiblockBuilder conditionalTooltip(Component component, boolean condition) {
        if (condition) {
            this.tooltips(component);
        }

        return this;
    }

    public CTPPMultiblockBuilder abilities(PartAbility... abilities) {
        return (CTPPMultiblockBuilder)super.abilities(abilities);
    }

    public CTPPMultiblockBuilder paintingColor(int paintingColor) {
        return (CTPPMultiblockBuilder)super.paintingColor(paintingColor);
    }

    public CTPPMultiblockBuilder recipeModifier(RecipeModifier recipeModifier) {
        return (CTPPMultiblockBuilder)super.recipeModifier(recipeModifier);
    }

    public CTPPMultiblockBuilder recipeModifier(RecipeModifier recipeModifier, boolean alwaysTryModifyRecipe) {
        return (CTPPMultiblockBuilder)super.recipeModifier(recipeModifier, alwaysTryModifyRecipe);
    }

    public CTPPMultiblockBuilder recipeModifiers(RecipeModifier... recipeModifiers) {
        return (CTPPMultiblockBuilder)super.recipeModifiers(recipeModifiers);
    }

    public CTPPMultiblockBuilder recipeModifiers(boolean alwaysTryModifyRecipe, RecipeModifier... recipeModifiers) {
        return (CTPPMultiblockBuilder)super.recipeModifiers(alwaysTryModifyRecipe, recipeModifiers);
    }

    public CTPPMultiblockBuilder noRecipeModifier() {
        return (CTPPMultiblockBuilder)super.noRecipeModifier();
    }

    public CTPPMultiblockBuilder alwaysTryModifyRecipe(boolean alwaysTryModifyRecipe) {
        return (CTPPMultiblockBuilder)super.alwaysTryModifyRecipe(alwaysTryModifyRecipe);
    }

    public CTPPMultiblockBuilder beforeWorking(BiPredicate<IRecipeLogicMachine, GTRecipe> beforeWorking) {
        return (CTPPMultiblockBuilder)super.beforeWorking(beforeWorking);
    }

    public CTPPMultiblockBuilder onWorking(Predicate<IRecipeLogicMachine> onWorking) {
        return (CTPPMultiblockBuilder)super.onWorking(onWorking);
    }

    public CTPPMultiblockBuilder onWaiting(Consumer<IRecipeLogicMachine> onWaiting) {
        return (CTPPMultiblockBuilder)super.onWaiting(onWaiting);
    }

    public CTPPMultiblockBuilder afterWorking(Consumer<IRecipeLogicMachine> afterWorking) {
        return (CTPPMultiblockBuilder)super.afterWorking(afterWorking);
    }

    public CTPPMultiblockBuilder regressWhenWaiting(boolean dampingWhenWaiting) {
        return (CTPPMultiblockBuilder)super.regressWhenWaiting(dampingWhenWaiting);
    }

    public CTPPMultiblockBuilder editableUI(@Nullable EditableMachineUI editableUI) {
        return (CTPPMultiblockBuilder)super.editableUI(editableUI);
    }

    public CTPPMultiblockBuilder onBlockEntityRegister(NonNullConsumer<BlockEntityType<BlockEntity>> onBlockEntityRegister) {
        return (CTPPMultiblockBuilder)super.onBlockEntityRegister(onBlockEntityRegister);
    }
    @Generated
    public CTPPMultiblockBuilder generator(boolean generator) {
        super.generator(generator);
        return this;
    }

    @Generated
    public CTPPMultiblockBuilder pattern(Function<MultiblockMachineDefinition, BlockPattern> pattern) {
        super.pattern(pattern);
        return this;
    }

    @Generated
    public CTPPMultiblockBuilder allowExtendedFacing(boolean allowExtendedFacing) {
        super.allowExtendedFacing(allowExtendedFacing);
        return this;
    }

    @Generated
    public CTPPMultiblockBuilder allowFlip(boolean allowFlip) {
        super.allowFlip(allowFlip);
        return this;
    }

    @Generated
    public CTPPMultiblockBuilder partSorter(Comparator<IMultiPart> partSorter) {
        super.partSorter(partSorter);
        return this;
    }

    @Generated
    public CTPPMultiblockBuilder partAppearance(TriFunction<IMultiController, IMultiPart, Direction, BlockState> partAppearance) {
        super.partAppearance(partAppearance);
        return this;
    }

    @Generated
    public BiConsumer<IMultiController, List<Component>> additionalDisplay() {
        return super.additionalDisplay();
    }

    @Generated
    public CTPPMultiblockBuilder additionalDisplay(BiConsumer<IMultiController, List<Component>> additionalDisplay) {
        super.additionalDisplay(additionalDisplay);
        return this;
    }

    @Override
    public MultiblockMachineDefinition register() {
        this.tooltips(Component.literal("-----------------------------------"),
                Component.translatable("ctpp.copyright.info"));
        return super.register();
    }
}
