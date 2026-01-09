package net.minecraft.entity.mob;

import java.util.Objects;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.EntityAttachments;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class ZombieHorseEntity extends AbstractHorseEntity {
   private static final EntityDimensions BABY_BASE_DIMENSIONS;

   public ZombieHorseEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   public static DefaultAttributeContainer.Builder createZombieHorseAttributes() {
      return createBaseHorseAttributes().add(EntityAttributes.MAX_HEALTH, 15.0).add(EntityAttributes.MOVEMENT_SPEED, 0.20000000298023224);
   }

   public static boolean canSpawn(EntityType type, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
      if (!SpawnReason.isAnySpawner(reason)) {
         return AnimalEntity.isValidNaturalSpawn(type, world, reason, pos, random);
      } else {
         return SpawnReason.isTrialSpawner(reason) || isLightLevelValidForNaturalSpawn(world, pos);
      }
   }

   protected void initAttributes(Random random) {
      EntityAttributeInstance var10000 = this.getAttributeInstance(EntityAttributes.JUMP_STRENGTH);
      Objects.requireNonNull(random);
      var10000.setBaseValue(getChildJumpStrengthBonus(random::nextDouble));
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_ZOMBIE_HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ZOMBIE_HORSE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_ZOMBIE_HORSE_HURT;
   }

   @Nullable
   public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
      return (PassiveEntity)EntityType.ZOMBIE_HORSE.create(world, SpawnReason.BREEDING);
   }

   public ActionResult interactMob(PlayerEntity player, Hand hand) {
      return (ActionResult)(!this.isTame() ? ActionResult.PASS : super.interactMob(player, hand));
   }

   protected void initCustomGoals() {
   }

   public EntityDimensions getBaseDimensions(EntityPose pose) {
      return this.isBaby() ? BABY_BASE_DIMENSIONS : super.getBaseDimensions(pose);
   }

   static {
      BABY_BASE_DIMENSIONS = EntityType.ZOMBIE_HORSE.getDimensions().withAttachments(EntityAttachments.builder().add(EntityAttachmentType.PASSENGER, 0.0F, EntityType.ZOMBIE_HORSE.getHeight() - 0.03125F, 0.0F)).scaled(0.5F);
   }
}
