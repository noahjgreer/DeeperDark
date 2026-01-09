package net.minecraft.entity.ai.brain.sensor;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.registry.tag.EntityTypeTags;

public class NearestFollowableFriendlyMobSensor extends NearestVisibleAdultSensor {
   protected void find(LivingEntity entity, LivingTargetCache targetCache) {
      Optional var10000 = targetCache.findFirst((potentialFriend) -> {
         return potentialFriend.getType().isIn(EntityTypeTags.FOLLOWABLE_FRIENDLY_MOBS) && !potentialFriend.isBaby();
      });
      Objects.requireNonNull(LivingEntity.class);
      Optional optional = var10000.map(LivingEntity.class::cast);
      entity.getBrain().remember(MemoryModuleType.NEAREST_VISIBLE_ADULT, optional);
   }
}
