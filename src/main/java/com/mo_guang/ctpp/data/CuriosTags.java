package com.mo_guang.ctpp.data;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.ctnhlang.Key;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mo_guang.ctpp.registry.CTPPBlocks;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("removal")
public final class CuriosTags implements DataProvider {

    @Key("curios.identifier.toolbox")
    @EN("Toolbox")
    @CN("工具箱")
    static Lang toolbox;

    private final PackOutput.PathProvider pathProvider;

    public CuriosTags(PackOutput output) {
        pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "tags/items");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        JsonObject json = new JsonObject();
        json.addProperty("replace", false);
        JsonArray values = new JsonArray();
        for (var toolbox : CTPPBlocks.TOOLBOXES) values.add(toolbox.getId().toString());
        json.add("values", values);
        Path path = pathProvider.json(new ResourceLocation("curios", "toolbox"));
        return DataProvider.saveStable(output, json, path);
    }

    @Override
    public String getName() {
        return "Curios toolbox tags";
    }
}
