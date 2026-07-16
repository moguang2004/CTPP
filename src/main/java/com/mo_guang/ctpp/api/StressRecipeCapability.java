package com.mo_guang.ctpp.api;

import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeDefinition;
import com.gregtechceu.gtceu.api.recipe.handler.RecipeHandlerGroup;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;

import net.minecraft.network.FriendlyByteBuf;

import com.mo_guang.ctpp.common.data.recipe.builder.CTPPRecipeHelper;
import com.mo_guang.ctpp.common.machine.NotifiableStressTrait;
import com.mojang.serialization.Codec;
import com.simibubi.create.AllBlocks;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;

public class StressRecipeCapability extends RecipeCapability<Float> {

    public final static StressRecipeCapability CAP = new StressRecipeCapability();

    protected StressRecipeCapability() {
        super("su", 0xFF77A400, false, Codec.FLOAT);
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
    public Float copyInner(Float content, int multiplier) {
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
        String langKey = "ctpp." + (isInput ? "stress_input" : "stress_output");
        float stress = (float) contents.stream().mapToDouble(Float::doubleValue).sum();
        group.addWidget(new LabelWidget(3 - xOffset, yOffset.addAndGet(10),
                LocalizationUtils.format(langKey, FormattingUtil.formatNumbers(stress))));
        var handler = new CustomItemStackHandler(AllBlocks.COGWHEEL.asStack());
        group.addWidget(new SlotWidget(handler, 0, group.getSize().width - 30,
                yOffset.getValue(), false, false));
    }
}
