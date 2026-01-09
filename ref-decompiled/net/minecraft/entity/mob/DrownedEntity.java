package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.ai.pathing.AmphibiousSwimNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class DrownedEntity extends ZombieEntity implements RangedAttackMob {
   public static final float field_30460 = 0.03F;
   boolean targetingUnderwater;

   public DrownedEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.moveControl = new DrownedMoveControl(this);
      this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
   }

   public static DefaultAttributeContainer.Builder createDrownedAttributes() {
      return ZombieEntity.createZombieAttributes().add(EntityAttributes.STEP_HEIGHT, 1.0);
   }

   protected EntityNavigation createNavigation(World world) {
      return new AmphibiousSwimNavigation(this, world);
   }

   protected void initCustomGoals() {
      this.goalSelector.add(1, new WanderAroundOnSurfaceGoal(this, 1.0));
      this.goalSelector.add(2, new TridentAttackGoal(this, 1.0, 40, 10.0F));
      this.goalSelector.add(2, new DrownedAttackGoal(this, 1.0, false));
      this.goalSelector.add(5, new LeaveWaterGoal(this, 1.0));
      this.goalSelector.add(6, new TargetAboveWaterGoal(this, 1.0, this.getWorld().getSeaLevel()));
      this.goalSelector.add(7, new WanderAroundGoal(this, 1.0));
      this.targetSelector.add(1, (new RevengeGoal(this, new Class[]{DrownedEntity.class})).setGroupRevenge(ZombifiedPiglinEntity.class));
      this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, 10, true, false, (target, world) -> {
         return this.canDrownedAttackTarget(target);
      }));
      this.targetSelector.add(3, new ActiveTargetGoal(this, MerchantEntity.class, false));
      this.targetSelector.add(3, new ActiveTargetGoal(this, IronGolemEntity.class, true));
      this.targetSelector.add(3, new ActiveTargetGoal(this, AxolotlEntity.class, true, false));
      this.targetSelector.add(5, new ActiveTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
   }

   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      entityData = super.initialize(world, difficulty, spawnReason, entityData);
      if (this.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty() && world.getRandom().nextFloat() < 0.03F) {
         this.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.NAUTILUS_SHELL));
         this.setDropGuaranteed(EquipmentSlot.OFFHAND);
      }

      return entityData;
   }

   public static boolean canSpawn(EntityType type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
      if (!world.getFluidState(pos.down()).isIn(FluidTags.WATER) && !SpawnReason.isAnySpawner(spawnReason)) {
         return false;
      } else {
         RegistryEntry registryEntry = world.getBiome(pos);
         boolean bl = world.getDifficulty() != Difficulty.PEACEFUL && (SpawnReason.isTrialSpawner(spawnReason) || isSpawnDark(world, pos, random)) && (SpawnReason.isAnySpawner(spawnReason) || world.getFluidState(pos).isIn(FluidTags.WATER));
         if (bl && (SpawnReason.isAnySpawner(spawnReason) || spawnReason == SpawnReason.REINFORCEMENT)) {
            return true;
         } else if (registryEntry.isIn(BiomeTags.MORE_FREQUENT_DROWNED_SPAWNS)) {
            return random.nextInt(15) == 0 && bl;
         } else {
            return random.nextInt(40) == 0 && isValidSpawnDepth(world, pos) && bl;
         }
      }
   }

   private static boolean isValidSpawnDepth(WorldAccess world, BlockPos pos) {
      return pos.getY() < world.getSeaLevel() - 5;
   }

   protected SoundEvent getAmbientSound() {
      return this.isTouchingWater() ? SoundEvents.ENTITY_DROWNED_AMBIENT_WATER : SoundEvents.ENTITY_DROWNED_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return this.isTouchingWater() ? SoundEvents.ENTITY_DROWNED_HURT_WATER : SoundEvents.ENTITY_DROWNED_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isTouchingWater() ? SoundEvents.ENTITY_DROWNED_DEATH_WATER : SoundEvents.ENTITY_DROWNED_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_DROWNED_STEP;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_DROWNED_SWIM;
   }

   protected boolean canSpawnAsReinforcementInFluid() {
      return true;
   }

   protected ItemStack getSkull() {
      return ItemStack.EMPTY;
   }

   protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
      if ((double)random.nextFloat() > 0.9) {
         int i = random.nextInt(16);
         if (i < 10) {
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
         } else {
            this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
         }
      }

   }

   protected boolean prefersNewEquipment(ItemStack newStack, ItemStack currentStack, EquipmentSlot slot) {
      return currentStack.isOf(Items.NAUTILUS_SHELL) ? false : super.prefersNewEquipment(newStack, currentStack, slot);
   }

   protected boolean canConvertInWater() {
      return false;
   }

   public boolean canSpawn(WorldView world) {
      return world.doesNotIntersectEntities(this);
   }

   public boolean canDrownedAttackTarget(@Nullable LivingEntity target) {
      if (target != null) {
         return !this.getWorld().isDay() || target.isTouchingWater();
      } else {
         return false;
      }
   }

   public boolean isPushedByFluids() {
      return !this.isSwimming();
   }

   boolean isTargetingUnderwater() {
      if (this.targetingUnderwater) {
         return true;
      } else {
         LivingEntity livingEntity = this.getTarget();
         return livingEntity != null && livingEntity.isTouchingWater();
      }
   }

   public void travel(Vec3d movementInput) {
      if (this.isSubmergedInWater() && this.isTargetingUnderwater()) {
         this.updateVelocity(0.01F, movementInput);
         this.move(MovementType.SELF, this.getVelocity());
         this.setVelocity(this.getVelocity().multiply(0.9));
      } else {
         super.travel(movementInput);
      }

   }

   public void updateSwimming() {
      if (!this.getWorld().isClient) {
         this.setSwimming(this.canActVoluntarily() && this.isSubmergedInWater() && this.isTargetingUnderwater());
      }

   }

   public boolean isInSwimmingPose() {
      return this.isSwimming();
   }

   protected boolean hasFinishedCurrentPath() {
      Path path = this.getNavigation().getCurrentPath();
      if (path != null) {
         BlockPos blockPos = path.getTarget();
         if (blockPos != null) {
            double d = this.squaredDistanceTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
            if (d < 4.0) {
               return true;
            }
         }
      }

      return false;
   }

   public void shootAt(LivingEntity target, float pullProgress) {
      ItemStack itemStack = this.getMainHandStack();
      ItemStack itemStack2 = itemStack.isOf(Items.TRIDENT) ? itemStack : new ItemStack(Items.TRIDENT);
      TridentEntity tridentEntity = new TridentEntity(this.getWorld(), this, itemStack2);
      double d = target.getX() - this.getX();
      double e = target.getBodyY(0.3333333333333333) - tridentEntity.getY();
      double f = target.getZ() - this.getZ();
      double g = Math.sqrt(d * d + f * f);
      World var15 = this.getWorld();
      if (var15 instanceof ServerWorld serverWorld) {
         ProjectileEntity.spawnWithVelocity(tridentEntity, serverWorld, itemStack2, d, e + g * 0.20000000298023224, f, 1.6F, (float)(14 - this.getWorld().getDifficulty().getId() * 4));
      }

      this.playSound(SoundEvents.ENTITY_DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
   }

   public TagKey getPreferredWeapons() {
      return ItemTags.DROWNED_PREFERRED_WEAPONS;
   }

   public void setTargetingUnderwater(boolean targetingUnderwater) {
      this.targetingUnderwater = targetingUnderwater;
   }

   private static class DrownedMoveControl extends MoveControl {
      private final DrownedEntity drowned;

      public DrownedMoveControl(DrownedEntity drowned) {
         super(drowned);
         this.drowned = drowned;
      }

      public void tick() {
         LivingEntity livingEntity = this.drowned.getTarget();
         if (this.drowned.isTargetingUnderwater() && this.drowned.isTouchingWater()) {
            if (livingEntity != null && livingEntity.getY() > this.drowned.getY() || this.drowned.targetingUnderwater) {
               this.drowned.setVelocity(this.drowned.getVelocity().add(0.0, 0.002, 0.0));
            }

            if (this.state != MoveControl.State.MOVE_TO || this.drowned.getNavigation().isIdle()) {
               this.drowned.setMovementSpeed(0.0F);
               return;
            }

            double d = this.targetX - this.drowned.getX();
            double e = this.targetY - this.drowned.getY();
            double f = this.targetZ - this.drowned.getZ();
            double g = Math.sqrt(d * d + e * e + f * f);
            e /= g;
            float h = (float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0F;
            this.drowned.setYaw(this.wrapDegrees(this.drowned.getYaw(), h, 90.0F));
            this.drowned.bodyYaw = this.drowned.getYaw();
            float i = (float)(this.speed * this.drowned.getAttributeValue(EntityAttributes.MOVEMENT_SPEED));
            float j = MathHelper.lerp(0.125F, this.drowned.getMovementSpeed(), i);
            this.drowned.setMovementSpeed(j);
            this.drowned.setVelocity(this.drowned.getVelocity().add((double)j * d * 0.005, (double)j * e * 0.1, (double)j * f * 0.005));
         } else {
            if (!this.drowned.isOnGround()) {
               this.drowned.setVelocity(this.drowned.getVelocity().add(0.0, -0.008, 0.0));
            }

            super.tick();
         }

      }
   }

   private static class WanderAroundOnSurfaceGoal extends Goal {
      private final PathAwareEntity mob;
      private double x;
      private double y;
      private double z;
      private final double speed;
      private final World world;

      public WanderAroundOnSurfaceGoal(PathAwareEntity mob, double speed) {
         this.mob = mob;
         this.speed = speed;
         this.world = mob.getWorld();
         this.setControls(EnumSet.of(Goal.Control.MOVE));
      }

      public boolean canStart() {
         if (!this.world.isDay()) {
            return false;
         } else if (this.mob.isTouchingWater()) {
            return false;
         } else {
            Vec3d vec3d = this.getWanderTarget();
            if (vec3d == null) {
               return false;
            } else {
               this.x = vec3d.x;
               this.y = vec3d.y;
               this.z = vec3d.z;
               return true;
            }
         }
      }

      public boolean shouldContinue() {
         return !this.mob.getNavigation().isIdle();
      }

      public void start() {
         this.mob.getNavigation().startMovingTo(this.x, this.y, this.z, this.speed);
      }

      @Nullable
      private Vec3d getWanderTarget() {
         Random random = this.mob.getRandom();
         BlockPos blockPos = this.mob.getBlockPos();

         for(int i = 0; i < 10; ++i) {
            BlockPos blockPos2 = blockPos.add(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
            if (this.world.getBlockState(blockPos2).isOf(Blocks.WATER)) {
               return Vec3d.ofBottomCenter(blockPos2);
            }
         }

         return null;
      }
   }

   static class TridentAttackGoal extends ProjectileAttackGoal {
      private final DrownedEntity drowned;

      public TridentAttackGoal(RangedAttackMob rangedAttackMob, double d, int i, float f) {
         super(rangedAttackMob, d, i, f);
         this.drowned = (DrownedEntity)rangedAttackMob;
      }

      public boolean canStart() {
         return super.canStart() && this.drowned.getMainHandStack().isOf(Items.TRIDENT);
      }

      public void start() {
         super.start();
         this.drowned.setAttacking(true);
         this.drowned.setCurrentHand(Hand.MAIN_HAND);
      }

      public void stop() {
         super.stop();
         this.drowned.clearActiveItem();
         this.drowned.setAttacking(false);
      }
   }

   static class DrownedAttackGoal extends ZombieAttackGoal {
      private final DrownedEntity drowned;

      public DrownedAttackGoal(DrownedEntity drowned, double speed, boolean pauseWhenMobIdle) {
         super(drowned, speed, pauseWhenMobIdle);
         this.drowned = drowned;
      }

      public boolean canStart() {
         return super.canStart() && this.drowned.canDrownedAttackTarget(this.drowned.getTarget());
      }

      public boolean shouldContinue() {
         return super.shouldContinue() && this.drowned.canDrownedAttackTarget(this.drowned.getTarget());
      }
   }

   static class LeaveWaterGoal extends MoveToTargetPosGoal {
      private final DrownedEntity drowned;

      public LeaveWaterGoal(DrownedEntity drowned, double speed) {
         super(drowned, speed, 8, 2);
         this.drowned = drowned;
      }

      public boolean canStart() {
         return super.canStart() && !this.drowned.getWorld().isDay() && this.drowned.isTouchingWater() && this.drowned.getY() >= (double)(this.drowned.getWorld().getSeaLevel() - 3);
      }

      public boolean shouldContinue() {
         return super.shouldContinue();
      }

      protected boolean isTargetPos(WorldView world, BlockPos pos) {
         BlockPos blockPos = pos.up();
         return world.isAir(blockPos) && world.isAir(blockPos.up()) ? world.getBlockState(pos).hasSolidTopSurface(world, pos, this.drowned) : false;
      }

      public void start() {
         this.drowned.setTargetingUnderwater(false);
         super.start();
      }

      public void stop() {
         super.stop();
      }
   }

   static class TargetAboveWaterGoal extends Goal {
      private final DrownedEntity drowned;
      private final double speed;
      private final int minY;
      private boolean foundTarget;

      public TargetAboveWaterGoal(DrownedEntity drowned, double speed, int minY) {
         this.drowned = drowned;
         this.speed = speed;
         this.minY = minY;
      }

      public boolean canStart() {
         return !this.drowned.getWorld().isDay() && this.drowned.isTouchingWater() && this.drowned.getY() < (double)(this.minY - 2);
      }

      public boolean shouldContinue() {
         return this.canStart() && !this.foundTarget;
      }

      public void tick() {
         if (this.drowned.getY() < (double)(this.minY - 1) && (this.drowned.getNavigation().isIdle() || this.drowned.hasFinishedCurrentPath())) {
            Vec3d vec3d = NoPenaltyTargeting.findTo(this.drowned, 4, 8, new Vec3d(this.drowned.getX(), (double)(this.minY - 1), this.drowned.getZ()), 1.5707963705062866);
            if (vec3d == null) {
               this.foundTarget = true;
               return;
            }

            this.drowned.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, this.speed);
         }

      }

      public void start() {
         this.drowned.setTargetingUnderwater(true);
         this.foundTarget = false;
      }

      public void stop() {
         this.drowned.setTargetingUnderwater(false);
      }
   }
}
