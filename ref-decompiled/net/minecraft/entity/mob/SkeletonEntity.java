package net.minecraft.entity.mob;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;

public class SkeletonEntity extends AbstractSkeletonEntity {
   private static final int TOTAL_CONVERSION_TIME = 300;
   private static final TrackedData CONVERTING;
   public static final String STRAY_CONVERSION_TIME_KEY = "StrayConversionTime";
   private static final int DEFAULT_STRAY_CONVERSION_TIME = -1;
   private int inPowderSnowTime;
   private int conversionTime;

   public SkeletonEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(CONVERTING, false);
   }

   public boolean isConverting() {
      return (Boolean)this.getDataTracker().get(CONVERTING);
   }

   public void setConverting(boolean converting) {
      this.dataTracker.set(CONVERTING, converting);
   }

   public boolean isShaking() {
      return this.isConverting();
   }

   public void tick() {
      if (!this.getWorld().isClient && this.isAlive() && !this.isAiDisabled()) {
         if (this.inPowderSnow) {
            if (this.isConverting()) {
               --this.conversionTime;
               if (this.conversionTime < 0) {
                  this.convertToStray();
               }
            } else {
               ++this.inPowderSnowTime;
               if (this.inPowderSnowTime >= 140) {
                  this.setConversionTime(300);
               }
            }
         } else {
            this.inPowderSnowTime = -1;
            this.setConverting(false);
         }
      }

      super.tick();
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putInt("StrayConversionTime", this.isConverting() ? this.conversionTime : -1);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      int i = view.getInt("StrayConversionTime", -1);
      if (i != -1) {
         this.setConversionTime(i);
      } else {
         this.setConverting(false);
      }

   }

   @VisibleForTesting
   public void setConversionTime(int time) {
      this.conversionTime = time;
      this.setConverting(true);
   }

   protected void convertToStray() {
      this.convertTo(EntityType.STRAY, EntityConversionContext.create(this, true, true), (stray) -> {
         if (!this.isSilent()) {
            this.getWorld().syncWorldEvent((Entity)null, 1048, this.getBlockPos(), 0);
         }

      });
   }

   public boolean canFreeze() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SKELETON_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_SKELETON_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SKELETON_DEATH;
   }

   SoundEvent getStepSound() {
      return SoundEvents.ENTITY_SKELETON_STEP;
   }

   protected void dropEquipment(ServerWorld world, DamageSource source, boolean causedByPlayer) {
      super.dropEquipment(world, source, causedByPlayer);
      Entity entity = source.getAttacker();
      if (entity instanceof CreeperEntity creeperEntity) {
         if (creeperEntity.shouldDropHead()) {
            creeperEntity.onHeadDropped();
            this.dropItem(world, Items.SKELETON_SKULL);
         }
      }

   }

   static {
      CONVERTING = DataTracker.registerData(SkeletonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
   }
}
