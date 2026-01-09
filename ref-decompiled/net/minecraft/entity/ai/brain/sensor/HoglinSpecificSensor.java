package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class HoglinSpecificSensor extends Sensor {
   public Set getOutputMemoryModules() {
      return ImmutableSet.of(MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_REPELLENT, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, new MemoryModuleType[0]);
   }

   protected void sense(ServerWorld serverWorld, HoglinEntity hoglinEntity) {
      Brain brain = hoglinEntity.getBrain();
      brain.remember(MemoryModuleType.NEAREST_REPELLENT, this.findNearestWarpedFungus(serverWorld, hoglinEntity));
      Optional optional = Optional.empty();
      int i = 0;
      List list = Lists.newArrayList();
      LivingTargetCache livingTargetCache = (LivingTargetCache)brain.getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS).orElse(LivingTargetCache.empty());
      Iterator var8 = livingTargetCache.iterate((livingEntityx) -> {
         return !livingEntityx.isBaby() && (livingEntityx instanceof PiglinEntity || livingEntityx instanceof HoglinEntity);
      }).iterator();

      while(var8.hasNext()) {
         LivingEntity livingEntity = (LivingEntity)var8.next();
         if (livingEntity instanceof PiglinEntity piglinEntity) {
            ++i;
            if (optional.isEmpty()) {
               optional = Optional.of(piglinEntity);
            }
         }

         if (livingEntity instanceof HoglinEntity hoglinEntity2) {
            list.add(hoglinEntity2);
         }
      }

      brain.remember(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, optional);
      brain.remember(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, (Object)list);
      brain.remember(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, (Object)i);
      brain.remember(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, (Object)list.size());
   }

   private Optional findNearestWarpedFungus(ServerWorld world, HoglinEntity hoglin) {
      return BlockPos.findClosest(hoglin.getBlockPos(), 8, 4, (pos) -> {
         return world.getBlockState(pos).isIn(BlockTags.HOGLIN_REPELLENTS);
      });
   }
}
