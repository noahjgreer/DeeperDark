package net.minecraft.entity.passive;

import java.util.Objects;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.EntityAttachments;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HorseEntity extends AbstractHorseEntity {
   private static final TrackedData VARIANT;
   private static final EntityDimensions BABY_BASE_DIMENSIONS;
   private static final int DEFAULT_VARIANT = 0;

   public HorseEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   protected void initAttributes(Random random) {
      EntityAttributeInstance var10000 = this.getAttributeInstance(EntityAttributes.MAX_HEALTH);
      Objects.requireNonNull(random);
      var10000.setBaseValue((double)getChildHealthBonus(random::nextInt));
      var10000 = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
      Objects.requireNonNull(random);
      var10000.setBaseValue(getChildMovementSpeedBonus(random::nextDouble));
      var10000 = this.getAttributeInstance(EntityAttributes.JUMP_STRENGTH);
      Objects.requireNonNull(random);
      var10000.setBaseValue(getChildJumpStrengthBonus(random::nextDouble));
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(VARIANT, 0);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putInt("Variant", this.getHorseVariant());
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.setHorseVariant(view.getInt("Variant", 0));
   }

   private void setHorseVariant(int variant) {
      this.dataTracker.set(VARIANT, variant);
   }

   private int getHorseVariant() {
      return (Integer)this.dataTracker.get(VARIANT);
   }

   private void setHorseVariant(HorseColor color, HorseMarking marking) {
      this.setHorseVariant(color.getIndex() & 255 | marking.getIndex() << 8 & '\uff00');
   }

   public HorseColor getHorseColor() {
      return HorseColor.byIndex(this.getHorseVariant() & 255);
   }

   private void setHorseColor(HorseColor color) {
      this.setHorseVariant(color.getIndex() & 255 | this.getHorseVariant() & -256);
   }

   @Nullable
   public Object get(ComponentType type) {
      return type == DataComponentTypes.HORSE_VARIANT ? castComponentValue(type, this.getHorseColor()) : super.get(type);
   }

   protected void copyComponentsFrom(ComponentsAccess from) {
      this.copyComponentFrom(from, DataComponentTypes.HORSE_VARIANT);
      super.copyComponentsFrom(from);
   }

   protected boolean setApplicableComponent(ComponentType type, Object value) {
      if (type == DataComponentTypes.HORSE_VARIANT) {
         this.setHorseColor((HorseColor)castComponentValue(DataComponentTypes.HORSE_VARIANT, value));
         return true;
      } else {
         return super.setApplicableComponent(type, value);
      }
   }

   public HorseMarking getMarking() {
      return HorseMarking.byIndex((this.getHorseVariant() & '\uff00') >> 8);
   }

   protected void playWalkSound(BlockSoundGroup group) {
      super.playWalkSound(group);
      if (this.random.nextInt(10) == 0) {
         this.playSound(SoundEvents.ENTITY_HORSE_BREATHE, group.getVolume() * 0.6F, group.getPitch());
      }

   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_HORSE_DEATH;
   }

   @Nullable
   protected SoundEvent getEatSound() {
      return SoundEvents.ENTITY_HORSE_EAT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_HORSE_HURT;
   }

   protected SoundEvent getAngrySound() {
      return SoundEvents.ENTITY_HORSE_ANGRY;
   }

   public ActionResult interactMob(PlayerEntity player, Hand hand) {
      boolean bl = !this.isBaby() && this.isTame() && player.shouldCancelInteraction();
      if (!this.hasPassengers() && !bl) {
         ItemStack itemStack = player.getStackInHand(hand);
         if (!itemStack.isEmpty()) {
            if (this.isBreedingItem(itemStack)) {
               return this.interactHorse(player, itemStack);
            }

            if (!this.isTame()) {
               this.playAngrySound();
               return ActionResult.SUCCESS;
            }
         }

         return super.interactMob(player, hand);
      } else {
         return super.interactMob(player, hand);
      }
   }

   public boolean canBreedWith(AnimalEntity other) {
      if (other == this) {
         return false;
      } else if (!(other instanceof DonkeyEntity) && !(other instanceof HorseEntity)) {
         return false;
      } else {
         return this.canBreed() && ((AbstractHorseEntity)other).canBreed();
      }
   }

   @Nullable
   public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
      if (entity instanceof DonkeyEntity) {
         MuleEntity muleEntity = (MuleEntity)EntityType.MULE.create(world, SpawnReason.BREEDING);
         if (muleEntity != null) {
            this.setChildAttributes(entity, muleEntity);
         }

         return muleEntity;
      } else {
         HorseEntity horseEntity = (HorseEntity)entity;
         HorseEntity horseEntity2 = (HorseEntity)EntityType.HORSE.create(world, SpawnReason.BREEDING);
         if (horseEntity2 != null) {
            int i = this.random.nextInt(9);
            HorseColor horseColor;
            if (i < 4) {
               horseColor = this.getHorseColor();
            } else if (i < 8) {
               horseColor = horseEntity.getHorseColor();
            } else {
               horseColor = (HorseColor)Util.getRandom((Object[])HorseColor.values(), this.random);
            }

            int j = this.random.nextInt(5);
            HorseMarking horseMarking;
            if (j < 2) {
               horseMarking = this.getMarking();
            } else if (j < 4) {
               horseMarking = horseEntity.getMarking();
            } else {
               horseMarking = (HorseMarking)Util.getRandom((Object[])HorseMarking.values(), this.random);
            }

            horseEntity2.setHorseVariant(horseColor, horseMarking);
            this.setChildAttributes(entity, horseEntity2);
         }

         return horseEntity2;
      }
   }

   public boolean canUseSlot(EquipmentSlot slot) {
      return true;
   }

   protected void damageArmor(DamageSource source, float amount) {
      this.damageEquipment(source, amount, new EquipmentSlot[]{EquipmentSlot.BODY});
   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      Random random = world.getRandom();
      HorseColor horseColor;
      if (entityData instanceof HorseData) {
         horseColor = ((HorseData)entityData).color;
      } else {
         horseColor = (HorseColor)Util.getRandom((Object[])HorseColor.values(), random);
         entityData = new HorseData(horseColor);
      }

      this.setHorseVariant(horseColor, (HorseMarking)Util.getRandom((Object[])HorseMarking.values(), random));
      return super.initialize(world, difficulty, spawnReason, (EntityData)entityData);
   }

   public EntityDimensions getBaseDimensions(EntityPose pose) {
      return this.isBaby() ? BABY_BASE_DIMENSIONS : super.getBaseDimensions(pose);
   }

   static {
      VARIANT = DataTracker.registerData(HorseEntity.class, TrackedDataHandlerRegistry.INTEGER);
      BABY_BASE_DIMENSIONS = EntityType.HORSE.getDimensions().withAttachments(EntityAttachments.builder().add(EntityAttachmentType.PASSENGER, 0.0F, EntityType.HORSE.getHeight() + 0.125F, 0.0F)).scaled(0.5F);
   }

   public static class HorseData extends PassiveEntity.PassiveData {
      public final HorseColor color;

      public HorseData(HorseColor color) {
         super(true);
         this.color = color;
      }
   }
}
