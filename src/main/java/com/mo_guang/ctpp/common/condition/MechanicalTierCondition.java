package com.mo_guang.ctpp.common.condition;

import com.google.gson.JsonObject;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeCondition;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeConditionType;
import com.mo_guang.ctpp.common.data.CTPPRecipeConditions;
import com.mo_guang.ctpp.common.machine.multiblock.KineticMultiblockMachine;
import com.mo_guang.ctpp.common.machine.multiblock.part.MechanicalUpgradePartMachine;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NoArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
public class MechanicalTierCondition extends RecipeCondition {
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
    public RecipeConditionType<?> getType() {
        return CTPPRecipeConditions.MECHANICAL_TIER;
    }

    @Override
    public Component getTooltips() {
        return Component.translatable("recipe.condition.mechanical_tier.tooltip", tier);
    }

    @Override
    public boolean testCondition(@NotNull GTRecipe gtRecipe, @NotNull RecipeLogic recipeLogic) {
        if (recipeLogic.machine instanceof KineticMultiblockMachine kineticMultiblockMachine) {
            for (IMultiPart multiPart: kineticMultiblockMachine.getParts()) {
                if (multiPart instanceof MechanicalUpgradePartMachine upgradePartMachine) {
                    return upgradePartMachine.tier >= tier;
                }
            }
        }
        return false;
    }

    @Override
    public RecipeCondition createTemplate() {
        return new MechanicalTierCondition();
    }
    @NotNull
    @Override
    public JsonObject serialize() {
        JsonObject config = super.serialize();
        config.addProperty("mechanical_tier", tier);
        return config;
    }

    @Override
    public RecipeCondition deserialize(@NotNull JsonObject config) {
        super.deserialize(config);
        tier = GsonHelper.getAsInt(config, "mechanical_tier", 0);
        return this;
    }

    @Override
    public RecipeCondition fromNetwork(FriendlyByteBuf buf) {
        super.fromNetwork(buf);
        tier = buf.readInt();
        return this;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        super.toNetwork(buf);
        buf.writeInt(tier);
    }
}
