package com.mo_guang.ctpp.dynamicPart.rotation;

import com.mo_guang.ctpp.CTPPEntityTypes;
import com.mo_guang.ctpp.dynamicPart.RotationWandItem;
import com.mojang.blaze3d.vertex.PoseStack;

import com.simibubi.create.content.contraptions.*;
import com.simibubi.create.foundation.collision.Matrix3d;
import lombok.Getter;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.Field;

public class SimpleRotatingContraptionEntity extends AbstractContraptionEntity{

    /** 服务端 authoritative 角度 **/
    private float prevXRot, prevYRot, prevZRot;
    protected float xRot=0f, yRot=0f, zRot;
    protected float serverXRot = 0f, serverYRot = 0f, serverZRot = 0f;

    /** 旋转速度（deg/tick） **/
    private static final EntityDataAccessor<Float> DATA_X_SPEED =
            SynchedEntityData.defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_Y_SPEED =
            SynchedEntityData.defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_Z_SPEED =
            SynchedEntityData.defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Vector3f> DATA_PIVOT =
            SynchedEntityData.defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Float> DATA_X_ROT =
            SynchedEntityData.defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_Y_ROT =
            SynchedEntityData.defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_Z_ROT =
            SynchedEntityData.defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.FLOAT);



    /** 本地速度缓存 **/
    private float xSpeed, ySpeed, zSpeed;

    /** 旋转基点 **/
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
        this.xSpeed = x;
        this.ySpeed = y;
        this.zSpeed = z;
        if (!level().isClientSide) {
            entityData.set(DATA_X_SPEED, x);
            entityData.set(DATA_Y_SPEED, y);
            entityData.set(DATA_Z_SPEED, z);
        }
    }

    public void setPivot(Vec3 pivot) {
        this.pivot = pivot;
        if (!level().isClientSide) {
            entityData.set(DATA_PIVOT, new Vector3f((float)pivot.x, (float)pivot.y, (float)pivot.z));
        }
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_X_SPEED, 0f);
        entityData.define(DATA_Y_SPEED, 0f);
        entityData.define(DATA_Z_SPEED, 0f);
        entityData.define(DATA_PIVOT, new Vector3f(0,0,0));
        entityData.define(DATA_X_ROT, 0f);
        entityData.define(DATA_Y_ROT, 0f);
        entityData.define(DATA_Z_ROT, 0f);

    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_X_SPEED.equals(key)) {
            this.xSpeed = entityData.get(DATA_X_SPEED);
        }
        if (DATA_Y_SPEED.equals(key)) {
            this.ySpeed = entityData.get(DATA_Y_SPEED);
        }
        if (DATA_Z_SPEED.equals(key)) {
            this.zSpeed = entityData.get(DATA_Z_SPEED);
        }
        if (DATA_PIVOT.equals(key)) {
            Vector3f vec = entityData.get(DATA_PIVOT);
            this.pivot = new Vec3(vec.x(), vec.y(), vec.z());
        }
        if (DATA_X_ROT.equals(key)) {
            float newXRot = entityData.get(DATA_X_ROT);
            // 客户端插值处理
            if (level().isClientSide) {
                xRot = newXRot;
            }
        }
        if (DATA_Y_ROT.equals(key)) {
            float newYRot = entityData.get(DATA_Y_ROT);
            if (level().isClientSide) {
                yRot = newYRot;
            }
        }
        if (DATA_Z_ROT.equals(key)) {
            float newZRot = entityData.get(DATA_Z_ROT);
            if (level().isClientSide) {
                zRot = newZRot;
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
        mat.multiply(new Matrix3d().asZRotation(AngleHelper.rad(-zRot)));
        mat.multiply(new Matrix3d().asYRotation(AngleHelper.rad(-yRot)));
        mat.multiply(new Matrix3d().asXRotation(AngleHelper.rad(-xRot)));



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
        float x = getXRot(partialTicks);
        float y = getYRot(partialTicks);
        float z = getZRot(partialTicks);

        // 旋转顺序: X -> Y -> Z (可根据需要调整)
        localPos = VecHelper.rotate(localPos, x, Direction.Axis.X);
        localPos = VecHelper.rotate(localPos, y, Direction.Axis.Y);
        localPos = VecHelper.rotate(localPos, z, Direction.Axis.Z);

        return localPos;
    }

    @Override
    public Vec3 reverseRotation(Vec3 localPos, float partialTicks) {
        float x = getXRot(partialTicks);
        float y = getYRot(partialTicks);
        float z = getZRot(partialTicks);

        // 逆向旋转: Z -> Y -> X
        localPos = VecHelper.rotate(localPos, -z, Direction.Axis.Z);
        localPos = VecHelper.rotate(localPos, -y, Direction.Axis.Y);
        localPos = VecHelper.rotate(localPos, -x, Direction.Axis.X);

        return localPos;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return pDistance < (16*16)*(16*16);
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
            // 服务端根据 speed 自行推进角度
            serverXRot = (serverXRot + xSpeed) % 360f;
            serverYRot = (serverYRot + ySpeed) % 360f;
            serverZRot = (serverZRot + zSpeed) % 360f;

            syncRotationAngles();
        }
        else {
            prevXRot = xRot;
            prevYRot = yRot;
            prevZRot = zRot;

            xRot = (xRot + xSpeed) % 360f;
            yRot = (yRot + ySpeed) % 360f;
            zRot = (zRot + zSpeed) % 360f;
        }

//        setPos(contraption.anchor.getX(), contraption.anchor.getY(), contraption.anchor.getZ());
    }
    private void syncRotationAngles() {
        // 使用阈值减少不必要的同步
        float lastXRot = entityData.get(DATA_X_ROT);
        float lastYRot = entityData.get(DATA_Y_ROT);
        float lastZRot = entityData.get(DATA_Z_ROT);

        float threshold = 0.5f; // 0.5度阈值

        if (Math.abs(serverXRot - lastXRot) > threshold ||
                Math.abs(serverYRot - lastYRot) > threshold ||
                Math.abs(serverZRot - lastZRot) > threshold) {

            entityData.set(DATA_X_ROT, serverXRot);
            entityData.set(DATA_Y_ROT, serverYRot);
            entityData.set(DATA_Z_ROT, serverZRot);
        }
    }
    @Override
    protected void tickContraption() {
        tickActors();
    }


    @Override
    protected StructureTransform makeStructureTransform() {
        BlockPos offset = net.minecraft.core.BlockPos.containing(pivot);
        return new StructureTransform(offset, xRot, yRot, zRot);
    }

    @Override
    protected void onContraptionStalled() {
        super.onContraptionStalled();
    }

    @Override
    protected float getStalledAngle() {
        return xRot;
    }

    @Override
    protected void handleStallInformation(double x, double y, double z, float angle) {
        setPosRaw(x, y, z);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void applyLocalTransforms(PoseStack matrixStack, float partialTicks) {
        float ix = getXRot(partialTicks);
        float iy = getYRot(partialTicks);
        float iz = getZRot(partialTicks);

        // 构造四元数（保持和 tick 完全一致）
        Quaternionf q = new Quaternionf();
        q.rotateX((float) Math.toRadians(ix));
        q.rotateY((float) Math.toRadians(iy));
        q.rotateZ((float) Math.toRadians(iz));

        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.mulPose(q);
        matrixStack.translate(-0.5, -0.5, -0.5);

    }

    public float getXRot(float partialTicks) {
        if (level().isClientSide) return AngleHelper.angleLerp(partialTicks, prevXRot, xRot);
        else return xRot;
    }
    public float getYRot(float partialTicks) {
        if (level().isClientSide) return AngleHelper.angleLerp(partialTicks, prevYRot, yRot);
        else return yRot;
    }
    public float getZRot(float partialTicks) {
        if (level().isClientSide) return AngleHelper.angleLerp(partialTicks, prevZRot, zRot);
        else return zRot;
    }
}
