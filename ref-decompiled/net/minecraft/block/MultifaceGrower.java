/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.MultifaceBlock
 *  net.minecraft.block.MultifaceGrower
 *  net.minecraft.block.MultifaceGrower$GrowChecker
 *  net.minecraft.block.MultifaceGrower$GrowPos
 *  net.minecraft.block.MultifaceGrower$GrowPosPredicate
 *  net.minecraft.block.MultifaceGrower$GrowType
 *  net.minecraft.block.MultifaceGrower$LichenGrowChecker
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.WorldAccess
 */
package net.minecraft.block;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.MultifaceBlock;
import net.minecraft.block.MultifaceGrower;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class MultifaceGrower {
    public static final GrowType[] GROW_TYPES = new GrowType[]{GrowType.SAME_POSITION, GrowType.SAME_PLANE, GrowType.WRAP_AROUND};
    private final GrowChecker growChecker;

    public MultifaceGrower(MultifaceBlock lichen) {
        this((GrowChecker)new LichenGrowChecker(lichen));
    }

    public MultifaceGrower(GrowChecker growChecker) {
        this.growChecker = growChecker;
    }

    public boolean canGrow(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return Direction.stream().anyMatch(direction2 -> this.getGrowPos(state, world, pos, direction, direction2, (arg_0, arg_1, arg_2) -> ((GrowChecker)this.growChecker).canGrow(arg_0, arg_1, arg_2)).isPresent());
    }

    public Optional<GrowPos> grow(BlockState state, WorldAccess world, BlockPos pos, Random random) {
        return Direction.shuffle((Random)random).stream().filter(direction -> this.growChecker.canGrow(state, direction)).map(direction -> this.grow(state, world, pos, direction, random, false)).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
    }

    public long grow(BlockState state, WorldAccess world, BlockPos pos, boolean markForPostProcessing) {
        return Direction.stream().filter(direction -> this.growChecker.canGrow(state, direction)).map(direction -> this.grow(state, world, pos, direction, markForPostProcessing)).reduce(0L, Long::sum);
    }

    public Optional<GrowPos> grow(BlockState state, WorldAccess world, BlockPos pos, Direction direction, Random random, boolean markForPostProcessing) {
        return Direction.shuffle((Random)random).stream().map(direction2 -> this.grow(state, world, pos, direction, direction2, markForPostProcessing)).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
    }

    private long grow(BlockState state, WorldAccess world, BlockPos pos, Direction direction, boolean markForPostProcessing) {
        return Direction.stream().map(direction2 -> this.grow(state, world, pos, direction, direction2, markForPostProcessing)).filter(Optional::isPresent).count();
    }

    @VisibleForTesting
    public Optional<GrowPos> grow(BlockState state, WorldAccess world, BlockPos pos, Direction oldDirection, Direction newDirection, boolean markForPostProcessing) {
        return this.getGrowPos(state, (BlockView)world, pos, oldDirection, newDirection, (arg_0, arg_1, arg_2) -> ((GrowChecker)this.growChecker).canGrow(arg_0, arg_1, arg_2)).flatMap(growPos -> this.place(world, growPos, markForPostProcessing));
    }

    public Optional<GrowPos> getGrowPos(BlockState state, BlockView world, BlockPos pos, Direction oldDirection, Direction newDirection, GrowPosPredicate predicate) {
        if (newDirection.getAxis() == oldDirection.getAxis()) {
            return Optional.empty();
        }
        if (!(this.growChecker.canGrow(state) || this.growChecker.hasDirection(state, oldDirection) && !this.growChecker.hasDirection(state, newDirection))) {
            return Optional.empty();
        }
        for (GrowType growType : this.growChecker.getGrowTypes()) {
            GrowPos growPos = growType.getGrowPos(pos, newDirection, oldDirection);
            if (!predicate.test(world, pos, growPos)) continue;
            return Optional.of(growPos);
        }
        return Optional.empty();
    }

    public Optional<GrowPos> place(WorldAccess world, GrowPos pos, boolean markForPostProcessing) {
        BlockState blockState = world.getBlockState(pos.pos());
        if (this.growChecker.place(world, pos, blockState, markForPostProcessing)) {
            return Optional.of(pos);
        }
        return Optional.empty();
    }
}

