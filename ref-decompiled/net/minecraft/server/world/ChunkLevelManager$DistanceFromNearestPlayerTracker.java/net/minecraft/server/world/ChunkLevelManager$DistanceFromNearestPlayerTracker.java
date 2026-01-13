/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 */
package net.minecraft.server.world;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.server.world.ChunkPosDistanceLevelPropagator;

class ChunkLevelManager.DistanceFromNearestPlayerTracker
extends ChunkPosDistanceLevelPropagator {
    protected final Long2ByteMap distanceFromNearestPlayer;
    protected final int maxDistance;

    protected ChunkLevelManager.DistanceFromNearestPlayerTracker(int maxDistance) {
        super(maxDistance + 2, 16, 256);
        this.distanceFromNearestPlayer = new Long2ByteOpenHashMap();
        this.maxDistance = maxDistance;
        this.distanceFromNearestPlayer.defaultReturnValue((byte)(maxDistance + 2));
    }

    @Override
    protected int getLevel(long id) {
        return this.distanceFromNearestPlayer.get(id);
    }

    @Override
    protected void setLevel(long id, int level) {
        byte b = level > this.maxDistance ? this.distanceFromNearestPlayer.remove(id) : this.distanceFromNearestPlayer.put(id, (byte)level);
        this.onDistanceChange(id, b, level);
    }

    protected void onDistanceChange(long pos, int oldDistance, int distance) {
    }

    @Override
    protected int getInitialLevel(long id) {
        return this.isPlayerInChunk(id) ? 0 : Integer.MAX_VALUE;
    }

    private boolean isPlayerInChunk(long chunkPos) {
        ObjectSet objectSet = (ObjectSet)ChunkLevelManager.this.playersByChunkPos.get(chunkPos);
        return objectSet != null && !objectSet.isEmpty();
    }

    public void updateLevels() {
        this.applyPendingUpdates(Integer.MAX_VALUE);
    }
}
