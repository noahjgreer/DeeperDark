package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.BreedTask;
import net.minecraft.entity.ai.brain.task.FleeTask;
import net.minecraft.entity.ai.brain.task.GoToLookTargetTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookAtMobWithIntervalTask;
import net.minecraft.entity.ai.brain.task.MoveToTargetTask;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.SingleTickTask;
import net.minecraft.entity.ai.brain.task.StayAboveWaterTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.ai.brain.task.TemptTask;
import net.minecraft.entity.ai.brain.task.TickCooldownTask;
import net.minecraft.entity.ai.brain.task.UpdateLookControlTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WalkTowardsEntityTask;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class ArmadilloBrain {
   private static final float field_47797 = 2.0F;
   private static final float field_47798 = 1.0F;
   private static final float field_47799 = 1.25F;
   private static final float field_47800 = 1.25F;
   private static final float field_47801 = 1.0F;
   private static final double field_48338 = 2.0;
   private static final double field_48339 = 1.0;
   private static final UniformIntProvider WALK_TOWARDS_CLOSEST_ADULT_RANGE = UniformIntProvider.create(5, 16);
   private static final ImmutableList SENSOR_TYPES;
   private static final ImmutableList MEMORY_MODULE_TYPES;
   private static final SingleTickTask UNROLL_TASK;

   public static Brain.Profile createBrainProfile() {
      return Brain.createProfile(MEMORY_MODULE_TYPES, SENSOR_TYPES);
   }

   protected static Brain create(Brain brain) {
      addCoreActivities(brain);
      addIdleActivities(brain);
      addPanicActivities(brain);
      brain.setCoreActivities(Set.of(Activity.CORE));
      brain.setDefaultActivity(Activity.IDLE);
      brain.resetPossibleActivities();
      return brain;
   }

   private static void addCoreActivities(Brain brain) {
      brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new StayAboveWaterTask(0.8F), new UnrollAndFleeTask(2.0F), new UpdateLookControlTask(45, 90), new MoveToTargetTask() {
         protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
            if (mobEntity instanceof ArmadilloEntity armadilloEntity) {
               if (armadilloEntity.isNotIdle()) {
                  return false;
               }
            }

            return super.shouldRun(serverWorld, mobEntity);
         }
      }, new TickCooldownTask(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS), new TickCooldownTask(MemoryModuleType.GAZE_COOLDOWN_TICKS), UNROLL_TASK));
   }

   private static void addIdleActivities(Brain brain) {
      brain.setTaskList(Activity.IDLE, ImmutableList.of(Pair.of(0, LookAtMobWithIntervalTask.follow(EntityType.PLAYER, 6.0F, UniformIntProvider.create(30, 60))), Pair.of(1, new BreedTask(EntityType.ARMADILLO, 1.0F, 1)), Pair.of(2, new RandomTask(ImmutableList.of(Pair.of(new TemptTask((armadillo) -> {
         return 1.25F;
      }, (armadillo) -> {
         return armadillo.isBaby() ? 1.0 : 2.0;
      }), 1), Pair.of(WalkTowardsEntityTask.createNearestVisibleAdult(WALK_TOWARDS_CLOSEST_ADULT_RANGE, 1.25F), 1)))), Pair.of(3, new LookAroundTask(UniformIntProvider.create(150, 250), 30.0F, 0.0F, 0.0F)), Pair.of(4, new RandomTask(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT), ImmutableList.of(Pair.of(StrollTask.create(1.0F), 1), Pair.of(GoToLookTargetTask.create(1.0F, 3), 1), Pair.of(new WaitTask(30, 60), 1))))));
   }

   private static void addPanicActivities(Brain brain) {
      brain.setTaskList(Activity.PANIC, ImmutableList.of(Pair.of(0, new RollUpTask())), Set.of(Pair.of(MemoryModuleType.DANGER_DETECTED_RECENTLY, MemoryModuleState.VALUE_PRESENT), Pair.of(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT)));
   }

   public static void updateActivities(ArmadilloEntity armadillo) {
      armadillo.getBrain().resetPossibleActivities((List)ImmutableList.of(Activity.PANIC, Activity.IDLE));
   }

   public static Predicate getTemptItemPredicate() {
      return (stack) -> {
         return stack.isIn(ItemTags.ARMADILLO_FOOD);
      };
   }

   static {
      SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.ARMADILLO_TEMPTATIONS, SensorType.NEAREST_ADULT, SensorType.ARMADILLO_SCARE_DETECTED);
      MEMORY_MODULE_TYPES = ImmutableList.of(MemoryModuleType.IS_PANICKING, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.GAZE_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, new MemoryModuleType[]{MemoryModuleType.BREED_TARGET, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.DANGER_DETECTED_RECENTLY});
      UNROLL_TASK = TaskTriggerer.task((context) -> {
         return context.group(context.queryMemoryAbsent(MemoryModuleType.DANGER_DETECTED_RECENTLY)).apply(context, (memoryQueryResult) -> {
            return (serverWorld, armadillo, l) -> {
               if (armadillo.isNotIdle()) {
                  armadillo.unroll();
                  return true;
               } else {
                  return false;
               }
            };
         });
      });
   }

   public static class UnrollAndFleeTask extends FleeTask {
      public UnrollAndFleeTask(float f) {
         super(f, (entity) -> {
            return DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES;
         });
      }

      protected void run(ServerWorld serverWorld, ArmadilloEntity armadilloEntity, long l) {
         armadilloEntity.unroll();
         super.run(serverWorld, (PathAwareEntity)armadilloEntity, l);
      }

      // $FF: synthetic method
      protected void run(final ServerWorld serverWorld, final PathAwareEntity pathAwareEntity, final long l) {
         this.run(serverWorld, (ArmadilloEntity)pathAwareEntity, l);
      }

      // $FF: synthetic method
      protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
         this.run(world, (ArmadilloEntity)entity, time);
      }
   }

   public static class RollUpTask extends MultiTickTask {
      static final int RUN_TIME_IN_TICKS;
      static final int field_49088 = 5;
      static final int field_49089 = 75;
      int ticksUntilPeek = 0;
      boolean considerPeeking;

      public RollUpTask() {
         super(Map.of(), RUN_TIME_IN_TICKS);
      }

      protected void keepRunning(ServerWorld serverWorld, ArmadilloEntity armadilloEntity, long l) {
         super.keepRunning(serverWorld, armadilloEntity, l);
         if (this.ticksUntilPeek > 0) {
            --this.ticksUntilPeek;
         }

         if (armadilloEntity.shouldSwitchToScaredState()) {
            armadilloEntity.setState(ArmadilloEntity.State.SCARED);
            if (armadilloEntity.isOnGround()) {
               armadilloEntity.playSoundIfNotSilent(SoundEvents.ENTITY_ARMADILLO_LAND);
            }

         } else {
            ArmadilloEntity.State state = armadilloEntity.getState();
            long m = armadilloEntity.getBrain().getMemoryExpiry(MemoryModuleType.DANGER_DETECTED_RECENTLY);
            boolean bl = m > 75L;
            if (bl != this.considerPeeking) {
               this.ticksUntilPeek = this.calculateTicksUntilPeek(armadilloEntity);
            }

            this.considerPeeking = bl;
            if (state == ArmadilloEntity.State.SCARED) {
               if (this.ticksUntilPeek == 0 && armadilloEntity.isOnGround() && bl) {
                  serverWorld.sendEntityStatus(armadilloEntity, (byte)64);
                  this.ticksUntilPeek = this.calculateTicksUntilPeek(armadilloEntity);
               }

               if (m < (long)ArmadilloEntity.State.UNROLLING.getLengthInTicks()) {
                  armadilloEntity.playSoundIfNotSilent(SoundEvents.ENTITY_ARMADILLO_UNROLL_START);
                  armadilloEntity.setState(ArmadilloEntity.State.UNROLLING);
               }
            } else if (state == ArmadilloEntity.State.UNROLLING && m > (long)ArmadilloEntity.State.UNROLLING.getLengthInTicks()) {
               armadilloEntity.setState(ArmadilloEntity.State.SCARED);
            }

         }
      }

      private int calculateTicksUntilPeek(ArmadilloEntity entity) {
         return ArmadilloEntity.State.SCARED.getLengthInTicks() + entity.getRandom().nextBetween(100, 400);
      }

      protected boolean shouldRun(ServerWorld serverWorld, ArmadilloEntity armadilloEntity) {
         return armadilloEntity.isOnGround();
      }

      protected boolean shouldKeepRunning(ServerWorld serverWorld, ArmadilloEntity armadilloEntity, long l) {
         return armadilloEntity.getState().shouldRunRollUpTask();
      }

      protected void run(ServerWorld serverWorld, ArmadilloEntity armadilloEntity, long l) {
         armadilloEntity.startRolling();
      }

      protected void finishRunning(ServerWorld serverWorld, ArmadilloEntity armadilloEntity, long l) {
         if (!armadilloEntity.canRollUp()) {
            armadilloEntity.unroll();
         }

      }

      // $FF: synthetic method
      protected boolean shouldKeepRunning(final ServerWorld world, final LivingEntity entity, final long time) {
         return this.shouldKeepRunning(world, (ArmadilloEntity)entity, time);
      }

      // $FF: synthetic method
      protected void finishRunning(final ServerWorld world, final LivingEntity entity, final long time) {
         this.finishRunning(world, (ArmadilloEntity)entity, time);
      }

      // $FF: synthetic method
      protected void keepRunning(final ServerWorld world, final LivingEntity entity, final long time) {
         this.keepRunning(world, (ArmadilloEntity)entity, time);
      }

      // $FF: synthetic method
      protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
         this.run(world, (ArmadilloEntity)entity, time);
      }

      static {
         RUN_TIME_IN_TICKS = 5 * TimeHelper.MINUTE_IN_SECONDS * 20;
      }
   }
}
