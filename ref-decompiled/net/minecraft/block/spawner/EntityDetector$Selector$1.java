/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.spawner;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.spawner.EntityDetector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;

class EntityDetector.Selector.1
implements EntityDetector.Selector {
    EntityDetector.Selector.1() {
    }

    public List<ServerPlayerEntity> getPlayers(ServerWorld world, Predicate<? super PlayerEntity> predicate) {
        return world.getPlayers(predicate);
    }

    @Override
    public <T extends Entity> List<T> getEntities(ServerWorld world, TypeFilter<Entity, T> typeFilter, Box box, Predicate<? super T> predicate) {
        return world.getEntitiesByType(typeFilter, box, predicate);
    }
}
