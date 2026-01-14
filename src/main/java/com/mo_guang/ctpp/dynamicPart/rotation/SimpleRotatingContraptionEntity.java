package com.mo_guang.ctpp.dynamicPart.rotation;

import com.mo_guang.ctpp.CTPPEntityTypes;
import com.mojang.blaze3d.vertex.PoseStack;

import com.simibubi.create.content.contraptions.*;
import com.simibubi.create.foundation.collision.Matrix3d;
import lombok.Getter;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.Field;

import static com.mo_guang.ctpp.util.MathUtil.*;

public class SimpleRotatingContraptionEntity extends AbstractContraptionEntity {

    /**
     * 服务端 authoritative 角度
     **/
    protected Quaternionf serverRotation = new Quaternionf();
    protected Quaternionf clientRotation = new Quaternionf();
    protected Quaternionf prevClientRotation = new Quaternionf();
    // 旋转速度（世界坐标系）
    private Vec3 angularVelocity = Vec3.ZERO;


    private static final EntityDataAccessor<Float> DATA_Q_W =
            SynchedEntityData.defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_Q_X =
            SynchedEntityData.defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_Q_Y =
            SynchedEntityData.defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_Q_Z =
            SynchedEntityData.defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Vector3f> DATA_ANGULAR_VEL =
            SynchedEntityData.defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Vector3f> DATA_PIVOT =
            SynchedEntityData.defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.VECTOR3);

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_Q_W, 1.0f);  // 四元数 w 分量
        entityData.define(DATA_Q_X, 0.0f);  // 四元数 x 分量
        entityData.define(DATA_Q_Y, 0.0f);  // 四元数 y 分量
        entityData.define(DATA_Q_Z, 0.0f);  // 四元数 z 分量
        entityData.define(DATA_ANGULAR_VEL, new Vector3f(0, 0, 0));
        entityData.define(DATA_PIVOT, new Vector3f(0, 0, 0));
    }


    /**
     * 旋转基点
     **/
    @Getter
    private Vec3 pivot = Vec3.ZERO;

    public SimpleRotatingContraptionEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public static SimpleRotatingContraptionEntity create(Level world, Contraption contraption, Vec3 pivot) {
        SimpleRotatingContraptionEntity entity =
                new SimpleRotatingContraptionEntity(CTPPEntityTypes.SIMPLE_CONTRAPTION.get(), world);
        entity.setContraption(contraption);
        entity.setPivot(pivot);
        return entity;
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        if (!level().isClientSide())
            return;
    }

    public void setRotationSpeed(float x, float y, float z) {
        Vec3 vec3 = new Vec3(x, y, z);
        setRotationSpeed(vec3.normalize(), (float) vec3.length());
    }

    public void setRotationSpeed(Vec3 worldAxis, float degPerTick) {
        if (worldAxis.length() < 0.001f) {
            this.angularVelocity = Vec3.ZERO;
        } else {
            this.angularVelocity = worldAxis.normalize().scale(degPerTick);
        }

        if (!level().isClientSide) {
            Vector3f vec = new Vector3f(
                    (float) angularVelocity.x,
                    (float) angularVelocity.y,
                    (float) angularVelocity.z
            );
            entityData.set(DATA_ANGULAR_VEL, vec);
        }
    }

    public void setPivot(Vec3 pivot) {
        this.pivot = pivot;
        if (!level().isClientSide) {
            entityData.set(DATA_PIVOT, new Vector3f((float) pivot.x, (float) pivot.y, (float) pivot.z));
        }
    }


    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);

        if (DATA_PIVOT.equals(key)) {
            Vector3f vec = entityData.get(DATA_PIVOT);
            this.pivot = new Vec3(vec.x(), vec.y(), vec.z());
        }
        if (level().isClientSide) {
            if (DATA_Q_W.equals(key) || DATA_Q_X.equals(key) ||
                    DATA_Q_Y.equals(key) || DATA_Q_Z.equals(key)) {

                // 更新服务端旋转参考值
                float w = entityData.get(DATA_Q_W);
                float x = entityData.get(DATA_Q_X);
                float y = entityData.get(DATA_Q_Y);
                float z = entityData.get(DATA_Q_Z);

                // 可以在这里立即更新clientRotation，或让checkAndCorrectRotation处理
                // 对于快速旋转，最好保持预测，只在差异大时纠正
            }

            if (DATA_ANGULAR_VEL.equals(key)) {
                Vector3f vel = entityData.get(DATA_ANGULAR_VEL);
                this.angularVelocity = new Vec3(vel.x(), vel.y(), vel.z());
            }
        }
    }

    @Override
    public Vec3 getContactPointMotion(Vec3 globalContactPoint) {
        if (contraption instanceof TranslatingContraption)
            return getDeltaMovement();
        return super.getContactPointMotion(globalContactPoint);
    }

    @Override
    protected void setContraption(Contraption contraption) {
        super.setContraption(contraption);
    }

    @Override
    public ContraptionRotationState getRotationState() {
        ContraptionRotationState crs = new ContraptionRotationState();

        Matrix3d mat = new Matrix3d().asIdentity();
        mat.multiply(new Matrix3d().asZRotation(AngleHelper.rad(-getEulerAngle().x)));
        mat.multiply(new Matrix3d().asYRotation(AngleHelper.rad(-getEulerAngle().y)));
        mat.multiply(new Matrix3d().asXRotation(AngleHelper.rad(-getEulerAngle().z)));


        // 直接设置 matrix 字段（asMatrix 会优先返回该 matrix）
        try {
            Field f = ContraptionRotationState.class.getDeclaredField("matrix");
            f.setAccessible(true);
            f.set(crs, mat);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 保持其它字段为默认（避免混淆）
        crs.xRotation = 0;
        crs.yRotation = 0;
        crs.zRotation = 0;
        crs.secondYRotation = 0;

        return crs;
    }

    @Override
    public Vec3 applyRotation(Vec3 localPos, float partialTicks) {
        Quaternionf interpQ;
        if (level().isClientSide) {
            interpQ = slerp(prevClientRotation, clientRotation, partialTicks);
        } else {
            interpQ = serverRotation;
        }

        return rotateByQuaternion(localPos, interpQ);
    }

    @Override
    public Vec3 reverseRotation(Vec3 globalPos, float partialTicks) {
        Quaternionf interpQ;
        if (level().isClientSide) {
            interpQ = slerp(prevClientRotation, clientRotation, partialTicks);
        } else {
            interpQ = serverRotation;
        }

        // 获取逆旋转（单位四元数的逆=共轭）
        Quaternionf inverseQ = new Quaternionf(interpQ).conjugate();

        return rotateByQuaternion(globalPos, inverseQ);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return pDistance < (16 * 16) * (16 * 16);
    }

    @Override
    public void teleportTo(double p_70634_1_, double p_70634_3_, double p_70634_5_) {
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void lerpTo(double x, double y, double z, float yw, float pt, int inc, boolean t) {
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            if (!angularVelocity.equals(Vec3.ZERO)) {
                float speed = (float) angularVelocity.length();
                Vec3 axis = angularVelocity.normalize();

                Quaternionf delta = new Quaternionf()
                        .fromAxisAngleRad(
                                (float) axis.x, (float) axis.y, (float) axis.z,
                                (float) Math.toRadians(speed)
                        );

                serverRotation = delta.mul(serverRotation);
                syncRotationQuaternion();
            }
        } else {
            prevClientRotation = new Quaternionf(clientRotation);

            // 2. 应用本地预测的旋转（保持流畅性）
            if (!angularVelocity.equals(Vec3.ZERO)) {
                float speed = (float) angularVelocity.length();
                if (speed > 0.001f) {
                    Vec3 axis = angularVelocity.normalize();
                    float angleRad = (float) Math.toRadians(speed);

                    Quaternionf delta = new Quaternionf()
                            .fromAxisAngleRad(
                                    (float) axis.x,
                                    (float) axis.y,
                                    (float) axis.z,
                                    angleRad
                            );

                    // 应用本地预测旋转
                    clientRotation = delta.mul(clientRotation, new Quaternionf());
                    clientRotation.normalize();
                }
            }
            // 3. 检查是否需要强制同步（纠正预测）
            checkAndCorrectRotation();
        }
        // TODO: fix this
        setPos(contraption.anchor.getX(), contraption.anchor.getY(), contraption.anchor.getZ());
    }

    /**
     * 同步四元数旋转到客户端（服务端调用）
     */
    private void syncRotationQuaternion() {
        // 获取当前存储的值
        float storedW = entityData.get(DATA_Q_W);
        float storedX = entityData.get(DATA_Q_X);
        float storedY = entityData.get(DATA_Q_Y);
        float storedZ = entityData.get(DATA_Q_Z);

        // 计算当前值与存储值的差异
        Quaternionf storedQ = new Quaternionf(storedX, storedY, storedZ, storedW);

        // 使用角度差作为阈值（例如2度）
        float angleDiff = quaternionAngleDifference(serverRotation, storedQ);
        float thresholdDeg = 2.0f;

        if (angleDiff > Math.toRadians(thresholdDeg)) {
            // 角度差异超过阈值，需要同步
            entityData.set(DATA_Q_W, serverRotation.w());
            entityData.set(DATA_Q_X, serverRotation.x());
            entityData.set(DATA_Q_Y, serverRotation.y());
            entityData.set(DATA_Q_Z, serverRotation.z());

        }
    }

    /**
     * 检查并纠正客户端旋转预测（客户端调用）
     */
    private void checkAndCorrectRotation() {
        // 获取服务端同步的旋转
        float serverW = entityData.get(DATA_Q_W);
        float serverX = entityData.get(DATA_Q_X);
        float serverY = entityData.get(DATA_Q_Y);
        float serverZ = entityData.get(DATA_Q_Z);
        Quaternionf serverQ = new Quaternionf(serverX, serverY, serverZ, serverW);

        // 计算预测旋转与服务端旋转的差异
        float angleDiff = quaternionAngleDifference(clientRotation, serverQ);
        float thresholdDeg = 5.0f;  // 可容忍的差异阈值

        if (angleDiff > Math.toRadians(thresholdDeg)) {
            // 差异过大，纠正客户端旋转
            // 使用插值平滑过渡到正确旋转
            float lerpFactor = 0.5f;  // 纠正强度

            // 插值到服务端旋转
            Quaternionf corrected = slerp(clientRotation, serverQ, lerpFactor);
            corrected.normalize();

            // 更新客户端旋转（保持预测但不完全覆盖）
            clientRotation = corrected;

        } else if (angleDiff > 0.001f) {
            // 微小差异，轻微纠正
            float lerpFactor = 0.3f;
            Quaternionf corrected = slerp(clientRotation, serverQ, lerpFactor);
            corrected.normalize();
            clientRotation = corrected;
        }
    }

    @Override
    protected void tickContraption() {
        tickActors();
    }


    @Override
    protected StructureTransform makeStructureTransform() {
        BlockPos offset = net.minecraft.core.BlockPos.containing(pivot);
        return new StructureTransform(offset, (float) getEulerAngle().x, (float) getEulerAngle().y, (float) getEulerAngle().z);
    }

    @Override
    protected void onContraptionStalled() {
        super.onContraptionStalled();
    }

    @Override
    protected float getStalledAngle() {
        return (float) getEulerAngle().x;
    }

    @Override
    protected void handleStallInformation(double x, double y, double z, float angle) {
        setPosRaw(x, y, z);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void applyLocalTransforms(PoseStack matrixStack, float partialTicks) {
        // 1. 获取插值后的四元数
        Quaternionf rotationQ;
        if (level().isClientSide) {
            // 客户端使用插值四元数
            rotationQ = slerp(prevClientRotation, clientRotation, partialTicks);
        } else {
            // 服务端直接使用当前旋转
            rotationQ = new Quaternionf(serverRotation);
        }

        // 3. 应用四元数旋转
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.mulPose(rotationQ);
        matrixStack.translate(-0.5, -0.5, -0.5);

    }
    public Vec3 getEulerAngle() {
        return getSmartEulerAngles(serverRotation, 5.0f);
    }
}
