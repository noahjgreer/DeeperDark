package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

public class NearestLivingEntitiesSensor extends Sensor {
   protected void sense(ServerWorld world, LivingEntity entity) {
      double d = entity.getAttributeValue(EntityAttributes.FOLLOW_RANGE);
      Box box = entity.getBoundingBox().expand(d, d, d);
      List list = world.getEntitiesByClass(LivingEntity.class, box, (e) -> {
         return e != entity && e.isAlive();
      });
      Objects.requireNonNull(entity);
      list.sort(Comparator.comparingDouble(entity::squaredDistanceTo));
      Brain brain = entity.getBrain();
      brain.remember(MemoryModuleType.MOBS, (Object)list);
      brain.remember(MemoryModuleType.VISIBLE_MOBS, (Object)(new LivingTargetCache(world, entity, list)));
   }

   public Set getOutputMemoryModules() {
      return ImmutableSet.of(MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS);
   }
}
