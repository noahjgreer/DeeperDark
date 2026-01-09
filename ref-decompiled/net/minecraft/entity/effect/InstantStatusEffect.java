package net.minecraft.entity.effect;

public class InstantStatusEffect extends StatusEffect {
   public InstantStatusEffect(StatusEffectCategory statusEffectCategory, int i) {
      super(statusEffectCategory, i);
   }

   public boolean isInstant() {
      return true;
   }

   public boolean canApplyUpdateEffect(int duration, int amplifier) {
      return duration >= 1;
   }
}
