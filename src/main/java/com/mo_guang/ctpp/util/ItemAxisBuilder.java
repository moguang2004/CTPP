package com.mo_guang.ctpp.util;

import com.gregtechceu.gtceu.api.item.MetaMachineItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

import com.tterrag.registrate.builders.ItemBuilder;

public class ItemAxisBuilder {

    public static void addShaft(ItemBuilder<? extends MetaMachineItem, ?> builder) {
        builder.model((ctx, prov) -> {

            ResourceLocation baseLoc = ResourceLocation.tryBuild(
                    builder.getOwner().getModid(),
                    "block/machine/" + ctx.getName());

            ResourceLocation cubeLoc = ResourceLocation.tryBuild(
                    builder.getOwner().getModid(),
                    "item/axis");

            var baseModel = prov.withExistingParent(ctx.getName() + "_base", baseLoc);

            var cubeModel = prov.withExistingParent(ctx.getName() + "_axis", cubeLoc);

            var modelBuilder = prov.getBuilder(ctx.getName());
            modelBuilder.transforms()
                    .transform(ItemDisplayContext.GUI)
                    .rotation(30, 225, 0)
                    .scale(0.625f)
                    .end()
                    .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                    .rotation(0, 45, 0)
                    .scale(0.4f)
                    .end()
                    .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                    .rotation(0, 225, 0)
                    .scale(0.4f)
                    .end()
                    .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                    .rotation(75, 45, 0)
                    .translation(0, 2.5f, 0)
                    .scale(0.375f)
                    .end()
                    .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                    .rotation(75, 45, 0)
                    .translation(0, 2.5f, 0)
                    .scale(0.375f)
                    .end()
                    .transform(ItemDisplayContext.GROUND)
                    .translation(0, 3f, 0)
                    .scale(0.25f)
                    .end()
                    .transform(ItemDisplayContext.FIXED)
                    .scale(0.5f)
                    .end();

            modelBuilder.customLoader(net.minecraftforge.client.model.generators.loaders.CompositeModelBuilder::begin)
                    .child("base", baseModel)
                    .child("axis", cubeModel);
        });
    }
}
