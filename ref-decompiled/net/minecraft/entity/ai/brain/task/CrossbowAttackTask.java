package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;

public class CrossbowAttackTask extends MultiTickTask {
   private static final int RUN_TIME = 1200;
   private int chargingCooldown;
   private CrossbowState state;

   public CrossbowAttackTask() {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT), 1200);
      this.state = CrossbowAttackTask.CrossbowState.UNCHARGED;
   }

   protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
      LivingEntity livingEntity = getAttackTarget(mobEntity);
      return mobEntity.isHolding(Items.CROSSBOW) && TargetUtil.isVisibleInMemory(mobEntity, livingEntity) && TargetUtil.isTargetWithinAttackRange(mobEntity, livingEntity, 0);
   }

   protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
      return mobEntity.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET) && this.shouldRun(serverWorld, mobEntity);
   }

   protected void keepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
      LivingEntity livingEntity = getAttackTarget(mobEntity);
      this.setLookTarget(mobEntity, livingEntity);
      this.tickState(mobEntity, livingEntity);
   }

   protected void finishRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
      if (mobEntity.isUsingItem()) {
         mobEntity.clearActiveItem();
      }

      if (mobEntity.isHolding(Items.CROSSBOW)) {
         ((CrossbowUser)mobEntity).setCharging(false);
         mobEntity.getActiveItem().set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);
      }

   }

   private void tickState(MobEntity entity, LivingEntity target) {
      if (this.state == CrossbowAttackTask.CrossbowState.UNCHARGED) {
         entity.setCurrentHand(ProjectileUtil.getHandPossiblyHolding(entity, Items.CROSSBOW));
         this.state = CrossbowAttackTask.CrossbowState.CHARGING;
         ((CrossbowUser)entity).setCharging(true);
      } else if (this.state == CrossbowAttackTask.CrossbowState.CHARGING) {
         if (!entity.isUsingItem()) {
            this.state = CrossbowAttackTask.CrossbowState.UNCHARGED;
         }

         int i = entity.getItemUseTime();
         ItemStack itemStack = entity.getActiveItem();
         if (i >= CrossbowItem.getPullTime(itemStack, entity)) {
            entity.stopUsingItem();
            this.state = CrossbowAttackTask.CrossbowState.CHARGED;
            this.chargingCooldown = 20 + entity.getRandom().nextInt(20);
            ((CrossbowUser)entity).setCharging(false);
         }
      } else if (this.state == CrossbowAttackTask.CrossbowState.CHARGED) {
         --this.chargingCooldown;
         if (this.chargingCooldown == 0) {
            this.state = CrossbowAttackTask.CrossbowState.READY_TO_ATTACK;
         }
      } else if (this.state == CrossbowAttackTask.CrossbowState.READY_TO_ATTACK) {
         ((RangedAttackMob)entity).shootAt(target, 1.0F);
         this.state = CrossbowAttackTask.CrossbowState.UNCHARGED;
      }

   }

   private void setLookTarget(MobEntity entity, LivingEntity target) {
      entity.getBrain().remember(MemoryModuleType.LOOK_TARGET, (Object)(new EntityLookTarget(target, true)));
   }

   private static LivingEntity getAttackTarget(LivingEntity entity) {
      return (LivingEntity)entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).get();
   }

   // $FF: synthetic method
   protected void finishRunning(final ServerWorld world, final LivingEntity entity, final long time) {
      this.finishRunning(world, (MobEntity)entity, time);
   }

   // $FF: synthetic method
   protected void keepRunning(final ServerWorld world, final LivingEntity entity, final long time) {
      this.keepRunning(world, (MobEntity)entity, time);
   }

   private static enum CrossbowState {
      UNCHARGED,
      CHARGING,
      CHARGED,
      READY_TO_ATTACK;

      // $FF: synthetic method
      private static CrossbowState[] method_36616() {
         return new CrossbowState[]{UNCHARGED, CHARGING, CHARGED, READY_TO_ATTACK};
      }
   }
}
