package net.minecraft.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.world.World;

public abstract class IllagerEntity extends RaiderEntity {
   protected IllagerEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   protected void initGoals() {
      super.initGoals();
   }

   public State getState() {
      return IllagerEntity.State.CROSSED;
   }

   public boolean canTarget(LivingEntity target) {
      return target instanceof MerchantEntity && target.isBaby() ? false : super.canTarget(target);
   }

   protected boolean isInSameTeam(Entity other) {
      if (super.isInSameTeam(other)) {
         return true;
      } else if (!other.getType().isIn(EntityTypeTags.ILLAGER_FRIENDS)) {
         return false;
      } else {
         return this.getScoreboardTeam() == null && other.getScoreboardTeam() == null;
      }
   }

   public static enum State {
      CROSSED,
      ATTACKING,
      SPELLCASTING,
      BOW_AND_ARROW,
      CROSSBOW_HOLD,
      CROSSBOW_CHARGE,
      CELEBRATING,
      NEUTRAL;

      // $FF: synthetic method
      private static State[] method_36647() {
         return new State[]{CROSSED, ATTACKING, SPELLCASTING, BOW_AND_ARROW, CROSSBOW_HOLD, CROSSBOW_CHARGE, CELEBRATING, NEUTRAL};
      }
   }

   protected class LongDoorInteractGoal extends net.minecraft.entity.ai.goal.LongDoorInteractGoal {
      public LongDoorInteractGoal(final RaiderEntity raider) {
         super(raider, false);
      }

      public boolean canStart() {
         return super.canStart() && IllagerEntity.this.hasActiveRaid();
      }
   }
}
