package net.minecraft.entity.passive;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;

public class PufferfishEntity extends FishEntity {
   private static final TrackedData PUFF_STATE;
   int inflateTicks;
   int deflateTicks;
   private static final TargetPredicate.EntityPredicate BLOW_UP_FILTER;
   static final TargetPredicate BLOW_UP_TARGET_PREDICATE;
   public static final int NOT_PUFFED = 0;
   public static final int SEMI_PUFFED = 1;
   public static final int FULLY_PUFFED = 2;
   private static final int DEFAULT_PUFF_STATE = 0;

   public PufferfishEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.calculateDimensions();
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(PUFF_STATE, 0);
   }

   public int getPuffState() {
      return (Integer)this.dataTracker.get(PUFF_STATE);
   }

   public void setPuffState(int puffState) {
      this.dataTracker.set(PUFF_STATE, puffState);
   }

   public void onTrackedDataSet(TrackedData data) {
      if (PUFF_STATE.equals(data)) {
         this.calculateDimensions();
      }

      super.onTrackedDataSet(data);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putInt("PuffState", this.getPuffState());
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.setPuffState(Math.min(view.getInt("PuffState", 0), 2));
   }

   public ItemStack getBucketItem() {
      return new ItemStack(Items.PUFFERFISH_BUCKET);
   }

   protected void initGoals() {
      super.initGoals();
      this.goalSelector.add(1, new InflateGoal(this));
   }

   public void tick() {
      if (!this.getWorld().isClient && this.isAlive() && this.canActVoluntarily()) {
         if (this.inflateTicks > 0) {
            if (this.getPuffState() == 0) {
               this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP);
               this.setPuffState(1);
            } else if (this.inflateTicks > 40 && this.getPuffState() == 1) {
               this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP);
               this.setPuffState(2);
            }

            ++this.inflateTicks;
         } else if (this.getPuffState() != 0) {
            if (this.deflateTicks > 60 && this.getPuffState() == 2) {
               this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT);
               this.setPuffState(1);
            } else if (this.deflateTicks > 100 && this.getPuffState() == 1) {
               this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT);
               this.setPuffState(0);
            }

            ++this.deflateTicks;
         }
      }

      super.tick();
   }

   public void tickMovement() {
      super.tickMovement();
      World var2 = this.getWorld();
      if (var2 instanceof ServerWorld serverWorld) {
         if (this.isAlive() && this.getPuffState() > 0) {
            List list = this.getWorld().getEntitiesByClass(MobEntity.class, this.getBoundingBox().expand(0.3), (mobEntityx) -> {
               return BLOW_UP_TARGET_PREDICATE.test(serverWorld, this, mobEntityx);
            });
            Iterator var3 = list.iterator();

            while(var3.hasNext()) {
               MobEntity mobEntity = (MobEntity)var3.next();
               if (mobEntity.isAlive()) {
                  this.sting(serverWorld, mobEntity);
               }
            }
         }
      }

   }

   private void sting(ServerWorld world, MobEntity target) {
      int i = this.getPuffState();
      if (target.damage(world, this.getDamageSources().mobAttack(this), (float)(1 + i))) {
         target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60 * i, 0), this);
         this.playSound(SoundEvents.ENTITY_PUFFER_FISH_STING, 1.0F, 1.0F);
      }

   }

   public void onPlayerCollision(PlayerEntity player) {
      int i = this.getPuffState();
      if (player instanceof ServerPlayerEntity serverPlayerEntity) {
         if (i > 0 && player.damage(serverPlayerEntity.getWorld(), this.getDamageSources().mobAttack(this), (float)(1 + i))) {
            if (!this.isSilent()) {
               serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PUFFERFISH_STING, 0.0F));
            }

            player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60 * i, 0), this);
         }
      }

   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_PUFFER_FISH_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_PUFFER_FISH_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ENTITY_PUFFER_FISH_FLOP;
   }

   public EntityDimensions getBaseDimensions(EntityPose pose) {
      return super.getBaseDimensions(pose).scaled(getScaleForPuffState(this.getPuffState()));
   }

   private static float getScaleForPuffState(int puffState) {
      switch (puffState) {
         case 0:
            return 0.5F;
         case 1:
            return 0.7F;
         default:
            return 1.0F;
      }
   }

   static {
      PUFF_STATE = DataTracker.registerData(PufferfishEntity.class, TrackedDataHandlerRegistry.INTEGER);
      BLOW_UP_FILTER = (entity, world) -> {
         if (entity instanceof PlayerEntity playerEntity) {
            if (playerEntity.isCreative()) {
               return false;
            }
         }

         return !entity.getType().isIn(EntityTypeTags.NOT_SCARY_FOR_PUFFERFISH);
      };
      BLOW_UP_TARGET_PREDICATE = TargetPredicate.createNonAttackable().ignoreDistanceScalingFactor().ignoreVisibility().setPredicate(BLOW_UP_FILTER);
   }

   private static class InflateGoal extends Goal {
      private final PufferfishEntity pufferfish;

      public InflateGoal(PufferfishEntity pufferfish) {
         this.pufferfish = pufferfish;
      }

      public boolean canStart() {
         List list = this.pufferfish.getWorld().getEntitiesByClass(LivingEntity.class, this.pufferfish.getBoundingBox().expand(2.0), (livingEntity) -> {
            return PufferfishEntity.BLOW_UP_TARGET_PREDICATE.test(getServerWorld(this.pufferfish), this.pufferfish, livingEntity);
         });
         return !list.isEmpty();
      }

      public void start() {
         this.pufferfish.inflateTicks = 1;
         this.pufferfish.deflateTicks = 0;
      }

      public void stop() {
         this.pufferfish.inflateTicks = 0;
      }
   }
}
