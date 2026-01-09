package net.minecraft.entity.passive;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.EntityAttachments;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.AmphibiousSwimNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class TurtleEntity extends AnimalEntity {
   private static final TrackedData HAS_EGG;
   private static final TrackedData DIGGING_SAND;
   private static final float BABY_SCALE = 0.3F;
   private static final EntityDimensions BABY_BASE_DIMENSIONS;
   private static final boolean DEFAULT_HAS_EGG = false;
   int sandDiggingCounter;
   public static final TargetPredicate.EntityPredicate BABY_TURTLE_ON_LAND_FILTER;
   BlockPos homePos;
   @Nullable
   BlockPos travelPos;
   boolean landBound;

   public TurtleEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.homePos = BlockPos.ORIGIN;
      this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
      this.setPathfindingPenalty(PathNodeType.DOOR_IRON_CLOSED, -1.0F);
      this.setPathfindingPenalty(PathNodeType.DOOR_WOOD_CLOSED, -1.0F);
      this.setPathfindingPenalty(PathNodeType.DOOR_OPEN, -1.0F);
      this.moveControl = new TurtleMoveControl(this);
   }

   public void setHomePos(BlockPos pos) {
      this.homePos = pos;
   }

   public boolean hasEgg() {
      return (Boolean)this.dataTracker.get(HAS_EGG);
   }

   void setHasEgg(boolean hasEgg) {
      this.dataTracker.set(HAS_EGG, hasEgg);
   }

   public boolean isDiggingSand() {
      return (Boolean)this.dataTracker.get(DIGGING_SAND);
   }

   void setDiggingSand(boolean diggingSand) {
      this.sandDiggingCounter = diggingSand ? 1 : 0;
      this.dataTracker.set(DIGGING_SAND, diggingSand);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(HAS_EGG, false);
      builder.add(DIGGING_SAND, false);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.put("home_pos", BlockPos.CODEC, this.homePos);
      view.putBoolean("has_egg", this.hasEgg());
   }

   protected void readCustomData(ReadView view) {
      this.setHomePos((BlockPos)view.read("home_pos", BlockPos.CODEC).orElse(this.getBlockPos()));
      super.readCustomData(view);
      this.setHasEgg(view.getBoolean("has_egg", false));
   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      this.setHomePos(this.getBlockPos());
      return super.initialize(world, difficulty, spawnReason, entityData);
   }

   public static boolean canSpawn(EntityType type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
      return pos.getY() < world.getSeaLevel() + 4 && TurtleEggBlock.isSandBelow(world, pos) && isLightLevelValidForNaturalSpawn(world, pos);
   }

   protected void initGoals() {
      this.goalSelector.add(0, new TurtleEscapeDangerGoal(this, 1.2));
      this.goalSelector.add(1, new MateGoal(this, 1.0));
      this.goalSelector.add(1, new LayEggGoal(this, 1.0));
      this.goalSelector.add(2, new TemptGoal(this, 1.1, (stack) -> {
         return stack.isIn(ItemTags.TURTLE_FOOD);
      }, false));
      this.goalSelector.add(3, new WanderInWaterGoal(this, 1.0));
      this.goalSelector.add(4, new GoHomeGoal(this, 1.0));
      this.goalSelector.add(7, new TravelGoal(this, 1.0));
      this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.add(9, new WanderOnLandGoal(this, 1.0, 100));
   }

   public static DefaultAttributeContainer.Builder createTurtleAttributes() {
      return AnimalEntity.createAnimalAttributes().add(EntityAttributes.MAX_HEALTH, 30.0).add(EntityAttributes.MOVEMENT_SPEED, 0.25).add(EntityAttributes.STEP_HEIGHT, 1.0);
   }

   public boolean isPushedByFluids() {
      return false;
   }

   public int getMinAmbientSoundDelay() {
      return 200;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return !this.isTouchingWater() && this.isOnGround() && !this.isBaby() ? SoundEvents.ENTITY_TURTLE_AMBIENT_LAND : super.getAmbientSound();
   }

   protected void playSwimSound(float volume) {
      super.playSwimSound(volume * 1.5F);
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_TURTLE_SWIM;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource source) {
      return this.isBaby() ? SoundEvents.ENTITY_TURTLE_HURT_BABY : SoundEvents.ENTITY_TURTLE_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return this.isBaby() ? SoundEvents.ENTITY_TURTLE_DEATH_BABY : SoundEvents.ENTITY_TURTLE_DEATH;
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      SoundEvent soundEvent = this.isBaby() ? SoundEvents.ENTITY_TURTLE_SHAMBLE_BABY : SoundEvents.ENTITY_TURTLE_SHAMBLE;
      this.playSound(soundEvent, 0.15F, 1.0F);
   }

   public boolean canEat() {
      return super.canEat() && !this.hasEgg();
   }

   protected float calculateNextStepSoundDistance() {
      return this.distanceTraveled + 0.15F;
   }

   public float getScaleFactor() {
      return this.isBaby() ? 0.3F : 1.0F;
   }

   protected EntityNavigation createNavigation(World world) {
      return new TurtleSwimNavigation(this, world);
   }

   @Nullable
   public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
      return (PassiveEntity)EntityType.TURTLE.create(world, SpawnReason.BREEDING);
   }

   public boolean isBreedingItem(ItemStack stack) {
      return stack.isIn(ItemTags.TURTLE_FOOD);
   }

   public float getPathfindingFavor(BlockPos pos, WorldView world) {
      if (!this.landBound && world.getFluidState(pos).isIn(FluidTags.WATER)) {
         return 10.0F;
      } else {
         return TurtleEggBlock.isSandBelow(world, pos) ? 10.0F : world.getPhototaxisFavor(pos);
      }
   }

   public void tickMovement() {
      super.tickMovement();
      if (this.isAlive() && this.isDiggingSand() && this.sandDiggingCounter >= 1 && this.sandDiggingCounter % 5 == 0) {
         BlockPos blockPos = this.getBlockPos();
         if (TurtleEggBlock.isSandBelow(this.getWorld(), blockPos)) {
            this.getWorld().syncWorldEvent(2001, blockPos, Block.getRawIdFromState(this.getWorld().getBlockState(blockPos.down())));
            this.emitGameEvent(GameEvent.ENTITY_ACTION);
         }
      }

   }

   protected void onGrowUp() {
      super.onGrowUp();
      if (!this.isBaby()) {
         World var2 = this.getWorld();
         if (var2 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var2;
            if (serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
               this.dropItem(serverWorld, Items.TURTLE_SCUTE, 1);
            }
         }
      }

   }

   public void travel(Vec3d movementInput) {
      if (this.isTouchingWater()) {
         this.updateVelocity(0.1F, movementInput);
         this.move(MovementType.SELF, this.getVelocity());
         this.setVelocity(this.getVelocity().multiply(0.9));
         if (this.getTarget() == null && (!this.landBound || !this.homePos.isWithinDistance(this.getPos(), 20.0))) {
            this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
         }
      } else {
         super.travel(movementInput);
      }

   }

   public boolean canBeLeashed() {
      return false;
   }

   public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
      this.damage(world, this.getDamageSources().lightningBolt(), Float.MAX_VALUE);
   }

   public EntityDimensions getBaseDimensions(EntityPose pose) {
      return this.isBaby() ? BABY_BASE_DIMENSIONS : super.getBaseDimensions(pose);
   }

   static {
      HAS_EGG = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
      DIGGING_SAND = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
      BABY_BASE_DIMENSIONS = EntityType.TURTLE.getDimensions().withAttachments(EntityAttachments.builder().add(EntityAttachmentType.PASSENGER, 0.0F, EntityType.TURTLE.getHeight(), -0.25F)).scaled(0.3F);
      BABY_TURTLE_ON_LAND_FILTER = (entity, world) -> {
         return entity.isBaby() && !entity.isTouchingWater();
      };
   }

   private static class TurtleMoveControl extends MoveControl {
      private final TurtleEntity turtle;

      TurtleMoveControl(TurtleEntity turtle) {
         super(turtle);
         this.turtle = turtle;
      }

      private void updateVelocity() {
         if (this.turtle.isTouchingWater()) {
            this.turtle.setVelocity(this.turtle.getVelocity().add(0.0, 0.005, 0.0));
            if (!this.turtle.homePos.isWithinDistance(this.turtle.getPos(), 16.0)) {
               this.turtle.setMovementSpeed(Math.max(this.turtle.getMovementSpeed() / 2.0F, 0.08F));
            }

            if (this.turtle.isBaby()) {
               this.turtle.setMovementSpeed(Math.max(this.turtle.getMovementSpeed() / 3.0F, 0.06F));
            }
         } else if (this.turtle.isOnGround()) {
            this.turtle.setMovementSpeed(Math.max(this.turtle.getMovementSpeed() / 2.0F, 0.06F));
         }

      }

      public void tick() {
         this.updateVelocity();
         if (this.state == MoveControl.State.MOVE_TO && !this.turtle.getNavigation().isIdle()) {
            double d = this.targetX - this.turtle.getX();
            double e = this.targetY - this.turtle.getY();
            double f = this.targetZ - this.turtle.getZ();
            double g = Math.sqrt(d * d + e * e + f * f);
            if (g < 9.999999747378752E-6) {
               this.entity.setMovementSpeed(0.0F);
            } else {
               e /= g;
               float h = (float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0F;
               this.turtle.setYaw(this.wrapDegrees(this.turtle.getYaw(), h, 90.0F));
               this.turtle.bodyYaw = this.turtle.getYaw();
               float i = (float)(this.speed * this.turtle.getAttributeValue(EntityAttributes.MOVEMENT_SPEED));
               this.turtle.setMovementSpeed(MathHelper.lerp(0.125F, this.turtle.getMovementSpeed(), i));
               this.turtle.setVelocity(this.turtle.getVelocity().add(0.0, (double)this.turtle.getMovementSpeed() * e * 0.1, 0.0));
            }
         } else {
            this.turtle.setMovementSpeed(0.0F);
         }
      }
   }

   private static class TurtleEscapeDangerGoal extends EscapeDangerGoal {
      TurtleEscapeDangerGoal(TurtleEntity turtle, double speed) {
         super(turtle, speed);
      }

      public boolean canStart() {
         if (!this.isInDanger()) {
            return false;
         } else {
            BlockPos blockPos = this.locateClosestWater(this.mob.getWorld(), this.mob, 7);
            if (blockPos != null) {
               this.targetX = (double)blockPos.getX();
               this.targetY = (double)blockPos.getY();
               this.targetZ = (double)blockPos.getZ();
               return true;
            } else {
               return this.findTarget();
            }
         }
      }
   }

   private static class MateGoal extends AnimalMateGoal {
      private final TurtleEntity turtle;

      MateGoal(TurtleEntity turtle, double speed) {
         super(turtle, speed);
         this.turtle = turtle;
      }

      public boolean canStart() {
         return super.canStart() && !this.turtle.hasEgg();
      }

      protected void breed() {
         ServerPlayerEntity serverPlayerEntity = this.animal.getLovingPlayer();
         if (serverPlayerEntity == null && this.mate.getLovingPlayer() != null) {
            serverPlayerEntity = this.mate.getLovingPlayer();
         }

         if (serverPlayerEntity != null) {
            serverPlayerEntity.incrementStat(Stats.ANIMALS_BRED);
            Criteria.BRED_ANIMALS.trigger(serverPlayerEntity, this.animal, this.mate, (PassiveEntity)null);
         }

         this.turtle.setHasEgg(true);
         this.animal.setBreedingAge(6000);
         this.mate.setBreedingAge(6000);
         this.animal.resetLoveTicks();
         this.mate.resetLoveTicks();
         Random random = this.animal.getRandom();
         if (castToServerWorld(this.world).getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.animal.getX(), this.animal.getY(), this.animal.getZ(), random.nextInt(7) + 1));
         }

      }
   }

   private static class LayEggGoal extends MoveToTargetPosGoal {
      private final TurtleEntity turtle;

      LayEggGoal(TurtleEntity turtle, double speed) {
         super(turtle, speed, 16);
         this.turtle = turtle;
      }

      public boolean canStart() {
         return this.turtle.hasEgg() && this.turtle.homePos.isWithinDistance(this.turtle.getPos(), 9.0) ? super.canStart() : false;
      }

      public boolean shouldContinue() {
         return super.shouldContinue() && this.turtle.hasEgg() && this.turtle.homePos.isWithinDistance(this.turtle.getPos(), 9.0);
      }

      public void tick() {
         super.tick();
         BlockPos blockPos = this.turtle.getBlockPos();
         if (!this.turtle.isTouchingWater() && this.hasReached()) {
            if (this.turtle.sandDiggingCounter < 1) {
               this.turtle.setDiggingSand(true);
            } else if (this.turtle.sandDiggingCounter > this.getTickCount(200)) {
               World world = this.turtle.getWorld();
               world.playSound((Entity)null, blockPos, SoundEvents.ENTITY_TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3F, 0.9F + world.random.nextFloat() * 0.2F);
               BlockPos blockPos2 = this.targetPos.up();
               BlockState blockState = (BlockState)Blocks.TURTLE_EGG.getDefaultState().with(TurtleEggBlock.EGGS, this.turtle.random.nextInt(4) + 1);
               world.setBlockState(blockPos2, blockState, 3);
               world.emitGameEvent(GameEvent.BLOCK_PLACE, blockPos2, GameEvent.Emitter.of(this.turtle, blockState));
               this.turtle.setHasEgg(false);
               this.turtle.setDiggingSand(false);
               this.turtle.setLoveTicks(600);
            }

            if (this.turtle.isDiggingSand()) {
               ++this.turtle.sandDiggingCounter;
            }
         }

      }

      protected boolean isTargetPos(WorldView world, BlockPos pos) {
         return !world.isAir(pos.up()) ? false : TurtleEggBlock.isSand(world, pos);
      }
   }

   static class WanderInWaterGoal extends MoveToTargetPosGoal {
      private static final int field_30385 = 1200;
      private final TurtleEntity turtle;

      WanderInWaterGoal(TurtleEntity turtle, double speed) {
         super(turtle, turtle.isBaby() ? 2.0 : speed, 24);
         this.turtle = turtle;
         this.lowestY = -1;
      }

      public boolean shouldContinue() {
         return !this.turtle.isTouchingWater() && this.tryingTime <= 1200 && this.isTargetPos(this.turtle.getWorld(), this.targetPos);
      }

      public boolean canStart() {
         if (this.turtle.isBaby() && !this.turtle.isTouchingWater()) {
            return super.canStart();
         } else {
            return !this.turtle.landBound && !this.turtle.isTouchingWater() && !this.turtle.hasEgg() ? super.canStart() : false;
         }
      }

      public boolean shouldResetPath() {
         return this.tryingTime % 160 == 0;
      }

      protected boolean isTargetPos(WorldView world, BlockPos pos) {
         return world.getBlockState(pos).isOf(Blocks.WATER);
      }
   }

   private static class GoHomeGoal extends Goal {
      private final TurtleEntity turtle;
      private final double speed;
      private boolean noPath;
      private int homeReachingTryTicks;
      private static final int MAX_TRY_TICKS = 600;

      GoHomeGoal(TurtleEntity turtle, double speed) {
         this.turtle = turtle;
         this.speed = speed;
      }

      public boolean canStart() {
         if (this.turtle.isBaby()) {
            return false;
         } else if (this.turtle.hasEgg()) {
            return true;
         } else if (this.turtle.getRandom().nextInt(toGoalTicks(700)) != 0) {
            return false;
         } else {
            return !this.turtle.homePos.isWithinDistance(this.turtle.getPos(), 64.0);
         }
      }

      public void start() {
         this.turtle.landBound = true;
         this.noPath = false;
         this.homeReachingTryTicks = 0;
      }

      public void stop() {
         this.turtle.landBound = false;
      }

      public boolean shouldContinue() {
         return !this.turtle.homePos.isWithinDistance(this.turtle.getPos(), 7.0) && !this.noPath && this.homeReachingTryTicks <= this.getTickCount(600);
      }

      public void tick() {
         BlockPos blockPos = this.turtle.homePos;
         boolean bl = blockPos.isWithinDistance(this.turtle.getPos(), 16.0);
         if (bl) {
            ++this.homeReachingTryTicks;
         }

         if (this.turtle.getNavigation().isIdle()) {
            Vec3d vec3d = Vec3d.ofBottomCenter(blockPos);
            Vec3d vec3d2 = NoPenaltyTargeting.findTo(this.turtle, 16, 3, vec3d, 0.3141592741012573);
            if (vec3d2 == null) {
               vec3d2 = NoPenaltyTargeting.findTo(this.turtle, 8, 7, vec3d, 1.5707963705062866);
            }

            if (vec3d2 != null && !bl && !this.turtle.getWorld().getBlockState(BlockPos.ofFloored(vec3d2)).isOf(Blocks.WATER)) {
               vec3d2 = NoPenaltyTargeting.findTo(this.turtle, 16, 5, vec3d, 1.5707963705062866);
            }

            if (vec3d2 == null) {
               this.noPath = true;
               return;
            }

            this.turtle.getNavigation().startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
         }

      }
   }

   private static class TravelGoal extends Goal {
      private final TurtleEntity turtle;
      private final double speed;
      private boolean noPath;

      TravelGoal(TurtleEntity turtle, double speed) {
         this.turtle = turtle;
         this.speed = speed;
      }

      public boolean canStart() {
         return !this.turtle.landBound && !this.turtle.hasEgg() && this.turtle.isTouchingWater();
      }

      public void start() {
         int i = true;
         int j = true;
         Random random = this.turtle.random;
         int k = random.nextInt(1025) - 512;
         int l = random.nextInt(9) - 4;
         int m = random.nextInt(1025) - 512;
         if ((double)l + this.turtle.getY() > (double)(this.turtle.getWorld().getSeaLevel() - 1)) {
            l = 0;
         }

         this.turtle.travelPos = BlockPos.ofFloored((double)k + this.turtle.getX(), (double)l + this.turtle.getY(), (double)m + this.turtle.getZ());
         this.noPath = false;
      }

      public void tick() {
         if (this.turtle.travelPos == null) {
            this.noPath = true;
         } else {
            if (this.turtle.getNavigation().isIdle()) {
               Vec3d vec3d = Vec3d.ofBottomCenter(this.turtle.travelPos);
               Vec3d vec3d2 = NoPenaltyTargeting.findTo(this.turtle, 16, 3, vec3d, 0.3141592741012573);
               if (vec3d2 == null) {
                  vec3d2 = NoPenaltyTargeting.findTo(this.turtle, 8, 7, vec3d, 1.5707963705062866);
               }

               if (vec3d2 != null) {
                  int i = MathHelper.floor(vec3d2.x);
                  int j = MathHelper.floor(vec3d2.z);
                  int k = true;
                  if (!this.turtle.getWorld().isRegionLoaded(i - 34, j - 34, i + 34, j + 34)) {
                     vec3d2 = null;
                  }
               }

               if (vec3d2 == null) {
                  this.noPath = true;
                  return;
               }

               this.turtle.getNavigation().startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
            }

         }
      }

      public boolean shouldContinue() {
         return !this.turtle.getNavigation().isIdle() && !this.noPath && !this.turtle.landBound && !this.turtle.isInLove() && !this.turtle.hasEgg();
      }

      public void stop() {
         this.turtle.travelPos = null;
         super.stop();
      }
   }

   private static class WanderOnLandGoal extends WanderAroundGoal {
      private final TurtleEntity turtle;

      WanderOnLandGoal(TurtleEntity turtle, double speed, int chance) {
         super(turtle, speed, chance);
         this.turtle = turtle;
      }

      public boolean canStart() {
         return !this.mob.isTouchingWater() && !this.turtle.landBound && !this.turtle.hasEgg() ? super.canStart() : false;
      }
   }

   static class TurtleSwimNavigation extends AmphibiousSwimNavigation {
      TurtleSwimNavigation(TurtleEntity owner, World world) {
         super(owner, world);
      }

      public boolean isValidPosition(BlockPos pos) {
         MobEntity var3 = this.entity;
         if (var3 instanceof TurtleEntity turtleEntity) {
            if (turtleEntity.travelPos != null) {
               return this.world.getBlockState(pos).isOf(Blocks.WATER);
            }
         }

         return !this.world.getBlockState(pos.down()).isAir();
      }
   }
}
