package net.minecraft.entity.mob;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.BreezeJumpTask;
import net.minecraft.entity.ai.brain.task.BreezeShootIfStuckTask;
import net.minecraft.entity.ai.brain.task.BreezeShootTask;
import net.minecraft.entity.ai.brain.task.BreezeSlideTowardsTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.MoveToTargetTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.StayAboveWaterTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.UpdateLookControlTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Unit;

public class BreezeBrain {
   public static final float field_47283 = 0.6F;
   public static final float field_47284 = 4.0F;
   public static final float field_47285 = 8.0F;
   public static final float field_47286 = 24.0F;
   static final List SENSORS;
   static final List MEMORY_MODULES;
   private static final int TIME_BEFORE_FORGETTING_TARGET = 100;

   protected static Brain create(BreezeEntity breeze, Brain brain) {
      addCoreTasks(brain);
      addIdleTasks(brain);
      addFightTasks(breeze, brain);
      brain.setCoreActivities(Set.of(Activity.CORE));
      brain.setDefaultActivity(Activity.FIGHT);
      brain.resetPossibleActivities();
      return brain;
   }

   private static void addCoreTasks(Brain brain) {
      brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new StayAboveWaterTask(0.8F), new UpdateLookControlTask(45, 90)));
   }

   private static void addIdleTasks(Brain brain) {
      brain.setTaskList(Activity.IDLE, ImmutableList.of(Pair.of(0, UpdateAttackTargetTask.create((world, breeze) -> {
         return breeze.getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEAREST_ATTACKABLE);
      })), Pair.of(1, UpdateAttackTargetTask.create((world, breeze) -> {
         return breeze.getHurtBy();
      })), Pair.of(2, new SlideAroundTask(20, 40)), Pair.of(3, new RandomTask(ImmutableList.of(Pair.of(new WaitTask(20, 100), 1), Pair.of(StrollTask.create(0.6F), 2))))));
   }

   private static void addFightTasks(BreezeEntity breeze, Brain brain) {
      Activity var10001 = Activity.FIGHT;
      Integer var10002 = 0;
      BiPredicate var10003 = Sensor.hasTargetBeenAttackableRecently(breeze, 100).negate();
      Objects.requireNonNull(var10003);
      brain.setTaskList(var10001, ImmutableList.of(Pair.of(var10002, ForgetAttackTargetTask.create(var10003::test)), Pair.of(1, new BreezeShootTask()), Pair.of(2, new BreezeJumpTask()), Pair.of(3, new BreezeShootIfStuckTask()), Pair.of(4, new BreezeSlideTowardsTargetTask())), ImmutableSet.of(Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT), Pair.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT)));
   }

   static void updateActivities(BreezeEntity breeze) {
      breeze.getBrain().resetPossibleActivities((List)ImmutableList.of(Activity.FIGHT, Activity.IDLE));
   }

   static {
      SENSORS = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.NEAREST_PLAYERS, SensorType.BREEZE_ATTACK_ENTITY_SENSOR);
      MEMORY_MODULES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.BREEZE_JUMP_COOLDOWN, MemoryModuleType.BREEZE_JUMP_INHALING, MemoryModuleType.BREEZE_SHOOT, MemoryModuleType.BREEZE_SHOOT_CHARGING, MemoryModuleType.BREEZE_SHOOT_RECOVER, MemoryModuleType.BREEZE_SHOOT_COOLDOWN, new MemoryModuleType[]{MemoryModuleType.BREEZE_JUMP_TARGET, MemoryModuleType.BREEZE_LEAVING_WATER, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.PATH});
   }

   public static class SlideAroundTask extends MoveToTargetTask {
      @VisibleForTesting
      public SlideAroundTask(int i, int j) {
         super(i, j);
      }

      protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
         super.run(serverWorld, mobEntity, l);
         mobEntity.playSoundIfNotSilent(SoundEvents.ENTITY_BREEZE_SLIDE);
         mobEntity.setPose(EntityPose.SLIDING);
      }

      protected void finishRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
         super.finishRunning(serverWorld, mobEntity, l);
         mobEntity.setPose(EntityPose.STANDING);
         if (mobEntity.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET)) {
            mobEntity.getBrain().remember(MemoryModuleType.BREEZE_SHOOT, Unit.INSTANCE, 60L);
         }

      }

      // $FF: synthetic method
      protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
         this.run(world, (MobEntity)entity, time);
      }
   }
}
