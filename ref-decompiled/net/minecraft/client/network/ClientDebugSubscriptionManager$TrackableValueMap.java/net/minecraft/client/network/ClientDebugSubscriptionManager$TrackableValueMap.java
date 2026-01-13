/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientDebugSubscriptionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

@Environment(value=EnvType.CLIENT)
static class ClientDebugSubscriptionManager.TrackableValueMap<V> {
    final ClientDebugSubscriptionManager.TrackableValue<ChunkPos, V> chunks = new ClientDebugSubscriptionManager.TrackableValue();
    final ClientDebugSubscriptionManager.TrackableValue<BlockPos, V> blocks = new ClientDebugSubscriptionManager.TrackableValue();
    final ClientDebugSubscriptionManager.TrackableValue<UUID, V> entities = new ClientDebugSubscriptionManager.TrackableValue();
    final List<ClientDebugSubscriptionManager.ValueWithExpiry<V>> values = new ArrayList<ClientDebugSubscriptionManager.ValueWithExpiry<V>>();

    ClientDebugSubscriptionManager.TrackableValueMap() {
    }

    public void ejectExpiredSubscriptions(long time2) {
        Predicate predicate = time -> time.hasExpired(time2);
        this.chunks.removeAll(predicate);
        this.blocks.removeAll(predicate);
        this.entities.removeAll(predicate);
        this.values.removeIf(predicate);
    }

    public void removeChunk(ChunkPos pos) {
        this.chunks.removeUUID(pos);
        this.blocks.removeKeys(pos::contains);
    }
}
