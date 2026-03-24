package com.mo_guang.ctpp.mixin.create.fix;

import com.simibubi.create.compat.jei.category.SequencedAssemblyCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = SequencedAssemblyCategory.class, remap = false)
public class SequencedAssemblyCategoryMixin {

    @ModifyConstant(method = "getTooltipStrings", constant = @Constant(doubleValue = 5.0D))
    private double ctnh$lowerSequenceTooltipArea(double original) {
        return 34.0D;
    }
}
