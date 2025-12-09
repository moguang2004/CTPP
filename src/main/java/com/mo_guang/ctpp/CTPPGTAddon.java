package com.mo_guang.ctpp;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.addon.events.KJSRecipeKeyEvent;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.mo_guang.ctpp.api.StressRecipeCapability;
import com.mo_guang.ctpp.common.data.CTPPRecipeCapabilities;
import com.mo_guang.ctpp.recipe.CTPPRecipes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.mo_guang.ctpp.integration.kjs.CTPPRecipeComponents.SU_IN;
import static com.mo_guang.ctpp.integration.kjs.CTPPRecipeComponents.SU_OUT;

@GTAddon
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

    @Override
    public void registerRecipeKeys(KJSRecipeKeyEvent event) {
        event.registerKey(
                StressRecipeCapability.CAP,
                Pair.of(SU_IN, SU_OUT)
        );
    }
}
