package net.minecraft.entity.conversion;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Scoreboard;

public enum EntityConversionType {
   SINGLE(true) {
      void setUpNewEntity(MobEntity oldEntity, MobEntity newEntity, EntityConversionContext context) {
         Entity entity = oldEntity.getFirstPassenger();
         newEntity.copyPositionAndRotation(oldEntity);
         newEntity.setVelocity(oldEntity.getVelocity());
         Entity entity2;
         if (entity != null) {
            entity.stopRiding();
            entity.ridingCooldown = 0;
            Iterator var5 = newEntity.getPassengerList().iterator();

            while(var5.hasNext()) {
               entity2 = (Entity)var5.next();
               entity2.stopRiding();
               entity2.remove(Entity.RemovalReason.DISCARDED);
            }

            entity.startRiding(newEntity);
         }

         Entity entity3 = oldEntity.getVehicle();
         if (entity3 != null) {
            oldEntity.stopRiding();
            newEntity.startRiding(entity3);
         }

         if (context.keepEquipment()) {
            Iterator var10 = EquipmentSlot.VALUES.iterator();

            while(var10.hasNext()) {
               EquipmentSlot equipmentSlot = (EquipmentSlot)var10.next();
               ItemStack itemStack = oldEntity.getEquippedStack(equipmentSlot);
               if (!itemStack.isEmpty()) {
                  newEntity.equipStack(equipmentSlot, itemStack.copyAndEmpty());
                  newEntity.setEquipmentDropChance(equipmentSlot, oldEntity.getEquipmentDropChances().get(equipmentSlot));
               }
            }
         }

         newEntity.fallDistance = oldEntity.fallDistance;
         newEntity.setFlag(7, oldEntity.isGliding());
         newEntity.playerHitTimer = oldEntity.playerHitTimer;
         newEntity.hurtTime = oldEntity.hurtTime;
         newEntity.bodyYaw = oldEntity.bodyYaw;
         newEntity.setOnGround(oldEntity.isOnGround());
         Optional var10000 = oldEntity.getSleepingPosition();
         Objects.requireNonNull(newEntity);
         var10000.ifPresent(newEntity::setSleepingPosition);
         entity2 = oldEntity.getLeashHolder();
         if (entity2 != null) {
            newEntity.attachLeash(entity2, true);
         }

         this.copyData(oldEntity, newEntity, context);
      }
   },
   SPLIT_ON_DEATH(false) {
      void setUpNewEntity(MobEntity oldEntity, MobEntity newEntity, EntityConversionContext context) {
         Entity entity = oldEntity.getFirstPassenger();
         if (entity != null) {
            entity.stopRiding();
         }

         Entity entity2 = oldEntity.getLeashHolder();
         if (entity2 != null) {
            oldEntity.detachLeash();
         }

         this.copyData(oldEntity, newEntity, context);
      }
   };

   private static final Set CUSTOM_COMPONENTS = Set.of(DataComponentTypes.CUSTOM_NAME, DataComponentTypes.CUSTOM_DATA);
   private final boolean discardOldEntity;

   EntityConversionType(final boolean discardOldEntity) {
      this.discardOldEntity = discardOldEntity;
   }

   public boolean shouldDiscardOldEntity() {
      return this.discardOldEntity;
   }

   abstract void setUpNewEntity(MobEntity oldEntity, MobEntity newEntity, EntityConversionContext context);

   void copyData(MobEntity oldEntity, MobEntity newEntity, EntityConversionContext context) {
      newEntity.setAbsorptionAmount(oldEntity.getAbsorptionAmount());
      Iterator var4 = oldEntity.getStatusEffects().iterator();

      while(var4.hasNext()) {
         StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var4.next();
         newEntity.addStatusEffect(new StatusEffectInstance(statusEffectInstance));
      }

      if (oldEntity.isBaby()) {
         newEntity.setBaby(true);
      }

      if (oldEntity instanceof PassiveEntity passiveEntity) {
         if (newEntity instanceof PassiveEntity passiveEntity2) {
            passiveEntity2.setBreedingAge(passiveEntity.getBreedingAge());
            passiveEntity2.forcedAge = passiveEntity.forcedAge;
            passiveEntity2.happyTicksRemaining = passiveEntity.happyTicksRemaining;
         }
      }

      Brain brain = oldEntity.getBrain();
      Brain brain2 = newEntity.getBrain();
      if (brain.isMemoryInState(MemoryModuleType.ANGRY_AT, MemoryModuleState.REGISTERED) && brain.hasMemoryModule(MemoryModuleType.ANGRY_AT)) {
         brain2.remember(MemoryModuleType.ANGRY_AT, brain.getOptionalRegisteredMemory(MemoryModuleType.ANGRY_AT));
      }

      if (context.preserveCanPickUpLoot()) {
         newEntity.setCanPickUpLoot(oldEntity.canPickUpLoot());
      }

      newEntity.setLeftHanded(oldEntity.isLeftHanded());
      newEntity.setAiDisabled(oldEntity.isAiDisabled());
      if (oldEntity.isPersistent()) {
         newEntity.setPersistent();
      }

      newEntity.setCustomNameVisible(oldEntity.isCustomNameVisible());
      newEntity.setOnFire(oldEntity.isOnFire());
      newEntity.setInvulnerable(oldEntity.isInvulnerable());
      newEntity.setNoGravity(oldEntity.hasNoGravity());
      newEntity.setPortalCooldown(oldEntity.getPortalCooldown());
      newEntity.setSilent(oldEntity.isSilent());
      Set var10000 = oldEntity.getCommandTags();
      Objects.requireNonNull(newEntity);
      var10000.forEach(newEntity::addCommandTag);
      Iterator var6 = CUSTOM_COMPONENTS.iterator();

      while(var6.hasNext()) {
         ComponentType componentType = (ComponentType)var6.next();
         copyComponent(oldEntity, newEntity, componentType);
      }

      if (context.team() != null) {
         Scoreboard scoreboard = newEntity.getWorld().getScoreboard();
         scoreboard.addScoreHolderToTeam(newEntity.getUuidAsString(), context.team());
         if (oldEntity.getScoreboardTeam() != null && oldEntity.getScoreboardTeam() == context.team()) {
            scoreboard.removeScoreHolderFromTeam(oldEntity.getUuidAsString(), oldEntity.getScoreboardTeam());
         }
      }

      if (oldEntity instanceof ZombieEntity zombieEntity) {
         if (zombieEntity.canBreakDoors() && newEntity instanceof ZombieEntity zombieEntity2) {
            zombieEntity2.setCanBreakDoors(true);
         }
      }

   }

   private static void copyComponent(MobEntity oldEntity, MobEntity newEntity, ComponentType type) {
      Object object = oldEntity.get(type);
      if (object != null) {
         newEntity.setComponent(type, object);
      }

   }

   // $FF: synthetic method
   private static EntityConversionType[] method_63610() {
      return new EntityConversionType[]{SINGLE, SPLIT_ON_DEATH};
   }
}
