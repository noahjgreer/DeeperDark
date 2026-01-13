/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.spawner;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.block.spawner.EntityDetector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;

static class EntityDetector.Selector.2
implements EntityDetector.Selector {
    final /* synthetic */ List field_48863;

    EntityDetector.Selector.2(List list) {
        this.field_48863 = list;
    }

    public List<PlayerEntity> getPlayers(ServerWorld world, Predicate<? super PlayerEntity> predicate) {
        return this.field_48863.stream().filter(predicate).toList();
    }

    @Override
    public <T extends Entity> List<T> getEntities(ServerWorld world, TypeFilter<Entity, T> typeFilter, Box box, Predicate<? super T> predicate) {
        return this.field_48863.stream().map(typeFilter::downcast).filter(Objects::nonNull).filter(predicate).toList();
    }
}
