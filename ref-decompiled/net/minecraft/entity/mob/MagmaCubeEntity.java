package net.minecraft.entity.mob;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class MagmaCubeEntity extends SlimeEntity {
   public MagmaCubeEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   public static DefaultAttributeContainer.Builder createMagmaCubeAttributes() {
      return HostileEntity.createHostileAttributes().add(EntityAttributes.MOVEMENT_SPEED, 0.20000000298023224);
   }

   public static boolean canMagmaCubeSpawn(EntityType type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
      return world.getDifficulty() != Difficulty.PEACEFUL;
   }

   public void setSize(int size, boolean heal) {
      super.setSize(size, heal);
      this.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue((double)(size * 3));
   }

   public float getBrightnessAtEyes() {
      return 1.0F;
   }

   protected ParticleEffect getParticles() {
      return ParticleTypes.FLAME;
   }

   public boolean isOnFire() {
      return false;
   }

   protected int getTicksUntilNextJump() {
      return super.getTicksUntilNextJump() * 4;
   }

   protected void updateStretch() {
      this.targetStretch *= 0.9F;
   }

   public void jump() {
      Vec3d vec3d = this.getVelocity();
      float f = (float)this.getSize() * 0.1F;
      this.setVelocity(vec3d.x, (double)(this.getJumpVelocity() + f), vec3d.z);
      this.velocityDirty = true;
   }

   protected void swimUpward(TagKey fluid) {
      if (fluid == FluidTags.LAVA) {
         Vec3d vec3d = this.getVelocity();
         this.setVelocity(vec3d.x, (double)(0.22F + (float)this.getSize() * 0.05F), vec3d.z);
         this.velocityDirty = true;
      } else {
         super.swimUpward(fluid);
      }

   }

   protected boolean canAttack() {
      return this.canActVoluntarily();
   }

   protected float getDamageAmount() {
      return super.getDamageAmount() + 2.0F;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return this.isSmall() ? SoundEvents.ENTITY_MAGMA_CUBE_HURT_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isSmall() ? SoundEvents.ENTITY_MAGMA_CUBE_DEATH_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_DEATH;
   }

   protected SoundEvent getSquishSound() {
      return this.isSmall() ? SoundEvents.ENTITY_MAGMA_CUBE_SQUISH_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_SQUISH;
   }

   protected SoundEvent getJumpSound() {
      return SoundEvents.ENTITY_MAGMA_CUBE_JUMP;
   }
}
