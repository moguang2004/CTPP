package com.mo_guang.ctpp.common.recipe.builder.create.metallurgy;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import com.mo_guang.ctpp.CTPP;

import java.util.Objects;

final class MetallurgyRecipeBuilderSupport {

    private MetallurgyRecipeBuilderSupport() {}

    static ResourceLocation id(String name) {
        return name.contains(":") ? ResourceLocation.parse(name) : CTPP.id(name);
    }

    static ItemStack itemStack(String itemId, int count) {
        return new ItemStack(item(itemId), count);
    }

    static Item item(String itemId) {
        ResourceLocation id = ResourceLocation.parse(itemId);
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(id), itemId);
    }

    static Fluid fluid(String fluidId) {
        ResourceLocation id = ResourceLocation.parse(fluidId);
        return Objects.requireNonNull(ForgeRegistries.FLUIDS.getValue(id), fluidId);
    }

    static FluidStack fluidStack(String fluidId, int amount) {
        return new FluidStack(fluid(fluidId), amount);
    }
}
