package com.mo_guang.ctpp.common.data;

import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.common.kinetic.fan.acidwashing.AcidwashingRecipe;
import com.mo_guang.ctpp.common.kinetic.fan.breathing.BreathingRecipe;
import com.mo_guang.ctpp.common.kinetic.fan.oiling.OilingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public enum CTPPRecipeTypeInfo implements IRecipeTypeInfo {
    BREATHING(BreathingRecipe::new),
    ACIDWASHING(AcidwashingRecipe::new),
    OILING(OilingRecipe::new);
    private final ResourceLocation id;
    private final RegistryObject<RecipeSerializer<?>> serializerObject;
    private final @Nullable RegistryObject<RecipeType<?>> typeObject;
    private final Supplier<RecipeType<?>> type;

    private CTPPRecipeTypeInfo(Supplier<RecipeSerializer<?>> serializerSupplier) {
        String name = this.name().toLowerCase();
        this.id = CTPP.id(name);
        this.serializerObject = CTPPRecipeTypeInfo.Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        this.typeObject = CTPPRecipeTypeInfo.Registers.TYPE_REGISTER.register(name, () -> RecipeType.simple(this.id));
        this.type = this.typeObject;
    }

    private CTPPRecipeTypeInfo(ProcessingRecipeBuilder.ProcessingRecipeFactory<?> processingFactory) {
        this(() -> new ProcessingRecipeSerializer<>(processingFactory));
    }

    public static void register(IEventBus modEventBus) {
        CTPPRecipeTypeInfo.Registers.SERIALIZER_REGISTER.register(modEventBus);
        CTPPRecipeTypeInfo.Registers.TYPE_REGISTER.register(modEventBus);
    }
    public <C extends Container, T extends Recipe<C>> Optional<T> find(C inv, Level world) {
        return world.getRecipeManager()
                .getRecipeFor(getType(), inv, world);
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) this.serializerObject.get();
    }

    public <T extends RecipeType<?>> T getType() {
        return (T) this.type.get();
    }

    private static class Registers {
        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER;
        private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER;

        private Registers() {
        }

        static {
            SERIALIZER_REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CTPP.MODID);
            TYPE_REGISTER = DeferredRegister.create(Registries.RECIPE_TYPE, CTPP.MODID);
        }
    }
}
