package net.minecraft.entity.ai.goal;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.PathAwareEntity;

public class AvoidSunlightGoal extends Goal {
   private final PathAwareEntity mob;

   public AvoidSunlightGoal(PathAwareEntity mob) {
      this.mob = mob;
   }

   public boolean canStart() {
      return this.mob.getWorld().isDay() && this.mob.getEquippedStack(EquipmentSlot.HEAD).isEmpty() && NavigationConditions.hasMobNavigation(this.mob);
   }

   public void start() {
      EntityNavigation var2 = this.mob.getNavigation();
      if (var2 instanceof MobNavigation mobNavigation) {
         mobNavigation.setAvoidSunlight(true);
      }

   }

   public void stop() {
      if (NavigationConditions.hasMobNavigation(this.mob)) {
         EntityNavigation var2 = this.mob.getNavigation();
         if (var2 instanceof MobNavigation) {
            MobNavigation mobNavigation = (MobNavigation)var2;
            mobNavigation.setAvoidSunlight(false);
         }
      }

   }
}
