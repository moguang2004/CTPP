package com.mo_guang.ctpp.data.tags;

import com.gregtechceu.gtceu.data.recipe.CustomTags;

import com.mo_guang.ctpp.registry.CTPPBlocks;
import com.tterrag.registrate.providers.RegistrateItemTagsProvider;

import java.util.Arrays;

public final class ItemTags {

    private ItemTags() {}

    public static void init(RegistrateItemTagsProvider provider) {
        provider.addTag(CustomTags.TOOLBOXES).add(Arrays.stream(CTPPBlocks.TOOLBOXES)
                .map(entry -> entry.get().asItem())
                .toArray(net.minecraft.world.item.Item[]::new));
    }
}
