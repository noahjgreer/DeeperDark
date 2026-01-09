package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

public interface ServerWorldAccess extends WorldAccess {
   ServerWorld toServerWorld();

   default void spawnEntityAndPassengers(Entity entity) {
      entity.streamSelfAndPassengers().forEach(this::spawnEntity);
   }
}
