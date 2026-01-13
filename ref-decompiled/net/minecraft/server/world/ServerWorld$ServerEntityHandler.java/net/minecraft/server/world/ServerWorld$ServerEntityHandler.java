/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntityHandler;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.minecraft.world.waypoint.ServerWaypoint;

final class ServerWorld.ServerEntityHandler
implements EntityHandler<Entity> {
    ServerWorld.ServerEntityHandler() {
    }

    @Override
    public void create(Entity entity) {
        ServerWaypoint serverWaypoint;
        if (entity instanceof ServerWaypoint && (serverWaypoint = (ServerWaypoint)((Object)entity)).hasWaypoint()) {
            ServerWorld.this.getWaypointHandler().onTrack(serverWaypoint);
        }
    }

    @Override
    public void destroy(Entity entity) {
        if (entity instanceof ServerWaypoint) {
            ServerWaypoint serverWaypoint = (ServerWaypoint)((Object)entity);
            ServerWorld.this.getWaypointHandler().onUntrack(serverWaypoint);
        }
        ServerWorld.this.getScoreboard().clearDeadEntity(entity);
    }

    @Override
    public void startTicking(Entity entity) {
        ServerWorld.this.entityList.add(entity);
    }

    @Override
    public void stopTicking(Entity entity) {
        ServerWorld.this.entityList.remove(entity);
    }

    @Override
    public void startTracking(Entity entity) {
        ServerWaypoint serverWaypoint;
        ServerWorld.this.getChunkManager().loadEntity(entity);
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
            ServerWorld.this.players.add(serverPlayerEntity);
            if (serverPlayerEntity.canReceiveWaypoints()) {
                ServerWorld.this.getWaypointHandler().addPlayer(serverPlayerEntity);
            }
            ServerWorld.this.updateSleepingPlayers();
        }
        if (entity instanceof ServerWaypoint && (serverWaypoint = (ServerWaypoint)((Object)entity)).hasWaypoint()) {
            ServerWorld.this.getWaypointHandler().onTrack(serverWaypoint);
        }
        if (entity instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity)entity;
            if (ServerWorld.this.duringListenerUpdate) {
                String string = "onTrackingStart called during navigation iteration";
                Util.logErrorOrPause("onTrackingStart called during navigation iteration", new IllegalStateException("onTrackingStart called during navigation iteration"));
            }
            ServerWorld.this.loadedMobs.add(mobEntity);
        }
        if (entity instanceof EnderDragonEntity) {
            EnderDragonEntity enderDragonEntity = (EnderDragonEntity)entity;
            for (EnderDragonPart enderDragonPart : enderDragonEntity.getBodyParts()) {
                ServerWorld.this.enderDragonParts.put(enderDragonPart.getId(), (Object)enderDragonPart);
            }
        }
        entity.updateEventHandler(EntityGameEventHandler::onEntitySetPosCallback);
    }

    @Override
    public void stopTracking(Entity entity) {
        ServerWorld.this.getChunkManager().unloadEntity(entity);
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
            ServerWorld.this.players.remove(serverPlayerEntity);
            ServerWorld.this.getWaypointHandler().removePlayer(serverPlayerEntity);
            ServerWorld.this.updateSleepingPlayers();
        }
        if (entity instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity)entity;
            if (ServerWorld.this.duringListenerUpdate) {
                String string = "onTrackingStart called during navigation iteration";
                Util.logErrorOrPause("onTrackingStart called during navigation iteration", new IllegalStateException("onTrackingStart called during navigation iteration"));
            }
            ServerWorld.this.loadedMobs.remove(mobEntity);
        }
        if (entity instanceof EnderDragonEntity) {
            EnderDragonEntity enderDragonEntity = (EnderDragonEntity)entity;
            for (EnderDragonPart enderDragonPart : enderDragonEntity.getBodyParts()) {
                ServerWorld.this.enderDragonParts.remove(enderDragonPart.getId());
            }
        }
        entity.updateEventHandler(EntityGameEventHandler::onEntityRemoval);
        ServerWorld.this.subscriptionTracker.untrackEntity(entity);
    }

    @Override
    public void updateLoadStatus(Entity entity) {
        entity.updateEventHandler(EntityGameEventHandler::onEntitySetPos);
    }

    @Override
    public /* synthetic */ void updateLoadStatus(Object entity) {
        this.updateLoadStatus((Entity)entity);
    }

    @Override
    public /* synthetic */ void stopTracking(Object entity) {
        this.stopTracking((Entity)entity);
    }

    @Override
    public /* synthetic */ void startTracking(Object entity) {
        this.startTracking((Entity)entity);
    }

    @Override
    public /* synthetic */ void startTicking(Object entity) {
        this.startTicking((Entity)entity);
    }

    @Override
    public /* synthetic */ void destroy(Object entity) {
        this.destroy((Entity)entity);
    }

    @Override
    public /* synthetic */ void create(Object entity) {
        this.create((Entity)entity);
    }
}
