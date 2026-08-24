package com.mo_guang.ctpp.dynamicPart.rotation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import com.mo_guang.ctpp.CTPPEntityTypes;
import com.simibubi.create.content.contraptions.Contraption;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import tech.vixhentx.mcmod.ctnhlib.utils.ExtendNbtUtils;

import static com.mo_guang.ctpp.util.MathUtil.slerp;

public class RubiksCubeContraptionEntity extends SimpleRotatingContraptionEntity {

    public static final float ROTATE_SPEED = 4.5f;
    private static final float QUARTER_TURN_DEGREES = 90.0f;
    private static final float MOVE_EPSILON_DEGREES = 1.0E-4f;
    public Direction frontFacing;
    public BlockPos startPos;

    public RubiksCubeContraptionEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.layer = RotationLayer.FRONT_LAYER;
        this.clockwise = true;
        this.shouldStop = true;
    }

    public boolean shouldStop;
    public RotationLayer layer;
    public boolean clockwise;
    private Quaternionf moveStartRotation = new Quaternionf();
    private Quaternionf moveTargetRotation = new Quaternionf();
    private Vec3 moveAxis = Vec3.ZERO;
    private float movedDegrees;
    private boolean moving;

    private static final int[][] AXIS_PERMUTATIONS = {
            { 0, 1, 2 }, { 0, 2, 1 }, { 1, 0, 2 },
            { 1, 2, 0 }, { 2, 0, 1 }, { 2, 1, 0 }
    };

    public static RubiksCubeContraptionEntity create(Level world, Contraption contraption, Vec3 pivot,
                                                     Direction frontFacing, BlockPos pos,
                                                     IContraptionMultiblock controller) {
        RubiksCubeContraptionEntity entity = new RubiksCubeContraptionEntity(
                CTPPEntityTypes.RUBIKS_CUBE_CONTRAPTION.get(), world);
        entity.controllerPos = controller.getBlockPosition();
        entity.setRunning(true);
        entity.frontFacing = frontFacing;
        entity.startPos = pos;
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

        public boolean contains(Vec3 localPosition, Direction frontFacing, Quaternionf rotation) {
            Direction direction = getDirection(frontFacing);
            Vector3f rotated = new Quaternionf(rotation).transform(new Vector3f(
                    (float) localPosition.x,
                    (float) localPosition.y,
                    (float) localPosition.z));
            float coordinate = switch (direction.getAxis()) {
                case X -> rotated.x();
                case Y -> rotated.y();
                case Z -> rotated.z();
            };
            return coordinate * direction.getAxisDirection().getStep() > 0.5f;
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
    }

    @Override
    protected boolean shouldAdvanceWithAngularVelocity() {
        return false;
    }

    @Override
    protected boolean shouldCorrectClientRotation() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || !moving) {
            return;
        }

        movedDegrees = Math.min(QUARTER_TURN_DEGREES, movedDegrees + ROTATE_SPEED);
        float progress = movedDegrees / QUARTER_TURN_DEGREES;
        serverRotation = slerp(moveStartRotation, moveTargetRotation, progress).normalize();
        syncRotationQuaternion(false);

        if (movedDegrees >= QUARTER_TURN_DEGREES - MOVE_EPSILON_DEGREES) {
            finishMove();
        }
    }

    private void beginMove() {
        if (moving || startPos == null || frontFacing == null || layer == null) {
            return;
        }

        // Every move starts from a legal cube orientation. This also repairs
        // entities created by older versions that accumulated quaternion error.
        moveStartRotation = snapToCubeRotation(serverRotation);
        serverRotation = new Quaternionf(moveStartRotation);

        Vec3 localPosition = startPos.getCenter().subtract(getPivot());
        if (!layer.contains(localPosition, frontFacing, moveStartRotation)) {
            shouldStop = true;
            syncRotationQuaternion(true);
            return;
        }

        moveAxis = layer.getRotationVector(frontFacing).scale(clockwise ? 1.0 : -1.0);
        Quaternionf quarterTurn = new Quaternionf().fromAxisAngleRad(
                (float) moveAxis.x,
                (float) moveAxis.y,
                (float) moveAxis.z,
                (float) Math.toRadians(QUARTER_TURN_DEGREES));
        moveTargetRotation = snapToCubeRotation(quarterTurn.mul(moveStartRotation, new Quaternionf()));
        movedDegrees = 0.0f;
        moving = true;
        setRotationSpeed(moveAxis, ROTATE_SPEED);
        syncRotationQuaternion(true);
    }

    private void finishMove() {
        serverRotation = new Quaternionf(moveTargetRotation).normalize();
        movedDegrees = QUARTER_TURN_DEGREES;
        moving = false;
        shouldStop = true;
        setRotationSpeed(Vec3.ZERO, 0);
        syncRotationQuaternion(true);
    }

    public void performStandardMove(String moveNotation) {
        String notation = moveNotation.trim().toUpperCase(java.util.Locale.ROOT);
        if ("STOP".equals(notation)) {
            if (moving) {
                finishMove();
            } else {
                shouldStop = true;
                setRotationSpeed(Vec3.ZERO, 0);
            }
            return;
        }
        if (moving) {
            return;
        }
        switch (notation) {
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
            default:
                return;
        }
        beginMove();
    }

    public boolean isMoving() {
        return moving;
    }

    private static Quaternionf snapToCubeRotation(Quaternionf rotation) {
        if (!isUsableRotation(rotation)) {
            return new Quaternionf();
        }
        Quaternionf normalized = new Quaternionf(rotation).normalize();
        Vector3f[] sourceAxes = {
                normalized.transform(new Vector3f(1, 0, 0)),
                normalized.transform(new Vector3f(0, 1, 0)),
                normalized.transform(new Vector3f(0, 0, 1))
        };
        float bestScore = -Float.MAX_VALUE;
        Matrix3f bestMatrix = null;

        for (int[] permutation : AXIS_PERMUTATIONS) {
            int parity = permutationParity(permutation);
            for (int signX : new int[] { -1, 1 }) {
                for (int signY : new int[] { -1, 1 }) {
                    int signZ = parity * signX * signY;
                    Vector3f[] columns = {
                            axisVector(permutation[0], signX),
                            axisVector(permutation[1], signY),
                            axisVector(permutation[2], signZ)
                    };
                    float score = sourceAxes[0].dot(columns[0]) + sourceAxes[1].dot(columns[1]) +
                            sourceAxes[2].dot(columns[2]);
                    if (score > bestScore) {
                        bestScore = score;
                        bestMatrix = new Matrix3f().identity()
                                .setColumn(0, columns[0])
                                .setColumn(1, columns[1])
                                .setColumn(2, columns[2]);
                    }
                }
            }
        }
        return new Quaternionf().setFromNormalized(bestMatrix).normalize();
    }

    private static int permutationParity(int[] permutation) {
        int inversions = 0;
        for (int i = 0; i < permutation.length; i++) {
            for (int j = i + 1; j < permutation.length; j++) {
                if (permutation[i] > permutation[j]) inversions++;
            }
        }
        return (inversions & 1) == 0 ? 1 : -1;
    }

    private static Vector3f axisVector(int axis, int sign) {
        return switch (axis) {
            case 0 -> new Vector3f(sign, 0, 0);
            case 1 -> new Vector3f(0, sign, 0);
            default -> new Vector3f(0, 0, sign);
        };
    }

    @Override
    protected void writeAdditional(CompoundTag nbt, boolean spawnPacket) {
        super.writeAdditional(nbt, spawnPacket); // 先调用父类方法保存父类字段

        // 保存子类自定义字段
        nbt.putString("FrontFacing", frontFacing.getName()); // 保存朝向
        nbt.put("StartPos", NbtUtils.writeBlockPos(startPos)); // 保存起始位置
        nbt.putBoolean("ShouldStop", shouldStop); // 保存停止标记
        nbt.putString("RotationLayer", layer.name()); // 保存旋转层
        nbt.putBoolean("Clockwise", clockwise); // 保存旋转方向

        CompoundTag moveTag = new CompoundTag();
        moveTag.putBoolean("Moving", moving);
        moveTag.putFloat("MovedDegrees", movedDegrees);
        moveTag.put("StartRotation", writeQuaternion(moveStartRotation));
        moveTag.put("TargetRotation", writeQuaternion(moveTargetRotation));
        moveTag.put("Axis", ExtendNbtUtils.writeVec3(moveAxis));
        nbt.put("RubiksMoveData", moveTag);
    }

    // ========== 核心修复：重写 NBT 读取 ==========
    @Override
    protected void readAdditional(CompoundTag nbt, boolean spawnData) {
        super.readAdditional(nbt, spawnData); // 先调用父类方法读取父类字段

        // 读取子类自定义字段（添加兜底，避免加载失败）
        this.frontFacing = Direction.byName(nbt.getString("FrontFacing"));
        if (this.frontFacing == null) this.frontFacing = Direction.NORTH;

        this.startPos = NbtUtils.readBlockPos(nbt.getCompound("StartPos"));
        this.shouldStop = nbt.getBoolean("ShouldStop");

        // 读取旋转层（兜底）
        try {
            this.layer = RotationLayer.valueOf(nbt.getString("RotationLayer"));
        } catch (IllegalArgumentException e) {
            this.layer = RotationLayer.FRONT_LAYER;
        }

        this.clockwise = nbt.getBoolean("Clockwise");
        this.serverRotation = snapToCubeRotation(this.serverRotation);
        this.moving = false;
        this.movedDegrees = 0.0f;
        this.moveStartRotation = new Quaternionf(this.serverRotation);
        this.moveTargetRotation = new Quaternionf(this.serverRotation);
        this.moveAxis = Vec3.ZERO;

        if (nbt.contains("RubiksMoveData", CompoundTag.TAG_COMPOUND)) {
            CompoundTag moveTag = nbt.getCompound("RubiksMoveData");
            Quaternionf savedStart = readQuaternion(moveTag, "StartRotation", this.serverRotation);
            Quaternionf savedTarget = readQuaternion(moveTag, "TargetRotation", this.serverRotation);
            Vec3 savedAxis = ExtendNbtUtils.readVec3(moveTag.getCompound("Axis"));
            float savedProgress = moveTag.getFloat("MovedDegrees");

            if (moveTag.getBoolean("Moving") && isUsableAxis(savedAxis) && Float.isFinite(savedProgress)) {
                this.moveStartRotation = snapToCubeRotation(savedStart);
                this.moveTargetRotation = snapToCubeRotation(savedTarget);
                this.moveAxis = savedAxis.normalize();
                this.movedDegrees = Math.max(0.0f,
                        Math.min(QUARTER_TURN_DEGREES, savedProgress));
                this.serverRotation = slerp(this.moveStartRotation, this.moveTargetRotation,
                        this.movedDegrees / QUARTER_TURN_DEGREES).normalize();
                this.moving = true;
                this.shouldStop = false;
            }
        }

        if (!level().isClientSide) {
            entityData.set(DATA_Q_W, serverRotation.w());
            entityData.set(DATA_Q_X, serverRotation.x());
            entityData.set(DATA_Q_Y, serverRotation.y());
            entityData.set(DATA_Q_Z, serverRotation.z());
            if (moving) {
                setRotationSpeed(moveAxis, ROTATE_SPEED);
            } else {
                setRotationSpeed(Vec3.ZERO, 0);
            }
        } else {
            this.clientRotation = new Quaternionf(this.serverRotation);
            this.prevClientRotation = new Quaternionf(this.serverRotation);
            setRotationSpeed(moving ? moveAxis : Vec3.ZERO, moving ? ROTATE_SPEED : 0);
        }
    }

    private static CompoundTag writeQuaternion(Quaternionf rotation) {
        return ExtendNbtUtils.writeQuaternionf(new Quaternionf(rotation).normalize());
    }

    private static Quaternionf readQuaternion(CompoundTag tag, String key, Quaternionf fallback) {
        if (!tag.contains(key, CompoundTag.TAG_COMPOUND)) {
            return new Quaternionf(fallback);
        }
        Quaternionf result = ExtendNbtUtils.readQuaternionf(tag.getCompound(key));
        if (!isUsableRotation(result)) {
            return new Quaternionf(fallback);
        }
        return result.normalize();
    }

    private static boolean isUsableRotation(Quaternionf rotation) {
        return Float.isFinite(rotation.x()) && Float.isFinite(rotation.y()) && Float.isFinite(rotation.z()) &&
                Float.isFinite(rotation.w()) && rotation.lengthSquared() > 1.0E-8f;
    }

    private static boolean isUsableAxis(Vec3 axis) {
        return axis.lengthSqr() > 1.0E-8 && Double.isFinite(axis.x) && Double.isFinite(axis.y) &&
                Double.isFinite(axis.z);
    }
}
