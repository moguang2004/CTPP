package com.mo_guang.ctpp.mixin.create;

import com.mo_guang.ctpp.util.IMatrix3dAccess;
import com.simibubi.create.foundation.collision.Matrix3d;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = Matrix3d.class, remap = false)
public class Matrix3dMixin implements IMatrix3dAccess {

    @Shadow double m00;
    @Shadow double m01;
    @Shadow double m02;

    @Shadow double m10;
    @Shadow double m11;
    @Shadow double m12;

    @Shadow double m20;
    @Shadow double m21;
    @Shadow double m22;

    @Override
    public void ctpp$setFromMatrix3f(Matrix3f m) {
        m00 = m.m00();
        m01 = m.m01();
        m02 = m.m02();
        m10 = m.m10();
        m11 = m.m11();
        m12 = m.m12();
        m20 = m.m20();
        m21 = m.m21();
        m22 = m.m22();
    }
}
