package com.mo_guang.ctpp.api.pattern;

import com.gregtechceu.gtceu.api.block.ActiveBlock;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockState;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.error.PatternError;
import com.gregtechceu.gtceu.api.pattern.error.PatternStringError;
import com.gregtechceu.gtceu.api.pattern.error.SinglePredicateError;
import com.gregtechceu.gtceu.api.pattern.predicates.SimplePredicate;
import com.gregtechceu.gtceu.api.pattern.util.PatternMatchContext;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class StaticBlockPattern extends BlockPattern {
    protected final TraceabilityPredicate[][][] staticBlockMatches;
    protected final TraceabilityPredicate[][][] dynamicBlockMatches;
    public StaticBlockPattern(TraceabilityPredicate[][][] predicatesIn, RelativeDirection[] structureDir, int[][] aisleRepetitions, int[] centerOffset,
                              TraceabilityPredicate[][][] staticPredicates, TraceabilityPredicate[][][] dynamicPredicates) {
        super(predicatesIn, structureDir, aisleRepetitions, centerOffset);
        this.staticBlockMatches = staticPredicates;
        this.dynamicBlockMatches = dynamicPredicates;
    }

    @Override
    public boolean checkPatternAt(MultiblockState worldState, boolean savePredicate) {
        IMultiController controller = worldState.getController();
        if (controller == null) {
            worldState.setError(new PatternStringError("no controller found"));
            return false;
        }
        BlockPos centerPos = controller.self().getPos();
        Direction frontFacing = controller.self().getFrontFacing();
        Direction[] facings = controller.hasFrontFacing() ? new Direction[] { frontFacing } :
                new Direction[] { Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST };
        Direction upwardsFacing = controller.self().getUpwardsFacing();
        boolean allowsFlip = controller.self().allowFlip();
        for (Direction direction : facings) {
            boolean result = checkPatternAt(worldState, centerPos, direction, upwardsFacing, false, savePredicate);
            if (result) {
                return true;
            } else if (allowsFlip) {
                return checkPatternAt(worldState, centerPos, direction, upwardsFacing, true, savePredicate);
            }
        }
        return false;
    }
    public List<BlockPos> getDynamicPart(MultiblockState worldState) {
        IMultiController controller = worldState.getController();
        BlockPos centerPos = controller.self().getPos();
        Direction frontFacing = controller.self().getFrontFacing();
        Direction upwardsFacing = controller.self().getUpwardsFacing();
        List<BlockPos> positions = new ArrayList<>();
        for (int c = 0; c < this.fingerLength; c++) {
            for (int b = 0; b < this.thumbLength; b++) {
                for (int a = 0; a < this.palmLength; a++) {
                    TraceabilityPredicate predicate = this.dynamicBlockMatches[c][b][a];
                    if (predicate.equals(Predicates.air())) continue;
                    int relativeX = a - centerOffset[0];
                    int relativeY = b - centerOffset[1];
                    int relativeZ = c - centerOffset[2];
                    var position = setActualRelativeOffset(relativeX, relativeY, relativeZ, frontFacing, upwardsFacing, false)
                            .offset(centerPos.getX(), centerPos.getY(), centerPos.getZ());
                    positions.add(position);
                }
            }
        }
        return positions;
    }
    @Override
    public boolean checkPatternAt(MultiblockState worldState, BlockPos centerPos, Direction frontFacing,
                                  Direction upwardsFacing, boolean isFlipped, boolean savePredicate) {
        boolean findFirstAisle = false;
        int minZ = -centerOffset[4];
        worldState.clean();
        PatternMatchContext matchContext = worldState.getMatchContext();
        Object2IntMap<SimplePredicate> globalCount = worldState.getGlobalCount();
        Object2IntMap<SimplePredicate> layerCount = worldState.getLayerCount();
        // Checking aisles
        for (int c = 0, z = minZ++, r; c < this.fingerLength; c++) {
            // Checking repeatable slices
            int validRepetitions = 0;
            loop:
            for (r = 0; (findFirstAisle ? r < aisleRepetitions[c][1] : z <= -centerOffset[3]); r++) {
                // Checking single slice
                layerCount.clear();

                for (int b = 0, y = -centerOffset[1]; b < this.thumbLength; b++, y++) {
                    for (int a = 0, x = -centerOffset[0]; a < this.palmLength; a++, x++) {
                        worldState.setError(null);
                        TraceabilityPredicate predicate = this.staticBlockMatches[c][b][a];
                        BlockPos pos = setActualRelativeOffset(x, y, z, frontFacing, upwardsFacing, isFlipped)
                                .offset(centerPos.getX(), centerPos.getY(), centerPos.getZ());
                        if (!worldState.update(pos, predicate)) {
                            return false;
                        }
                        if (predicate.addCache()) {
                            worldState.addPosCache(pos);
                            if (savePredicate) {
                                matchContext.getOrCreate("predicates", HashMap::new).put(pos, predicate);
                            }
                        }
                        boolean canPartShared = true;
                        if (worldState.getTileEntity() instanceof IMachineBlockEntity machineBlockEntity &&
                                machineBlockEntity.getMetaMachine() instanceof IMultiPart part) { // add detected parts
                            if (!predicate.isAny()) {
                                if (part.isFormed() && !part.canShared() &&
                                        !part.hasController(worldState.controllerPos)) { // check part can be shared
                                    canPartShared = false;
                                    worldState.setError(new PatternStringError("multiblocked.pattern.error.share"));
                                } else {
                                    matchContext.getOrCreate("parts", HashSet::new).add(part);
                                }
                            }
                        }
                        if (worldState.getBlockState().getBlock() instanceof ActiveBlock) {
                            matchContext.getOrCreate("vaBlocks", LongOpenHashSet::new)
                                    .add(worldState.getPos().asLong());
                        }
                        if (!predicate.test(worldState) || !canPartShared) { // matching failed
                            if (findFirstAisle) {
                                if (r < aisleRepetitions[c][0]) {// retreat to see if the first aisle can start later
                                    r = c = 0;
                                    z = minZ++;
                                    matchContext.reset();
                                    findFirstAisle = false;
                                }
                            } else {
                                z++;// continue searching for the first aisle
                            }
                            continue loop;
                        }
                        matchContext.getOrCreate("ioMap", Long2ObjectOpenHashMap::new).put(worldState.getPos().asLong(),
                                worldState.io);
                    }
                }
                findFirstAisle = true;
                z++;

                // Check layer-local matcher predicate
                for (var entry : layerCount.object2IntEntrySet()) {
                    if (entry.getIntValue() < entry.getKey().minLayerCount) {
                        worldState.setError(new SinglePredicateError(entry.getKey(), 3));
                        return false;
                    }
                }
                validRepetitions++;
            }
            // Repetitions out of range
            if (r < aisleRepetitions[c][0] || worldState.hasError() || !findFirstAisle) {
                if (!worldState.hasError()) {
                    worldState.setError(new PatternError());
                }
                return false;
            }

            // finished checking the aisle, so store the repetitions
            formedRepetitionCount[c] = validRepetitions;
        }

        // Check count matches amount
        for (var entry : globalCount.object2IntEntrySet()) {
            if (entry.getIntValue() < entry.getKey().minCount) {
                worldState.setError(new SinglePredicateError(entry.getKey(), 1));
                return false;
            }
        }

        worldState.setError(null);
        worldState.setNeededFlip(isFlipped);
        return true;
    }
    private BlockPos setActualRelativeOffset(int x, int y, int z, Direction facing, Direction upwardsFacing,
                                             boolean isFlipped) {
        int[] c0 = new int[] { x, y, z }, c1 = new int[3];
        if (facing == Direction.UP || facing == Direction.DOWN) {
            Direction of = facing == Direction.DOWN ? upwardsFacing : upwardsFacing.getOpposite();
            for (int i = 0; i < 3; i++) {
                switch (structureDir[i].getActualDirection(of)) {
                    case UP -> c1[1] = c0[i];
                    case DOWN -> c1[1] = -c0[i];
                    case WEST -> c1[0] = -c0[i];
                    case EAST -> c1[0] = c0[i];
                    case NORTH -> c1[2] = -c0[i];
                    case SOUTH -> c1[2] = c0[i];
                }
            }
            int xOffset = upwardsFacing.getStepX();
            int zOffset = upwardsFacing.getStepZ();
            int tmp;
            if (xOffset == 0) {
                tmp = c1[2];
                c1[2] = zOffset > 0 ? c1[1] : -c1[1];
                c1[1] = zOffset > 0 ? -tmp : tmp;
            } else {
                tmp = c1[0];
                c1[0] = xOffset > 0 ? c1[1] : -c1[1];
                c1[1] = xOffset > 0 ? -tmp : tmp;
            }
            if (isFlipped) {
                if (upwardsFacing == Direction.NORTH || upwardsFacing == Direction.SOUTH) {
                    c1[0] = -c1[0]; // flip X-axis
                } else {
                    c1[2] = -c1[2]; // flip Z-axis
                }
            }
        } else {
            for (int i = 0; i < 3; i++) {
                switch (structureDir[i].getActualDirection(facing)) {
                    case UP -> c1[1] = c0[i];
                    case DOWN -> c1[1] = -c0[i];
                    case WEST -> c1[0] = -c0[i];
                    case EAST -> c1[0] = c0[i];
                    case NORTH -> c1[2] = -c0[i];
                    case SOUTH -> c1[2] = c0[i];
                }
            }
            if (upwardsFacing == Direction.WEST || upwardsFacing == Direction.EAST) {
                int xOffset = upwardsFacing == Direction.EAST ? facing.getClockWise().getStepX() :
                        facing.getClockWise().getOpposite().getStepX();
                int zOffset = upwardsFacing == Direction.EAST ? facing.getClockWise().getStepZ() :
                        facing.getClockWise().getOpposite().getStepZ();
                int tmp;
                if (xOffset == 0) {
                    tmp = c1[2];
                    c1[2] = zOffset > 0 ? -c1[1] : c1[1];
                    c1[1] = zOffset > 0 ? tmp : -tmp;
                } else {
                    tmp = c1[0];
                    c1[0] = xOffset > 0 ? -c1[1] : c1[1];
                    c1[1] = xOffset > 0 ? tmp : -tmp;
                }
            } else if (upwardsFacing == Direction.SOUTH) {
                c1[1] = -c1[1];
                if (facing.getStepX() == 0) {
                    c1[0] = -c1[0];
                } else {
                    c1[2] = -c1[2];
                }
            }
            if (isFlipped) {
                if (upwardsFacing == Direction.NORTH || upwardsFacing == Direction.SOUTH) {
                    if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                        c1[0] = -c1[0]; // flip X-axis
                    } else {
                        c1[2] = -c1[2]; // flip Z-axis
                    }
                } else {
                    c1[1] = -c1[1]; // flip Y-axis
                }
            }
        }
        return new BlockPos(c1[0], c1[1], c1[2]);
    }
}
