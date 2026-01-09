package net.minecraft.entity.ai.brain.sensor;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class ArmadilloScareDetectedSensor extends Sensor {
   private final BiPredicate threateningEntityPredicate;
   private final Predicate canRollUpPredicate;
   private final MemoryModuleType memoryModuleType;
   private final int expiry;

   public ArmadilloScareDetectedSensor(int senseInterval, BiPredicate threateningEntityPredicate, Predicate canRollUpPredicate, MemoryModuleType memoryModuleType, int expiry) {
      super(senseInterval);
      this.threateningEntityPredicate = threateningEntityPredicate;
      this.canRollUpPredicate = canRollUpPredicate;
      this.memoryModuleType = memoryModuleType;
      this.expiry = expiry;
   }

   protected void sense(ServerWorld world, LivingEntity entity) {
      if (!this.canRollUpPredicate.test(entity)) {
         this.clear(entity);
      } else {
         this.tryDetectThreat(entity);
      }

   }

   public Set getOutputMemoryModules() {
      return Set.of(MemoryModuleType.MOBS);
   }

   public void tryDetectThreat(LivingEntity entity) {
      Optional optional = entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.MOBS);
      if (!optional.isEmpty()) {
         boolean bl = ((List)optional.get()).stream().anyMatch((threat) -> {
            return this.threateningEntityPredicate.test(entity, threat);
         });
         if (bl) {
            this.onDetected(entity);
         }

      }
   }

   public void onDetected(LivingEntity entity) {
      entity.getBrain().remember(this.memoryModuleType, true, (long)this.expiry);
   }

   public void clear(LivingEntity entity) {
      entity.getBrain().forget(this.memoryModuleType);
   }
}
