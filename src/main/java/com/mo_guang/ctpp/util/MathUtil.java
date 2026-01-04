package com.mo_guang.ctpp.util;

import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class MathUtil {
    public static float rpm2rads(float rpm) {
        return rpm / AllConfigs.server().kinetics.maxRotationSpeed.get() * 360 / 20 * 4;
    }
    /**
     * 计算两个四元数之间的角度差
     */
    public static float quaternionAngleDifference(Quaternionf q1, Quaternionf q2) {
        // 使用点积计算角度
        float dot = Math.abs(q1.x() * q2.x() +
                q1.y() * q2.y() +
                q1.z() * q2.z() +
                q1.w() * q2.w());

        // 确保值在有效范围内
        dot = Math.min(1.0f, Math.max(-1.0f, dot));

        return (float)(2.0f * Math.acos(dot));
    }

    /**
     * 四元数球面线性插值
     */
    public static Quaternionf slerp(Quaternionf from, Quaternionf to, float alpha) {
        Quaternionf result = new Quaternionf();

        // 计算点积
        float dot = from.x() * to.x() +
                from.y() * to.y() +
                from.z() * to.z() +
                from.w() * to.w();

        // 如果点积为负，四元数取反以获得最短弧
        if (dot < 0.0f) {
            to = new Quaternionf(-to.x(), -to.y(), -to.z(), -to.w());
            dot = -dot;
        }

        // 确保点积在有效范围内
        dot = Math.min(1.0f, Math.max(-1.0f, dot));

        // 计算插值
        float theta0 = (float)Math.acos(dot);
        float theta = theta0 * alpha;

        float sinTheta = (float)Math.sin(theta);
        float sinTheta0 = (float)Math.sin(theta0);

        if (sinTheta0 > 0.001f) {
            float s0 = (float)Math.cos(theta) - dot * sinTheta / sinTheta0;
            float s1 = sinTheta / sinTheta0;

            result.x = from.x() * s0 + to.x() * s1;
            result.y = from.y() * s0 + to.y() * s1;
            result.z = from.z() * s0 + to.z() * s1;
            result.w = from.w() * s0 + to.w() * s1;
        } else {
            // 角度很小，使用线性插值
            result = from.slerp(to, alpha, new Quaternionf());
        }

        return result;
    }

    /**
     * 用四元数旋转向量
     */
    public static Vec3 rotateByQuaternion(Vec3 vec, Quaternionf q) {
        // 四元数旋转公式: v' = q * v * q^-1
        float x = (float) vec.x;
        float y = (float) vec.y;
        float z = (float) vec.z;

        // 计算 q * v
        float qw = q.w();
        float qx = q.x();
        float qy = q.y();
        float qz = q.z();

        float ix = qw * x + qy * z - qz * y;
        float iy = qw * y + qz * x - qx * z;
        float iz = qw * z + qx * y - qy * x;
        float iw = -qx * x - qy * y - qz * z;

        // 计算 (q*v) * q^-1 (对于单位四元数，q^-1 = 共轭)
        float rx = ix * qw + iw * -qx + iy * -qz - iz * -qy;
        float ry = iy * qw + iw * -qy + iz * -qx - ix * -qz;
        float rz = iz * qw + iw * -qz + ix * -qy - iy * -qx;

        return new Vec3(rx, ry, rz);
    }
    /**
     * 智能选择欧拉角转换方法
     */
    public static Vec3 getSmartEulerAngles(Quaternionf q, float thresholdDeg) {
        // 检查是否轴对齐
        if (isAxisAligned(q, thresholdDeg)) {
            // 轴对齐时使用特殊处理
            return getAxisAlignedEulerAngles(q);
        } else {
            // 正常情况使用稳定算法
            return getStableEulerAngles(q);
        }
    }
    /**
     * 检测旋转是否与某个轴对齐
     */
    public static boolean isAxisAligned(Quaternionf q, float thresholdDeg) {
        q.normalize();

        // 检查是否接近绕单个轴的旋转
        float angle = 2 * (float)Math.acos(Math.abs(q.w()));
        Vec3 axis = new Vec3(q.x(), q.y(), q.z()).normalize();

        // 检查与坐标轴的对齐程度
        float alignX = Math.abs((float)axis.dot(new Vec3(1, 0, 0)));
        float alignY = Math.abs((float)axis.dot(new Vec3(0, 1, 0)));
        float alignZ = Math.abs((float)axis.dot(new Vec3(0, 0, 1)));

        // 如果接近某个坐标轴（夹角小于阈值）
        return alignX > 1 - thresholdDeg/90f ||
                alignY > 1 - thresholdDeg/90f ||
                alignZ > 1 - thresholdDeg/90f;
    }

    /**
     * 获取主轴对齐的欧拉角（更稳定）
     */
    public static Vec3 getAxisAlignedEulerAngles(Quaternionf q) {
        q.normalize();

        // 计算绕每个轴的旋转角度
        float angleX = getRotationAngleAroundAxis(q, new Vec3(1, 0, 0));
        float angleY = getRotationAngleAroundAxis(q, new Vec3(0, 1, 0));
        float angleZ = getRotationAngleAroundAxis(q, new Vec3(0, 0, 1));

        // 找到主要旋转轴（角度最大的）
        float maxAngle = Math.max(Math.abs(angleX), Math.max(Math.abs(angleY), Math.abs(angleZ)));

        // 如果主要旋转很明显，优先使用它
        if (maxAngle > 45.0f) {
            if (Math.abs(angleX) == maxAngle) {
                // 主要绕X轴旋转
                return new Vec3(angleX, 0, 0);
            } else if (Math.abs(angleY) == maxAngle) {
                // 主要绕Y轴旋转
                return new Vec3(0, angleY, 0);
            } else {
                // 主要绕Z轴旋转
                return new Vec3(0, 0, angleZ);
            }
        }

        // 否则使用标准转换
        return getStableEulerAngles(q);
    }
    /**
     * 计算绕特定轴的旋转角度（稳定版本）
     */
    public static float getRotationAngleAroundAxis(Quaternionf q, Vec3 axis) {
        q.normalize();

        // 确保axis是单位向量
        axis = axis.normalize();

        // 将四元数转换为轴角表示
        float angle = 2 * (float)Math.acos(Math.min(1.0f, Math.max(-1.0f, q.w())));

        // 如果角度很小，直接返回0
        if (Math.abs(angle) < 1e-6) {
            return 0;
        }

        // 计算旋转轴
        float sinHalfAngle = (float)Math.sin(angle / 2);
        Vec3 qAxis = new Vec3(
                q.x() / sinHalfAngle,
                q.y() / sinHalfAngle,
                q.z() / sinHalfAngle
        );

        // 计算与目标轴的点积
        float dot = (float)qAxis.dot(axis);

        // 实际绕目标轴的旋转角度 = 总角度 × 与目标轴的相似度
        float projectedAngle = angle * dot;

        // 限制在合理范围内
        return (float)Math.toDegrees(Math.max(-Math.PI, Math.min(Math.PI, projectedAngle)));
    }
    /**
     * 从四元数提取欧拉角（X->Y->Z顺序），处理轴对齐的特殊情况
     * 使用稳定的算法避免万向节死锁问题
     */
    public static Vec3 getStableEulerAngles(Quaternionf q) {
        q.normalize();
        float w = q.w(), x = q.x(), y = q.y(), z = q.z();

        // 方法1：使用atan2的稳定算法
        float sinr_cosp = 2 * (w * x + y * z);
        float cosr_cosp = 1 - 2 * (x * x + y * y);
        float roll = (float)Math.atan2(sinr_cosp, cosr_cosp);

        // 检查是否接近万向节死锁
        float sinp = 2 * (w * y - z * x);
        float pitch;
        if (Math.abs(sinp) >= 1) {
            // 在万向节死锁处，使用atan2的符号
            pitch = (float)Math.copySign(Math.PI / 2, sinp);
        } else {
            pitch = (float)Math.asin(sinp);
        }

        float siny_cosp = 2 * (w * z + x * y);
        float cosy_cosp = 1 - 2 * (y * y + z * z);
        float yaw = (float)Math.atan2(siny_cosp, cosy_cosp);

        return new Vec3(
                normalizeAngle((float)Math.toDegrees(pitch)),   // X rotation
                normalizeAngle((float)Math.toDegrees(yaw)),     // Y rotation
                normalizeAngle((float)Math.toDegrees(roll))     // Z rotation
        );
    }
    /**
     * 将角度标准化到0-360度范围
     */
    public static float normalizeAngle(float angle) {
        angle %= 360.0f;
        if (angle < 0) {
            angle += 360.0f;
        }
        return angle;
    }
}

