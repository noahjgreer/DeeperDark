package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.raid.RaiderEntity;
import org.jetbrains.annotations.Nullable;

public class RaidGoal extends ActiveTargetGoal {
   private static final int MAX_COOLDOWN = 200;
   private int cooldown = 0;

   public RaidGoal(RaiderEntity raider, Class targetEntityClass, boolean checkVisibility, @Nullable TargetPredicate.EntityPredicate targetPredicate) {
      super(raider, targetEntityClass, 500, checkVisibility, false, targetPredicate);
   }

   public int getCooldown() {
      return this.cooldown;
   }

   public void decreaseCooldown() {
      --this.cooldown;
   }

   public boolean canStart() {
      if (this.cooldown <= 0 && this.mob.getRandom().nextBoolean()) {
         if (!((RaiderEntity)this.mob).hasActiveRaid()) {
            return false;
         } else {
            this.findClosestTarget();
            return this.targetEntity != null;
         }
      } else {
         return false;
      }
   }

   public void start() {
      this.cooldown = toGoalTicks(200);
      super.start();
   }
}
