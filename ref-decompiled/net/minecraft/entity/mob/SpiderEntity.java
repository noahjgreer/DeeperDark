package net.minecraft.entity.mob;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SpiderNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SpiderEntity extends HostileEntity {
   private static final TrackedData SPIDER_FLAGS;
   private static final float field_30498 = 0.1F;

   public SpiderEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   protected void initGoals() {
      this.goalSelector.add(1, new SwimGoal(this));
      this.goalSelector.add(2, new FleeEntityGoal(this, ArmadilloEntity.class, 6.0F, 1.0, 1.2, (entity) -> {
         return !((ArmadilloEntity)entity).isNotIdle();
      }));
      this.goalSelector.add(3, new PounceAtTargetGoal(this, 0.4F));
      this.goalSelector.add(4, new AttackGoal(this));
      this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
      this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.add(6, new LookAroundGoal(this));
      this.targetSelector.add(1, new RevengeGoal(this, new Class[0]));
      this.targetSelector.add(2, new TargetGoal(this, PlayerEntity.class));
      this.targetSelector.add(3, new TargetGoal(this, IronGolemEntity.class));
   }

   protected EntityNavigation createNavigation(World world) {
      return new SpiderNavigation(this, world);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(SPIDER_FLAGS, (byte)0);
   }

   public void tick() {
      super.tick();
      if (!this.getWorld().isClient) {
         this.setClimbingWall(this.horizontalCollision);
      }

   }

   public static DefaultAttributeContainer.Builder createSpiderAttributes() {
      return HostileEntity.createHostileAttributes().add(EntityAttributes.MAX_HEALTH, 16.0).add(EntityAttributes.MOVEMENT_SPEED, 0.30000001192092896);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SPIDER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_SPIDER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SPIDER_DEATH;
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
   }

   public boolean isClimbing() {
      return this.isClimbingWall();
   }

   public void slowMovement(BlockState state, Vec3d multiplier) {
      if (!state.isOf(Blocks.COBWEB)) {
         super.slowMovement(state, multiplier);
      }

   }

   public boolean canHaveStatusEffect(StatusEffectInstance effect) {
      return effect.equals(StatusEffects.POISON) ? false : super.canHaveStatusEffect(effect);
   }

   public boolean isClimbingWall() {
      return ((Byte)this.dataTracker.get(SPIDER_FLAGS) & 1) != 0;
   }

   public void setClimbingWall(boolean climbing) {
      byte b = (Byte)this.dataTracker.get(SPIDER_FLAGS);
      if (climbing) {
         b = (byte)(b | 1);
      } else {
         b &= -2;
      }

      this.dataTracker.set(SPIDER_FLAGS, b);
   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      EntityData entityData = super.initialize(world, difficulty, spawnReason, entityData);
      Random random = world.getRandom();
      if (random.nextInt(100) == 0) {
         SkeletonEntity skeletonEntity = (SkeletonEntity)EntityType.SKELETON.create(this.getWorld(), SpawnReason.JOCKEY);
         if (skeletonEntity != null) {
            skeletonEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0F);
            skeletonEntity.initialize(world, difficulty, spawnReason, (EntityData)null);
            skeletonEntity.startRiding(this);
         }
      }

      if (entityData == null) {
         entityData = new SpiderData();
         if (world.getDifficulty() == Difficulty.HARD && random.nextFloat() < 0.1F * difficulty.getClampedLocalDifficulty()) {
            ((SpiderData)entityData).setEffect(random);
         }
      }

      if (entityData instanceof SpiderData spiderData) {
         RegistryEntry registryEntry = spiderData.effect;
         if (registryEntry != null) {
            this.addStatusEffect(new StatusEffectInstance(registryEntry, -1));
         }
      }

      return (EntityData)entityData;
   }

   public Vec3d getVehicleAttachmentPos(Entity vehicle) {
      return vehicle.getWidth() <= this.getWidth() ? new Vec3d(0.0, 0.3125 * (double)this.getScale(), 0.0) : super.getVehicleAttachmentPos(vehicle);
   }

   static {
      SPIDER_FLAGS = DataTracker.registerData(SpiderEntity.class, TrackedDataHandlerRegistry.BYTE);
   }

   private static class AttackGoal extends MeleeAttackGoal {
      public AttackGoal(SpiderEntity spider) {
         super(spider, 1.0, true);
      }

      public boolean canStart() {
         return super.canStart() && !this.mob.hasPassengers();
      }

      public boolean shouldContinue() {
         float f = this.mob.getBrightnessAtEyes();
         if (f >= 0.5F && this.mob.getRandom().nextInt(100) == 0) {
            this.mob.setTarget((LivingEntity)null);
            return false;
         } else {
            return super.shouldContinue();
         }
      }
   }

   static class TargetGoal extends ActiveTargetGoal {
      public TargetGoal(SpiderEntity spider, Class targetEntityClass) {
         super(spider, targetEntityClass, true);
      }

      public boolean canStart() {
         float f = this.mob.getBrightnessAtEyes();
         return f >= 0.5F ? false : super.canStart();
      }
   }

   public static class SpiderData implements EntityData {
      @Nullable
      public RegistryEntry effect;

      public void setEffect(Random random) {
         int i = random.nextInt(5);
         if (i <= 1) {
            this.effect = StatusEffects.SPEED;
         } else if (i <= 2) {
            this.effect = StatusEffects.STRENGTH;
         } else if (i <= 3) {
            this.effect = StatusEffects.REGENERATION;
         } else if (i <= 4) {
            this.effect = StatusEffects.INVISIBILITY;
         }

      }
   }
}
