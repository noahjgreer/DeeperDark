/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.debug;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockValueDebugS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkValueDebugS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityValueDebugS2CPacket;
import net.minecraft.server.debug.TrackedSubscription;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.debug.DebugSubscriptionType;
import net.minecraft.world.debug.DebugTrackable;

public static class TrackedSubscription.UpdateTrackedSubscription<T>
extends TrackedSubscription<T> {
    private final Map<ChunkPos, TrackedSubscription.UpdateQuerier<T>> trackedChunks = new HashMap<ChunkPos, TrackedSubscription.UpdateQuerier<T>>();
    private final Map<BlockPos, TrackedSubscription.UpdateQuerier<T>> trackedBlockEntities = new HashMap<BlockPos, TrackedSubscription.UpdateQuerier<T>>();
    private final Map<UUID, TrackedSubscription.UpdateQuerier<T>> trackedEntities = new HashMap<UUID, TrackedSubscription.UpdateQuerier<T>>();

    public TrackedSubscription.UpdateTrackedSubscription(DebugSubscriptionType<T> debugSubscriptionType) {
        super(debugSubscriptionType);
    }

    @Override
    protected void clear() {
        this.trackedChunks.clear();
        this.trackedBlockEntities.clear();
        this.trackedEntities.clear();
    }

    @Override
    protected void sendUpdate(ServerWorld world) {
        DebugSubscriptionType.OptionalValue<T> optionalValue;
        for (Map.Entry<ChunkPos, TrackedSubscription.UpdateQuerier<T>> entry : this.trackedChunks.entrySet()) {
            optionalValue = entry.getValue().queryUpdate(this.type);
            if (optionalValue == null) continue;
            ChunkPos chunkPos = entry.getKey();
            this.sendToTrackingPlayers(world, chunkPos, (Packet<ClientPlayPacketListener>)new ChunkValueDebugS2CPacket(chunkPos, optionalValue));
        }
        for (Map.Entry<Object, TrackedSubscription.UpdateQuerier<T>> entry : this.trackedBlockEntities.entrySet()) {
            optionalValue = entry.getValue().queryUpdate(this.type);
            if (optionalValue == null) continue;
            BlockPos blockPos = (BlockPos)entry.getKey();
            ChunkPos chunkPos2 = new ChunkPos(blockPos);
            this.sendToTrackingPlayers(world, chunkPos2, (Packet<ClientPlayPacketListener>)new BlockValueDebugS2CPacket(blockPos, optionalValue));
        }
        for (Map.Entry<Object, TrackedSubscription.UpdateQuerier<T>> entry : this.trackedEntities.entrySet()) {
            optionalValue = entry.getValue().queryUpdate(this.type);
            if (optionalValue == null) continue;
            Entity entity = Objects.requireNonNull(world.getEntity((UUID)entry.getKey()));
            this.sendToTrackingPlayers(world, entity, (Packet<ClientPlayPacketListener>)new EntityValueDebugS2CPacket(entity.getId(), optionalValue));
        }
    }

    public void trackChunk(ChunkPos chunkPos, DebugTrackable.DebugDataSupplier<T> dataSupplier) {
        this.trackedChunks.put(chunkPos, new TrackedSubscription.UpdateQuerier<T>(dataSupplier));
    }

    public void trackBlockEntity(BlockPos chunkPos, DebugTrackable.DebugDataSupplier<T> dataSupplier) {
        this.trackedBlockEntities.put(chunkPos, new TrackedSubscription.UpdateQuerier<T>(dataSupplier));
    }

    public void trackEntity(UUID uuid, DebugTrackable.DebugDataSupplier<T> dataSupplier) {
        this.trackedEntities.put(uuid, new TrackedSubscription.UpdateQuerier<T>(dataSupplier));
    }

    public void untrackChunk(ChunkPos chunkPos) {
        this.trackedChunks.remove(chunkPos);
        this.trackedBlockEntities.keySet().removeIf(chunkPos::contains);
    }

    public void untrackBlockEntity(ServerWorld world, BlockPos pos) {
        TrackedSubscription.UpdateQuerier<T> updateQuerier = this.trackedBlockEntities.remove(pos);
        if (updateQuerier != null) {
            ChunkPos chunkPos = new ChunkPos(pos);
            this.sendToTrackingPlayers(world, chunkPos, (Packet<ClientPlayPacketListener>)new BlockValueDebugS2CPacket(pos, this.type.optionalValueFor()));
        }
    }

    public void untrackEntity(Entity entity) {
        this.trackedEntities.remove(entity.getUuid());
    }

    @Override
    protected void sendInitial(ServerPlayerEntity player, ChunkPos chunkPos) {
        TrackedSubscription.UpdateQuerier<T> updateQuerier = this.trackedChunks.get(chunkPos);
        if (updateQuerier != null && updateQuerier.lastData != null) {
            player.networkHandler.sendPacket(new ChunkValueDebugS2CPacket(chunkPos, this.type.optionalValueFor(updateQuerier.lastData)));
        }
        for (Map.Entry<BlockPos, TrackedSubscription.UpdateQuerier<T>> entry : this.trackedBlockEntities.entrySet()) {
            BlockPos blockPos;
            Object object = entry.getValue().lastData;
            if (object == null || !chunkPos.contains(blockPos = entry.getKey())) continue;
            player.networkHandler.sendPacket(new BlockValueDebugS2CPacket(blockPos, this.type.optionalValueFor(object)));
        }
    }

    @Override
    protected void sendInitial(ServerPlayerEntity player, Entity entity) {
        TrackedSubscription.UpdateQuerier<T> updateQuerier = this.trackedEntities.get(entity.getUuid());
        if (updateQuerier != null && updateQuerier.lastData != null) {
            player.networkHandler.sendPacket(new EntityValueDebugS2CPacket(entity.getId(), this.type.optionalValueFor(updateQuerier.lastData)));
        }
    }
}
