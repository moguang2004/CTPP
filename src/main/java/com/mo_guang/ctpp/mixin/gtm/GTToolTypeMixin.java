package com.mo_guang.ctpp.mixin.gtm;

import com.gregtechceu.gtceu.api.item.tool.GTToolType;

import com.mo_guang.ctpp.common.item.GTHammerItem;
import com.mo_guang.ctpp.common.item.GTWireCutterItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GTToolType.class, remap = false)
public class GTToolTypeMixin {

    @Redirect(method = "<clinit>",
              at = @At(value = "INVOKE",
                       target = "Lcom/gregtechceu/gtceu/api/item/tool/GTToolType;builder(Ljava/lang/String;)Lcom/gregtechceu/gtceu/api/item/tool/GTToolType$Builder;"))
    private static GTToolType.Builder injectConstructor(String name) {
        var builder = GTToolType.builder(name);
        if (name.equals("wire_cutter")) {
            builder.constructor(GTWireCutterItem::new);
        } else if (name.equals("hammer")) {
            builder.constructor(GTHammerItem::new);
        }
        return builder;
    }
}
