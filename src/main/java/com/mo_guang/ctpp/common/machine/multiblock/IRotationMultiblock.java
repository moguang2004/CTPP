package com.mo_guang.ctpp.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.mo_guang.ctpp.api.pattern.StaticBlockPattern;

public interface IRotationMultiblock extends IMultiController {
    @Override
    default boolean checkPattern() {
        if (isFormed()) {
            return checkStaticPattern();
        }
        return IMultiController.super.checkPattern();
    }
    default boolean checkStaticPattern() {
        var pattern = self().getDefinition().getPatternFactory().get();
        if (pattern instanceof StaticBlockPattern staticBlockPattern) {
            return staticBlockPattern.checkPatternAt(this.getMultiblockState(), false);
        }
        return true;
    }
    default void assemble() {
        var pattern = self().getDefinition().getPatternFactory().get();
        if (pattern instanceof StaticBlockPattern staticBlockPattern) {
            var dymanicPart = staticBlockPattern.getDynamicPart(self().getMultiblockState());
            for (var blockpos : dymanicPart) {

            }
        }

    };
}
