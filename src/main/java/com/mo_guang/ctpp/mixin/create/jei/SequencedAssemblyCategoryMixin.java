package com.mo_guang.ctpp.mixin.create.jei;

import net.minecraft.network.chat.Component;

import com.llamalad7.mixinextras.sugar.Local;
import com.mo_guang.ctpp.util.IWorkingMachineStep;
import com.simibubi.create.compat.jei.category.SequencedAssemblyCategory;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = SequencedAssemblyCategory.class, remap = false)
public class SequencedAssemblyCategoryMixin {

    @Inject(method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/simibubi/create/content/processing/sequenced/SequencedAssemblyRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",
            at = @At(value = "INVOKE",
                     target = "Lcom/simibubi/create/compat/jei/category/sequencedAssembly/SequencedAssemblySubCategory;setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lcom/simibubi/create/content/processing/sequenced/SequencedRecipe;Lmezz/jei/api/recipe/IFocusGroup;I)V"))
    void addWorkingMachine(IRecipeLayoutBuilder builder, SequencedAssemblyRecipe recipe, IFocusGroup focuses,
                           CallbackInfo ci,
                           @Local(name = "subCategory") SequencedAssemblySubCategory subCategory,
                           @Local(name = "x") int x) {
        if (subCategory instanceof IWorkingMachineStep workingMachineStep) {
            workingMachineStep.addWorkMachine(builder, x);
        }
    }

    @Inject(method = "getTooltipStrings(Lcom/simibubi/create/content/processing/sequenced/SequencedAssemblyRecipe;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;DD)Ljava/util/List;",
            at = @At(value = "INVOKE",
                     target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
                     ordinal = 3,
                     shift = At.Shift.AFTER))
    void addWorkingMachineName(SequencedAssemblyRecipe recipe, IRecipeSlotsView iRecipeSlotsView, double mouseX,
                               double mouseY,
                               CallbackInfoReturnable<List<Component>> cir,
                               @Local(name = "subCategory") SequencedAssemblySubCategory subCategory,
                               @Local(name = "tooltip") List<Component> tooltip) {
        var machine = IWorkingMachineStep.getWorkMachineMap().get(subCategory.getClass());
        if (machine != null) {
            tooltip.add(machine.getHoverName());
        }
    }
}
