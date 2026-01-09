package net.minecraft.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class HuskEntity extends ZombieEntity {
   public HuskEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   public static boolean canSpawn(EntityType type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
      return canSpawnInDark(type, world, spawnReason, pos, random) && (SpawnReason.isAnySpawner(spawnReason) || world.isSkyVisible(pos));
   }

   protected boolean burnsInDaylight() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_HUSK_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_HUSK_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_HUSK_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_HUSK_STEP;
   }

   public boolean tryAttack(ServerWorld world, Entity target) {
      boolean bl = super.tryAttack(world, target);
      if (bl && this.getMainHandStack().isEmpty() && target instanceof LivingEntity) {
         float f = this.getWorld().getLocalDifficulty(this.getBlockPos()).getLocalDifficulty();
         ((LivingEntity)target).addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 140 * (int)f), this);
      }

      return bl;
   }

   protected boolean canConvertInWater() {
      return true;
   }

   protected void convertInWater() {
      this.convertTo(EntityType.ZOMBIE);
      if (!this.isSilent()) {
         this.getWorld().syncWorldEvent((Entity)null, 1041, this.getBlockPos(), 0);
      }

   }

   protected ItemStack getSkull() {
      return ItemStack.EMPTY;
   }
}
