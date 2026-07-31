package com.mo_guang.ctpp.data;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import com.google.gson.JsonObject;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("removal")
public final class ToolboxBlockstates implements DataProvider {

    private final PackOutput.PathProvider pathProvider;

    public ToolboxBlockstates(PackOutput output) {
        pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        CompletableFuture<?>[] futures = new CompletableFuture[DyeColor.values().length];
        for (DyeColor color : DyeColor.values()) {
            String name = color.getName() + "_toolbox";
            JsonObject variants = new JsonObject();
            addVariant(variants, "east", false, name, 90);
            addVariant(variants, "east", true, name, 90);
            addVariant(variants, "north", false, name, 0);
            addVariant(variants, "north", true, name, 0);
            addVariant(variants, "south", false, name, 180);
            addVariant(variants, "south", true, name, 180);
            addVariant(variants, "west", false, name, 270);
            addVariant(variants, "west", true, name, 270);
            JsonObject root = new JsonObject();
            root.add("variants", variants);
            Path path = pathProvider.json(new ResourceLocation("ctpp", name));
            futures[color.getId()] = DataProvider.saveStable(output, root, path);
        }
        return CompletableFuture.allOf(futures);
    }

    private static void addVariant(JsonObject variants, String facing, boolean waterlogged, String model,
                                   int rotation) {
        JsonObject value = new JsonObject();
        value.addProperty("model", "create:block/" + model);
        if (rotation != 0) value.addProperty("y", rotation);
        variants.add("facing=" + facing + ",waterlogged=" + waterlogged, value);
    }

    @Override
    public String getName() {
        return "CTPP toolbox blockstates";
    }
}
