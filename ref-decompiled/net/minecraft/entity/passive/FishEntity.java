package net.minecraft.entity.passive;

import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class FishEntity extends WaterCreatureEntity implements Bucketable {
   private static final TrackedData FROM_BUCKET;
   private static final boolean DEFAULT_FROM_BUCKET = false;

   public FishEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.moveControl = new FishMoveControl(this);
   }

   public static DefaultAttributeContainer.Builder createFishAttributes() {
      return MobEntity.createMobAttributes().add(EntityAttributes.MAX_HEALTH, 3.0);
   }

   public boolean cannotDespawn() {
      return super.cannotDespawn() || this.isFromBucket();
   }

   public boolean canImmediatelyDespawn(double distanceSquared) {
      return !this.isFromBucket() && !this.hasCustomName();
   }

   public int getLimitPerChunk() {
      return 8;
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(FROM_BUCKET, false);
   }

   public boolean isFromBucket() {
      return (Boolean)this.dataTracker.get(FROM_BUCKET);
   }

   public void setFromBucket(boolean fromBucket) {
      this.dataTracker.set(FROM_BUCKET, fromBucket);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putBoolean("FromBucket", this.isFromBucket());
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.setFromBucket(view.getBoolean("FromBucket", false));
   }

   protected void initGoals() {
      super.initGoals();
      this.goalSelector.add(0, new EscapeDangerGoal(this, 1.25));
      GoalSelector var10000 = this.goalSelector;
      Predicate var10009 = EntityPredicates.EXCEPT_SPECTATOR;
      Objects.requireNonNull(var10009);
      var10000.add(2, new FleeEntityGoal(this, PlayerEntity.class, 8.0F, 1.6, 1.4, var10009::test));
      this.goalSelector.add(4, new SwimToRandomPlaceGoal(this));
   }

   protected EntityNavigation createNavigation(World world) {
      return new SwimNavigation(this, world);
   }

   public void travel(Vec3d movementInput) {
      if (this.isTouchingWater()) {
         this.updateVelocity(0.01F, movementInput);
         this.move(MovementType.SELF, this.getVelocity());
         this.setVelocity(this.getVelocity().multiply(0.9));
         if (this.getTarget() == null) {
            this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
         }
      } else {
         super.travel(movementInput);
      }

   }

   public void tickMovement() {
      if (!this.isTouchingWater() && this.isOnGround() && this.verticalCollision) {
         this.setVelocity(this.getVelocity().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F), 0.4000000059604645, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F)));
         this.setOnGround(false);
         this.velocityDirty = true;
         this.playSound(this.getFlopSound());
      }

      super.tickMovement();
   }

   protected ActionResult interactMob(PlayerEntity player, Hand hand) {
      return (ActionResult)Bucketable.tryBucket(player, hand, this).orElse(super.interactMob(player, hand));
   }

   public void copyDataToStack(ItemStack stack) {
      Bucketable.copyDataToStack(this, stack);
   }

   public void copyDataFromNbt(NbtCompound nbt) {
      Bucketable.copyDataFromNbt(this, nbt);
   }

   public SoundEvent getBucketFillSound() {
      return SoundEvents.ITEM_BUCKET_FILL_FISH;
   }

   protected boolean hasSelfControl() {
      return true;
   }

   protected abstract SoundEvent getFlopSound();

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_FISH_SWIM;
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
   }

   static {
      FROM_BUCKET = DataTracker.registerData(FishEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
   }

   private static class FishMoveControl extends MoveControl {
      private final FishEntity fish;

      FishMoveControl(FishEntity owner) {
         super(owner);
         this.fish = owner;
      }

      public void tick() {
         if (this.fish.isSubmergedIn(FluidTags.WATER)) {
            this.fish.setVelocity(this.fish.getVelocity().add(0.0, 0.005, 0.0));
         }

         if (this.state == MoveControl.State.MOVE_TO && !this.fish.getNavigation().isIdle()) {
            float f = (float)(this.speed * this.fish.getAttributeValue(EntityAttributes.MOVEMENT_SPEED));
            this.fish.setMovementSpeed(MathHelper.lerp(0.125F, this.fish.getMovementSpeed(), f));
            double d = this.targetX - this.fish.getX();
            double e = this.targetY - this.fish.getY();
            double g = this.targetZ - this.fish.getZ();
            if (e != 0.0) {
               double h = Math.sqrt(d * d + e * e + g * g);
               this.fish.setVelocity(this.fish.getVelocity().add(0.0, (double)this.fish.getMovementSpeed() * (e / h) * 0.1, 0.0));
            }

            if (d != 0.0 || g != 0.0) {
               float i = (float)(MathHelper.atan2(g, d) * 57.2957763671875) - 90.0F;
               this.fish.setYaw(this.wrapDegrees(this.fish.getYaw(), i, 90.0F));
               this.fish.bodyYaw = this.fish.getYaw();
            }

         } else {
            this.fish.setMovementSpeed(0.0F);
         }
      }
   }

   static class SwimToRandomPlaceGoal extends SwimAroundGoal {
      private final FishEntity fish;

      public SwimToRandomPlaceGoal(FishEntity fish) {
         super(fish, 1.0, 40);
         this.fish = fish;
      }

      public boolean canStart() {
         return this.fish.hasSelfControl() && super.canStart();
      }
   }
}
