package com.mo_guang.ctpp.common.condition;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeCondition;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeConditionType;

import net.minecraft.network.chat.Component;

import com.mo_guang.ctpp.api.CTPPRecipeConditions;
import com.mo_guang.ctpp.common.machine.multiblock.KineticMultiblockMachine;
import com.mo_guang.ctpp.util.CTPPValues;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.CN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.EN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.Prefix;

@NoArgsConstructor
@Prefix("recipe.condition")
public class MechanicalTierCondition extends RecipeCondition<MechanicalTierCondition> {

    public static final Codec<MechanicalTierCondition> CODEC = RecordCodecBuilder
            .create(instance -> RecipeCondition.isReverse(instance)
                    .and(Codec.INT.fieldOf("mechanical_tier").forGetter(val -> val.tier))
                    .apply(instance, MechanicalTierCondition::new));
    private int tier;
    public static final MechanicalTierCondition INSTANCE = new MechanicalTierCondition();

    public MechanicalTierCondition(boolean isReverse, int tier) {
        super(isReverse);
        this.tier = tier;
    }

    public MechanicalTierCondition(int tier) {
        this.tier = tier;
    }

    @Override
    public RecipeConditionType<MechanicalTierCondition> getType() {
        return CTPPRecipeConditions.MECHANICAL_TIER;
    }

    @CN("机械等级：%d(%s)")
    @EN("Mechanical Tier: %d(%s)")
    static Lang mechanical_tier;

    @Override
    public Component getTooltips() {
        return mechanical_tier.translate(tier, CTPPValues.MT[tier]);
    }

    @Override
    public boolean testCondition(@NotNull GTRecipe gtRecipe, @NotNull RecipeLogic recipeLogic) {
        if (recipeLogic.machine instanceof KineticMultiblockMachine kineticMultiblockMachine) {
            return kineticMultiblockMachine.tier >= tier;
        }
        return false;
    }

    @Override
    public MechanicalTierCondition createTemplate() {
        return new MechanicalTierCondition();
    }
}
