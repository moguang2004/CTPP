package com.mo_guang.ctpp.dynamicPart.rotation;

import com.mo_guang.ctpp.CTPPEntityTypes;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RubiksCubeContraptionEntity extends SimpleRotatingContraptionEntity{
    public static float ROTATE_SPEED = 4.5f; // 90 degrees per 10 ticks
    public Direction frontFacing;
    public BlockPos startPos;

    public RubiksCubeContraptionEntity(EntityType<?> type, Level world, Direction frontFacing, BlockPos pos) {
        super(type, world);
        this.layer = RotationLayer.FRONT_LAYER;
        this.clockwise = true;
        this.shouldStop = true;
        this.frontFacing = frontFacing;
        this.startPos = pos;
    }
    public boolean shouldStop;
    public RotationLayer layer;
    public boolean clockwise;
    public static RubiksCubeContraptionEntity create(Level world, Contraption contraption, Vec3 pivot, Direction frontFacing, BlockPos pos) {
        RubiksCubeContraptionEntity entity =
                new RubiksCubeContraptionEntity(CTPPEntityTypes.SIMPLE_CONTRAPTION.get(), world, frontFacing, pos);
        entity.setContraption(contraption);
        entity.setPivot(pivot);
        return entity;
    }

    // 旋转层定义
    public enum RotationLayer {
        // 相对于整体朝向的层
        FRONT_LAYER,     // 前面层
        BACK_LAYER,      // 后面层
        LEFT_LAYER,      // 左面层
        RIGHT_LAYER,     // 右面层
        TOP_LAYER,       // 顶面层
        BOTTOM_LAYER;    // 底面层

        /**
         * 根据整体朝向和层类型获取实际的方向
         */
        public Direction getDirection(Direction frontFacing) {
            return switch (this) {
                case FRONT_LAYER -> frontFacing;
                case BACK_LAYER -> frontFacing.getOpposite();
                case LEFT_LAYER -> getLeftDirection(frontFacing);
                case RIGHT_LAYER -> getRightDirection(frontFacing);
                case TOP_LAYER -> Direction.UP;
                case BOTTOM_LAYER -> Direction.DOWN;
                default -> frontFacing;
            };
        }

        /**
         * 根据整体朝向获取旋转轴
         */
        public Direction.Axis getRotationAxis(Direction frontFacing) {
            Direction layerDirection = getDirection(frontFacing);
            return layerDirection.getAxis();
        }

        /**
         * 根据整体朝向获取旋转轴向量
         */
        public Vec3 getRotationVector(Direction frontFacing) {
            Direction layerDirection = getDirection(frontFacing);
            return new Vec3(layerDirection.getStepX(), layerDirection.getStepY(), layerDirection.getStepZ());
        }

        /**
         * 获取左方向（基于整体朝向）
         */
        private static Direction getLeftDirection(Direction frontFacing) {
            // 根据frontFacing计算左方向
            return switch (frontFacing) {
                case NORTH -> Direction.WEST;
                case SOUTH -> Direction.EAST;
                case WEST -> Direction.SOUTH;
                case EAST -> Direction.NORTH;
                default -> Direction.WEST;
            };
        }

        /**
         * 获取右方向（基于整体朝向）
         */
        private static Direction getRightDirection(Direction frontFacing) {
            return getLeftDirection(frontFacing).getOpposite();
        }

        /**
         * 判断一个角块位置是否属于这个旋转层
         */
        public boolean isInLayer(Vec3 startPos, Direction frontFacing, Quaternionf rotating) {
            Direction layerDirection = this.getDirection(frontFacing);
            Vector3f rotated = new Vector3f((float) startPos.x, (float) startPos.y, (float) startPos.z);
            Vector3f localPos = rotated.rotate(rotating);

            // 根据层的方向判断位置是否在指定层
            return switch (layerDirection) {
                case EAST -> Math.signum(localPos.x) == 1;
                case WEST -> Math.signum(localPos.x) == -1;
                case UP -> Math.signum(localPos.y) == 1;
                case DOWN -> Math.signum(localPos.y) == -1;
                case SOUTH -> Math.signum(localPos.z) == 1;
                case NORTH -> Math.signum(localPos.z) == -1;
            };
        }
    }




    public void notifyChange() {
        Quaternionf q = serverRotation;
        if (this.layer.isInLayer(this.startPos.getCenter().subtract(getPivot()), frontFacing, q) && !this.shouldStop) {
            float speed = clockwise ? ROTATE_SPEED : - ROTATE_SPEED;
            Vec3 worldAxisVector = layer.getRotationVector(frontFacing);

            this.setRotationSpeed(
                    ((float)worldAxisVector.x * speed),
                    ((float)worldAxisVector.y * speed),
                    ((float)worldAxisVector.z * speed)
            );
        }
        else {
            setRotationSpeed(0, 0, 0);
        }
    }


    public void performStandardMove(String moveNotation) {
        switch (moveNotation.toUpperCase()) {
            case "U": // 顶层顺时针
                this.layer = RotationLayer.TOP_LAYER;
                this.clockwise = true;
                this.shouldStop = false;
                break;
            case "U'": // 顶层逆时针
                this.layer = RotationLayer.TOP_LAYER;
                this.clockwise = false;
                this.shouldStop = false;
                break;
            case "D": // 底层顺时针
                this.layer = RotationLayer.BOTTOM_LAYER;
                this.clockwise = true;
                this.shouldStop = false;
                break;
            case "D'": // 底层逆时针
                this.layer = RotationLayer.BOTTOM_LAYER;
                this.clockwise = false;
                this.shouldStop = false;
                break;
            case "L": // 左层顺时针
                this.layer = RotationLayer.LEFT_LAYER;
                this.clockwise = true;
                this.shouldStop = false;
                break;
            case "L'": // 左层逆时针
                this.layer = RotationLayer.LEFT_LAYER;
                this.clockwise = false;
                this.shouldStop = false;
                break;
            case "R": // 右层顺时针
                this.layer = RotationLayer.RIGHT_LAYER;
                this.clockwise = true;
                this.shouldStop = false;
                break;
            case "R'": // 右层逆时针
                this.layer = RotationLayer.RIGHT_LAYER;
                this.clockwise = false;
                this.shouldStop = false;
                break;
            case "F": // 前层顺时针
                this.layer = RotationLayer.BACK_LAYER;
                this.clockwise = true;
                this.shouldStop = false;
                break;
            case "F'": // 前层逆时针
                this.layer = RotationLayer.BACK_LAYER;
                this.clockwise = false;
                this.shouldStop = false;
                break;
            case "B": // 后层顺时针
                this.layer = RotationLayer.FRONT_LAYER;
                this.clockwise = true;
                this.shouldStop = false;
                break;
            case "B'": // 后层逆时针
                this.layer = RotationLayer.FRONT_LAYER;
                this.clockwise = false;
                this.shouldStop = false;
                break;
            case "STOP":
                this.shouldStop = true;
        }
        notifyChange();
    }
}
