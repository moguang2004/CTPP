package com.mo_guang.ctpp.rotate;

import com.mo_guang.ctpp.CTPPEntityTypes;
import com.mojang.blaze3d.vertex.PoseStack;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.*;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.collision.Matrix3d;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import lombok.Getter;
import lombok.Setter;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.Field;

public class SimpleRotatingContraptionEntity extends AbstractContraptionEntity{

    /** 服务端 authoritative 角度 **/
    private float prevXRot, prevYRot, prevZRot;
    private float xRot=45f, yRot=0f, zRot;

    /** 旋转速度（deg/tick） **/
    private static final EntityDataAccessor<Float> DATA_X_SPEED =
            SynchedEntityData.defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_Y_SPEED =
            SynchedEntityData.defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_Z_SPEED =
            SynchedEntityData.defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Vector3f> DATA_PIVOT =
            SynchedEntityData.defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.VECTOR3);



    /** 本地速度缓存 **/
    private float xSpeed, ySpeed, zSpeed;

    /** 旋转基点 **/
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

        // 直接构造一个 Matrix3d，使其与渲染/位置计算使用的旋转顺序和角度一致
        // 这里我们用 X -> Y -> Z 顺序（与你在渲染中使用的 rotateXYZ 保持一致）
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
    public void teleportTo(double p_70634_1_, double p_70634_3_, double p_70634_5_) {
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void lerpTo(double x, double y, double z, float yw, float pt, int inc, boolean t) {
    }

    @Override
    public void tick() {
        super.tick();

        prevXRot = xRot;
        prevYRot = yRot;
        prevZRot = zRot;

        // 服务端 / 客户端都根据 speed 自行推进角度
//        xRot = 45f;
//        yRot = 0f;
//        zRot = 0f;
        xRot = (xRot + xSpeed) % 360f;
        yRot = (yRot + ySpeed) % 360f;
        zRot = (zRot + 5f) % 360f;

        Vec3 offset = contraption.anchor.getCenter().subtract(pivot);

        Quaternionf q = new Quaternionf()
                .rotateXYZ((float) Math.toRadians(xRot),
                        (float) Math.toRadians(yRot),
                        (float) Math.toRadians(zRot));
        Vector3f rotated = new Vector3f((float) offset.x, (float) offset.y, (float) offset.z);

       rotated.rotate(q);

        Vec3 worldPos = pivot.add(rotated.x, rotated.y, rotated.z);

        setPos(worldPos.x-0.5, worldPos.y-0.5, worldPos.z-0.5);

        //setPos(contraption.anchor.getX(), contraption.anchor.getY(), contraption.anchor.getZ());


        if (tickCount % 20 == 0) {
            if(!level().isClientSide){
                System.out.printf("[RotatingContraptionEntity.tick] Server tick (%.1f, %.1f, %.1f) speed=(%.2f, %.2f, %.2f)%n",
                        xRot, yRot, zRot, xSpeed, ySpeed, zSpeed);
            }
            else {
                System.out.printf("[RotatingContraptionEntity.tick] Client tick (%.1f, %.1f, %.1f) speed=(%.2f, %.2f, %.2f)%n",
                        xRot, yRot, zRot, xSpeed, ySpeed, zSpeed);
            }
        }
    }

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
        Quaternionf q = new Quaternionf()
                .rotateXYZ((float) Math.toRadians(ix),
                        (float) Math.toRadians(iy),
                        (float) Math.toRadians(iz));

//        var dx = pivot.x - getX();
//        var dy = pivot.y - getY();
//        var dz = pivot.z - getZ();
        //TransformStack.of(matrixStack).center()
        matrixStack.translate(0.5f, 0.5f, 0.5f);
        matrixStack.mulPose(q);
        matrixStack.translate(-0.5f, -0.5f, -0.5f);

    }

    public float getXRot(float partialTicks) {
        return AngleHelper.angleLerp(partialTicks, prevXRot, xRot);
    }
    public float getYRot(float partialTicks) {
        return AngleHelper.angleLerp(partialTicks, prevYRot, yRot);
    }
    public float getZRot(float partialTicks) {
        return AngleHelper.angleLerp(partialTicks, prevZRot, zRot);
    }

}
