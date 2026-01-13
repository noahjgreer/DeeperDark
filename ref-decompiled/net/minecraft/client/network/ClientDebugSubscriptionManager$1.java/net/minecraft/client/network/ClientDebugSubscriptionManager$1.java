/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.network;

import java.util.UUID;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientDebugSubscriptionManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.DebugSubscriptionType;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class ClientDebugSubscriptionManager.1
implements DebugDataStore {
    final /* synthetic */ World field_62936;

    ClientDebugSubscriptionManager.1() {
        this.field_62936 = world;
    }

    @Override
    public <T> void forEachChunkData(DebugSubscriptionType<T> type, BiConsumer<ChunkPos, T> action) {
        ClientDebugSubscriptionManager.this.forEachValue(type, ClientDebugSubscriptionManager.forChunks(), action);
    }

    @Override
    public <T> @Nullable T getChunkData(DebugSubscriptionType<T> type, ChunkPos chunkPos) {
        return ClientDebugSubscriptionManager.this.getValue(type, chunkPos, ClientDebugSubscriptionManager.forChunks());
    }

    @Override
    public <T> void forEachBlockData(DebugSubscriptionType<T> type, BiConsumer<BlockPos, T> action) {
        ClientDebugSubscriptionManager.this.forEachValue(type, ClientDebugSubscriptionManager.forBlocks(), action);
    }

    @Override
    public <T> @Nullable T getBlockData(DebugSubscriptionType<T> type, BlockPos pos) {
        return ClientDebugSubscriptionManager.this.getValue(type, pos, ClientDebugSubscriptionManager.forBlocks());
    }

    @Override
    public <T> void forEachEntityData(DebugSubscriptionType<T> type2, BiConsumer<Entity, T> action) {
        ClientDebugSubscriptionManager.this.forEachValue(type2, ClientDebugSubscriptionManager.forEntities(), (uuid, type) -> {
            Entity entity = this.field_62936.getEntity((UUID)uuid);
            if (entity != null) {
                action.accept(entity, type);
            }
        });
    }

    @Override
    public <T> @Nullable T getEntityData(DebugSubscriptionType<T> type, Entity entity) {
        return ClientDebugSubscriptionManager.this.getValue(type, entity.getUuid(), ClientDebugSubscriptionManager.forEntities());
    }

    @Override
    public <T> void forEachEvent(DebugSubscriptionType<T> type, DebugDataStore.EventConsumer<T> action) {
        ClientDebugSubscriptionManager.TrackableValueMap<T> trackableValueMap = ClientDebugSubscriptionManager.this.getTrackableValueMaps(type);
        if (trackableValueMap == null) {
            return;
        }
        long l = this.field_62936.getTime();
        for (ClientDebugSubscriptionManager.ValueWithExpiry valueWithExpiry : trackableValueMap.values) {
            int i = (int)(valueWithExpiry.expiresAfterTime() - l);
            int j = type.getExpiry();
            action.accept(valueWithExpiry.value(), i, j);
        }
    }
}
