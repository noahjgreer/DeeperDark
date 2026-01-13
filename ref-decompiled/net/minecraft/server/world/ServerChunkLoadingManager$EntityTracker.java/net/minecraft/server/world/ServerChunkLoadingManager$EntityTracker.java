/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package net.minecraft.server.world;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.PlayerAssociatedNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;

class ServerChunkLoadingManager.EntityTracker
implements EntityTrackerEntry.TrackerPacketSender {
    final EntityTrackerEntry entry;
    final Entity entity;
    private final int maxDistance;
    ChunkSectionPos trackedSection;
    final Set<PlayerAssociatedNetworkHandler> listeners = Sets.newIdentityHashSet();

    public ServerChunkLoadingManager.EntityTracker(Entity entity, int maxDistance, int tickInterval, boolean alwaysUpdateVelocity) {
        this.entry = new EntityTrackerEntry(ServerChunkLoadingManager.this.world, entity, tickInterval, alwaysUpdateVelocity, this);
        this.entity = entity;
        this.maxDistance = maxDistance;
        this.trackedSection = ChunkSectionPos.from(entity);
    }

    public boolean equals(Object o) {
        if (o instanceof ServerChunkLoadingManager.EntityTracker) {
            return ((ServerChunkLoadingManager.EntityTracker)o).entity.getId() == this.entity.getId();
        }
        return false;
    }

    public int hashCode() {
        return this.entity.getId();
    }

    @Override
    public void sendToListeners(Packet<? super ClientPlayPacketListener> packet) {
        for (PlayerAssociatedNetworkHandler playerAssociatedNetworkHandler : this.listeners) {
            playerAssociatedNetworkHandler.sendPacket(packet);
        }
    }

    @Override
    public void sendToSelfAndListeners(Packet<? super ClientPlayPacketListener> packet) {
        this.sendToListeners(packet);
        Entity entity = this.entity;
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
            serverPlayerEntity.networkHandler.sendPacket(packet);
        }
    }

    @Override
    public void sendToListenersIf(Packet<? super ClientPlayPacketListener> packet, Predicate<ServerPlayerEntity> predicate) {
        for (PlayerAssociatedNetworkHandler playerAssociatedNetworkHandler : this.listeners) {
            if (!predicate.test(playerAssociatedNetworkHandler.getPlayer())) continue;
            playerAssociatedNetworkHandler.sendPacket(packet);
        }
    }

    public void stopTracking() {
        for (PlayerAssociatedNetworkHandler playerAssociatedNetworkHandler : this.listeners) {
            this.entry.stopTracking(playerAssociatedNetworkHandler.getPlayer());
        }
    }

    public void stopTracking(ServerPlayerEntity player) {
        if (this.listeners.remove(player.networkHandler)) {
            this.entry.stopTracking(player);
            if (this.listeners.isEmpty()) {
                ServerChunkLoadingManager.this.world.getSubscriptionTracker().untrackEntity(this.entity);
            }
        }
    }

    public void updateTrackedStatus(ServerPlayerEntity player) {
        boolean bl;
        if (player == this.entity) {
            return;
        }
        Vec3d vec3d = player.getEntityPos().subtract(this.entity.getEntityPos());
        int i = ServerChunkLoadingManager.this.getViewDistance(player);
        double e = vec3d.x * vec3d.x + vec3d.z * vec3d.z;
        double d = Math.min(this.getMaxTrackDistance(), i * 16);
        double f = d * d;
        boolean bl2 = bl = e <= f && this.entity.canBeSpectated(player) && ServerChunkLoadingManager.this.isTracked(player, this.entity.getChunkPos().x, this.entity.getChunkPos().z);
        if (bl) {
            if (this.listeners.add(player.networkHandler)) {
                this.entry.startTracking(player);
                if (this.listeners.size() == 1) {
                    ServerChunkLoadingManager.this.world.getSubscriptionTracker().trackEntity(this.entity);
                }
                ServerChunkLoadingManager.this.world.getSubscriptionTracker().sendInitialIfSubscribed(player, this.entity);
            }
        } else {
            this.stopTracking(player);
        }
    }

    private int adjustTrackingDistance(int initialDistance) {
        return ServerChunkLoadingManager.this.world.getServer().adjustTrackingDistance(initialDistance);
    }

    private int getMaxTrackDistance() {
        int i = this.maxDistance;
        for (Entity entity : this.entity.getPassengersDeep()) {
            int j = entity.getType().getMaxTrackDistance() * 16;
            if (j <= i) continue;
            i = j;
        }
        return this.adjustTrackingDistance(i);
    }

    public void updateTrackedStatus(List<ServerPlayerEntity> players) {
        for (ServerPlayerEntity serverPlayerEntity : players) {
            this.updateTrackedStatus(serverPlayerEntity);
        }
    }
}
