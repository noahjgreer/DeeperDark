/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.spawner;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;

public static interface EntityDetector.Selector {
    public static final EntityDetector.Selector IN_WORLD = new EntityDetector.Selector(){

        public List<ServerPlayerEntity> getPlayers(ServerWorld world, Predicate<? super PlayerEntity> predicate) {
            return world.getPlayers(predicate);
        }

        @Override
        public <T extends Entity> List<T> getEntities(ServerWorld world, TypeFilter<Entity, T> typeFilter, Box box, Predicate<? super T> predicate) {
            return world.getEntitiesByType(typeFilter, box, predicate);
        }
    };

    public List<? extends PlayerEntity> getPlayers(ServerWorld var1, Predicate<? super PlayerEntity> var2);

    public <T extends Entity> List<T> getEntities(ServerWorld var1, TypeFilter<Entity, T> var2, Box var3, Predicate<? super T> var4);

    public static EntityDetector.Selector ofPlayer(PlayerEntity player) {
        return EntityDetector.Selector.ofPlayers(List.of(player));
    }

    public static EntityDetector.Selector ofPlayers(final List<PlayerEntity> players) {
        return new EntityDetector.Selector(){

            public List<PlayerEntity> getPlayers(ServerWorld world, Predicate<? super PlayerEntity> predicate) {
                return players.stream().filter(predicate).toList();
            }

            @Override
            public <T extends Entity> List<T> getEntities(ServerWorld world, TypeFilter<Entity, T> typeFilter, Box box, Predicate<? super T> predicate) {
                return players.stream().map(typeFilter::downcast).filter(Objects::nonNull).filter(predicate).toList();
            }
        };
    }
}
