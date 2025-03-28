package com.mo_guang.ctpp.recipe;

import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.mo_guang.ctpp.api.StressRecipeCapability;
import com.mo_guang.ctpp.common.condition.RPMCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class CTPPRecipeBuilder extends GTRecipeBuilder {
    public CTPPRecipeBuilder(ResourceLocation id, GTRecipeType recipeType) {
        super(id, recipeType);
    }

    public CTPPRecipeBuilder(GTRecipe toCopy, GTRecipeType recipeType) {
        super(toCopy, recipeType);
    }
    @SuppressWarnings("all")
    public static CTPPRecipeBuilder of(ResourceLocation id, GTRecipeType recipeType) {
        return new CTPPRecipeBuilder(id, recipeType);
    }
    @Override
    @SuppressWarnings("all")
    public <T> CTPPRecipeBuilder inputs(RecipeCapability<T> capability, Object... obj) {
        super.inputs(capability, obj);
        return this;
    }
    @Override
    @SuppressWarnings("all")
    public final <T> CTPPRecipeBuilder input(RecipeCapability<T> capability, T... obj) {
        super.input(capability, obj);
        return this;
    }

    @Override
    @SuppressWarnings("all")
    public <T> CTPPRecipeBuilder output(RecipeCapability<T> capability, T... obj) {
        super.output(capability, obj);
        return this;
    }

    @Override
    @SuppressWarnings("all")
    public <T> CTPPRecipeBuilder outputs(RecipeCapability<T> capability, Object... obj) {
        super.outputs(capability, obj);
        return this;
    }
    @Override
    @SuppressWarnings("all")
    public CTPPRecipeBuilder inputItems(ItemStack input) {
        super.inputItems(input);
        return this;
    }

    @Override
    @SuppressWarnings("all")
    public CTPPRecipeBuilder outputItems(ItemStack output) {
        super.outputItems(output);
        return this;
    }

    @Override
    public CTPPRecipeBuilder inputFluids(FluidStack input) {
        super.inputFluids(input);
        return this;
    }

    @Override
    public CTPPRecipeBuilder outputFluids(FluidStack output) {
        super.outputFluids(output);
        return this;
    }

    @Override
    public CTPPRecipeBuilder duration(int duration) {
        super.duration(duration);
        return this;
    }
    @Override
    public CTPPRecipeBuilder notConsumableFluid(FluidStack fluid) {
        super.notConsumableFluid(fluid);
        return this;
    }
    @Override
    public CTPPRecipeBuilder notConsumable(ItemStack input) {
        super.notConsumable(input);
        return this;
    }
    @Override
    public CTPPRecipeBuilder circuitMeta(int configuration) {
        super.circuitMeta(configuration);
        return this;
    }
    public CTPPRecipeBuilder noEUt() {
        tickInput.remove(EURecipeCapability.CAP);
        tickOutput.remove(EURecipeCapability.CAP);
        return this;
    }

    public CTPPRecipeBuilder inputStress(float stress) {
        input(StressRecipeCapability.CAP, stress);
        this.data.putFloat("input_stress",stress);
        return this;
    }

    public CTPPRecipeBuilder outputStress(float stress) {
        output(StressRecipeCapability.CAP, stress);
        this.data.putFloat("output_stress",stress);
        return this;
    }

    public CTPPRecipeBuilder rpm(float rpm, boolean reverse) {
        addCondition(new RPMCondition(rpm).setReverse(reverse));
        return this;
    }

    public CTPPRecipeBuilder rpm(float rpm) {
        return rpm(rpm, false);
    }
}
