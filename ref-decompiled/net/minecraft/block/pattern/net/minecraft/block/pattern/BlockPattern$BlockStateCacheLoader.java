/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheLoader
 */
package net.minecraft.block.pattern;

import com.google.common.cache.CacheLoader;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

static class BlockPattern.BlockStateCacheLoader
extends CacheLoader<BlockPos, CachedBlockPosition> {
    private final WorldView world;
    private final boolean forceLoad;

    public BlockPattern.BlockStateCacheLoader(WorldView world, boolean forceLoad) {
        this.world = world;
        this.forceLoad = forceLoad;
    }

    public CachedBlockPosition load(BlockPos blockPos) {
        return new CachedBlockPosition(this.world, blockPos, this.forceLoad);
    }

    public /* synthetic */ Object load(Object pos) throws Exception {
        return this.load((BlockPos)pos);
    }
}
