package com.mo_guang.ctpp.dynamicPart;

import com.mo_guang.ctpp.dynamicPart.rotation.SimpleRotatingContraptionEntity;
import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SimpleContraptionEntityRenderer extends ContraptionEntityRenderer<SimpleRotatingContraptionEntity> {
    public SimpleContraptionEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
