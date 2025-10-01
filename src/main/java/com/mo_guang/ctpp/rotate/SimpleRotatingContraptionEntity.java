package com.mo_guang.ctpp.rotate;

import com.mo_guang.ctpp.CTPPEntityTypes;
import com.mojang.blaze3d.vertex.PoseStack;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.*;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
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

public class SimpleRotatingContraptionEntity extends AbstractContraptionEntity{

    @Setter
    private float rotationSpeed = 0;
    protected BlockPos controllerPos;

    @Getter
    @Setter
    protected Direction.Axis rotationAxis = Direction.Axis.Y;
    protected float prevAngle;

    protected float angle;
    protected float angleDelta;

    public SimpleRotatingContraptionEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public static SimpleRotatingContraptionEntity create(Level world, Contraption contraption) {
        SimpleRotatingContraptionEntity entity =
                new SimpleRotatingContraptionEntity(CTPPEntityTypes.SIMPLE_CONTRAPTION.get(), world);
        entity.controllerPos = contraption.anchor;
        entity.setContraption(contraption);
        return entity;
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        if (!level().isClientSide())
            return;
        for (Entity entity : getPassengers())
            positionRider(entity);
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
        if (contraption instanceof BearingContraption)
            rotationAxis = ((BearingContraption) contraption).getFacing()
                    .getAxis();
    }

    @Override
    public ContraptionRotationState getRotationState() {
        ContraptionRotationState crs = new ContraptionRotationState();
        if (rotationAxis == Direction.Axis.X)
            crs.xRotation = angle;
        if (rotationAxis == Direction.Axis.Y)
            crs.yRotation = angle;
        if (rotationAxis == Direction.Axis.Z)
            crs.zRotation = angle;
        return crs;
    }

    @Override
    public Vec3 applyRotation(Vec3 localPos, float partialTicks) {
        localPos = VecHelper.rotate(localPos, getAngle(partialTicks), rotationAxis);
        return localPos;
    }

    @Override
    public Vec3 reverseRotation(Vec3 localPos, float partialTicks) {
        localPos = VecHelper.rotate(localPos, -getAngle(partialTicks), rotationAxis);
        return localPos;
    }

    public void setAngle(float angle) {
        this.angle = angle;

        if(tickCount%10 == 0 && !level().isClientSide)
            entityData.set(DATA_ANGLE, angle);

    }

    public float getAngle(float partialTicks) {
        return AngleHelper.angleLerp(partialTicks, prevAngle, angle);
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

        prevAngle = angle;
        if (level().isClientSide) {
            // 客户端只做插值，不更新逻辑角度
            return;
        }

        float newAngle = angle + rotationSpeed;
        newAngle %= 360;
        setAngle(newAngle);

        if (tickCount % 20 == 0) {
            System.out.println("[RotatingContraptionEntity.tick] Server tick pos=" + getX() + "," + getY() + "," + getZ()
                    + " angle=" + newAngle + " speed=" + rotationSpeed);
        }
    }

    protected void tickContraption() {
        if (level().isClientSide)
            setPos(getX(), getY(), getZ());

        //markDirty();

        angleDelta = angle - prevAngle;
        if(angleDelta > 0)

        prevAngle = angle;
        tickActors();

    }

    @Override
    protected boolean shouldActorTrigger(MovementContext context, StructureTemplate.StructureBlockInfo blockInfo, MovementBehaviour actor,
                                         Vec3 actorPosition, BlockPos gridPosition) {
        if (super.shouldActorTrigger(context, blockInfo, actor, actorPosition, gridPosition))
            return true;

        // Special activation timer for actors in the center of a bearing contraption
        if (!(contraption instanceof BearingContraption bc))
            return false;
        Direction facing = bc.getFacing();
        Vec3 activeAreaOffset = actor.getActiveAreaOffset(context);
        if (!activeAreaOffset.multiply(VecHelper.axisAlingedPlaneOf(Vec3.atLowerCornerOf(facing.getNormal())))
                .equals(Vec3.ZERO))
            return false;
        if (!VecHelper.onSameAxis(blockInfo.pos(), net.minecraft.core.BlockPos.ZERO, facing.getAxis()))
            return false;
        context.motion = Vec3.atLowerCornerOf(facing.getNormal())
                .scale(angleDelta / 360.0);
        context.relativeMotion = context.motion;
        int timer = context.data.getInt("StationaryTimer");
        if (timer > 0) {
            context.data.putInt("StationaryTimer", timer - 1);
            return false;
        }

        context.data.putInt("StationaryTimer", 20);
        return true;
    }

    protected IControlContraption getController() {
        if (controllerPos == null)
            return null;
        if (!level().isLoaded(controllerPos))
            return null;
        BlockEntity be = level().getBlockEntity(controllerPos);
        if (!(be instanceof IControlContraption))
            return null;
        return (IControlContraption) be;
    }

    @Override
    protected StructureTransform makeStructureTransform() {
        BlockPos offset = net.minecraft.core.BlockPos.containing(getAnchorVec());
        float xRot = rotationAxis == Direction.Axis.X ? angle : 0;
        float yRot = rotationAxis == Direction.Axis.Y ? angle : 0;
        float zRot = rotationAxis == Direction.Axis.Z ? angle : 0;
        return new StructureTransform(offset, xRot, yRot, zRot);
    }

    @Override
    protected void onContraptionStalled() {
        IControlContraption controller = getController();
        if (controller != null)
            controller.onStall();
        super.onContraptionStalled();
    }

    @Override
    protected float getStalledAngle() {
        return angle;
    }

    @Override
    protected void handleStallInformation(double x, double y, double z, float angle) {
        setPosRaw(x, y, z);
        this.angle = this.prevAngle = angle;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void applyLocalTransforms(PoseStack matrixStack, float partialTicks) {
        float angle = getAngle(partialTicks);
        Direction.Axis axis = getRotationAxis();

        if (axis != null) {
            TransformStack.of(matrixStack)
                    .nudge(getId())
                    .center()
                    .rotateDegrees(angle, axis)
                    .uncenter();
        }
    }


    private static final EntityDataAccessor<Float> DATA_ANGLE =
            SynchedEntityData.defineId(SimpleRotatingContraptionEntity.class, EntityDataSerializers.FLOAT);

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_ANGLE, 0f);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_ANGLE.equals(key)) {
            // 客户端接收到的更新
            this.prevAngle = this.angle;
            this.angle = this.entityData.get(DATA_ANGLE);
        }
    }
}
