package com.mo_guang.ctpp.common.data;

import com.gregtechceu.gtceu.api.recipe.condition.RecipeConditionType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.mo_guang.ctpp.common.condition.MechanicalTierCondition;
import com.mo_guang.ctpp.common.condition.RPMCondition;

public class CTPPRecipeConditions {
    public static final RecipeConditionType<RPMCondition> RPM = GTRegistries.RECIPE_CONDITIONS.register("rpm",
            new RecipeConditionType<>(RPMCondition::new, RPMCondition.CODEC));
    public static final RecipeConditionType<MechanicalTierCondition> MECHANICAL_TIER = GTRegistries.RECIPE_CONDITIONS.register("mechanical_tier",
            new RecipeConditionType<>(MechanicalTierCondition::new, MechanicalTierCondition.CODEC));
    public static void init() {}
}
