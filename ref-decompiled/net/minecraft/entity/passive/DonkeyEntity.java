package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DonkeyEntity extends AbstractDonkeyEntity {
   public DonkeyEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_DONKEY_AMBIENT;
   }

   protected SoundEvent getAngrySound() {
      return SoundEvents.ENTITY_DONKEY_ANGRY;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_DONKEY_DEATH;
   }

   @Nullable
   protected SoundEvent getEatSound() {
      return SoundEvents.ENTITY_DONKEY_EAT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_DONKEY_HURT;
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

   protected void playJumpSound() {
      this.playSound(SoundEvents.ENTITY_DONKEY_JUMP, 0.4F, 1.0F);
   }

   @Nullable
   public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
      EntityType entityType = entity instanceof HorseEntity ? EntityType.MULE : EntityType.DONKEY;
      AbstractHorseEntity abstractHorseEntity = (AbstractHorseEntity)entityType.create(world, SpawnReason.BREEDING);
      if (abstractHorseEntity != null) {
         this.setChildAttributes(entity, abstractHorseEntity);
      }

      return abstractHorseEntity;
   }
}
