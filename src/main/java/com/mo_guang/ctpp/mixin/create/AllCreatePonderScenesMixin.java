package com.mo_guang.ctpp.mixin.create;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderScenes;
import com.simibubi.create.infrastructure.ponder.scenes.KineticsScenes;

import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AllCreatePonderScenes.class, remap = false)
public abstract class AllCreatePonderScenesMixin {

    @Inject(method = "register", at = @At("TAIL"))
    private static void addEncasedCogwheelPonders(PonderSceneRegistrationHelper<ResourceLocation> helper,
                                                  CallbackInfo ci) {
        helper.forComponents(
                AllBlocks.BRASS_ENCASED_COGWHEEL.getId(),
                AllBlocks.BRASS_ENCASED_LARGE_COGWHEEL.getId(),
                AllBlocks.ANDESITE_ENCASED_COGWHEEL.getId(),
                AllBlocks.ANDESITE_ENCASED_LARGE_COGWHEEL.getId())
                .addStoryBoard("cog/encasing", KineticsScenes::cogwheelsCanBeEncased);
    }
}