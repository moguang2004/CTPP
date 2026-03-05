package com.mo_guang.ctpp.dynamicPart;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.mo_guang.ctpp.dynamicPart.rotation.SimpleRotatingContraptionEntity;
import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;

public class SimpleContraptionEntityRenderer extends ContraptionEntityRenderer<SimpleRotatingContraptionEntity> {

    public SimpleContraptionEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
