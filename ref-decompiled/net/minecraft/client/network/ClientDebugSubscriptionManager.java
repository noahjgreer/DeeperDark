/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.gui.hud.DebugHud
 *  net.minecraft.client.network.ClientDebugSubscriptionManager
 *  net.minecraft.client.network.ClientDebugSubscriptionManager$TrackableValue
 *  net.minecraft.client.network.ClientDebugSubscriptionManager$TrackableValueGetter
 *  net.minecraft.client.network.ClientDebugSubscriptionManager$TrackableValueMap
 *  net.minecraft.client.network.ClientDebugSubscriptionManager$ValueWithExpiry
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.DebugSubscriptionRequestC2SPacket
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.util.profiler.log.DebugSampleType
 *  net.minecraft.world.World
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.debug.DebugSubscriptionType
 *  net.minecraft.world.debug.DebugSubscriptionType$OptionalValue
 *  net.minecraft.world.debug.DebugSubscriptionType$Value
 *  net.minecraft.world.debug.DebugSubscriptionTypes
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.network;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.network.ClientDebugSubscriptionManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.DebugSubscriptionRequestC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.log.DebugSampleType;
import net.minecraft.world.World;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.DebugSubscriptionType;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
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
        ClientDebugSubscriptionManager.addDebugSubscription((Set)set, (DebugSubscriptionType)DebugSampleType.TICK_TIME.getSubscriptionType(), (boolean)this.debugHud.shouldRenderTickCharts());
        if (SharedConstants.DEBUG_ENABLED) {
            ClientDebugSubscriptionManager.addDebugSubscription((Set)set, (DebugSubscriptionType)DebugSubscriptionTypes.BEES, (boolean)SharedConstants.BEES);
            ClientDebugSubscriptionManager.addDebugSubscription((Set)set, (DebugSubscriptionType)DebugSubscriptionTypes.BEE_HIVES, (boolean)SharedConstants.BEES);
            ClientDebugSubscriptionManager.addDebugSubscription((Set)set, (DebugSubscriptionType)DebugSubscriptionTypes.BRAINS, (boolean)SharedConstants.BRAIN);
            ClientDebugSubscriptionManager.addDebugSubscription((Set)set, (DebugSubscriptionType)DebugSubscriptionTypes.BREEZES, (boolean)SharedConstants.BREEZE_MOB);
            ClientDebugSubscriptionManager.addDebugSubscription((Set)set, (DebugSubscriptionType)DebugSubscriptionTypes.ENTITY_BLOCK_INTERSECTIONS, (boolean)SharedConstants.ENTITY_BLOCK_INTERSECTION);
            ClientDebugSubscriptionManager.addDebugSubscription((Set)set, (DebugSubscriptionType)DebugSubscriptionTypes.ENTITY_PATHS, (boolean)SharedConstants.PATHFINDING);
            ClientDebugSubscriptionManager.addDebugSubscription((Set)set, (DebugSubscriptionType)DebugSubscriptionTypes.GAME_EVENTS, (boolean)SharedConstants.GAME_EVENT_LISTENERS);
            ClientDebugSubscriptionManager.addDebugSubscription((Set)set, (DebugSubscriptionType)DebugSubscriptionTypes.GAME_EVENT_LISTENERS, (boolean)SharedConstants.GAME_EVENT_LISTENERS);
            ClientDebugSubscriptionManager.addDebugSubscription((Set)set, (DebugSubscriptionType)DebugSubscriptionTypes.GOAL_SELECTORS, (SharedConstants.GOAL_SELECTOR || SharedConstants.BEES ? 1 : 0) != 0);
            ClientDebugSubscriptionManager.addDebugSubscription((Set)set, (DebugSubscriptionType)DebugSubscriptionTypes.NEIGHBOR_UPDATES, (boolean)SharedConstants.NEIGHBORSUPDATE);
            ClientDebugSubscriptionManager.addDebugSubscription((Set)set, (DebugSubscriptionType)DebugSubscriptionTypes.POIS, (boolean)SharedConstants.POI);
            ClientDebugSubscriptionManager.addDebugSubscription((Set)set, (DebugSubscriptionType)DebugSubscriptionTypes.RAIDS, (boolean)SharedConstants.RAIDS);
            ClientDebugSubscriptionManager.addDebugSubscription((Set)set, (DebugSubscriptionType)DebugSubscriptionTypes.REDSTONE_WIRE_ORIENTATIONS, (boolean)SharedConstants.EXPERIMENTAL_REDSTONEWIRE_UPDATE_ORDER);
            ClientDebugSubscriptionManager.addDebugSubscription((Set)set, (DebugSubscriptionType)DebugSubscriptionTypes.STRUCTURES, (boolean)SharedConstants.STRUCTURES);
            ClientDebugSubscriptionManager.addDebugSubscription((Set)set, (DebugSubscriptionType)DebugSubscriptionTypes.VILLAGE_SECTIONS, (boolean)SharedConstants.VILLAGE_SECTIONS);
        }
        return set;
    }

    public void clearAllSubscriptions() {
        this.clientSubscriptions = Set.of();
        this.clearValues();
    }

    public void startTick(long time) {
        Set set = this.getRequestedSubscriptions();
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
        this.networkHandler.sendPacket((Packet)new DebugSubscriptionRequestC2SPacket(subscriptions));
    }

    private void clearSubscriptions(Set<DebugSubscriptionType<?>> subscriptions) {
        for (DebugSubscriptionType<?> debugSubscriptionType : subscriptions) {
            this.valuesBySubscription.computeIfAbsent(debugSubscriptionType, type -> new TrackableValueMap());
        }
    }

    <V> // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ClientDebugSubscriptionManager.TrackableValueMap<V> getTrackableValueMaps(DebugSubscriptionType<V> type) {
        return (TrackableValueMap)this.valuesBySubscription.get(type);
    }

    private <K, V> // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ClientDebugSubscriptionManager.TrackableValue<K, V> getValue(DebugSubscriptionType<V> type, TrackableValueGetter<K, V> getter) {
        TrackableValueMap trackableValueMap = this.getTrackableValueMaps(type);
        return trackableValueMap != null ? getter.get(trackableValueMap) : null;
    }

    <K, V> @Nullable V getValue(DebugSubscriptionType<V> type, K object, TrackableValueGetter<K, V> getter) {
        TrackableValue trackableValue = this.getValue(type, getter);
        return (V)(trackableValue != null ? trackableValue.get(object) : null);
    }

    public DebugDataStore createDebugDataStore(World world) {
        return new /* Unavailable Anonymous Inner Class!! */;
    }

    public <T> void updateChunk(long lifetime, ChunkPos pos, DebugSubscriptionType.OptionalValue<T> optional) {
        this.updateTrackableValueMap(lifetime, (Object)pos, optional, ClientDebugSubscriptionManager.forChunks());
    }

    public <T> void updateBlock(long lifetime, BlockPos pos, DebugSubscriptionType.OptionalValue<T> optional) {
        this.updateTrackableValueMap(lifetime, (Object)pos, optional, ClientDebugSubscriptionManager.forBlocks());
    }

    public <T> void updateEntity(long lifetime, Entity entity, DebugSubscriptionType.OptionalValue<T> optional) {
        this.updateTrackableValueMap(lifetime, (Object)entity.getUuid(), optional, ClientDebugSubscriptionManager.forEntities());
    }

    public <T> void addEvent(long lifetime, DebugSubscriptionType.Value<T> value) {
        TrackableValueMap trackableValueMap = this.getTrackableValueMaps(value.subscription());
        if (trackableValueMap != null) {
            trackableValueMap.values.add(new ValueWithExpiry(value.value(), lifetime + (long)value.subscription().getExpiry()));
        }
    }

    private <K, V> void updateTrackableValueMap(long lifetime, K object, DebugSubscriptionType.OptionalValue<V> optional, TrackableValueGetter<K, V> trackableValueGetter) {
        TrackableValue trackableValue = this.getValue(optional.subscription(), trackableValueGetter);
        if (trackableValue != null) {
            trackableValue.apply(lifetime, object, optional);
        }
    }

    <K, V> void forEachValue(DebugSubscriptionType<V> type, TrackableValueGetter<K, V> getter, BiConsumer<K, V> visitor) {
        TrackableValue trackableValue = this.getValue(type, getter);
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
        for (TrackableValueMap trackableValueMap : this.valuesBySubscription.values()) {
            trackableValueMap.removeChunk(pos);
        }
    }

    public void removeEntity(Entity entity) {
        if (this.valuesBySubscription.isEmpty()) {
            return;
        }
        for (TrackableValueMap trackableValueMap : this.valuesBySubscription.values()) {
            trackableValueMap.entities.removeUUID((Object)entity.getUuid());
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
}

