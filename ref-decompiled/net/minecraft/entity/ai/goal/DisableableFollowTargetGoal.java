package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.raid.RaiderEntity;
import org.jetbrains.annotations.Nullable;

public class DisableableFollowTargetGoal extends ActiveTargetGoal {
   private boolean enabled = true;

   public DisableableFollowTargetGoal(RaiderEntity actor, Class targetEntityClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable TargetPredicate.EntityPredicate targetPredicate) {
      super(actor, targetEntityClass, reciprocalChance, checkVisibility, checkCanNavigate, targetPredicate);
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public boolean canStart() {
      return this.enabled && super.canStart();
   }
}
