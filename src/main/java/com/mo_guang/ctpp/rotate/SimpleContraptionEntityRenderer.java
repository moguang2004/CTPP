package com.mo_guang.ctpp.rotate;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SimpleContraptionEntityRenderer extends ContraptionEntityRenderer<SimpleRotatingContraptionEntity> {
    public SimpleContraptionEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
