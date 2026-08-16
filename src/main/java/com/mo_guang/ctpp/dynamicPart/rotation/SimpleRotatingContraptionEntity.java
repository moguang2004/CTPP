package com.mo_guang.ctpp.dynamicPart.rotation;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mo_guang.ctpp.CTPPEntityTypes;
import com.mo_guang.ctpp.dynamicPart.QuaternionRotationState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.*;
import lombok.Getter;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import tech.vixhentx.mcmod.ctnhlib.utils.ExtendNbtUtils;

import static com.mo_guang.ctpp.util.MathUtil.*;

/**
 * 一个可以被控制的绕着某个锚点自由旋转的装置实体
 */
public class SimpleRotatingContraptionEntity extends AbstractContraptionEntity {

    public BlockPos controllerPos;
    /**
     * 服务端 authoritative 角度
     **/
    protected Quaternionf serverRotation = new Quaternionf();
    protected Quaternionf clientRotation = new Quaternionf();
    protected Quaternionf prevClientRotation = new Quaternionf();
    // 旋转速度（世界坐标系）
    private Vec3 angularVelocity = Vec3.ZERO;
    // 装置运行状态（主要用于刚刚载入游戏时的自锁）
    @Getter
    protected boolean isRunning = false;

    protected static final EntityDataAccessor<Float> DATA_Q_W = SynchedEntityData
            .defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> DATA_Q_X = SynchedEntityData
            .defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> DATA_Q_Y = SynchedEntityData
            .defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> DATA_Q_Z = SynchedEntityData
            .defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Vector3f> DATA_ANGULAR_VEL = SynchedEntityData
            .defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.VECTOR3);
    protected static final EntityDataAccessor<Vector3f> DATA_PIVOT = SynchedEntityData
            .defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.VECTOR3);
    protected static final EntityDataAccessor<Boolean> DATA_IS_RUNNING = SynchedEntityData
            .defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.BOOLEAN);

    /**
     * 同步服务端与客户端的四元数数据
     **/
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_Q_W, 1.0f);  // 四元数 w 分量
        entityData.define(DATA_Q_X, 0.0f);  // 四元数 x 分量
        entityData.define(DATA_Q_Y, 0.0f);  // 四元数 y 分量
        entityData.define(DATA_Q_Z, 0.0f);  // 四元数 z 分量
        entityData.define(DATA_ANGULAR_VEL, new Vector3f(0, 0, 0));
        entityData.define(DATA_PIVOT, new Vector3f(0, 0, 0));
        entityData.define(DATA_IS_RUNNING, false);
    }

    /**
     * 旋转基点
     **/
    @Getter
    private Vec3 pivot = Vec3.ZERO;

    public SimpleRotatingContraptionEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public static SimpleRotatingContraptionEntity create(Level world, Contraption contraption,
                                                         IContraptionMultiblock controller, Vec3 pivot) {
        SimpleRotatingContraptionEntity entity = new SimpleRotatingContraptionEntity(
                CTPPEntityTypes.SIMPLE_CONTRAPTION.get(), world);
        entity.controllerPos = controller.getBlockPosition();
        entity.setRunning(true);
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
        if (!isRunning) {
            this.angularVelocity = Vec3.ZERO;
            return;
        }
        if (worldAxis.length() < 0.001f) {
            this.angularVelocity = Vec3.ZERO;
        } else {
            this.angularVelocity = worldAxis.normalize().scale(degPerTick);
        }

        if (!level().isClientSide) {
            Vector3f vec = new Vector3f(
                    (float) angularVelocity.x,
                    (float) angularVelocity.y,
                    (float) angularVelocity.z);
            entityData.set(DATA_ANGULAR_VEL, vec);
        }
    }

    public void setRotationSpeedRPM(Vec3 worldAxis, float rpm) {
        float degPerTick = rpm * 360f / (20f * 60); // 1转=360度，1秒20tick，60秒1分钟
        setRotationSpeed(worldAxis, degPerTick);
    }

    public void setRunning(boolean running) {
        this.isRunning = running;
        if (!level().isClientSide()) {
            entityData.set(DATA_IS_RUNNING, running);
            // 停止运行时，强制清零角速度（避免残留）
            if (!running) {
                this.angularVelocity = Vec3.ZERO;
                entityData.set(DATA_ANGULAR_VEL, new Vector3f(0, 0, 0));
            }
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
        if (DATA_IS_RUNNING.equals(key)) {
            this.isRunning = entityData.get(DATA_IS_RUNNING);
            // 客户端停止运行时，清零预测旋转差值
            if (level().isClientSide() && !this.isRunning) {
                this.clientRotation = new Quaternionf(this.serverRotation);
                this.prevClientRotation = new Quaternionf(this.serverRotation);
            }
        }
        if (level().isClientSide) {
            if (DATA_Q_W.equals(key) || DATA_Q_X.equals(key) ||
                    DATA_Q_Y.equals(key) || DATA_Q_Z.equals(key)) {

                // 更新服务端旋转参考值
                float w = entityData.get(DATA_Q_W);
                float x = entityData.get(DATA_Q_X);
                float y = entityData.get(DATA_Q_Y);
                float z = entityData.get(DATA_Q_Z);

                Quaternionf syncedRotation = new Quaternionf(x, y, z, w);
                if (!isUsableRotation(syncedRotation)) {
                    syncedRotation.identity();
                } else {
                    syncedRotation.normalize();
                }
                this.serverRotation = syncedRotation;
                // Do not leave the renderer on a stale pose once prediction is idle.
                if (!hasAngularVelocity() || !this.isRunning) {
                    this.clientRotation = new Quaternionf(syncedRotation);
                    this.prevClientRotation = new Quaternionf(syncedRotation);
                }
            }

            if (DATA_ANGULAR_VEL.equals(key)) {
                Vector3f vel = entityData.get(DATA_ANGULAR_VEL);
                this.angularVelocity = new Vec3(vel.x(), vel.y(), vel.z());
                if (!hasAngularVelocity()) {
                    Quaternionf syncedRotation = readSyncedRotation();
                    this.serverRotation = syncedRotation;
                    this.clientRotation = new Quaternionf(syncedRotation);
                    this.prevClientRotation = new Quaternionf(syncedRotation);
                }
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
        return new QuaternionRotationState(level().isClientSide ? clientRotation : serverRotation);
    }

    /**
     * Discrete rotating contraptions can own their server-side progression while
     * retaining angular velocity for client-side prediction.
     */
    protected boolean shouldAdvanceWithAngularVelocity() {
        return true;
    }

    /**
     * Continuous contraptions use authoritative snapshots to correct local
     * prediction. Discrete contraptions can opt out while a known move runs.
     */
    protected boolean shouldCorrectClientRotation() {
        return true;
    }

    protected boolean hasAngularVelocity() {
        return angularVelocity.lengthSqr() > 1.0E-8;
    }

    @OnlyIn(Dist.CLIENT)
    protected Quaternionf getInterpolatedClientRotation(float partialTicks) {
        return slerp(prevClientRotation, clientRotation, partialTicks);
    }

    @Override
    public Vec3 applyRotation(Vec3 localPos, float partialTicks) {
        Quaternionf interpQ = level().isClientSide ? getInterpolatedClientRotation(partialTicks) : serverRotation;

        return rotateByQuaternion(localPos, interpQ);
    }

    @Override
    public Vec3 reverseRotation(Vec3 globalPos, float partialTicks) {
        Quaternionf interpQ = level().isClientSide ? getInterpolatedClientRotation(partialTicks) : serverRotation;

        // 获取逆旋转（单位四元数的逆=共轭）
        Quaternionf inverseQ = new Quaternionf(interpQ).conjugate();

        return rotateByQuaternion(globalPos, inverseQ);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return pDistance < (16 * 16) * (16 * 16);
    }

    @Override
    public void teleportTo(double p_70634_1_, double p_70634_3_, double p_70634_5_) {}

    @Override
    @OnlyIn(Dist.CLIENT)
    public void lerpTo(double x, double y, double z, float yw, float pt, int inc, boolean t) {}

    @Override
    public void tick() {
        super.tick();
        // Always try to find and reattach to controller, even if isRunning=false
        // This handles chunk reload scenarios where the entity loads before the controller
        IContraptionMultiblock controller = getController();
        if (controller == null) {
            // Controller not available yet (chunk might not be loaded).
            // Don't discard — wait for it on next tick.
            return;
        }
        if (!isRunning || !controller.isAttachedTo(this)) {
            // Try to reattach: if already attached, tickContraption handles this too,
            // but we also try here to cover edge cases.
            if (!controller.isAttachedTo(this)) {
                controller.attach(this);
            }
            setRunning(true);
        }
        if (!level().isClientSide) {
            if (shouldAdvanceWithAngularVelocity() && hasAngularVelocity()) {
                float speed = (float) angularVelocity.length();
                Vec3 axis = angularVelocity.normalize();

                Quaternionf delta = new Quaternionf()
                        .fromAxisAngleRad(
                                (float) axis.x, (float) axis.y, (float) axis.z,
                                (float) Math.toRadians(speed));

                serverRotation = delta.mul(serverRotation, new Quaternionf()).normalize();
                syncRotationQuaternion();
            }
        } else {
            prevClientRotation = new Quaternionf(clientRotation);

            // 2. 应用本地预测的旋转（保持流畅性）
            if (hasAngularVelocity()) {
                float speed = (float) angularVelocity.length();
                if (speed > 0.001f) {
                    Vec3 axis = angularVelocity.normalize();
                    float angleRad = (float) Math.toRadians(speed);

                    Quaternionf delta = new Quaternionf()
                            .fromAxisAngleRad(
                                    (float) axis.x,
                                    (float) axis.y,
                                    (float) axis.z,
                                    angleRad);

                    // 应用本地预测旋转
                    clientRotation = delta.mul(clientRotation, new Quaternionf());
                    clientRotation.normalize();
                }
            }
            // 3. 检查是否需要强制同步（纠正预测）
            if (shouldCorrectClientRotation()) {
                checkAndCorrectRotation();
            }
        }
    }

    /**
     * 同步四元数旋转到客户端（服务端调用）
     */
    public void syncRotationQuaternion() {
        syncRotationQuaternion(false);
    }

    protected void syncRotationQuaternion(boolean force) {
        if (!isRunning) {
            return;
        }
        serverRotation.normalize();
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

        if (force || angleDiff > Math.toRadians(thresholdDeg)) {
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
        if (!isRunning) {
            return;
        }
        Quaternionf serverQ = readSyncedRotation();
        serverRotation = new Quaternionf(serverQ);

        // 计算预测旋转与服务端旋转的差异
        float angleDiff = quaternionAngleDifference(clientRotation, serverQ);
        if (!hasAngularVelocity()) {
            clientRotation = new Quaternionf(serverQ);
            prevClientRotation = new Quaternionf(serverQ);
            return;
        }
        float thresholdDeg = 5.0f;  // 可容忍的差异阈值

        if (angleDiff > Math.toRadians(thresholdDeg)) {
            // 差异过大，纠正客户端旋转
            // 使用插值平滑过渡到正确旋转
            float lerpFactor = 1f;  // 纠正强度

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

    private Quaternionf readSyncedRotation() {
        float w = entityData.get(DATA_Q_W);
        float x = entityData.get(DATA_Q_X);
        float y = entityData.get(DATA_Q_Y);
        float z = entityData.get(DATA_Q_Z);
        Quaternionf syncedRotation = new Quaternionf(x, y, z, w);
        if (!isUsableRotation(syncedRotation)) {
            return new Quaternionf();
        }
        return syncedRotation.normalize();
    }

    private static boolean isUsableRotation(Quaternionf rotation) {
        return Float.isFinite(rotation.x()) && Float.isFinite(rotation.y()) && Float.isFinite(rotation.z()) &&
                Float.isFinite(rotation.w()) && rotation.lengthSquared() > 1.0E-8f;
    }

    protected IContraptionMultiblock getController() {
        if (controllerPos == null)
            return null;
        if (!level().isLoaded(controllerPos))
            return null;
        var controller = MetaMachine.getMachine(level(), controllerPos);
        if (!(controller instanceof IContraptionMultiblock))
            return null;
        return (IContraptionMultiblock) controller;
    }

    @Override
    protected void tickContraption() {
        tickActors();
        if (controllerPos == null)
            return;
        if (!level().isLoaded(controllerPos))
            return;
        IContraptionMultiblock controller = getController();
        if (controller == null) {
            // Controller chunk loaded but machine not found.
            // Don't discard immediately — the machine might still be initializing.
            // Will be cleaned up if machine truly gone (structure invalid will handle it).
            return;
        }
        if (!controller.isAttachedTo(this)) {
            controller.attach(this);
            setRunning(true);
            if (level().isClientSide)
                setPos(getX(), getY(), getZ());
        }
    }

    @Override
    protected StructureTransform makeStructureTransform() {
        BlockPos offset = BlockPos.containing(pivot);
        return new StructureTransform(offset, 0, 0, 0);
    }

    @Override
    protected void onContraptionStalled() {
        super.onContraptionStalled();
        setRunning(false);
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
        Quaternionf rotationQ = getInterpolatedClientRotation(partialTicks);

        // 3. 应用四元数旋转
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.mulPose(rotationQ);
        matrixStack.translate(-0.5, -0.5, -0.5);
    }

    @Override
    protected void writeAdditional(CompoundTag nbt, boolean spawnPacket) {
        ensureContraptionReadyForSave();
        // 先调用父类方法，保存父类的核心数据
        super.writeAdditional(nbt, spawnPacket);

        nbt.put("ControllerRelative", NbtUtils.writeBlockPos(controllerPos.subtract(blockPosition())));
        nbt.put("Pivot", ExtendNbtUtils.writeVec3(pivot));
        // 校验服务端旋转四元数是否为空，避免空指针异常
        if (this.serverRotation == null) {
            this.serverRotation = new Quaternionf().identity(); // 兜底：初始化为单位四元数（无旋转）
        }

        CompoundTag rotationTag = new CompoundTag();
        rotationTag.putBoolean("IsRunning", this.isRunning);
        // 将四元数的 x/y/z/w 四个分量写入子 NBT（浮点型数据）
        rotationTag.put("Quaternionf", ExtendNbtUtils.writeQuaternionf(this.serverRotation));

        // 将子 NBT 标签写入实体主 NBT，指定唯一键名（如 "ContraptionRotationData"）
        nbt.put("ContraptionRotationData", rotationTag);
    }

    @Override
    protected void readAdditional(CompoundTag nbt, boolean spawnData) {
        super.readAdditional(nbt, spawnData);

        controllerPos = NbtUtils.readBlockPos(nbt.getCompound("ControllerRelative")).offset(blockPosition());
        pivot = ExtendNbtUtils.readVec3(nbt.getCompound("Pivot"));
        // 校验旋转子 NBT 标签是否存在，避免空指针异常
        if (!nbt.contains("ContraptionRotationData", CompoundTag.TAG_COMPOUND)) {
            // 兜底：初始化默认旋转状态（无旋转）
            this.serverRotation = new Quaternionf().identity();
            this.angularVelocity = Vec3.ZERO;
            return;
        }

        // 读取旋转子 NBT 标签
        CompoundTag rotationTag = nbt.getCompound("ContraptionRotationData");
        this.isRunning = rotationTag.getBoolean("IsRunning");
        // 构建服务端旋转四元数，还原旋转角度
        this.serverRotation = ExtendNbtUtils.readQuaternionf(rotationTag.getCompound("Quaternionf"));

        // 四元数归一化（关键）：修复 NBT 存储/读取过程中可能出现的精度损失，保证旋转有效性
        this.serverRotation.normalize();

        if (!this.level().isClientSide()) {
            entityData.set(DATA_Q_W, this.serverRotation.w());
            entityData.set(DATA_Q_X, this.serverRotation.x());
            entityData.set(DATA_Q_Y, this.serverRotation.y());
            entityData.set(DATA_Q_Z, this.serverRotation.z());
            entityData.set(DATA_IS_RUNNING, this.isRunning);
            // 角速度从 NBT 恢复，之后由控制器重新设置
            this.angularVelocity = Vec3.ZERO;
            entityData.set(DATA_ANGULAR_VEL, new Vector3f(0, 0, 0));
        }
        // 客户端同步：加载时立即将服务端数据同步到客户端，避免视觉延迟
        if (this.level().isClientSide()) {
            this.clientRotation = new Quaternionf(this.serverRotation);
            this.prevClientRotation = new Quaternionf(this.serverRotation);
            this.angularVelocity = Vec3.ZERO;
        }
    }

    public Vec3 getEulerAngle() {
        return getSmartEulerAngles(serverRotation, 5.0f);
    }

    public void ensureContraptionReadyForSave() {
        if (contraption == null || level() == null) {
            return;
        }
        boolean hasUninitializedActors = contraption.getActors().stream()
                .anyMatch(actor -> actor != null && actor.right == null);
        if (hasUninitializedActors) {
            contraption.startMoving(level());
        }
    }
}
