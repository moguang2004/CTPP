package com.mo_guang.ctpp.mixin.create.diesel;

import net.minecraft.world.item.crafting.Recipe;

import com.jesz.createdieselgenerators.content.basin_lid.BasinFermentingRecipe;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BasinRecipe.class, remap = false)
public class BasinRecipeMixin {

    @Redirect(method = "apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z",
              at = @At(value = "INVOKE",
                       target = "Lcom/simibubi/create/content/processing/recipe/HeatCondition;testBlazeBurner(Lcom/simibubi/create/content/processing/burner/BlazeBurnerBlock$HeatLevel;)Z"))
    private static boolean checkFermenting(HeatCondition instance, BlazeBurnerBlock.HeatLevel level,
                                           @Local(argsOnly = true) Recipe<?> recipe) {
        if (recipe instanceof BasinFermentingRecipe) {
            return switch (instance) {
                case NONE -> level == BlazeBurnerBlock.HeatLevel.NONE ||
                        level == BlazeBurnerBlock.HeatLevel.SMOULDERING;
                case HEATED -> level == BlazeBurnerBlock.HeatLevel.FADING ||
                        level == BlazeBurnerBlock.HeatLevel.KINDLED;
                case SUPERHEATED -> level == BlazeBurnerBlock.HeatLevel.SEETHING;
            };
        } else {
            return instance.testBlazeBurner(level);
        }
    }
}
