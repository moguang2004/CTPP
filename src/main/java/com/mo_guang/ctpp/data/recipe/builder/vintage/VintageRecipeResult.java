package com.mo_guang.ctpp.data.recipe.builder.vintage;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import com.google.gson.JsonObject;

import java.util.Objects;

final class VintageRecipeResult {

    private final JsonObject json;

    VintageRecipeResult(JsonObject json) {
        this.json = json;
    }

    static VintageRecipeResult item(ItemStack stack, Double chance) {
        JsonObject json = new JsonObject();
        json.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString());
        if (stack.getCount() != 1) json.addProperty("count", stack.getCount());
        if (stack.hasTag()) json.addProperty("nbt", String.valueOf(stack.getTag()));
        if (chance != null) json.addProperty("chance", chance);
        return new VintageRecipeResult(json);
    }

    static VintageRecipeResult fluid(FluidStack stack) {
        JsonObject json = new JsonObject();
        json.addProperty("fluid", Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(stack.getFluid())).toString());
        json.addProperty("amount", stack.getAmount());
        return new VintageRecipeResult(json);
    }

    JsonObject toJson() {
        return json.deepCopy();
    }
}
