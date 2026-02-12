package com.mo_guang.ctpp.dynamicPart.rotation;

import com.mo_guang.ctpp.CTPPEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.simibubi.create.content.contraptions.Contraption;
import tech.vixhentx.mcmod.ctnhlib.utils.ExtendNbtUtils;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * 固定轴旋转的装置实体，支持设定目标角度并自动旋转到该角度
 * 继承自 SimpleRotatingContraptionEntity，专注于单轴定向旋转和角度控制
 */
public class FixedAxisRotatingContraptionEntity extends SimpleRotatingContraptionEntity {

    // 同步数据定义
    private static final EntityDataAccessor<Vector3f> DATA_FIXED_AXIS =
            SynchedEntityData.defineId(FixedAxisRotatingContraptionEntity.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Float> DATA_TARGET_ANGLE =
            SynchedEntityData.defineId(FixedAxisRotatingContraptionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_CURRENT_ANGLE =
            SynchedEntityData.defineId(FixedAxisRotatingContraptionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_ROTATE_SPEED =
            SynchedEntityData.defineId(FixedAxisRotatingContraptionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DATA_IS_SEEKING_TARGET =
            SynchedEntityData.defineId(FixedAxisRotatingContraptionEntity.class, EntityDataSerializers.BOOLEAN);

    // 核心属性
    public static Vec3 Yaxis = new Vec3(0, 1, 0);
    private Vec3 fixedRotationAxis = Yaxis; // 默认绕Y轴旋转
    private float targetAngle = 0.0f; // 目标角度（度）
    private float currentAngle = 0.0f; // 当前角度（度）
    private float rotateSpeed = 5.0f; // 旋转速度（度/刻）
    private boolean isSeekingTarget = false; // 是否正在向目标角度移动

    public FixedAxisRotatingContraptionEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    /**
     * 创建固定轴旋转实体的静态工厂方法
     * @param world 世界对象
     * @param contraption 装置实例
     * @param controller 旋转控制器
     * @param pivot 旋转基点
     * @param fixedAxis 固定旋转轴（世界坐标系）
     * @param initialAngle 初始角度（度）
     * @return 初始化后的固定轴旋转实体
     */
    public static FixedAxisRotatingContraptionEntity create(Level world, Contraption contraption,
                                                            IRotationMultiblock controller, Vec3 pivot,
                                                            Vec3 fixedAxis, float initialAngle) {
        FixedAxisRotatingContraptionEntity entity =
                new FixedAxisRotatingContraptionEntity(CTPPEntityTypes.SIMPLE_CONTRAPTION.get(), world);
        entity.controllerPos = controller.getBlockPosition();
        entity.isRunning = true;
        entity.setContraption(contraption);
        entity.setPivot(pivot);

        // 初始化固定轴和角度
        entity.setFixedRotationAxis(fixedAxis);
        entity.currentAngle = initialAngle;
        entity.targetAngle = initialAngle;

        // 同步初始数据
        if (!world.isClientSide()) {
            entity.syncFixedAxisData();
            entity.syncAngleData();
        }

        return entity;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        // 初始化同步数据
        entityData.define(DATA_FIXED_AXIS, new Vector3f(0, 1, 0)); // 默认Y轴
        entityData.define(DATA_TARGET_ANGLE, 0.0f);
        entityData.define(DATA_CURRENT_ANGLE, 0.0f);
        entityData.define(DATA_ROTATE_SPEED, 5.0f);
        entityData.define(DATA_IS_SEEKING_TARGET, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);

        // 同步固定轴数据
        if (DATA_FIXED_AXIS.equals(key)) {
            Vector3f vec = entityData.get(DATA_FIXED_AXIS);
            this.fixedRotationAxis = new Vec3(vec.x(), vec.y(), vec.z()).normalize();
        }

        // 同步角度相关数据
        if (DATA_TARGET_ANGLE.equals(key)) {
            this.targetAngle = entityData.get(DATA_TARGET_ANGLE);
        }
        if (DATA_CURRENT_ANGLE.equals(key)) {
            this.currentAngle = entityData.get(DATA_CURRENT_ANGLE);
        }
        if (DATA_ROTATE_SPEED.equals(key)) {
            this.rotateSpeed = entityData.get(DATA_ROTATE_SPEED);
        }
        if (DATA_IS_SEEKING_TARGET.equals(key)) {
            this.isSeekingTarget = entityData.get(DATA_IS_SEEKING_TARGET);
        }
    }

    /**
     * 设置固定旋转轴（世界坐标系）
     * @param axis 旋转轴向量（会自动归一化）
     */
    public void setFixedRotationAxis(Vec3 axis) {
        if (axis.length() < 0.001f) {
            this.fixedRotationAxis = Yaxis; // 兜底为Y轴
        } else {
            this.fixedRotationAxis = axis.normalize();
        }

        if (!level().isClientSide()) {
            entityData.set(DATA_FIXED_AXIS, new Vector3f(
                    (float) this.fixedRotationAxis.x,
                    (float) this.fixedRotationAxis.y,
                    (float) this.fixedRotationAxis.z
            ));
        }
    }

    /**
     * 设置旋转目标角度
     * @param angle 目标角度（度），会自动归一化到0-360度范围
     */
    public void setTargetAngle(float angle) {
        this.targetAngle = normalizeAngle(angle);
        this.isSeekingTarget = true;

        if (!level().isClientSide()) {
            entityData.set(DATA_TARGET_ANGLE, this.targetAngle);
            entityData.set(DATA_IS_SEEKING_TARGET, true);
        }
    }

    /**
     * 设置旋转速度（度/刻）
     * @param speed 旋转速度，最小0.1度/刻
     */
    public void setRotateSpeed(float speed) {
        this.rotateSpeed = Math.max(0.1f, speed);

        if (!level().isClientSide()) {
            entityData.set(DATA_ROTATE_SPEED, this.rotateSpeed);
        }
    }

    /**
     * 停止向目标角度移动
     */
    public void stopSeekingTarget() {
        this.isSeekingTarget = false;

        if (!level().isClientSide()) {
            entityData.set(DATA_IS_SEEKING_TARGET, false);
            // 停止时清零角速度
            super.setRotationSpeed(Vec3.ZERO, 0);
        }
    }

    /**
     * 归一化角度到0-360度范围
     */
    private float normalizeAngle(float angle) {
        angle = angle % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    /**
     * 计算两个角度之间的最短旋转方向和差值
     * @return 最短差值（带符号，正值为顺时针，负值为逆时针）
     */
    private float calculateShortestAngleDiff(float from, float to) {
        float diff = to - from;
        diff = diff % 360;
        if (diff > 180) {
            diff -= 360;
        } else if (diff < -180) {
            diff += 360;
        }
        return diff;
    }

    @Override
    public void tick() {
        super.tick();

        if (!isRunning || !isSeekingTarget) {
            return;
        }

        // 仅服务端处理角度计算和同步
        if (!level().isClientSide()) {
            float angleDiff = calculateShortestAngleDiff(currentAngle, targetAngle);
            float angleThreshold = 0.5f; // 角度误差阈值

            // 检查是否到达目标角度
            if (Math.abs(angleDiff) <= angleThreshold) {
                currentAngle = targetAngle;
                stopSeekingTarget();
                super.setRotationSpeed(Vec3.ZERO, 0);
            } else {
                // 计算本次tick需要旋转的角度
                float rotateStep = Math.min(Math.abs(angleDiff), rotateSpeed);
                float direction = angleDiff > 0 ? 1 : -1;

                // 更新当前角度
                currentAngle += direction * rotateStep;
                currentAngle = normalizeAngle(currentAngle);

                // 设置旋转速度（沿固定轴）
                super.setRotationSpeed(fixedRotationAxis, direction * rotateStep);

                // 更新四元数旋转（覆盖父类的旋转逻辑）
                updateRotationFromAngle();
            }

            // 同步角度数据到客户端
            syncAngleData();
        }
    }

    /**
     * 根据当前角度和固定轴更新四元数旋转
     */
    private void updateRotationFromAngle() {
        // 创建基于固定轴和当前角度的四元数
        Quaternionf newRotation = new Quaternionf()
                .fromAxisAngleRad(
                        (float) fixedRotationAxis.x,
                        (float) fixedRotationAxis.y,
                        (float) fixedRotationAxis.z,
                        (float) Math.toRadians(currentAngle)
                );

        this.serverRotation = newRotation;
        syncRotationQuaternion();
    }

    /**
     * 同步角度相关数据到客户端
     */
    private void syncAngleData() {
        if (!level().isClientSide()) {
            entityData.set(DATA_CURRENT_ANGLE, this.currentAngle);
            entityData.set(DATA_TARGET_ANGLE, this.targetAngle);
            entityData.set(DATA_IS_SEEKING_TARGET, this.isSeekingTarget);
        }
    }

    /**
     * 同步固定轴数据到客户端
     */
    private void syncFixedAxisData() {
        if (!level().isClientSide()) {
            entityData.set(DATA_FIXED_AXIS, new Vector3f(
                    (float) fixedRotationAxis.x,
                    (float) fixedRotationAxis.y,
                    (float) fixedRotationAxis.z
            ));
        }
    }

    @Override
    protected void writeAdditional(CompoundTag nbt, boolean spawnPacket) {
        super.writeAdditional(nbt, spawnPacket);

        // 保存固定轴旋转相关数据
        CompoundTag fixedAxisTag = new CompoundTag();
        fixedAxisTag.put("FixedRotationAxis", ExtendNbtUtils.writeVec3(fixedRotationAxis));
        fixedAxisTag.putFloat("TargetAngle", targetAngle);
        fixedAxisTag.putFloat("CurrentAngle", currentAngle);
        fixedAxisTag.putFloat("RotateSpeed", rotateSpeed);
        fixedAxisTag.putBoolean("IsSeekingTarget", isSeekingTarget);

        nbt.put("FixedAxisRotationData", fixedAxisTag);
    }

    @Override
    protected void readAdditional(CompoundTag nbt, boolean spawnData) {
        super.readAdditional(nbt, spawnData);

        // 读取固定轴旋转相关数据
        if (nbt.contains("FixedAxisRotationData", CompoundTag.TAG_COMPOUND)) {
            CompoundTag fixedAxisTag = nbt.getCompound("FixedAxisRotationData");

            this.fixedRotationAxis = ExtendNbtUtils.readVec3(fixedAxisTag.getCompound("FixedRotationAxis"));
            this.targetAngle = fixedAxisTag.getFloat("TargetAngle");
            this.currentAngle = fixedAxisTag.getFloat("CurrentAngle");
            this.rotateSpeed = fixedAxisTag.getFloat("RotateSpeed");
            this.isSeekingTarget = fixedAxisTag.getBoolean("IsSeekingTarget");

            // 归一化轴向量和角度
            if (this.fixedRotationAxis.length() < 0.001f) {
                this.fixedRotationAxis = Yaxis;
            } else {
                this.fixedRotationAxis = this.fixedRotationAxis.normalize();
            }
            this.targetAngle = normalizeAngle(targetAngle);
            this.currentAngle = normalizeAngle(currentAngle);

            // 服务端同步数据到entityData
            if (!level().isClientSide()) {
                syncFixedAxisData();
                syncAngleData();
            }
        }
    }

    // Getter方法
    public Vec3 getFixedRotationAxis() {
        return fixedRotationAxis;
    }

    public float getTargetAngle() {
        return targetAngle;
    }

    public float getCurrentAngle() {
        return currentAngle;
    }

    public float getRotateSpeed() {
        return rotateSpeed;
    }

    public boolean isSeekingTarget() {
        return isSeekingTarget;
    }
}
