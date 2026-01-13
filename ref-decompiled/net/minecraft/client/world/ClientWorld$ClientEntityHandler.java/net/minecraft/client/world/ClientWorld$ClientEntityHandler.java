/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.world;

import java.lang.runtime.SwitchBootstraps;
import java.util.Arrays;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.world.entity.EntityHandler;

@Environment(value=EnvType.CLIENT)
final class ClientWorld.ClientEntityHandler
implements EntityHandler<Entity> {
    ClientWorld.ClientEntityHandler() {
    }

    @Override
    public void create(Entity entity) {
    }

    @Override
    public void destroy(Entity entity) {
    }

    @Override
    public void startTicking(Entity entity) {
        ClientWorld.this.entityList.add(entity);
    }

    @Override
    public void stopTicking(Entity entity) {
        ClientWorld.this.entityList.remove(entity);
    }

    @Override
    public void startTracking(Entity entity) {
        Entity entity2 = entity;
        Objects.requireNonNull(entity2);
        Entity entity3 = entity2;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{AbstractClientPlayerEntity.class, EnderDragonEntity.class}, (Object)entity3, n)) {
            case 0: {
                AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)entity3;
                ClientWorld.this.players.add(abstractClientPlayerEntity);
                break;
            }
            case 1: {
                EnderDragonEntity enderDragonEntity = (EnderDragonEntity)entity3;
                ClientWorld.this.enderDragonParts.addAll(Arrays.asList(enderDragonEntity.getBodyParts()));
                break;
            }
        }
    }

    @Override
    public void stopTracking(Entity entity) {
        entity.detach();
        Entity entity2 = entity;
        Objects.requireNonNull(entity2);
        Entity entity3 = entity2;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{AbstractClientPlayerEntity.class, EnderDragonEntity.class}, (Object)entity3, n)) {
            case 0: {
                AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)entity3;
                ClientWorld.this.players.remove(abstractClientPlayerEntity);
                break;
            }
            case 1: {
                EnderDragonEntity enderDragonEntity = (EnderDragonEntity)entity3;
                ClientWorld.this.enderDragonParts.removeAll(Arrays.asList(enderDragonEntity.getBodyParts()));
                break;
            }
        }
    }

    @Override
    public void updateLoadStatus(Entity entity) {
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
