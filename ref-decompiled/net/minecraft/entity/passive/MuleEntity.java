package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MuleEntity extends AbstractDonkeyEntity {
   public MuleEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_MULE_AMBIENT;
   }

   protected SoundEvent getAngrySound() {
      return SoundEvents.ENTITY_MULE_ANGRY;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_MULE_DEATH;
   }

   @Nullable
   protected SoundEvent getEatSound() {
      return SoundEvents.ENTITY_MULE_EAT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_MULE_HURT;
   }

   protected void playJumpSound() {
      this.playSound(SoundEvents.ENTITY_MULE_JUMP, 0.4F, 1.0F);
   }

   protected void playAddChestSound() {
      this.playSound(SoundEvents.ENTITY_MULE_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
   }

   @Nullable
   public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
      return (PassiveEntity)EntityType.MULE.create(world, SpawnReason.BREEDING);
   }
}
