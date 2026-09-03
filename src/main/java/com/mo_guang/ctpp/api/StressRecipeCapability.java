package com.mo_guang.ctpp.api;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeDefinition;
import com.gregtechceu.gtceu.api.recipe.handler.RecipeHandlerGroup;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.mo_guang.ctpp.common.machine.NotifiableStressTrait;
import com.mo_guang.ctpp.data.recipe.builder.CTPPRecipeHelper;
import com.mojang.serialization.Codec;
import com.simibubi.create.AllBlocks;
import org.apache.commons.lang3.mutable.MutableInt;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

import java.util.List;

public class StressRecipeCapability extends RecipeCapability<Float> {

    public final static StressRecipeCapability CAP = new StressRecipeCapability();

    @CN("应力")
    @EN("Create Stress")
    static Lang capabilityName;

    @CN("应力输入：§b%s su§r")
    @EN("Stress Input: §b%s su§r")
    static Lang stressInput;

    @CN("应力输出：§b%s su§r")
    @EN("Stress Output: §b%s su§r")
    static Lang stressOutput;

    @CN("应力消耗：")
    @EN("Stress Consumption: ")
    static Lang stressConsumption;

    @CN("应力产出：")
    @EN("Stress Production: ")
    static Lang stressProduction;

    protected StressRecipeCapability() {
        super("su", 0xFF77A400, false, Codec.FLOAT);
    }

    @Override
    public MutableComponent getName() {
        return capabilityName.translate();
    }

    @Override
    public Float fromNetwork(FriendlyByteBuf friendlyByteBuf) {
        return friendlyByteBuf.readFloat();
    }

    @Override
    public void toNetwork(Float ingredient, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeFloat(ingredient);
    }

    @Override
    public Float copyWithMultiplier(Float content, float multiplier) {
        return content * multiplier;
    }

    @Override
    public int getMaxParallelByInput(RecipeHandlerGroup holder, GTRecipe recipe, int parallelAmount,
                                     boolean tick) {
        float inputStress = holder.getInputHandlerMap().getOrDefault(this, List.of()).stream()
                .filter(NotifiableStressTrait.class::isInstance)
                .map(NotifiableStressTrait.class::cast)
                .map(NotifiableStressTrait::getContents)
                .flatMap(List::stream)
                .filter(Float.class::isInstance)
                .map(Float.class::cast)
                .reduce(0f, Float::sum);
        float recipeStress = CTPPRecipeHelper.getInputStress(recipe);
        if (recipeStress == 0) return parallelAmount;
        return (int) Math.min(Math.max(inputStress, 0) / recipeStress, parallelAmount);
    }

    @Override
    public int limitMaxParallelByOutput(RecipeHandlerGroup holder, GTRecipe recipe, int maxMultiplier,
                                        boolean tick) {
        float outputStress = holder.getOutputHandlerMap().getOrDefault(this, List.of()).stream()
                .filter(NotifiableStressTrait.class::isInstance)
                .map(NotifiableStressTrait.class::cast)
                .map(NotifiableStressTrait::getContents)
                .flatMap(List::stream)
                .filter(Float.class::isInstance)
                .map(Float.class::cast)
                .reduce(0f, Float::sum);
        float recipeStress = CTPPRecipeHelper.getOutputStress(recipe);
        if (recipeStress == 0) return maxMultiplier;
        return (int) Math.min(Math.abs(outputStress) / recipeStress, maxMultiplier);
    }

    @Override
    public void addXEIInfo(WidgetGroup group, int xOffset,
                           GTRecipeDefinition recipe, List<Float> contents,
                           int duration, boolean perTick, boolean isInput, MutableInt yOffset) {
        float stress = contents.stream().reduce(0f, Float::sum);
        group.addWidget(new LabelWidget(3 - xOffset, yOffset.addAndGet(10),
                (isInput ? stressInput : stressOutput)
                        .translate(FormattingUtil.formatNumbers(stress)).getString()));
        var handler = new CustomItemStackHandler(AllBlocks.COGWHEEL.asStack());
        group.addWidget(new SlotWidget(handler, 0, group.getSize().width - 30,
                yOffset.getValue(), false, false));
    }

    @Override
    public void appendJadeRecipeTooltip(IO io, boolean tick, List<Float> contents, RecipeLogic logic,
                                        ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (tick) return;
        float stress = contents.stream().reduce(0f, Float::sum);
        if (stress == 0) return;
        tooltip.add((io == IO.IN ? stressConsumption : stressProduction)
                .translate()
                .append(Component.literal(FormattingUtil.formatNumbers(stress)).withStyle(ChatFormatting.AQUA)));
    }
}
