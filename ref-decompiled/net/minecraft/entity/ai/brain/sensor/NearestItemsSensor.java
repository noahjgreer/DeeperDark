package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class NearestItemsSensor extends Sensor {
   private static final long HORIZONTAL_RANGE = 32L;
   private static final long VERTICAL_RANGE = 16L;
   public static final int MAX_RANGE = 32;

   public Set getOutputMemoryModules() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
   }

   protected void sense(ServerWorld serverWorld, MobEntity mobEntity) {
      Brain brain = mobEntity.getBrain();
      List list = serverWorld.getEntitiesByClass(ItemEntity.class, mobEntity.getBoundingBox().expand(32.0, 16.0, 32.0), (itemEntity) -> {
         return true;
      });
      Objects.requireNonNull(mobEntity);
      list.sort(Comparator.comparingDouble(mobEntity::squaredDistanceTo));
      Stream var10000 = list.stream().filter((itemEntity) -> {
         return mobEntity.canGather(serverWorld, itemEntity.getStack());
      }).filter((itemEntityx) -> {
         return itemEntityx.isInRange(mobEntity, 32.0);
      });
      Objects.requireNonNull(mobEntity);
      Optional optional = var10000.filter(mobEntity::canSee).findFirst();
      brain.remember(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, optional);
   }
}
