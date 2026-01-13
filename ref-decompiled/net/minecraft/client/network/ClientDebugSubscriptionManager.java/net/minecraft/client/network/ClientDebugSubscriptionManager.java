/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.network;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.DebugSubscriptionRequestC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.log.DebugSampleType;
import net.minecraft.world.World;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.DebugSubscriptionType;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ClientDebugSubscriptionManager {
    private final ClientPlayNetworkHandler networkHandler;
    private final DebugHud debugHud;
    private Set<DebugSubscriptionType<?>> clientSubscriptions = Set.of();
    private final Map<DebugSubscriptionType<?>, TrackableValueMap<?>> valuesBySubscription = new HashMap();

    public ClientDebugSubscriptionManager(ClientPlayNetworkHandler networkHandler, DebugHud debugHud) {
        this.debugHud = debugHud;
        this.networkHandler = networkHandler;
    }

    private static void addDebugSubscription(Set<DebugSubscriptionType<?>> types, DebugSubscriptionType<?> type, boolean enable) {
        if (enable) {
            types.add(type);
        }
    }

    private Set<DebugSubscriptionType<?>> getRequestedSubscriptions() {
        ReferenceOpenHashSet set = new ReferenceOpenHashSet();
        ClientDebugSubscriptionManager.addDebugSubscription(set, DebugSampleType.TICK_TIME.getSubscriptionType(), this.debugHud.shouldRenderTickCharts());
        if (SharedConstants.DEBUG_ENABLED) {
            ClientDebugSubscriptionManager.addDebugSubscription(set, DebugSubscriptionTypes.BEES, SharedConstants.BEES);
            ClientDebugSubscriptionManager.addDebugSubscription(set, DebugSubscriptionTypes.BEE_HIVES, SharedConstants.BEES);
            ClientDebugSubscriptionManager.addDebugSubscription(set, DebugSubscriptionTypes.BRAINS, SharedConstants.BRAIN);
            ClientDebugSubscriptionManager.addDebugSubscription(set, DebugSubscriptionTypes.BREEZES, SharedConstants.BREEZE_MOB);
            ClientDebugSubscriptionManager.addDebugSubscription(set, DebugSubscriptionTypes.ENTITY_BLOCK_INTERSECTIONS, SharedConstants.ENTITY_BLOCK_INTERSECTION);
            ClientDebugSubscriptionManager.addDebugSubscription(set, DebugSubscriptionTypes.ENTITY_PATHS, SharedConstants.PATHFINDING);
            ClientDebugSubscriptionManager.addDebugSubscription(set, DebugSubscriptionTypes.GAME_EVENTS, SharedConstants.GAME_EVENT_LISTENERS);
            ClientDebugSubscriptionManager.addDebugSubscription(set, DebugSubscriptionTypes.GAME_EVENT_LISTENERS, SharedConstants.GAME_EVENT_LISTENERS);
            ClientDebugSubscriptionManager.addDebugSubscription(set, DebugSubscriptionTypes.GOAL_SELECTORS, SharedConstants.GOAL_SELECTOR || SharedConstants.BEES);
            ClientDebugSubscriptionManager.addDebugSubscription(set, DebugSubscriptionTypes.NEIGHBOR_UPDATES, SharedConstants.NEIGHBORSUPDATE);
            ClientDebugSubscriptionManager.addDebugSubscription(set, DebugSubscriptionTypes.POIS, SharedConstants.POI);
            ClientDebugSubscriptionManager.addDebugSubscription(set, DebugSubscriptionTypes.RAIDS, SharedConstants.RAIDS);
            ClientDebugSubscriptionManager.addDebugSubscription(set, DebugSubscriptionTypes.REDSTONE_WIRE_ORIENTATIONS, SharedConstants.EXPERIMENTAL_REDSTONEWIRE_UPDATE_ORDER);
            ClientDebugSubscriptionManager.addDebugSubscription(set, DebugSubscriptionTypes.STRUCTURES, SharedConstants.STRUCTURES);
            ClientDebugSubscriptionManager.addDebugSubscription(set, DebugSubscriptionTypes.VILLAGE_SECTIONS, SharedConstants.VILLAGE_SECTIONS);
        }
        return set;
    }

    public void clearAllSubscriptions() {
        this.clientSubscriptions = Set.of();
        this.clearValues();
    }

    public void startTick(long time) {
        Set<DebugSubscriptionType<?>> set = this.getRequestedSubscriptions();
        if (!set.equals(this.clientSubscriptions)) {
            this.clientSubscriptions = set;
            this.onSubscriptionsChanged(set);
        }
        this.valuesBySubscription.forEach((type, valueMap) -> {
            if (type.getExpiry() != 0) {
                valueMap.ejectExpiredSubscriptions(time);
            }
        });
    }

    private void onSubscriptionsChanged(Set<DebugSubscriptionType<?>> subscriptions) {
        this.valuesBySubscription.keySet().retainAll(subscriptions);
        this.clearSubscriptions(subscriptions);
        this.networkHandler.sendPacket(new DebugSubscriptionRequestC2SPacket(subscriptions));
    }

    private void clearSubscriptions(Set<DebugSubscriptionType<?>> subscriptions) {
        for (DebugSubscriptionType<?> debugSubscriptionType : subscriptions) {
            this.valuesBySubscription.computeIfAbsent(debugSubscriptionType, type -> new TrackableValueMap());
        }
    }

    <V> @Nullable TrackableValueMap<V> getTrackableValueMaps(DebugSubscriptionType<V> type) {
        return this.valuesBySubscription.get(type);
    }

    private <K, V> @Nullable TrackableValue<K, V> getValue(DebugSubscriptionType<V> type, TrackableValueGetter<K, V> getter) {
        TrackableValueMap<V> trackableValueMap = this.getTrackableValueMaps(type);
        return trackableValueMap != null ? getter.get(trackableValueMap) : null;
    }

    <K, V> @Nullable V getValue(DebugSubscriptionType<V> type, K object, TrackableValueGetter<K, V> getter) {
        TrackableValue<K, V> trackableValue = this.getValue(type, getter);
        return trackableValue != null ? (V)trackableValue.get(object) : null;
    }

    public DebugDataStore createDebugDataStore(final World world) {
        return new DebugDataStore(){

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
                    Entity entity = world.getEntity((UUID)uuid);
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
                TrackableValueMap<T> trackableValueMap = ClientDebugSubscriptionManager.this.getTrackableValueMaps(type);
                if (trackableValueMap == null) {
                    return;
                }
                long l = world.getTime();
                for (ValueWithExpiry valueWithExpiry : trackableValueMap.values) {
                    int i = (int)(valueWithExpiry.expiresAfterTime() - l);
                    int j = type.getExpiry();
                    action.accept(valueWithExpiry.value(), i, j);
                }
            }
        };
    }

    public <T> void updateChunk(long lifetime, ChunkPos pos, DebugSubscriptionType.OptionalValue<T> optional) {
        this.updateTrackableValueMap(lifetime, pos, optional, ClientDebugSubscriptionManager.forChunks());
    }

    public <T> void updateBlock(long lifetime, BlockPos pos, DebugSubscriptionType.OptionalValue<T> optional) {
        this.updateTrackableValueMap(lifetime, pos, optional, ClientDebugSubscriptionManager.forBlocks());
    }

    public <T> void updateEntity(long lifetime, Entity entity, DebugSubscriptionType.OptionalValue<T> optional) {
        this.updateTrackableValueMap(lifetime, entity.getUuid(), optional, ClientDebugSubscriptionManager.forEntities());
    }

    public <T> void addEvent(long lifetime, DebugSubscriptionType.Value<T> value) {
        TrackableValueMap<T> trackableValueMap = this.getTrackableValueMaps(value.subscription());
        if (trackableValueMap != null) {
            trackableValueMap.values.add(new ValueWithExpiry<T>(value.value(), lifetime + (long)value.subscription().getExpiry()));
        }
    }

    private <K, V> void updateTrackableValueMap(long lifetime, K object, DebugSubscriptionType.OptionalValue<V> optional, TrackableValueGetter<K, V> trackableValueGetter) {
        TrackableValue<K, V> trackableValue = this.getValue(optional.subscription(), trackableValueGetter);
        if (trackableValue != null) {
            trackableValue.apply(lifetime, object, optional);
        }
    }

    <K, V> void forEachValue(DebugSubscriptionType<V> type, TrackableValueGetter<K, V> getter, BiConsumer<K, V> visitor) {
        TrackableValue<K, V> trackableValue = this.getValue(type, getter);
        if (trackableValue != null) {
            trackableValue.forEach(visitor);
        }
    }

    public void clearValues() {
        this.valuesBySubscription.clear();
        this.clearSubscriptions(this.clientSubscriptions);
    }

    public void removeChunk(ChunkPos pos) {
        if (this.valuesBySubscription.isEmpty()) {
            return;
        }
        for (TrackableValueMap<?> trackableValueMap : this.valuesBySubscription.values()) {
            trackableValueMap.removeChunk(pos);
        }
    }

    public void removeEntity(Entity entity) {
        if (this.valuesBySubscription.isEmpty()) {
            return;
        }
        for (TrackableValueMap<?> trackableValueMap : this.valuesBySubscription.values()) {
            trackableValueMap.entities.removeUUID(entity.getUuid());
        }
    }

    static <T> TrackableValueGetter<UUID, T> forEntities() {
        return maps -> maps.entities;
    }

    static <T> TrackableValueGetter<BlockPos, T> forBlocks() {
        return maps -> maps.blocks;
    }

    static <T> TrackableValueGetter<ChunkPos, T> forChunks() {
        return maps -> maps.chunks;
    }

    @Environment(value=EnvType.CLIENT)
    static class TrackableValueMap<V> {
        final TrackableValue<ChunkPos, V> chunks = new TrackableValue();
        final TrackableValue<BlockPos, V> blocks = new TrackableValue();
        final TrackableValue<UUID, V> entities = new TrackableValue();
        final List<ValueWithExpiry<V>> values = new ArrayList<ValueWithExpiry<V>>();

        TrackableValueMap() {
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

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    static interface TrackableValueGetter<K, V> {
        public TrackableValue<K, V> get(TrackableValueMap<V> var1);
    }

    @Environment(value=EnvType.CLIENT)
    static class TrackableValue<K, V> {
        private final Map<K, ValueWithExpiry<V>> trackableValues = new HashMap<K, ValueWithExpiry<V>>();

        TrackableValue() {
        }

        public void removeAll(Predicate<ValueWithExpiry<V>> predicate) {
            this.trackableValues.values().removeIf(predicate);
        }

        public void removeUUID(K key) {
            this.trackableValues.remove(key);
        }

        public void removeKeys(Predicate<K> predicate) {
            this.trackableValues.keySet().removeIf(predicate);
        }

        public @Nullable V get(K object) {
            ValueWithExpiry<V> valueWithExpiry = this.trackableValues.get(object);
            return valueWithExpiry != null ? (V)valueWithExpiry.value() : null;
        }

        public void apply(long time, K key, DebugSubscriptionType.OptionalValue<V> value) {
            if (value.value().isPresent()) {
                this.trackableValues.put(key, new ValueWithExpiry<V>(value.value().get(), time + (long)value.subscription().getExpiry()));
            } else {
                this.trackableValues.remove(key);
            }
        }

        public void forEach(BiConsumer<K, V> action) {
            this.trackableValues.forEach((? super K k, ? super V v) -> action.accept(k, v.value()));
        }
    }

    @Environment(value=EnvType.CLIENT)
    record ValueWithExpiry<T>(T value, long expiresAfterTime) {
        private static final long INEXPIRABLE = -1L;

        public boolean hasExpired(long time) {
            if (this.expiresAfterTime == -1L) {
                return false;
            }
            return time >= this.expiresAfterTime;
        }
    }
}
