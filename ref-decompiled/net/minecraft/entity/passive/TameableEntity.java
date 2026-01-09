package net.minecraft.entity.passive;

import java.util.Optional;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class TameableEntity extends AnimalEntity implements Tameable {
   public static final int field_52002 = 144;
   private static final int field_52003 = 2;
   private static final int field_52004 = 3;
   private static final int field_52005 = 1;
   private static final boolean DEFAULT_SITTING = false;
   protected static final TrackedData TAMEABLE_FLAGS;
   protected static final TrackedData OWNER_UUID;
   private boolean sitting = false;

   protected TameableEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(TAMEABLE_FLAGS, (byte)0);
      builder.add(OWNER_UUID, Optional.empty());
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      LazyEntityReference lazyEntityReference = this.getOwnerReference();
      LazyEntityReference.writeData(lazyEntityReference, view, "Owner");
      view.putBoolean("Sitting", this.sitting);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      LazyEntityReference lazyEntityReference = LazyEntityReference.fromDataOrPlayerName(view, "Owner", this.getWorld());
      if (lazyEntityReference != null) {
         try {
            this.dataTracker.set(OWNER_UUID, Optional.of(lazyEntityReference));
            this.setTamed(true, false);
         } catch (Throwable var4) {
            this.setTamed(false, true);
         }
      } else {
         this.dataTracker.set(OWNER_UUID, Optional.empty());
         this.setTamed(false, true);
      }

      this.sitting = view.getBoolean("Sitting", false);
      this.setInSittingPose(this.sitting);
   }

   public boolean canBeLeashed() {
      return true;
   }

   protected void showEmoteParticle(boolean positive) {
      ParticleEffect particleEffect = ParticleTypes.HEART;
      if (!positive) {
         particleEffect = ParticleTypes.SMOKE;
      }

      for(int i = 0; i < 7; ++i) {
         double d = this.random.nextGaussian() * 0.02;
         double e = this.random.nextGaussian() * 0.02;
         double f = this.random.nextGaussian() * 0.02;
         this.getWorld().addParticleClient(particleEffect, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
      }

   }

   public void handleStatus(byte status) {
      if (status == 7) {
         this.showEmoteParticle(true);
      } else if (status == 6) {
         this.showEmoteParticle(false);
      } else {
         super.handleStatus(status);
      }

   }

   public boolean isTamed() {
      return ((Byte)this.dataTracker.get(TAMEABLE_FLAGS) & 4) != 0;
   }

   public void setTamed(boolean tamed, boolean updateAttributes) {
      byte b = (Byte)this.dataTracker.get(TAMEABLE_FLAGS);
      if (tamed) {
         this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b | 4));
      } else {
         this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b & -5));
      }

      if (updateAttributes) {
         this.updateAttributesForTamed();
      }

   }

   protected void updateAttributesForTamed() {
   }

   public boolean isInSittingPose() {
      return ((Byte)this.dataTracker.get(TAMEABLE_FLAGS) & 1) != 0;
   }

   public void setInSittingPose(boolean inSittingPose) {
      byte b = (Byte)this.dataTracker.get(TAMEABLE_FLAGS);
      if (inSittingPose) {
         this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b | 1));
      } else {
         this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b & -2));
      }

   }

   @Nullable
   public LazyEntityReference getOwnerReference() {
      return (LazyEntityReference)((Optional)this.dataTracker.get(OWNER_UUID)).orElse((Object)null);
   }

   public void setOwner(@Nullable LivingEntity owner) {
      this.dataTracker.set(OWNER_UUID, Optional.ofNullable(owner).map(LazyEntityReference::new));
   }

   public void setOwner(@Nullable LazyEntityReference owner) {
      this.dataTracker.set(OWNER_UUID, Optional.ofNullable(owner));
   }

   public void setTamedBy(PlayerEntity player) {
      this.setTamed(true, true);
      this.setOwner((LivingEntity)player);
      if (player instanceof ServerPlayerEntity serverPlayerEntity) {
         Criteria.TAME_ANIMAL.trigger(serverPlayerEntity, this);
      }

   }

   public boolean canTarget(LivingEntity target) {
      return this.isOwner(target) ? false : super.canTarget(target);
   }

   public boolean isOwner(LivingEntity entity) {
      return entity == this.getOwner();
   }

   public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
      return true;
   }

   @Nullable
   public Team getScoreboardTeam() {
      Team team = super.getScoreboardTeam();
      if (team != null) {
         return team;
      } else {
         if (this.isTamed()) {
            LivingEntity livingEntity = this.getTopLevelOwner();
            if (livingEntity != null) {
               return livingEntity.getScoreboardTeam();
            }
         }

         return null;
      }
   }

   protected boolean isInSameTeam(Entity other) {
      if (this.isTamed()) {
         LivingEntity livingEntity = this.getTopLevelOwner();
         if (other == livingEntity) {
            return true;
         }

         if (livingEntity != null) {
            return livingEntity.isInSameTeam(other);
         }
      }

      return super.isInSameTeam(other);
   }

   public void onDeath(DamageSource damageSource) {
      World var3 = this.getWorld();
      if (var3 instanceof ServerWorld serverWorld) {
         if (serverWorld.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES)) {
            LivingEntity var4 = this.getOwner();
            if (var4 instanceof ServerPlayerEntity) {
               ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var4;
               serverPlayerEntity.sendMessage(this.getDamageTracker().getDeathMessage());
            }
         }
      }

      super.onDeath(damageSource);
   }

   public boolean isSitting() {
      return this.sitting;
   }

   public void setSitting(boolean sitting) {
      this.sitting = sitting;
   }

   public void tryTeleportToOwner() {
      LivingEntity livingEntity = this.getOwner();
      if (livingEntity != null) {
         this.tryTeleportNear(livingEntity.getBlockPos());
      }

   }

   public boolean shouldTryTeleportToOwner() {
      LivingEntity livingEntity = this.getOwner();
      return livingEntity != null && this.squaredDistanceTo(this.getOwner()) >= 144.0;
   }

   private void tryTeleportNear(BlockPos pos) {
      for(int i = 0; i < 10; ++i) {
         int j = this.random.nextBetween(-3, 3);
         int k = this.random.nextBetween(-3, 3);
         if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
            int l = this.random.nextBetween(-1, 1);
            if (this.tryTeleportTo(pos.getX() + j, pos.getY() + l, pos.getZ() + k)) {
               return;
            }
         }
      }

   }

   private boolean tryTeleportTo(int x, int y, int z) {
      if (!this.canTeleportTo(new BlockPos(x, y, z))) {
         return false;
      } else {
         this.refreshPositionAndAngles((double)x + 0.5, (double)y, (double)z + 0.5, this.getYaw(), this.getPitch());
         this.navigation.stop();
         return true;
      }
   }

   private boolean canTeleportTo(BlockPos pos) {
      PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType((MobEntity)this, (BlockPos)pos);
      if (pathNodeType != PathNodeType.WALKABLE) {
         return false;
      } else {
         BlockState blockState = this.getWorld().getBlockState(pos.down());
         if (!this.canTeleportOntoLeaves() && blockState.getBlock() instanceof LeavesBlock) {
            return false;
         } else {
            BlockPos blockPos = pos.subtract(this.getBlockPos());
            return this.getWorld().isSpaceEmpty(this, this.getBoundingBox().offset(blockPos));
         }
      }
   }

   public final boolean cannotFollowOwner() {
      return this.isSitting() || this.hasVehicle() || this.mightBeLeashed() || this.getOwner() != null && this.getOwner().isSpectator();
   }

   protected boolean canTeleportOntoLeaves() {
      return false;
   }

   static {
      TAMEABLE_FLAGS = DataTracker.registerData(TameableEntity.class, TrackedDataHandlerRegistry.BYTE);
      OWNER_UUID = DataTracker.registerData(TameableEntity.class, TrackedDataHandlerRegistry.LAZY_ENTITY_REFERENCE);
   }

   public class TameableEscapeDangerGoal extends EscapeDangerGoal {
      public TameableEscapeDangerGoal(final double speed, final TagKey dangerousDamageTypes) {
         super(TameableEntity.this, speed, (TagKey)dangerousDamageTypes);
      }

      public TameableEscapeDangerGoal(final double speed) {
         super(TameableEntity.this, speed);
      }

      public void tick() {
         if (!TameableEntity.this.cannotFollowOwner() && TameableEntity.this.shouldTryTeleportToOwner()) {
            TameableEntity.this.tryTeleportToOwner();
         }

         super.tick();
      }
   }
}
