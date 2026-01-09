package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class TemptTask extends MultiTickTask {
   public static final int TEMPTATION_COOLDOWN_TICKS = 100;
   public static final double DEFAULT_STOP_DISTANCE = 2.5;
   public static final double LARGE_ENTITY_STOP_DISTANCE = 3.5;
   private final Function speed;
   private final Function stopDistanceGetter;
   private final boolean useEyeHeight;

   public TemptTask(Function speed) {
      this(speed, (entity) -> {
         return 2.5;
      });
   }

   public TemptTask(Function speed, Function stopDistanceGetter) {
      this(speed, stopDistanceGetter, false);
   }

   public TemptTask(Function speed, Function stopDistanceGetter, boolean useEyeHeight) {
      super((Map)Util.make(() -> {
         ImmutableMap.Builder builder = ImmutableMap.builder();
         builder.put(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED);
         builder.put(MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED);
         builder.put(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleState.VALUE_ABSENT);
         builder.put(MemoryModuleType.IS_TEMPTED, MemoryModuleState.VALUE_ABSENT);
         builder.put(MemoryModuleType.TEMPTING_PLAYER, MemoryModuleState.VALUE_PRESENT);
         builder.put(MemoryModuleType.BREED_TARGET, MemoryModuleState.VALUE_ABSENT);
         builder.put(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT);
         return builder.build();
      }));
      this.speed = speed;
      this.stopDistanceGetter = stopDistanceGetter;
      this.useEyeHeight = useEyeHeight;
   }

   protected float getSpeed(PathAwareEntity entity) {
      return (Float)this.speed.apply(entity);
   }

   private Optional getTemptingPlayer(PathAwareEntity entity) {
      return entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.TEMPTING_PLAYER);
   }

   protected boolean isTimeLimitExceeded(long time) {
      return false;
   }

   protected boolean shouldKeepRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
      return this.getTemptingPlayer(pathAwareEntity).isPresent() && !pathAwareEntity.getBrain().hasMemoryModule(MemoryModuleType.BREED_TARGET) && !pathAwareEntity.getBrain().hasMemoryModule(MemoryModuleType.IS_PANICKING);
   }

   protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
      pathAwareEntity.getBrain().remember(MemoryModuleType.IS_TEMPTED, (Object)true);
   }

   protected void finishRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
      Brain brain = pathAwareEntity.getBrain();
      brain.remember(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, (int)100);
      brain.forget(MemoryModuleType.IS_TEMPTED);
      brain.forget(MemoryModuleType.WALK_TARGET);
      brain.forget(MemoryModuleType.LOOK_TARGET);
   }

   protected void keepRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
      PlayerEntity playerEntity = (PlayerEntity)this.getTemptingPlayer(pathAwareEntity).get();
      Brain brain = pathAwareEntity.getBrain();
      brain.remember(MemoryModuleType.LOOK_TARGET, (Object)(new EntityLookTarget(playerEntity, true)));
      double d = (Double)this.stopDistanceGetter.apply(pathAwareEntity);
      if (pathAwareEntity.squaredDistanceTo(playerEntity) < MathHelper.square(d)) {
         brain.forget(MemoryModuleType.WALK_TARGET);
      } else {
         brain.remember(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(new EntityLookTarget(playerEntity, this.useEyeHeight, this.useEyeHeight), this.getSpeed(pathAwareEntity), 2)));
      }

   }

   // $FF: synthetic method
   protected void finishRunning(final ServerWorld world, final LivingEntity entity, final long time) {
      this.finishRunning(world, (PathAwareEntity)entity, time);
   }

   // $FF: synthetic method
   protected void keepRunning(final ServerWorld world, final LivingEntity entity, final long time) {
      this.keepRunning(world, (PathAwareEntity)entity, time);
   }

   // $FF: synthetic method
   protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
      this.run(world, (PathAwareEntity)entity, time);
   }
}
