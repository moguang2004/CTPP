package com.mo_guang.ctpp.integration.kjs;

import com.gregtechceu.gtceu.integration.kjs.recipe.components.ContentJS;
import com.mo_guang.ctpp.common.data.CTPPRecipeCapabilities;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;


public class CTPPRecipeComponents {
    public static final ContentJS<Float> SU_IN = new ContentJS<>(NumberComponent.ANY_FLOAT, CTPPRecipeCapabilities.SU,
            false);
    public static final ContentJS<Float> SU_OUT = new ContentJS<>(NumberComponent.ANY_FLOAT, CTPPRecipeCapabilities.SU,
            true);
    static{

    }
}
