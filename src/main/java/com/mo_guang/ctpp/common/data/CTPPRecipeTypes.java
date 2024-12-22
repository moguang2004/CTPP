package com.mo_guang.ctpp.common.data;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import com.mo_guang.ctpp.common.condition.RPMCondition;
import com.mo_guang.ctpp.recipe.CTPPRecipeBuilder;
import com.simibubi.create.AllBlocks;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.MACERATOR_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.MIXER_RECIPES;
import static com.lowdragmc.lowdraglib.gui.texture.ProgressTexture.FillDirection.LEFT_TO_RIGHT;

public class CTPPRecipeTypes {
    public static final String KINETIC = "kinetic";
    public static final GTRecipeType KINETIC_MIXER_RECIPES = GTRecipeTypes.register("kinetic_mixer", KINETIC)
            .setMaxIOSize(6, 1, 2, 1)
                .setSlotOverlay(false, false, GuiTextures.DUST_OVERLAY)
                .setSlotOverlay(true, false, GuiTextures.DUST_OVERLAY)
                .setProgressBar(GuiTextures.PROGRESS_BAR_MIXER, LEFT_TO_RIGHT)
                .setSound(GTSoundEntries.MIXER)
                .setMaxTooltips(4)
                .setUiBuilder((recipe, group) -> {
        if (!recipe.conditions.isEmpty() && recipe.conditions.get(0) instanceof RPMCondition) {
            var handler = new CustomItemStackHandler(AllBlocks.SHAFT.asStack());
            group.addWidget(new SlotWidget(handler, 0, group.getSize().width - 30,
                    group.getSize().height - 30, false, false));
        }
    });
    public static final GTRecipeType SMASHING_FACTORY_RECIPES = GTRecipeTypes.register("smashing_factory_recipes", KINETIC)
            .setMaxIOSize(1,6,1,3)
            .setSlotOverlay(false, false, GuiTextures.DUST_OVERLAY)
            .setSlotOverlay(true, false, GuiTextures.DUST_OVERLAY)
            .setProgressBar(GuiTextures.PROGRESS_BAR_MIXER, LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.MIXER)
            .setMaxTooltips(4)
            .setUiBuilder((recipe, group) -> {
                if (!recipe.conditions.isEmpty() && recipe.conditions.get(0) instanceof RPMCondition) {
                    var handler = new CustomItemStackHandler(AllBlocks.SHAFT.asStack());
                    group.addWidget(new SlotWidget(handler, 0, group.getSize().width - 30,
                            group.getSize().height - 30, false, false));
                }
            });
    public static void init(){
        MIXER_RECIPES.onRecipeBuild((builder, provider) -> {
                assert KINETIC_MIXER_RECIPES != null;
                var newrecipe = KINETIC_MIXER_RECIPES.copyFrom(builder)
                        .duration(Math.max((builder.duration / 2), 1))
                        .buildRawRecipe();
                new CTPPRecipeBuilder(newrecipe,KINETIC_MIXER_RECIPES).rpm(64)
                        .noEUt()
                        .inputStress(2048)
                        .save(provider);
        });
        MACERATOR_RECIPES.onRecipeBuild((builder, provider) ->{
            assert SMASHING_FACTORY_RECIPES != null;
            var newrecipe = SMASHING_FACTORY_RECIPES.copyFrom(builder)
                    .duration(Math.max((builder.duration / 2), 1))
                    .buildRawRecipe();
            new CTPPRecipeBuilder(newrecipe,SMASHING_FACTORY_RECIPES).rpm(64)
                    .noEUt()
                    .inputStress(2048)
                    .save(provider);
        });
    }
}
