/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.cache.LoadingCache
 */
package net.minecraft.block.pattern;

import com.google.common.base.MoreObjects;
import com.google.common.cache.LoadingCache;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public static class BlockPattern.Result {
    private final BlockPos frontTopLeft;
    private final Direction forwards;
    private final Direction up;
    private final LoadingCache<BlockPos, CachedBlockPosition> cache;
    private final int width;
    private final int height;
    private final int depth;

    public BlockPattern.Result(BlockPos frontTopLeft, Direction forwards, Direction up, LoadingCache<BlockPos, CachedBlockPosition> cache, int width, int height, int depth) {
        this.frontTopLeft = frontTopLeft;
        this.forwards = forwards;
        this.up = up;
        this.cache = cache;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public BlockPos getFrontTopLeft() {
        return this.frontTopLeft;
    }

    public Direction getForwards() {
        return this.forwards;
    }

    public Direction getUp() {
        return this.up;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getDepth() {
        return this.depth;
    }

    public CachedBlockPosition translate(int offsetLeft, int offsetDown, int offsetForwards) {
        return (CachedBlockPosition)this.cache.getUnchecked((Object)BlockPattern.translate(this.frontTopLeft, this.getForwards(), this.getUp(), offsetLeft, offsetDown, offsetForwards));
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("up", (Object)this.up).add("forwards", (Object)this.forwards).add("frontTopLeft", (Object)this.frontTopLeft).toString();
    }
}
