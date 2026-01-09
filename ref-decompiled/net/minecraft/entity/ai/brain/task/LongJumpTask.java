package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.LongJumpUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LongJumpTask extends MultiTickTask {
   protected static final int MAX_TARGET_SEARCH_TIME = 20;
   private static final int JUMP_WINDUP_TIME = 40;
   protected static final int PATHING_DISTANCE = 8;
   private static final int RUN_TIME = 200;
   private static final List RAM_RANGES = Lists.newArrayList(new Integer[]{65, 70, 75, 80});
   private final UniformIntProvider cooldownRange;
   protected final int verticalRange;
   protected final int horizontalRange;
   protected final float maxRange;
   protected List potentialTargets;
   protected Optional startPos;
   @Nullable
   protected Vec3d currentTarget;
   protected int targetSearchTime;
   protected long targetPickedTime;
   private final Function entityToSound;
   private final BiPredicate jumpToPredicate;

   public LongJumpTask(UniformIntProvider cooldownRange, int verticalRange, int horizontalRange, float maxRange, Function entityToSound) {
      this(cooldownRange, verticalRange, horizontalRange, maxRange, entityToSound, LongJumpTask::shouldJumpTo);
   }

   public static boolean shouldJumpTo(MobEntity entity, BlockPos pos) {
      World world = entity.getWorld();
      BlockPos blockPos = pos.down();
      return world.getBlockState(blockPos).isOpaqueFullCube() && entity.getPathfindingPenalty(LandPathNodeMaker.getLandNodeType(entity, pos)) == 0.0F;
   }

   public LongJumpTask(UniformIntProvider cooldownRange, int verticalRange, int horizontalRange, float maxRange, Function entityToSound, BiPredicate jumpToPredicate) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.LONG_JUMP_COOLING_DOWN, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleState.VALUE_ABSENT), 200);
      this.potentialTargets = Lists.newArrayList();
      this.startPos = Optional.empty();
      this.cooldownRange = cooldownRange;
      this.verticalRange = verticalRange;
      this.horizontalRange = horizontalRange;
      this.maxRange = maxRange;
      this.entityToSound = entityToSound;
      this.jumpToPredicate = jumpToPredicate;
   }

   protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
      boolean bl = mobEntity.isOnGround() && !mobEntity.isTouchingWater() && !mobEntity.isInLava() && !serverWorld.getBlockState(mobEntity.getBlockPos()).isOf(Blocks.HONEY_BLOCK);
      if (!bl) {
         mobEntity.getBrain().remember(MemoryModuleType.LONG_JUMP_COOLING_DOWN, (Object)(this.cooldownRange.get(serverWorld.random) / 2));
      }

      return bl;
   }

   protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
      boolean bl = this.startPos.isPresent() && ((Vec3d)this.startPos.get()).equals(mobEntity.getPos()) && this.targetSearchTime > 0 && !mobEntity.isTouchingWater() && (this.currentTarget != null || !this.potentialTargets.isEmpty());
      if (!bl && mobEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.LONG_JUMP_MID_JUMP).isEmpty()) {
         mobEntity.getBrain().remember(MemoryModuleType.LONG_JUMP_COOLING_DOWN, (Object)(this.cooldownRange.get(serverWorld.random) / 2));
         mobEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
      }

      return bl;
   }

   protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
      this.currentTarget = null;
      this.targetSearchTime = 20;
      this.startPos = Optional.of(mobEntity.getPos());
      BlockPos blockPos = mobEntity.getBlockPos();
      int i = blockPos.getX();
      int j = blockPos.getY();
      int k = blockPos.getZ();
      this.potentialTargets = (List)BlockPos.stream(i - this.horizontalRange, j - this.verticalRange, k - this.horizontalRange, i + this.horizontalRange, j + this.verticalRange, k + this.horizontalRange).filter((pos) -> {
         return !pos.equals(blockPos);
      }).map((pos) -> {
         return new Target(pos.toImmutable(), MathHelper.ceil(blockPos.getSquaredDistance(pos)));
      }).collect(Collectors.toCollection(Lists::newArrayList));
   }

   protected void keepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
      if (this.currentTarget != null) {
         if (l - this.targetPickedTime >= 40L) {
            mobEntity.setYaw(mobEntity.bodyYaw);
            mobEntity.setNoDrag(true);
            double d = this.currentTarget.length();
            double e = d + (double)mobEntity.getJumpBoostVelocityModifier();
            mobEntity.setVelocity(this.currentTarget.multiply(e / d));
            mobEntity.getBrain().remember(MemoryModuleType.LONG_JUMP_MID_JUMP, (Object)true);
            serverWorld.playSoundFromEntity((Entity)null, mobEntity, (SoundEvent)this.entityToSound.apply(mobEntity), SoundCategory.NEUTRAL, 1.0F, 1.0F);
         }
      } else {
         --this.targetSearchTime;
         this.pickTarget(serverWorld, mobEntity, l);
      }

   }

   protected void pickTarget(ServerWorld world, MobEntity entity, long time) {
      while(true) {
         if (!this.potentialTargets.isEmpty()) {
            Optional optional = this.removeRandomTarget(world);
            if (optional.isEmpty()) {
               continue;
            }

            Target target = (Target)optional.get();
            BlockPos blockPos = target.pos();
            if (!this.canJumpTo(world, entity, blockPos)) {
               continue;
            }

            Vec3d vec3d = Vec3d.ofCenter(blockPos);
            Vec3d vec3d2 = this.getJumpingVelocity(entity, vec3d);
            if (vec3d2 == null) {
               continue;
            }

            entity.getBrain().remember(MemoryModuleType.LOOK_TARGET, (Object)(new BlockPosLookTarget(blockPos)));
            EntityNavigation entityNavigation = entity.getNavigation();
            Path path = entityNavigation.findPathTo(blockPos, 0, 8);
            if (path != null && path.reachesTarget()) {
               continue;
            }

            this.currentTarget = vec3d2;
            this.targetPickedTime = time;
            return;
         }

         return;
      }
   }

   protected Optional removeRandomTarget(ServerWorld world) {
      Optional optional = Weighting.getRandom(world.random, this.potentialTargets, Target::weight);
      List var10001 = this.potentialTargets;
      Objects.requireNonNull(var10001);
      optional.ifPresent(var10001::remove);
      return optional;
   }

   private boolean canJumpTo(ServerWorld world, MobEntity entity, BlockPos pos) {
      BlockPos blockPos = entity.getBlockPos();
      int i = blockPos.getX();
      int j = blockPos.getZ();
      return i == pos.getX() && j == pos.getZ() ? false : this.jumpToPredicate.test(entity, pos);
   }

   @Nullable
   protected Vec3d getJumpingVelocity(MobEntity entity, Vec3d targetPos) {
      List list = Lists.newArrayList(RAM_RANGES);
      Collections.shuffle(list);
      float f = (float)(entity.getAttributeValue(EntityAttributes.JUMP_STRENGTH) * (double)this.maxRange);
      Iterator var5 = list.iterator();

      Optional optional;
      do {
         if (!var5.hasNext()) {
            return null;
         }

         int i = (Integer)var5.next();
         optional = LongJumpUtil.getJumpingVelocity(entity, targetPos, f, i, true);
      } while(!optional.isPresent());

      return (Vec3d)optional.get();
   }

   // $FF: synthetic method
   protected boolean shouldKeepRunning(final ServerWorld world, final LivingEntity entity, final long time) {
      return this.shouldKeepRunning(world, (MobEntity)entity, time);
   }

   // $FF: synthetic method
   protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
      this.run(world, (MobEntity)entity, time);
   }

   public static record Target(BlockPos pos, int weight) {
      public Target(BlockPos pos, int weight) {
         this.pos = pos;
         this.weight = weight;
      }

      public BlockPos pos() {
         return this.pos;
      }

      public int weight() {
         return this.weight;
      }
   }
}
