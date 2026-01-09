package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;

public class WardenAttackablesSensor extends NearestLivingEntitiesSensor {
   public Set getOutputMemoryModules() {
      return ImmutableSet.copyOf(Iterables.concat(super.getOutputMemoryModules(), List.of(MemoryModuleType.NEAREST_ATTACKABLE)));
   }

   protected void sense(ServerWorld serverWorld, WardenEntity wardenEntity) {
      super.sense(serverWorld, wardenEntity);
      findNearestTarget(wardenEntity, (entityx) -> {
         return entityx.getType() == EntityType.PLAYER;
      }).or(() -> {
         return findNearestTarget(wardenEntity, (entityx) -> {
            return entityx.getType() != EntityType.PLAYER;
         });
      }).ifPresentOrElse((entityx) -> {
         wardenEntity.getBrain().remember(MemoryModuleType.NEAREST_ATTACKABLE, (Object)entityx);
      }, () -> {
         wardenEntity.getBrain().forget(MemoryModuleType.NEAREST_ATTACKABLE);
      });
   }

   private static Optional findNearestTarget(WardenEntity warden, Predicate targetPredicate) {
      Stream var10000 = warden.getBrain().getOptionalRegisteredMemory(MemoryModuleType.MOBS).stream().flatMap(Collection::stream);
      Objects.requireNonNull(warden);
      return var10000.filter(warden::isValidTarget).filter(targetPredicate).findFirst();
   }
}
