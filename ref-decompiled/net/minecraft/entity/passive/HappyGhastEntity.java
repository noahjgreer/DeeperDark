package net.minecraft.entity.passive;

import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

public class HappyGhastEntity extends AnimalEntity {
   public static final float field_59681 = 0.2375F;
   public static final int field_59682 = 16;
   public static final int field_59683 = 32;
   public static final int field_59684 = 64;
   public static final int field_59685 = 16;
   public static final int field_59686 = 20;
   public static final int field_59687 = 600;
   public static final int field_59688 = 4;
   private static final int field_61061 = 60;
   private static final int field_60551 = 10;
   public static final float field_59689 = 2.0F;
   public static final Predicate FOOD_PREDICATE = (stack) -> {
      return stack.isIn(ItemTags.HAPPY_GHAST_FOOD);
   };
   private int ropeRemovalTimer = 0;
   private int stillTimeout;
   private static final TrackedData HAS_ROPES;
   private static final TrackedData STAYING_STILL;
   private static final float field_60550 = 1.0F;

   public HappyGhastEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.moveControl = new GhastEntity.GhastMoveControl(this, true, this::method_72227);
      this.lookControl = new HappyGhastLookControl();
   }

   private void setStillTimeout(int stillTimeout) {
      if (this.stillTimeout <= 0 && stillTimeout > 0) {
         World var3 = this.getWorld();
         if (var3 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var3;
            this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
            serverWorld.getChunkManager().chunkLoadingManager.sendToOtherNearbyPlayers(this, EntityPositionSyncS2CPacket.create(this));
         }
      }

      this.stillTimeout = stillTimeout;
      this.syncStayingStill();
   }

   private EntityNavigation createGhastlingNavigation(World world) {
      return new GhastlingNavigation(this, world);
   }

   protected void initGoals() {
      this.goalSelector.add(3, new HappyGhastSwimGoal());
      this.goalSelector.add(4, new TemptGoal.HappyGhastTemptGoal(this, 1.0, (stack) -> {
         return !this.isWearingBodyArmor() && !this.isBaby() ? stack.isIn(ItemTags.HAPPY_GHAST_TEMPT_ITEMS) : FOOD_PREDICATE.test(stack);
      }, false, 7.0));
      this.goalSelector.add(5, new GhastEntity.FlyRandomlyGoal(this, 16));
   }

   private void initAdultHappyGhast() {
      this.moveControl = new GhastEntity.GhastMoveControl(this, true, this::method_72227);
      this.lookControl = new HappyGhastLookControl();
      this.navigation = this.createNavigation(this.getWorld());
      World var2 = this.getWorld();
      if (var2 instanceof ServerWorld serverWorld) {
         this.clearGoals((goal) -> {
            return true;
         });
         this.initGoals();
         this.brain.stopAllTasks(serverWorld, this);
         this.brain.forgetAll();
      }

   }

   private void initGhastling() {
      this.moveControl = new FlightMoveControl(this, 180, true);
      this.lookControl = new LookControl(this);
      this.navigation = this.createGhastlingNavigation(this.getWorld());
      this.setStillTimeout(0);
      this.clearGoals((goal) -> {
         return true;
      });
   }

   protected void onGrowUp() {
      if (this.isBaby()) {
         this.initGhastling();
      } else {
         this.initAdultHappyGhast();
      }

      super.onGrowUp();
   }

   public static DefaultAttributeContainer.Builder createHappyGhastAttributes() {
      return AnimalEntity.createAnimalAttributes().add(EntityAttributes.MAX_HEALTH, 20.0).add(EntityAttributes.TEMPT_RANGE, 16.0).add(EntityAttributes.FLYING_SPEED, 0.05).add(EntityAttributes.MOVEMENT_SPEED, 0.05).add(EntityAttributes.FOLLOW_RANGE, 16.0).add(EntityAttributes.CAMERA_DISTANCE, 8.0);
   }

   protected float clampScale(float scale) {
      return Math.min(scale, 1.0F);
   }

   protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
   }

   public boolean isClimbing() {
      return false;
   }

   public void travel(Vec3d movementInput) {
      float f = (float)this.getAttributeValue(EntityAttributes.FLYING_SPEED) * 5.0F / 3.0F;
      this.travelFlying(movementInput, f, f, f);
   }

   public float getPathfindingFavor(BlockPos pos, WorldView world) {
      if (!world.isAir(pos)) {
         return 0.0F;
      } else {
         return world.isAir(pos.down()) && !world.isAir(pos.down(2)) ? 10.0F : 5.0F;
      }
   }

   public boolean canBreatheInWater() {
      return this.isBaby() ? true : super.canBreatheInWater();
   }

   protected boolean shouldFollowLeash() {
      return false;
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
   }

   public float getSoundPitch() {
      return 1.0F;
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.NEUTRAL;
   }

   public int getMinAmbientSoundDelay() {
      int i = super.getMinAmbientSoundDelay();
      return this.hasPassengers() ? i * 6 : i;
   }

   protected SoundEvent getAmbientSound() {
      return this.isBaby() ? SoundEvents.ENTITY_GHASTLING_AMBIENT : SoundEvents.ENTITY_HAPPY_GHAST_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return this.isBaby() ? SoundEvents.ENTITY_GHASTLING_HURT : SoundEvents.ENTITY_HAPPY_GHAST_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isBaby() ? SoundEvents.ENTITY_GHASTLING_DEATH : SoundEvents.ENTITY_HAPPY_GHAST_DEATH;
   }

   public int getLimitPerChunk() {
      return 1;
   }

   public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
      return (PassiveEntity)EntityType.HAPPY_GHAST.create(world, SpawnReason.BREEDING);
   }

   public boolean canEat() {
      return false;
   }

   public float getScaleFactor() {
      return this.isBaby() ? 0.2375F : 1.0F;
   }

   public boolean isBreedingItem(ItemStack stack) {
      return FOOD_PREDICATE.test(stack);
   }

   public boolean canUseSlot(EquipmentSlot slot) {
      if (slot != EquipmentSlot.BODY) {
         return super.canUseSlot(slot);
      } else {
         return this.isAlive() && !this.isBaby();
      }
   }

   protected boolean canDispenserEquipSlot(EquipmentSlot slot) {
      return slot == EquipmentSlot.BODY;
   }

   public ActionResult interactMob(PlayerEntity player, Hand hand) {
      if (this.isBaby()) {
         return super.interactMob(player, hand);
      } else {
         ItemStack itemStack = player.getStackInHand(hand);
         if (!itemStack.isEmpty()) {
            ActionResult actionResult = itemStack.useOnEntity(player, this, hand);
            if (actionResult.isAccepted()) {
               return actionResult;
            }
         }

         if (this.isWearingBodyArmor() && !player.shouldCancelInteraction()) {
            this.addPassenger(player);
            return ActionResult.SUCCESS;
         } else {
            return super.interactMob(player, hand);
         }
      }
   }

   private void addPassenger(PlayerEntity player) {
      if (!this.getWorld().isClient) {
         player.startRiding(this);
      }

   }

   protected void addPassenger(Entity passenger) {
      if (!this.hasPassengers()) {
         this.getWorld().playSound((Entity)null, this.getX(), this.getY(), this.getZ(), (SoundEvent)SoundEvents.ENTITY_HAPPY_GHAST_HARNESS_GOGGLES_DOWN, this.getSoundCategory(), 1.0F, 1.0F);
      }

      super.addPassenger(passenger);
      if (!this.getWorld().isClient) {
         if (!this.hasPlayerOnTop()) {
            this.setStillTimeout(0);
         } else if (this.stillTimeout > 10) {
            this.setStillTimeout(10);
         }
      }

   }

   protected void removePassenger(Entity passenger) {
      super.removePassenger(passenger);
      if (!this.getWorld().isClient) {
         this.setStillTimeout(10);
      }

      if (!this.hasPassengers()) {
         this.clearPositionTarget();
         this.getWorld().playSound((Entity)null, this.getX(), this.getY(), this.getZ(), (SoundEvent)SoundEvents.ENTITY_HAPPY_GHAST_HARNESS_GOGGLES_UP, this.getSoundCategory(), 1.0F, 1.0F);
      }

   }

   protected boolean canAddPassenger(Entity passenger) {
      return this.getPassengerList().size() < 4;
   }

   public @Nullable LivingEntity getControllingPassenger() {
      Entity entity = this.getFirstPassenger();
      if (this.isWearingBodyArmor() && !this.method_72227() && entity instanceof PlayerEntity playerEntity) {
         return playerEntity;
      } else {
         return super.getControllingPassenger();
      }
   }

   protected Vec3d getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput) {
      float f = controllingPlayer.sidewaysSpeed;
      float g = 0.0F;
      float h = 0.0F;
      if (controllingPlayer.forwardSpeed != 0.0F) {
         float i = MathHelper.cos(controllingPlayer.getPitch() * 0.017453292F);
         float j = -MathHelper.sin(controllingPlayer.getPitch() * 0.017453292F);
         if (controllingPlayer.forwardSpeed < 0.0F) {
            i *= -0.5F;
            j *= -0.5F;
         }

         h = j;
         g = i;
      }

      if (controllingPlayer.isJumping()) {
         h += 0.5F;
      }

      return (new Vec3d((double)f, (double)h, (double)g)).multiply(3.9000000953674316 * this.getAttributeValue(EntityAttributes.FLYING_SPEED));
   }

   protected Vec2f getGhastRotation(LivingEntity controllingEntity) {
      return new Vec2f(controllingEntity.getPitch() * 0.5F, controllingEntity.getYaw());
   }

   protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
      super.tickControlled(controllingPlayer, movementInput);
      Vec2f vec2f = this.getGhastRotation(controllingPlayer);
      float f = this.getYaw();
      float g = MathHelper.wrapDegrees(vec2f.y - f);
      float h = 0.08F;
      f += g * 0.08F;
      this.setRotation(f, vec2f.x);
      this.lastYaw = this.bodyYaw = this.headYaw = f;
   }

   protected Brain.Profile createBrainProfile() {
      return HappyGhastBrain.createBrainProfile();
   }

   protected Brain deserializeBrain(Dynamic dynamic) {
      return HappyGhastBrain.create(this.createBrainProfile().deserialize(dynamic));
   }

   protected void mobTick(ServerWorld world) {
      if (this.isBaby()) {
         Profiler profiler = Profilers.get();
         profiler.push("happyGhastBrain");
         this.brain.tick(world, this);
         profiler.pop();
         profiler.push("happyGhastActivityUpdate");
         HappyGhastBrain.updateActivities(this);
         profiler.pop();
      }

      this.updatePositionTarget();
      super.mobTick(world);
   }

   public void tick() {
      super.tick();
      if (!this.getWorld().isClient()) {
         if (this.ropeRemovalTimer > 0) {
            --this.ropeRemovalTimer;
         }

         this.setHasRopes(this.ropeRemovalTimer > 0);
         if (this.stillTimeout > 0) {
            if (this.age > 60) {
               --this.stillTimeout;
            }

            this.setStillTimeout(this.stillTimeout);
         }

         if (this.hasPlayerOnTop()) {
            this.setStillTimeout(10);
         }

      }
   }

   public void tickMovement() {
      if (!this.getWorld().isClient) {
         this.setAlwaysSyncAbsolute(this.method_72227());
      }

      super.tickMovement();
      this.tickRegeneration();
   }

   private int getUpdatedPositionTargetRange() {
      return !this.isBaby() && this.getEquippedStack(EquipmentSlot.BODY).isEmpty() ? 64 : 32;
   }

   private void updatePositionTarget() {
      if (!this.isLeashed() && !this.hasPassengers()) {
         int i = this.getUpdatedPositionTargetRange();
         if (!this.hasPositionTarget() || !this.getPositionTarget().isWithinDistance(this.getBlockPos(), (double)(i + 16)) || i != this.getPositionTargetRange()) {
            this.setPositionTarget(this.getBlockPos(), i);
         }
      }
   }

   private void tickRegeneration() {
      World var2 = this.getWorld();
      if (var2 instanceof ServerWorld serverWorld) {
         if (this.isAlive() && this.deathTime == 0 && this.getMaxHealth() != this.getHealth()) {
            boolean bl = serverWorld.getDimension().natural() && (this.isAtCloudHeight() || serverWorld.getPrecipitation(this.getBlockPos()) != Biome.Precipitation.NONE);
            if (this.age % (bl ? 20 : 600) == 0) {
               this.heal(1.0F);
            }

            return;
         }
      }

   }

   protected void sendAiDebugData() {
      super.sendAiDebugData();
      DebugInfoSender.sendBrainDebugData(this);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(HAS_ROPES, false);
      builder.add(STAYING_STILL, false);
   }

   private void setHasRopes(boolean hasRopes) {
      this.dataTracker.set(HAS_ROPES, hasRopes);
   }

   public boolean hasRopes() {
      return (Boolean)this.dataTracker.get(HAS_ROPES);
   }

   private void syncStayingStill() {
      this.dataTracker.set(STAYING_STILL, this.stillTimeout > 0);
   }

   public boolean isStayingStill() {
      return (Boolean)this.dataTracker.get(STAYING_STILL);
   }

   public boolean hasQuadLeashAttachmentPoints() {
      return true;
   }

   public Vec3d[] getHeldQuadLeashOffsets() {
      return Leashable.createQuadLeashOffsets(this, -0.03125, 0.4375, 0.46875, 0.03125);
   }

   public Vec3d getLeashOffset() {
      return Vec3d.ZERO;
   }

   public double getElasticLeashDistance() {
      return 10.0;
   }

   public double getLeashSnappingDistance() {
      return 16.0;
   }

   public void onLongLeashTick() {
      super.onLongLeashTick();
      this.getMoveControl().setWaiting();
   }

   public void tickHeldLeash(Leashable leashedEntity) {
      if (leashedEntity.canUseQuadLeashAttachmentPoint()) {
         this.ropeRemovalTimer = 5;
      }

   }

   public void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putInt("still_timeout", this.stillTimeout);
   }

   public void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.setStillTimeout(view.getInt("still_timeout", 0));
   }

   public boolean method_72227() {
      return this.isStayingStill() || this.stillTimeout > 0;
   }

   private boolean hasPlayerOnTop() {
      Box box = this.getBoundingBox();
      Box box2 = new Box(box.minX - 1.0, box.maxY - 9.999999747378752E-6, box.minZ - 1.0, box.maxX + 1.0, box.maxY + box.getLengthY() / 2.0, box.maxZ + 1.0);
      Iterator var3 = this.getWorld().getPlayers().iterator();

      while(var3.hasNext()) {
         PlayerEntity playerEntity = (PlayerEntity)var3.next();
         if (!playerEntity.isSpectator()) {
            Entity entity = playerEntity.getRootVehicle();
            if (!(entity instanceof HappyGhastEntity) && box2.contains(entity.getPos())) {
               return true;
            }
         }
      }

      return false;
   }

   protected BodyControl createBodyControl() {
      return new HappyGhastBodyControl();
   }

   public boolean isCollidable(@Nullable Entity entity) {
      if (!this.isBaby() && this.isAlive()) {
         if (this.getWorld().isClient() && entity instanceof PlayerEntity && entity.getPos().y >= this.getBoundingBox().maxY) {
            return true;
         } else {
            return this.hasPassengers() && entity instanceof HappyGhastEntity ? true : this.method_72227();
         }
      } else {
         return false;
      }
   }

   public boolean isFlyingVehicle() {
      return !this.isBaby();
   }

   static {
      HAS_ROPES = DataTracker.registerData(HappyGhastEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
      STAYING_STILL = DataTracker.registerData(HappyGhastEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
   }

   private class HappyGhastLookControl extends LookControl {
      HappyGhastLookControl() {
         super(HappyGhastEntity.this);
      }

      public void tick() {
         if (HappyGhastEntity.this.method_72227()) {
            float f = getYawToSubtract(HappyGhastEntity.this.getYaw());
            HappyGhastEntity.this.setYaw(HappyGhastEntity.this.getYaw() - f);
            HappyGhastEntity.this.setHeadYaw(HappyGhastEntity.this.getYaw());
         } else if (this.lookAtTimer > 0) {
            --this.lookAtTimer;
            double d = this.x - HappyGhastEntity.this.getX();
            double e = this.z - HappyGhastEntity.this.getZ();
            HappyGhastEntity.this.setYaw(-((float)MathHelper.atan2(d, e)) * 57.295776F);
            HappyGhastEntity.this.bodyYaw = HappyGhastEntity.this.getYaw();
            HappyGhastEntity.this.headYaw = HappyGhastEntity.this.bodyYaw;
         } else {
            GhastEntity.updateYaw(this.entity);
         }
      }

      public static float getYawToSubtract(float yaw) {
         float f = yaw % 90.0F;
         if (f >= 45.0F) {
            f -= 90.0F;
         }

         if (f < -45.0F) {
            f += 90.0F;
         }

         return f;
      }
   }

   private static class GhastlingNavigation extends BirdNavigation {
      public GhastlingNavigation(HappyGhastEntity entity, World world) {
         super(entity, world);
         this.setCanOpenDoors(false);
         this.setCanSwim(true);
         this.setMaxFollowRange(48.0F);
      }

      protected boolean canPathDirectlyThrough(Vec3d origin, Vec3d target) {
         return doesNotCollide(this.entity, origin, target, false);
      }
   }

   private class HappyGhastSwimGoal extends SwimGoal {
      public HappyGhastSwimGoal() {
         super(HappyGhastEntity.this);
      }

      public boolean canStart() {
         return !HappyGhastEntity.this.method_72227() && super.canStart();
      }
   }

   class HappyGhastBodyControl extends BodyControl {
      public HappyGhastBodyControl() {
         super(HappyGhastEntity.this);
      }

      public void tick() {
         if (HappyGhastEntity.this.hasPassengers()) {
            HappyGhastEntity.this.headYaw = HappyGhastEntity.this.getYaw();
            HappyGhastEntity.this.bodyYaw = HappyGhastEntity.this.headYaw;
         }

         super.tick();
      }
   }
}
