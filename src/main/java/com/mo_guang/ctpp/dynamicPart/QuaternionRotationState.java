package com.mo_guang.ctpp.dynamicPart;

import com.mo_guang.ctpp.util.IMatrix3dAccess;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.foundation.collision.Matrix3d;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class QuaternionRotationState extends AbstractContraptionEntity.ContraptionRotationState {

    private final Quaternionf quaternion = new Quaternionf();
    private Matrix3d cachedMatrix;

    public QuaternionRotationState(Quaternionf q) {
        quaternion.set(q);
        if (quaternion.lengthSquared() < 1.0E-8f) {
            quaternion.identity();
        } else {
            quaternion.normalize();
        }
    }

    @Override
    public Matrix3d asMatrix() {
        if (cachedMatrix != null)
            return cachedMatrix;

        Matrix3f m = new Matrix3f().set(quaternion);

        cachedMatrix = new Matrix3d();
        ((IMatrix3dAccess) cachedMatrix).ctpp$setFromMatrix3f(m);

        return cachedMatrix;
    }

    @Override
    public boolean hasVerticalRotation() {
        Vector3f up = new Vector3f(0, 1, 0);
        quaternion.transform(up);
        return Math.abs(up.y - 1) > 1e-4;
    }

    @Override
    public float getYawOffset() {
        Vector3f forward = new Vector3f(0, 0, 1);
        quaternion.transform(forward);
        return (float) Math.atan2(forward.x, forward.z);
    }
}
