package com.mo_guang.ctpp.api;

import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.content.SerializerFloat;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;

import com.mo_guang.ctpp.common.data.recipe.builder.CTPPRecipeHelper;
import com.mo_guang.ctpp.common.machine.multiblock.KineticOutputMachine;
import com.mo_guang.ctpp.common.machine.multiblock.KineticWorkableMultiblockMachine;
import com.simibubi.create.AllBlocks;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Collection;
import java.util.List;

public class StressRecipeCapability extends RecipeCapability<Float> {

    public final static StressRecipeCapability CAP = new StressRecipeCapability();

    protected StressRecipeCapability() {
        super("su", 0xFF77A400, false, 44, SerializerFloat.INSTANCE);
    }

    @Override
    public Float copyInner(Float content) {
        return content;
    }

    @Override
    public Float copyWithModifier(Float content, ContentModifier modifier) {
        return modifier.apply(content);
    }

    @Override
    public List<Object> compressIngredients(Collection<Object> ingredients) {
        return List.of(ingredients.stream().map(Float.class::cast).reduce(0f, Float::sum));
    }

    @Override
    public int getMaxParallelByInput(IRecipeCapabilityHolder holder, GTRecipe recipe, int parallelAmount,
                                     boolean tick) {
        if (holder instanceof KineticWorkableMultiblockMachine machine) {
            float inputStress = Math.max(machine.getTotalInputStress(), 0);
            float recipeStress = CTPPRecipeHelper.getInputStress(recipe);
            if (recipeStress == 0) return parallelAmount;
            return (int) Math.min(inputStress / recipeStress, parallelAmount);
        }
        return super.getMaxParallelByInput(holder, recipe, parallelAmount, tick);
    }

    @Override
    public int limitMaxParallelByOutput(IRecipeCapabilityHolder holder, GTRecipe recipe, int maxMultiplier,
                                        boolean tick) {
        if (holder instanceof KineticOutputMachine kineticOutputMachine) {
            float outputStress = Math.abs(kineticOutputMachine.getMaxOutputStress());
            float recipeStress = CTPPRecipeHelper.getOutputStress(recipe);
            if (recipeStress == 0) return maxMultiplier;
            return (int) Math.min(outputStress / recipeStress, maxMultiplier);
        }
        return super.limitMaxParallelByOutput(holder, recipe, maxMultiplier, tick);
    }

    @Override
    public void addXEIInfo(WidgetGroup group, int xOffset, GTRecipe recipe, List<Content> contents, boolean perTick,
                           boolean isInput, MutableInt yOffset) {
        String langKey = "ctpp." + (isInput ? "stress_input" : "stress_output");
        float stress = (float) contents.stream().map(Content::getContent).mapToDouble(CAP::of).sum();
        group.addWidget(new LabelWidget(3 - xOffset, yOffset.addAndGet(10),
                LocalizationUtils.format(langKey, FormattingUtil.formatNumbers(stress))));
        var handler = new CustomItemStackHandler(AllBlocks.COGWHEEL.asStack());
        group.addWidget(new SlotWidget(handler, 0, group.getSize().width - 30,
                group.getSize().height - 30, false, false));
    }
}
