package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class VexEntity extends HostileEntity implements Ownable {
   public static final float field_30502 = 45.836624F;
   public static final int field_28645 = MathHelper.ceil(3.9269907F);
   protected static final TrackedData VEX_FLAGS;
   private static final int CHARGING_FLAG = 1;
   @Nullable
   private LazyEntityReference owner;
   @Nullable
   private BlockPos bounds;
   private boolean alive;
   private int lifeTicks;

   public VexEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.moveControl = new VexMoveControl(this);
      this.experiencePoints = 3;
   }

   public boolean isFlappingWings() {
      return this.age % field_28645 == 0;
   }

   protected boolean shouldTickBlockCollision() {
      return !this.isRemoved();
   }

   public void tick() {
      this.noClip = true;
      super.tick();
      this.noClip = false;
      this.setNoGravity(true);
      if (this.alive && --this.lifeTicks <= 0) {
         this.lifeTicks = 20;
         this.serverDamage(this.getDamageSources().starve(), 1.0F);
      }

   }

   protected void initGoals() {
      super.initGoals();
      this.goalSelector.add(0, new SwimGoal(this));
      this.goalSelector.add(4, new ChargeTargetGoal());
      this.goalSelector.add(8, new LookAtTargetGoal());
      this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
      this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
      this.targetSelector.add(1, (new RevengeGoal(this, new Class[]{RaiderEntity.class})).setGroupRevenge());
      this.targetSelector.add(2, new TrackOwnerTargetGoal(this));
      this.targetSelector.add(3, new ActiveTargetGoal(this, PlayerEntity.class, true));
   }

   public static DefaultAttributeContainer.Builder createVexAttributes() {
      return HostileEntity.createHostileAttributes().add(EntityAttributes.MAX_HEALTH, 14.0).add(EntityAttributes.ATTACK_DAMAGE, 4.0);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(VEX_FLAGS, (byte)0);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.bounds = (BlockPos)view.read("bound_pos", BlockPos.CODEC).orElse((Object)null);
      view.getOptionalInt("life_ticks").ifPresentOrElse(this::setLifeTicks, () -> {
         this.alive = false;
      });
      this.owner = LazyEntityReference.fromData(view, "owner");
   }

   public void copyFrom(Entity original) {
      super.copyFrom(original);
      if (original instanceof VexEntity vexEntity) {
         this.owner = vexEntity.owner;
      }

   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putNullable("bound_pos", BlockPos.CODEC, this.bounds);
      if (this.alive) {
         view.putInt("life_ticks", this.lifeTicks);
      }

      LazyEntityReference.writeData(this.owner, view, "owner");
   }

   @Nullable
   public MobEntity getOwner() {
      return (MobEntity)LazyEntityReference.resolve(this.owner, this.getWorld(), MobEntity.class);
   }

   @Nullable
   public BlockPos getBounds() {
      return this.bounds;
   }

   public void setBounds(@Nullable BlockPos bounds) {
      this.bounds = bounds;
   }

   private boolean areFlagsSet(int mask) {
      int i = (Byte)this.dataTracker.get(VEX_FLAGS);
      return (i & mask) != 0;
   }

   private void setVexFlag(int mask, boolean value) {
      int i = (Byte)this.dataTracker.get(VEX_FLAGS);
      if (value) {
         i |= mask;
      } else {
         i &= ~mask;
      }

      this.dataTracker.set(VEX_FLAGS, (byte)(i & 255));
   }

   public boolean isCharging() {
      return this.areFlagsSet(1);
   }

   public void setCharging(boolean charging) {
      this.setVexFlag(1, charging);
   }

   public void setOwner(MobEntity owner) {
      this.owner = new LazyEntityReference(owner);
   }

   public void setLifeTicks(int lifeTicks) {
      this.alive = true;
      this.lifeTicks = lifeTicks;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_VEX_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_VEX_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_VEX_HURT;
   }

   public float getBrightnessAtEyes() {
      return 1.0F;
   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      Random random = world.getRandom();
      this.initEquipment(random, difficulty);
      this.updateEnchantments(world, random, difficulty);
      return super.initialize(world, difficulty, spawnReason, entityData);
   }

   protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
      this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
      this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0F);
   }

   // $FF: synthetic method
   @Nullable
   public Entity getOwner() {
      return this.getOwner();
   }

   static {
      VEX_FLAGS = DataTracker.registerData(VexEntity.class, TrackedDataHandlerRegistry.BYTE);
   }

   private class VexMoveControl extends MoveControl {
      public VexMoveControl(final VexEntity owner) {
         super(owner);
      }

      public void tick() {
         if (this.state == MoveControl.State.MOVE_TO) {
            Vec3d vec3d = new Vec3d(this.targetX - VexEntity.this.getX(), this.targetY - VexEntity.this.getY(), this.targetZ - VexEntity.this.getZ());
            double d = vec3d.length();
            if (d < VexEntity.this.getBoundingBox().getAverageSideLength()) {
               this.state = MoveControl.State.WAIT;
               VexEntity.this.setVelocity(VexEntity.this.getVelocity().multiply(0.5));
            } else {
               VexEntity.this.setVelocity(VexEntity.this.getVelocity().add(vec3d.multiply(this.speed * 0.05 / d)));
               if (VexEntity.this.getTarget() == null) {
                  Vec3d vec3d2 = VexEntity.this.getVelocity();
                  VexEntity.this.setYaw(-((float)MathHelper.atan2(vec3d2.x, vec3d2.z)) * 57.295776F);
                  VexEntity.this.bodyYaw = VexEntity.this.getYaw();
               } else {
                  double e = VexEntity.this.getTarget().getX() - VexEntity.this.getX();
                  double f = VexEntity.this.getTarget().getZ() - VexEntity.this.getZ();
                  VexEntity.this.setYaw(-((float)MathHelper.atan2(e, f)) * 57.295776F);
                  VexEntity.this.bodyYaw = VexEntity.this.getYaw();
               }
            }

         }
      }
   }

   private class ChargeTargetGoal extends Goal {
      public ChargeTargetGoal() {
         this.setControls(EnumSet.of(Goal.Control.MOVE));
      }

      public boolean canStart() {
         LivingEntity livingEntity = VexEntity.this.getTarget();
         if (livingEntity != null && livingEntity.isAlive() && !VexEntity.this.getMoveControl().isMoving() && VexEntity.this.random.nextInt(toGoalTicks(7)) == 0) {
            return VexEntity.this.squaredDistanceTo(livingEntity) > 4.0;
         } else {
            return false;
         }
      }

      public boolean shouldContinue() {
         return VexEntity.this.getMoveControl().isMoving() && VexEntity.this.isCharging() && VexEntity.this.getTarget() != null && VexEntity.this.getTarget().isAlive();
      }

      public void start() {
         LivingEntity livingEntity = VexEntity.this.getTarget();
         if (livingEntity != null) {
            Vec3d vec3d = livingEntity.getEyePos();
            VexEntity.this.moveControl.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
         }

         VexEntity.this.setCharging(true);
         VexEntity.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
      }

      public void stop() {
         VexEntity.this.setCharging(false);
      }

      public boolean shouldRunEveryTick() {
         return true;
      }

      public void tick() {
         LivingEntity livingEntity = VexEntity.this.getTarget();
         if (livingEntity != null) {
            if (VexEntity.this.getBoundingBox().intersects(livingEntity.getBoundingBox())) {
               VexEntity.this.tryAttack(castToServerWorld(VexEntity.this.getWorld()), livingEntity);
               VexEntity.this.setCharging(false);
            } else {
               double d = VexEntity.this.squaredDistanceTo(livingEntity);
               if (d < 9.0) {
                  Vec3d vec3d = livingEntity.getEyePos();
                  VexEntity.this.moveControl.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
               }
            }

         }
      }
   }

   class LookAtTargetGoal extends Goal {
      public LookAtTargetGoal() {
         this.setControls(EnumSet.of(Goal.Control.MOVE));
      }

      public boolean canStart() {
         return !VexEntity.this.getMoveControl().isMoving() && VexEntity.this.random.nextInt(toGoalTicks(7)) == 0;
      }

      public boolean shouldContinue() {
         return false;
      }

      public void tick() {
         BlockPos blockPos = VexEntity.this.getBounds();
         if (blockPos == null) {
            blockPos = VexEntity.this.getBlockPos();
         }

         for(int i = 0; i < 3; ++i) {
            BlockPos blockPos2 = blockPos.add(VexEntity.this.random.nextInt(15) - 7, VexEntity.this.random.nextInt(11) - 5, VexEntity.this.random.nextInt(15) - 7);
            if (VexEntity.this.getWorld().isAir(blockPos2)) {
               VexEntity.this.moveControl.moveTo((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5, 0.25);
               if (VexEntity.this.getTarget() == null) {
                  VexEntity.this.getLookControl().lookAt((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5, 180.0F, 20.0F);
               }
               break;
            }
         }

      }
   }

   private class TrackOwnerTargetGoal extends TrackTargetGoal {
      private final TargetPredicate targetPredicate = TargetPredicate.createNonAttackable().ignoreVisibility().ignoreDistanceScalingFactor();

      public TrackOwnerTargetGoal(final PathAwareEntity mob) {
         super(mob, false);
      }

      public boolean canStart() {
         MobEntity mobEntity = VexEntity.this.getOwner();
         return mobEntity != null && mobEntity.getTarget() != null && this.canTrack(mobEntity.getTarget(), this.targetPredicate);
      }

      public void start() {
         MobEntity mobEntity = VexEntity.this.getOwner();
         VexEntity.this.setTarget(mobEntity != null ? mobEntity.getTarget() : null);
         super.start();
      }
   }
}
