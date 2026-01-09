package net.minecraft.entity.passive;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class CamelEntity extends AbstractHorseEntity {
   public static final float field_45127 = 0.45F;
   public static final int field_40132 = 55;
   public static final int field_41764 = 30;
   private static final float field_40146 = 0.1F;
   private static final float field_40147 = 1.4285F;
   private static final float field_40148 = 22.2222F;
   private static final int field_43388 = 5;
   private static final int field_40149 = 40;
   private static final int field_40133 = 52;
   private static final int field_40134 = 80;
   private static final float field_40135 = 1.43F;
   private static final long DEFAULT_LAST_POSE_TICK = 0L;
   public static final TrackedData DASHING;
   public static final TrackedData LAST_POSE_TICK;
   public final AnimationState sittingTransitionAnimationState = new AnimationState();
   public final AnimationState sittingAnimationState = new AnimationState();
   public final AnimationState standingTransitionAnimationState = new AnimationState();
   public final AnimationState idlingAnimationState = new AnimationState();
   public final AnimationState dashingAnimationState = new AnimationState();
   private static final EntityDimensions SITTING_DIMENSIONS;
   private int dashCooldown = 0;
   private int idleAnimationCooldown = 0;

   public CamelEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.moveControl = new CamelMoveControl();
      this.lookControl = new CamelLookControl();
      MobNavigation mobNavigation = (MobNavigation)this.getNavigation();
      mobNavigation.setCanSwim(true);
      mobNavigation.setCanWalkOverFences(true);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putLong("LastPoseTick", (Long)this.dataTracker.get(LAST_POSE_TICK));
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      long l = view.getLong("LastPoseTick", 0L);
      if (l < 0L) {
         this.setPose(EntityPose.SITTING);
      }

      this.setLastPoseTick(l);
   }

   public static DefaultAttributeContainer.Builder createCamelAttributes() {
      return createBaseHorseAttributes().add(EntityAttributes.MAX_HEALTH, 32.0).add(EntityAttributes.MOVEMENT_SPEED, 0.09000000357627869).add(EntityAttributes.JUMP_STRENGTH, 0.41999998688697815).add(EntityAttributes.STEP_HEIGHT, 1.5);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(DASHING, false);
      builder.add(LAST_POSE_TICK, 0L);
   }

   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      CamelBrain.initialize(this, world.getRandom());
      this.initLastPoseTick(world.toServerWorld().getTime());
      return super.initialize(world, difficulty, spawnReason, entityData);
   }

   public static boolean canSpawn(EntityType type, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
      return world.getBlockState(pos.down()).isIn(BlockTags.CAMELS_SPAWNABLE_ON) && isLightLevelValidForNaturalSpawn(world, pos);
   }

   protected Brain.Profile createBrainProfile() {
      return CamelBrain.createBrainProfile();
   }

   protected void initGoals() {
   }

   protected Brain deserializeBrain(Dynamic dynamic) {
      return CamelBrain.create(this.createBrainProfile().deserialize(dynamic));
   }

   public EntityDimensions getBaseDimensions(EntityPose pose) {
      return pose == EntityPose.SITTING ? SITTING_DIMENSIONS.scaled(this.getScaleFactor()) : super.getBaseDimensions(pose);
   }

   protected void mobTick(ServerWorld world) {
      Profiler profiler = Profilers.get();
      profiler.push("camelBrain");
      Brain brain = this.getBrain();
      brain.tick(world, this);
      profiler.pop();
      profiler.push("camelActivityUpdate");
      CamelBrain.updateActivities(this);
      profiler.pop();
      super.mobTick(world);
   }

   public void tick() {
      super.tick();
      if (this.isDashing() && this.dashCooldown < 50 && (this.isOnGround() || this.isInFluid() || this.hasVehicle())) {
         this.setDashing(false);
      }

      if (this.dashCooldown > 0) {
         --this.dashCooldown;
         if (this.dashCooldown == 0) {
            this.getWorld().playSound((Entity)null, this.getBlockPos(), SoundEvents.ENTITY_CAMEL_DASH_READY, SoundCategory.NEUTRAL, 1.0F, 1.0F);
         }
      }

      if (this.getWorld().isClient()) {
         this.updateAnimations();
      }

      if (this.isStationary()) {
         this.clampHeadYaw();
      }

      if (this.isSitting() && this.isTouchingWater()) {
         this.setStanding();
      }

   }

   private void updateAnimations() {
      if (this.idleAnimationCooldown <= 0) {
         this.idleAnimationCooldown = this.random.nextInt(40) + 80;
         this.idlingAnimationState.start(this.age);
      } else {
         --this.idleAnimationCooldown;
      }

      if (this.shouldUpdateSittingAnimations()) {
         this.standingTransitionAnimationState.stop();
         this.dashingAnimationState.stop();
         if (this.shouldPlaySittingTransitionAnimation()) {
            this.sittingTransitionAnimationState.startIfNotRunning(this.age);
            this.sittingAnimationState.stop();
         } else {
            this.sittingTransitionAnimationState.stop();
            this.sittingAnimationState.startIfNotRunning(this.age);
         }
      } else {
         this.sittingTransitionAnimationState.stop();
         this.sittingAnimationState.stop();
         this.dashingAnimationState.setRunning(this.isDashing(), this.age);
         this.standingTransitionAnimationState.setRunning(this.isChangingPose() && this.getTimeSinceLastPoseTick() >= 0L, this.age);
      }

   }

   protected void updateLimbs(float posDelta) {
      float f;
      if (this.getPose() == EntityPose.STANDING && !this.dashingAnimationState.isRunning()) {
         f = Math.min(posDelta * 6.0F, 1.0F);
      } else {
         f = 0.0F;
      }

      this.limbAnimator.updateLimbs(f, 0.2F, this.isBaby() ? 3.0F : 1.0F);
   }

   public void travel(Vec3d movementInput) {
      if (this.isStationary() && this.isOnGround()) {
         this.setVelocity(this.getVelocity().multiply(0.0, 1.0, 0.0));
         movementInput = movementInput.multiply(0.0, 1.0, 0.0);
      }

      super.travel(movementInput);
   }

   protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
      super.tickControlled(controllingPlayer, movementInput);
      if (controllingPlayer.forwardSpeed > 0.0F && this.isSitting() && !this.isChangingPose()) {
         this.startStanding();
      }

   }

   public boolean isStationary() {
      return this.isSitting() || this.isChangingPose();
   }

   protected float getSaddledSpeed(PlayerEntity controllingPlayer) {
      float f = controllingPlayer.isSprinting() && this.getJumpCooldown() == 0 ? 0.1F : 0.0F;
      return (float)this.getAttributeValue(EntityAttributes.MOVEMENT_SPEED) + f;
   }

   protected Vec2f getControlledRotation(LivingEntity controllingPassenger) {
      return this.isStationary() ? new Vec2f(this.getPitch(), this.getYaw()) : super.getControlledRotation(controllingPassenger);
   }

   protected Vec3d getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput) {
      return this.isStationary() ? Vec3d.ZERO : super.getControlledMovementInput(controllingPlayer, movementInput);
   }

   public boolean canJump() {
      return !this.isStationary() && super.canJump();
   }

   public void setJumpStrength(int strength) {
      if (this.hasSaddleEquipped() && this.dashCooldown <= 0 && this.isOnGround()) {
         super.setJumpStrength(strength);
      }
   }

   public boolean canSprintAsVehicle() {
      return true;
   }

   protected void jump(float strength, Vec3d movementInput) {
      double d = (double)this.getJumpVelocity();
      this.addVelocityInternal(this.getRotationVector().multiply(1.0, 0.0, 1.0).normalize().multiply((double)(22.2222F * strength) * this.getAttributeValue(EntityAttributes.MOVEMENT_SPEED) * (double)this.getVelocityMultiplier()).add(0.0, (double)(1.4285F * strength) * d, 0.0));
      this.dashCooldown = 55;
      this.setDashing(true);
      this.velocityDirty = true;
   }

   public boolean isDashing() {
      return (Boolean)this.dataTracker.get(DASHING);
   }

   public void setDashing(boolean dashing) {
      this.dataTracker.set(DASHING, dashing);
   }

   public void startJumping(int height) {
      this.playSound(SoundEvents.ENTITY_CAMEL_DASH);
      this.emitGameEvent(GameEvent.ENTITY_ACTION);
      this.setDashing(true);
   }

   public void stopJumping() {
   }

   public int getJumpCooldown() {
      return this.dashCooldown;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_CAMEL_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_CAMEL_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_CAMEL_HURT;
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      if (state.isIn(BlockTags.CAMEL_SAND_STEP_SOUND_BLOCKS)) {
         this.playSound(SoundEvents.ENTITY_CAMEL_STEP_SAND, 1.0F, 1.0F);
      } else {
         this.playSound(SoundEvents.ENTITY_CAMEL_STEP, 1.0F, 1.0F);
      }

   }

   public boolean isBreedingItem(ItemStack stack) {
      return stack.isIn(ItemTags.CAMEL_FOOD);
   }

   public ActionResult interactMob(PlayerEntity player, Hand hand) {
      ItemStack itemStack = player.getStackInHand(hand);
      if (player.shouldCancelInteraction() && !this.isBaby()) {
         this.openInventory(player);
         return ActionResult.SUCCESS;
      } else {
         ActionResult actionResult = itemStack.useOnEntity(player, this, hand);
         if (actionResult.isAccepted()) {
            return actionResult;
         } else if (this.isBreedingItem(itemStack)) {
            return this.interactHorse(player, itemStack);
         } else {
            if (this.getPassengerList().size() < 2 && !this.isBaby()) {
               this.putPlayerOnBack(player);
            }

            return ActionResult.SUCCESS;
         }
      }
   }

   public void onLongLeashTick() {
      super.onLongLeashTick();
      if (this.isSitting() && !this.isChangingPose() && this.canChangePose()) {
         this.startStanding();
      }

   }

   public Vec3d[] getQuadLeashOffsets() {
      return Leashable.createQuadLeashOffsets(this, 0.02, 0.48, 0.25, 0.82);
   }

   public boolean canChangePose() {
      return this.wouldNotSuffocateInPose(this.isSitting() ? EntityPose.STANDING : EntityPose.SITTING);
   }

   protected boolean receiveFood(PlayerEntity player, ItemStack item) {
      if (!this.isBreedingItem(item)) {
         return false;
      } else {
         boolean bl = this.getHealth() < this.getMaxHealth();
         if (bl) {
            this.heal(2.0F);
         }

         boolean bl2 = this.isTame() && this.getBreedingAge() == 0 && this.canEat();
         if (bl2) {
            this.lovePlayer(player);
         }

         boolean bl3 = this.isBaby();
         if (bl3) {
            this.getWorld().addParticleClient(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), 0.0, 0.0, 0.0);
            if (!this.getWorld().isClient) {
               this.growUp(10);
            }
         }

         if (!bl && !bl2 && !bl3) {
            return false;
         } else {
            if (!this.isSilent()) {
               SoundEvent soundEvent = this.getEatSound();
               if (soundEvent != null) {
                  this.getWorld().playSound((Entity)null, this.getX(), this.getY(), this.getZ(), (SoundEvent)soundEvent, this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
               }
            }

            this.emitGameEvent(GameEvent.EAT);
            return true;
         }
      }
   }

   protected boolean shouldAmbientStand() {
      return false;
   }

   public boolean canBreedWith(AnimalEntity other) {
      boolean var10000;
      if (other != this && other instanceof CamelEntity camelEntity) {
         if (this.canBreed() && camelEntity.canBreed()) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   @Nullable
   public CamelEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
      return (CamelEntity)EntityType.CAMEL.create(serverWorld, SpawnReason.BREEDING);
   }

   @Nullable
   protected SoundEvent getEatSound() {
      return SoundEvents.ENTITY_CAMEL_EAT;
   }

   protected void applyDamage(ServerWorld world, DamageSource source, float amount) {
      this.setStanding();
      super.applyDamage(world, source, amount);
   }

   protected Vec3d getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
      int i = Math.max(this.getPassengerList().indexOf(passenger), 0);
      boolean bl = i == 0;
      float f = 0.5F;
      float g = (float)(this.isRemoved() ? 0.009999999776482582 : this.getPassengerAttachmentY(bl, 0.0F, dimensions, scaleFactor));
      if (this.getPassengerList().size() > 1) {
         if (!bl) {
            f = -0.7F;
         }

         if (passenger instanceof AnimalEntity) {
            f += 0.2F;
         }
      }

      return (new Vec3d(0.0, (double)g, (double)(f * scaleFactor))).rotateY(-this.getYaw() * 0.017453292F);
   }

   public float getScaleFactor() {
      return this.isBaby() ? 0.45F : 1.0F;
   }

   private double getPassengerAttachmentY(boolean primaryPassenger, float tickProgress, EntityDimensions dimensions, float scaleFactor) {
      double d = (double)(dimensions.height() - 0.375F * scaleFactor);
      float f = scaleFactor * 1.43F;
      float g = f - scaleFactor * 0.2F;
      float h = f - g;
      boolean bl = this.isChangingPose();
      boolean bl2 = this.isSitting();
      if (bl) {
         int i = bl2 ? 40 : 52;
         int j;
         float k;
         if (bl2) {
            j = 28;
            k = primaryPassenger ? 0.5F : 0.1F;
         } else {
            j = primaryPassenger ? 24 : 32;
            k = primaryPassenger ? 0.6F : 0.35F;
         }

         float l = MathHelper.clamp((float)this.getTimeSinceLastPoseTick() + tickProgress, 0.0F, (float)i);
         boolean bl3 = l < (float)j;
         float m = bl3 ? l / (float)j : (l - (float)j) / (float)(i - j);
         float n = f - k * g;
         d += bl2 ? (double)MathHelper.lerp(m, bl3 ? f : n, bl3 ? n : h) : (double)MathHelper.lerp(m, bl3 ? h - f : h - n, bl3 ? h - n : 0.0F);
      }

      if (bl2 && !bl) {
         d += (double)h;
      }

      return d;
   }

   public Vec3d getLeashOffset(float tickProgress) {
      EntityDimensions entityDimensions = this.getDimensions(this.getPose());
      float f = this.getScaleFactor();
      return new Vec3d(0.0, this.getPassengerAttachmentY(true, tickProgress, entityDimensions, f) - (double)(0.2F * f), (double)(entityDimensions.width() * 0.56F));
   }

   public int getMaxHeadRotation() {
      return 30;
   }

   protected boolean canAddPassenger(Entity passenger) {
      return this.getPassengerList().size() <= 2;
   }

   protected void sendAiDebugData() {
      super.sendAiDebugData();
      DebugInfoSender.sendBrainDebugData(this);
   }

   public boolean isSitting() {
      return (Long)this.dataTracker.get(LAST_POSE_TICK) < 0L;
   }

   public boolean shouldUpdateSittingAnimations() {
      return this.getTimeSinceLastPoseTick() < 0L != this.isSitting();
   }

   public boolean isChangingPose() {
      long l = this.getTimeSinceLastPoseTick();
      return l < (long)(this.isSitting() ? 40 : 52);
   }

   private boolean shouldPlaySittingTransitionAnimation() {
      return this.isSitting() && this.getTimeSinceLastPoseTick() < 40L && this.getTimeSinceLastPoseTick() >= 0L;
   }

   public void startSitting() {
      if (!this.isSitting()) {
         this.playSound(SoundEvents.ENTITY_CAMEL_SIT);
         this.setPose(EntityPose.SITTING);
         this.emitGameEvent(GameEvent.ENTITY_ACTION);
         this.setLastPoseTick(-this.getWorld().getTime());
      }
   }

   public void startStanding() {
      if (this.isSitting()) {
         this.playSound(SoundEvents.ENTITY_CAMEL_STAND);
         this.setPose(EntityPose.STANDING);
         this.emitGameEvent(GameEvent.ENTITY_ACTION);
         this.setLastPoseTick(this.getWorld().getTime());
      }
   }

   public void setStanding() {
      this.setPose(EntityPose.STANDING);
      this.emitGameEvent(GameEvent.ENTITY_ACTION);
      this.initLastPoseTick(this.getWorld().getTime());
   }

   @VisibleForTesting
   public void setLastPoseTick(long lastPoseTick) {
      this.dataTracker.set(LAST_POSE_TICK, lastPoseTick);
   }

   private void initLastPoseTick(long time) {
      this.setLastPoseTick(Math.max(0L, time - 52L - 1L));
   }

   public long getTimeSinceLastPoseTick() {
      return this.getWorld().getTime() - Math.abs((Long)this.dataTracker.get(LAST_POSE_TICK));
   }

   protected RegistryEntry getEquipSound(EquipmentSlot slot, ItemStack stack, EquippableComponent equippableComponent) {
      return (RegistryEntry)(slot == EquipmentSlot.SADDLE ? SoundEvents.ENTITY_CAMEL_SADDLE : super.getEquipSound(slot, stack, equippableComponent));
   }

   public void onTrackedDataSet(TrackedData data) {
      if (!this.firstUpdate && DASHING.equals(data)) {
         this.dashCooldown = this.dashCooldown == 0 ? 55 : this.dashCooldown;
      }

      super.onTrackedDataSet(data);
   }

   public boolean isTame() {
      return true;
   }

   public void openInventory(PlayerEntity player) {
      if (!this.getWorld().isClient) {
         player.openHorseInventory(this, this.items);
      }

   }

   protected BodyControl createBodyControl() {
      return new CamelBodyControl(this);
   }

   // $FF: synthetic method
   @Nullable
   public PassiveEntity createChild(final ServerWorld world, final PassiveEntity entity) {
      return this.createChild(world, entity);
   }

   static {
      DASHING = DataTracker.registerData(CamelEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
      LAST_POSE_TICK = DataTracker.registerData(CamelEntity.class, TrackedDataHandlerRegistry.LONG);
      SITTING_DIMENSIONS = EntityDimensions.changing(EntityType.CAMEL.getWidth(), EntityType.CAMEL.getHeight() - 1.43F).withEyeHeight(0.845F);
   }

   class CamelMoveControl extends MoveControl {
      public CamelMoveControl() {
         super(CamelEntity.this);
      }

      public void tick() {
         if (this.state == MoveControl.State.MOVE_TO && !CamelEntity.this.isLeashed() && CamelEntity.this.isSitting() && !CamelEntity.this.isChangingPose() && CamelEntity.this.canChangePose()) {
            CamelEntity.this.startStanding();
         }

         super.tick();
      }
   }

   private class CamelLookControl extends LookControl {
      CamelLookControl() {
         super(CamelEntity.this);
      }

      public void tick() {
         if (!CamelEntity.this.hasControllingPassenger()) {
            super.tick();
         }

      }
   }

   private class CamelBodyControl extends BodyControl {
      public CamelBodyControl(final CamelEntity camel) {
         super(camel);
      }

      public void tick() {
         if (!CamelEntity.this.isStationary()) {
            super.tick();
         }

      }
   }
}
