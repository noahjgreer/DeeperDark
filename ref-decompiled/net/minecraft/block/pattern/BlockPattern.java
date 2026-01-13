/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  net.minecraft.block.pattern.BlockPattern
 *  net.minecraft.block.pattern.BlockPattern$BlockStateCacheLoader
 *  net.minecraft.block.pattern.BlockPattern$Result
 *  net.minecraft.block.pattern.CachedBlockPosition
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.WorldView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.pattern;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.function.Predicate;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.WorldView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class BlockPattern {
    private final Predicate<CachedBlockPosition>[][][] pattern;
    private final int depth;
    private final int height;
    private final int width;

    public BlockPattern(Predicate<CachedBlockPosition>[][][] pattern) {
        this.pattern = pattern;
        this.depth = pattern.length;
        if (this.depth > 0) {
            this.height = pattern[0].length;
            this.width = this.height > 0 ? pattern[0][0].length : 0;
        } else {
            this.height = 0;
            this.width = 0;
        }
    }

    public int getDepth() {
        return this.depth;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    @VisibleForTesting
    public Predicate<CachedBlockPosition>[][][] getPattern() {
        return this.pattern;
    }

    @VisibleForTesting
    public // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable BlockPattern.Result testTransform(WorldView world, BlockPos frontTopLeft, Direction forwards, Direction up) {
        LoadingCache loadingCache = BlockPattern.makeCache((WorldView)world, (boolean)false);
        return this.testTransform(frontTopLeft, forwards, up, loadingCache);
    }

    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable BlockPattern.Result testTransform(BlockPos frontTopLeft, Direction forwards, Direction up, LoadingCache<BlockPos, CachedBlockPosition> cache) {
        for (int i = 0; i < this.width; ++i) {
            for (int j = 0; j < this.height; ++j) {
                for (int k = 0; k < this.depth; ++k) {
                    if (this.pattern[k][j][i].test((CachedBlockPosition)cache.getUnchecked((Object)BlockPattern.translate((BlockPos)frontTopLeft, (Direction)forwards, (Direction)up, (int)i, (int)j, (int)k)))) continue;
                    return null;
                }
            }
        }
        return new Result(frontTopLeft, forwards, up, cache, this.width, this.height, this.depth);
    }

    public // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable BlockPattern.Result searchAround(WorldView world, BlockPos pos) {
        LoadingCache loadingCache = BlockPattern.makeCache((WorldView)world, (boolean)false);
        int i = Math.max(Math.max(this.width, this.height), this.depth);
        for (BlockPos blockPos : BlockPos.iterate((BlockPos)pos, (BlockPos)pos.add(i - 1, i - 1, i - 1))) {
            for (Direction direction : Direction.values()) {
                for (Direction direction2 : Direction.values()) {
                    Result result;
                    if (direction2 == direction || direction2 == direction.getOpposite() || (result = this.testTransform(blockPos, direction, direction2, loadingCache)) == null) continue;
                    return result;
                }
            }
        }
        return null;
    }

    public static LoadingCache<BlockPos, CachedBlockPosition> makeCache(WorldView world, boolean forceLoad) {
        return CacheBuilder.newBuilder().build((CacheLoader)new BlockStateCacheLoader(world, forceLoad));
    }

    protected static BlockPos translate(BlockPos pos, Direction forwards, Direction up, int offsetLeft, int offsetDown, int offsetForwards) {
        if (forwards == up || forwards == up.getOpposite()) {
            throw new IllegalArgumentException("Invalid forwards & up combination");
        }
        Vec3i vec3i = new Vec3i(forwards.getOffsetX(), forwards.getOffsetY(), forwards.getOffsetZ());
        Vec3i vec3i2 = new Vec3i(up.getOffsetX(), up.getOffsetY(), up.getOffsetZ());
        Vec3i vec3i3 = vec3i.crossProduct(vec3i2);
        return pos.add(vec3i2.getX() * -offsetDown + vec3i3.getX() * offsetLeft + vec3i.getX() * offsetForwards, vec3i2.getY() * -offsetDown + vec3i3.getY() * offsetLeft + vec3i.getY() * offsetForwards, vec3i2.getZ() * -offsetDown + vec3i3.getZ() * offsetLeft + vec3i.getZ() * offsetForwards);
    }
}

