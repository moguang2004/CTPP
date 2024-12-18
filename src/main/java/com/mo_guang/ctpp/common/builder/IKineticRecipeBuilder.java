package com.mo_guang.ctpp.common.builder;

import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;

public interface IKineticRecipeBuilder {
    public GTRecipeBuilder inputStress_(float stress);
    public GTRecipeBuilder outputStress_(float stress);
    public GTRecipeBuilder rpm_(float rpm, boolean reverse);
    public GTRecipeBuilder rpm_(float rpm);
}
