package net.minecraft.entity.effect;

import net.minecraft.util.Formatting;

public enum StatusEffectCategory {
   BENEFICIAL(Formatting.BLUE),
   HARMFUL(Formatting.RED),
   NEUTRAL(Formatting.BLUE);

   private final Formatting formatting;

   private StatusEffectCategory(final Formatting format) {
      this.formatting = format;
   }

   public Formatting getFormatting() {
      return this.formatting;
   }

   // $FF: synthetic method
   private static StatusEffectCategory[] method_36600() {
      return new StatusEffectCategory[]{BENEFICIAL, HARMFUL, NEUTRAL};
   }
}
