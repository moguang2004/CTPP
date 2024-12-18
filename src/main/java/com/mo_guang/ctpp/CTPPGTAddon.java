package com.mo_guang.ctpp;

import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.mo_guang.ctpp.common.data.CTPPRecipeCapabilities;
import com.mo_guang.ctpp.common.data.CTPPRecipes;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

public class CTPPGTAddon implements IGTAddon {
    @Override
    public GTRegistrate getRegistrate() {
        return CTPPRegistration.REGISTRATE;
    }

    @Override
    public void initializeAddon() {

    }

    @Override
    public String addonModId() {
        return CTPP.MODID;
    }

    @Override
    public void registerRecipeCapabilities() {
        CTPPRecipeCapabilities.init();
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        CTPPRecipes.init(provider);
    }
}
