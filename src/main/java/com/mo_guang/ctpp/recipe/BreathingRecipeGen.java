package com.mo_guang.ctpp.recipe;

import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.common.data.CTPPRecipeTypeInfo;
import com.simibubi.create.Create;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

public class BreathingRecipeGen extends ProcessingRecipeGen {
    public BreathingRecipeGen(PackOutput generator) {
        super(generator, CTPP.MODID);
    }
    GeneratedRecipe BOTTLE = convert(Items.GLASS_BOTTLE, Items.DRAGON_BREATH);
//    public static void register(PackOutput generator) {
//        GENERATORS.add(new BreathingRecipeGen(generator));
//    }
    public GeneratedRecipe convert(ItemLike input, ItemLike result) {
        return convert(() -> Ingredient.of(input), () -> result);
    }

    public GeneratedRecipe convert(Supplier<Ingredient> input, Supplier<ItemLike> result) {
        return create(CTPP.id(CatnipServices.REGISTRIES.getKeyOrThrow(result.get()
                                .asItem())
                        .getPath()),
                p -> p.withItemIngredients(input.get())
                        .output(result.get()));
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CTPPRecipeTypeInfo.BREATHING;
    }
}
