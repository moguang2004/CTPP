package com.mo_guang.ctpp.common.condition;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeCondition;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeConditionType;

import net.minecraft.network.chat.Component;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.mo_guang.ctpp.api.CTPPRecipeConditions;
import com.mo_guang.ctpp.common.machine.IKineticMachine;
import com.mo_guang.ctpp.common.machine.multiblock.KineticWorkableMultiblockMachine;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

@NoArgsConstructor
public class RPMCondition extends RecipeCondition<RPMCondition> {

    public static final Codec<RPMCondition> CODEC = RecordCodecBuilder
            .create(instance -> RecipeCondition.isReverse(instance)
                    .and(Codec.FLOAT.fieldOf("rpm").forGetter(val -> val.rpm))
                    .apply(instance, RPMCondition::new));

    public final static RPMCondition INSTANCE = new RPMCondition();
    private float rpm;

    @CN("转速: %d")
    @EN("RPM: %d")
    static Lang rpmTooltip;

    public RPMCondition(boolean isReverse, float rpm) {
        super(isReverse);
        this.rpm = rpm;
    }

    public RPMCondition(float rpm) {
        this.rpm = rpm;
    }

    @Override
    public RecipeConditionType<RPMCondition> getType() {
        return CTPPRecipeConditions.RPM;
    }

    @Override
    public Component getTooltips() {
        return rpmTooltip.translate(rpm);
    }

    public float getRpm() {
        return rpm;
    }

    @Override
    public boolean testCondition(@NotNull GTRecipe recipe, @NotNull RecipeLogic recipeLogic) {
        if (recipeLogic.machine instanceof IKineticMachine kineticMachine &&
                Math.abs(kineticMachine.getKineticHolder().getSpeed()) >= rpm) {
            return true;
        }
        if (recipeLogic.machine instanceof KineticWorkableMultiblockMachine controller) {
            return controller.speed >= rpm;
        }
        return false;
    }

    @Override
    public RPMCondition createTemplate() {
        return new RPMCondition();
    }

    // @NotNull
    // @Override
    // public JsonObject serialize() {
    // JsonObject config = super.serialize();
    // config.addProperty("rpm", rpm);
    // return config;
    // }
    //
    // @Override
    // public RecipeCondition deserialize(@NotNull JsonObject config) {
    // super.deserialize(config);
    // rpm = GsonHelper.getAsFloat(config, "rpm", 0);
    // return this;
    // }
    //
    // @Override
    // public RecipeCondition fromNetwork(FriendlyByteBuf buf) {
    // super.fromNetwork(buf);
    // rpm = buf.readFloat();
    // return this;
    // }
    //
    // @Override
    // public void toNetwork(FriendlyByteBuf buf) {
    // super.toNetwork(buf);
    // buf.writeFloat(rpm);
    // }
}
