/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.network.PendingUpdateManager
 *  net.minecraft.client.network.PendingUpdateManager$PendingUpdate
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.math.BlockPos
 */
package net.minecraft.client.network;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class PendingUpdateManager
implements AutoCloseable {
    private final Long2ObjectOpenHashMap<PendingUpdate> blockPosToPendingUpdate = new Long2ObjectOpenHashMap();
    private int sequence;
    private boolean pendingSequence;

    public void addPendingUpdate(BlockPos pos, BlockState state, ClientPlayerEntity player) {
        this.blockPosToPendingUpdate.compute(pos.asLong(), (posLong, pendingUpdate) -> {
            if (pendingUpdate != null) {
                return pendingUpdate.withSequence(this.sequence);
            }
            return new PendingUpdate(this.sequence, state, player.getEntityPos());
        });
    }

    public boolean hasPendingUpdate(BlockPos pos, BlockState state) {
        PendingUpdate pendingUpdate = (PendingUpdate)this.blockPosToPendingUpdate.get(pos.asLong());
        if (pendingUpdate == null) {
            return false;
        }
        pendingUpdate.setBlockState(state);
        return true;
    }

    public void processPendingUpdates(int maxProcessableSequence, ClientWorld world) {
        ObjectIterator objectIterator = this.blockPosToPendingUpdate.long2ObjectEntrySet().iterator();
        while (objectIterator.hasNext()) {
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)objectIterator.next();
            PendingUpdate pendingUpdate = (PendingUpdate)entry.getValue();
            if (pendingUpdate.sequence > maxProcessableSequence) continue;
            BlockPos blockPos = BlockPos.fromLong((long)entry.getLongKey());
            objectIterator.remove();
            world.processPendingUpdate(blockPos, pendingUpdate.blockState, pendingUpdate.playerPos);
        }
    }

    public PendingUpdateManager incrementSequence() {
        ++this.sequence;
        this.pendingSequence = true;
        return this;
    }

    @Override
    public void close() {
        this.pendingSequence = false;
    }

    public int getSequence() {
        return this.sequence;
    }

    public boolean hasPendingSequence() {
        return this.pendingSequence;
    }
}

