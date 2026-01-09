package net.minecraft.entity.mob;

import java.util.Objects;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.EntityAttachments;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.SkeletonHorseTrapTriggerGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class SkeletonHorseEntity extends AbstractHorseEntity {
   private final SkeletonHorseTrapTriggerGoal trapTriggerGoal = new SkeletonHorseTrapTriggerGoal(this);
   private static final int DESPAWN_AGE = 18000;
   private static final boolean DEFAULT_TRAPPED = false;
   private static final int DEFAULT_TRAP_TIME = 0;
   private static final EntityDimensions BABY_BASE_DIMENSIONS;
   private boolean trapped = false;
   private int trapTime = 0;

   public SkeletonHorseEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   public static DefaultAttributeContainer.Builder createSkeletonHorseAttributes() {
      return createBaseHorseAttributes().add(EntityAttributes.MAX_HEALTH, 15.0).add(EntityAttributes.MOVEMENT_SPEED, 0.20000000298023224);
   }

   public static boolean canSpawn(EntityType type, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
      if (!SpawnReason.isAnySpawner(reason)) {
         return AnimalEntity.isValidNaturalSpawn(type, world, reason, pos, random);
      } else {
         return SpawnReason.isTrialSpawner(reason) || isLightLevelValidForNaturalSpawn(world, pos);
      }
   }

   protected void initAttributes(Random random) {
      EntityAttributeInstance var10000 = this.getAttributeInstance(EntityAttributes.JUMP_STRENGTH);
      Objects.requireNonNull(random);
      var10000.setBaseValue(getChildJumpStrengthBonus(random::nextDouble));
   }

   protected void initCustomGoals() {
   }

   protected SoundEvent getAmbientSound() {
      return this.isSubmergedIn(FluidTags.WATER) ? SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT_WATER : SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SKELETON_HORSE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_SKELETON_HORSE_HURT;
   }

   protected SoundEvent getSwimSound() {
      if (this.isOnGround()) {
         if (!this.hasPassengers()) {
            return SoundEvents.ENTITY_SKELETON_HORSE_STEP_WATER;
         }

         ++this.soundTicks;
         if (this.soundTicks > 5 && this.soundTicks % 3 == 0) {
            return SoundEvents.ENTITY_SKELETON_HORSE_GALLOP_WATER;
         }

         if (this.soundTicks <= 5) {
            return SoundEvents.ENTITY_SKELETON_HORSE_STEP_WATER;
         }
      }

      return SoundEvents.ENTITY_SKELETON_HORSE_SWIM;
   }

   protected void playSwimSound(float volume) {
      if (this.isOnGround()) {
         super.playSwimSound(0.3F);
      } else {
         super.playSwimSound(Math.min(0.1F, volume * 25.0F));
      }

   }

   protected void playJumpSound() {
      if (this.isTouchingWater()) {
         this.playSound(SoundEvents.ENTITY_SKELETON_HORSE_JUMP_WATER, 0.4F, 1.0F);
      } else {
         super.playJumpSound();
      }

   }

   public EntityDimensions getBaseDimensions(EntityPose pose) {
      return this.isBaby() ? BABY_BASE_DIMENSIONS : super.getBaseDimensions(pose);
   }

   public void tickMovement() {
      super.tickMovement();
      if (this.isTrapped() && this.trapTime++ >= 18000) {
         this.discard();
      }

   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putBoolean("SkeletonTrap", this.isTrapped());
      view.putInt("SkeletonTrapTime", this.trapTime);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.setTrapped(view.getBoolean("SkeletonTrap", false));
      this.trapTime = view.getInt("SkeletonTrapTime", 0);
   }

   protected float getBaseWaterMovementSpeedMultiplier() {
      return 0.96F;
   }

   public boolean isTrapped() {
      return this.trapped;
   }

   public void setTrapped(boolean trapped) {
      if (trapped != this.trapped) {
         this.trapped = trapped;
         if (trapped) {
            this.goalSelector.add(1, this.trapTriggerGoal);
         } else {
            this.goalSelector.remove(this.trapTriggerGoal);
         }

      }
   }

   @Nullable
   public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
      return (PassiveEntity)EntityType.SKELETON_HORSE.create(world, SpawnReason.BREEDING);
   }

   public ActionResult interactMob(PlayerEntity player, Hand hand) {
      return (ActionResult)(!this.isTame() ? ActionResult.PASS : super.interactMob(player, hand));
   }

   static {
      BABY_BASE_DIMENSIONS = EntityType.SKELETON_HORSE.getDimensions().withAttachments(EntityAttachments.builder().add(EntityAttachmentType.PASSENGER, 0.0F, EntityType.SKELETON_HORSE.getHeight() - 0.03125F, 0.0F)).scaled(0.5F);
   }
}
