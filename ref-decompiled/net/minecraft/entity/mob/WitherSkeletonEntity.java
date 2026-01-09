package net.minecraft.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WitherSkeletonEntity extends AbstractSkeletonEntity {
   public WitherSkeletonEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.setPathfindingPenalty(PathNodeType.LAVA, 8.0F);
   }

   protected void initGoals() {
      this.targetSelector.add(3, new ActiveTargetGoal(this, AbstractPiglinEntity.class, true));
      super.initGoals();
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_WITHER_SKELETON_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_WITHER_SKELETON_DEATH;
   }

   SoundEvent getStepSound() {
      return SoundEvents.ENTITY_WITHER_SKELETON_STEP;
   }

   public TagKey getPreferredWeapons() {
      return null;
   }

   public boolean canPickupItem(ItemStack stack) {
      return !stack.isIn(ItemTags.WITHER_SKELETON_DISLIKED_WEAPONS) && super.canPickupItem(stack);
   }

   protected void dropEquipment(ServerWorld world, DamageSource source, boolean causedByPlayer) {
      super.dropEquipment(world, source, causedByPlayer);
      Entity entity = source.getAttacker();
      if (entity instanceof CreeperEntity creeperEntity) {
         if (creeperEntity.shouldDropHead()) {
            creeperEntity.onHeadDropped();
            this.dropItem(world, Items.WITHER_SKELETON_SKULL);
         }
      }

   }

   protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
      this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
   }

   protected void updateEnchantments(ServerWorldAccess world, Random random, LocalDifficulty localDifficulty) {
   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      EntityData entityData2 = super.initialize(world, difficulty, spawnReason, entityData);
      this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
      this.updateAttackType();
      return entityData2;
   }

   public boolean tryAttack(ServerWorld world, Entity target) {
      if (!super.tryAttack(world, target)) {
         return false;
      } else {
         if (target instanceof LivingEntity) {
            ((LivingEntity)target).addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 200), this);
         }

         return true;
      }
   }

   protected PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier, @Nullable ItemStack shotFrom) {
      PersistentProjectileEntity persistentProjectileEntity = super.createArrowProjectile(arrow, damageModifier, shotFrom);
      persistentProjectileEntity.setOnFireFor(100.0F);
      return persistentProjectileEntity;
   }

   public boolean canHaveStatusEffect(StatusEffectInstance effect) {
      return effect.equals(StatusEffects.WITHER) ? false : super.canHaveStatusEffect(effect);
   }
}
