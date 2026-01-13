/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Degradable
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.math.random.Random
 */
package net.minecraft.block;

import java.util.Iterator;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public interface Degradable<T extends Enum<T>> {
    public static final int DEGRADING_RANGE = 4;

    public Optional<BlockState> getDegradationResult(BlockState var1);

    public float getDegradationChanceMultiplier();

    default public void tickDegradation(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        float f = 0.05688889f;
        if (random.nextFloat() < 0.05688889f) {
            this.tryDegrade(state, world, pos, random).ifPresent(degraded -> world.setBlockState(pos, degraded));
        }
    }

    public T getDegradationLevel();

    default public Optional<BlockState> tryDegrade(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockPos blockPos;
        int l;
        int i = this.getDegradationLevel().ordinal();
        int j = 0;
        int k = 0;
        Iterator iterator = BlockPos.iterateOutwards((BlockPos)pos, (int)4, (int)4, (int)4).iterator();
        while (iterator.hasNext() && (l = (blockPos = (BlockPos)iterator.next()).getManhattanDistance((Vec3i)pos)) <= 4) {
            Block block;
            if (blockPos.equals((Object)pos) || !((block = world.getBlockState(blockPos).getBlock()) instanceof Degradable)) continue;
            Degradable degradable = (Degradable)block;
            Enum enum_ = degradable.getDegradationLevel();
            if (this.getDegradationLevel().getClass() != enum_.getClass()) continue;
            int m = enum_.ordinal();
            if (m < i) {
                return Optional.empty();
            }
            if (m > i) {
                ++k;
                continue;
            }
            ++j;
        }
        float f = (float)(k + 1) / (float)(k + j + 1);
        float g = f * f * this.getDegradationChanceMultiplier();
        if (random.nextFloat() < g) {
            return this.getDegradationResult(state);
        }
        return Optional.empty();
    }
}

