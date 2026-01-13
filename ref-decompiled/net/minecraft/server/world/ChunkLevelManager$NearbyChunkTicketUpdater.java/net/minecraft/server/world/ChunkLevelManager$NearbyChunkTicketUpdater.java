/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2IntMap
 *  it.unimi.dsi.fastutil.longs.Long2IntMaps
 *  it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 */
package net.minecraft.server.world;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntMaps;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.world.ChunkLevelManager;
import net.minecraft.server.world.ChunkTicket;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.util.math.ChunkPos;

class ChunkLevelManager.NearbyChunkTicketUpdater
extends ChunkLevelManager.DistanceFromNearestPlayerTracker {
    private int watchDistance;
    private final Long2IntMap distances;
    private final LongSet positionsAffected;

    protected ChunkLevelManager.NearbyChunkTicketUpdater(int i) {
        super(ChunkLevelManager.this, i);
        this.distances = Long2IntMaps.synchronize((Long2IntMap)new Long2IntOpenHashMap());
        this.positionsAffected = new LongOpenHashSet();
        this.watchDistance = 0;
        this.distances.defaultReturnValue(i + 2);
    }

    @Override
    protected void onDistanceChange(long pos, int oldDistance, int distance) {
        this.positionsAffected.add(pos);
    }

    public void setWatchDistance(int watchDistance) {
        for (Long2ByteMap.Entry entry : this.distanceFromNearestPlayer.long2ByteEntrySet()) {
            byte b = entry.getByteValue();
            long l = entry.getLongKey();
            this.updateTicket(l, b, this.isWithinViewDistance(b), b <= watchDistance);
        }
        this.watchDistance = watchDistance;
    }

    private void updateTicket(long pos, int distance, boolean oldWithinViewDistance, boolean withinViewDistance) {
        if (oldWithinViewDistance != withinViewDistance) {
            ChunkTicket chunkTicket = new ChunkTicket(ChunkTicketType.PLAYER_LOADING, NEARBY_PLAYER_TICKET_LEVEL);
            if (withinViewDistance) {
                ChunkLevelManager.this.scheduler.add(() -> ChunkLevelManager.this.mainThreadExecutor.execute(() -> {
                    if (this.isWithinViewDistance(this.getLevel(pos))) {
                        ChunkLevelManager.this.ticketManager.addTicket(pos, chunkTicket);
                        ChunkLevelManager.this.freshPlayerTicketPositions.add(pos);
                    } else {
                        ChunkLevelManager.this.scheduler.remove(pos, () -> {}, false);
                    }
                }), pos, () -> distance);
            } else {
                ChunkLevelManager.this.scheduler.remove(pos, () -> ChunkLevelManager.this.mainThreadExecutor.execute(() -> ChunkLevelManager.this.ticketManager.removeTicket(pos, chunkTicket)), true);
            }
        }
    }

    @Override
    public void updateLevels() {
        super.updateLevels();
        if (!this.positionsAffected.isEmpty()) {
            LongIterator longIterator = this.positionsAffected.iterator();
            while (longIterator.hasNext()) {
                int j;
                long l = longIterator.nextLong();
                int i = this.distances.get(l);
                if (i == (j = this.getLevel(l))) continue;
                ChunkLevelManager.this.scheduler.updateLevel(new ChunkPos(l), () -> this.distances.get(l), j, level -> {
                    if (level >= this.distances.defaultReturnValue()) {
                        this.distances.remove(l);
                    } else {
                        this.distances.put(l, level);
                    }
                });
                this.updateTicket(l, j, this.isWithinViewDistance(i), this.isWithinViewDistance(j));
            }
            this.positionsAffected.clear();
        }
    }

    private boolean isWithinViewDistance(int distance) {
        return distance <= this.watchDistance;
    }
}
