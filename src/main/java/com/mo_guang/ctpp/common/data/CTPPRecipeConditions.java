package com.mo_guang.ctpp.common.data;

import com.gregtechceu.gtceu.api.recipe.condition.RecipeConditionType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.mo_guang.ctpp.common.condition.RPMCondition;

public class CTPPRecipeConditions {
    public static final RecipeConditionType<RPMCondition> RPM = GTRegistries.RECIPE_CONDITIONS.register("rpm",
            new RecipeConditionType<>(RPMCondition::new, RPMCondition.CODEC));
    public static void init() {}
}
