package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.passive.TameableEntity;
import org.jetbrains.annotations.Nullable;

public class UntamedActiveTargetGoal extends ActiveTargetGoal {
   private final TameableEntity tameable;

   public UntamedActiveTargetGoal(TameableEntity tameable, Class targetClass, boolean checkVisibility, @Nullable TargetPredicate.EntityPredicate targetPredicate) {
      super(tameable, targetClass, 10, checkVisibility, false, targetPredicate);
      this.tameable = tameable;
   }

   public boolean canStart() {
      return !this.tameable.isTamed() && super.canStart();
   }

   public boolean shouldContinue() {
      return this.targetPredicate != null ? this.targetPredicate.test(getServerWorld(this.mob), this.mob, this.targetEntity) : super.shouldContinue();
   }
}
