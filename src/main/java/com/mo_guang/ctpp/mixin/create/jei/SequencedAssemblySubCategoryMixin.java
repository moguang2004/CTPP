package com.mo_guang.ctpp.mixin.create.jei;

import com.mo_guang.ctpp.util.IWorkingMachineStep;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = SequencedAssemblySubCategory.class, remap = false)
public class SequencedAssemblySubCategoryMixin implements IWorkingMachineStep {}
