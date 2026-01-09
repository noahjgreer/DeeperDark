package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.predicate.entity.LootContextPredicateValidator;

public class ImpossibleCriterion implements Criterion {
   public void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer conditions) {
   }

   public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer conditions) {
   }

   public void endTracking(PlayerAdvancementTracker tracker) {
   }

   public Codec getConditionsCodec() {
      return ImpossibleCriterion.Conditions.CODEC;
   }

   public static record Conditions() implements CriterionConditions {
      public static final Codec CODEC = Codec.unit(new Conditions());

      public void validate(LootContextPredicateValidator validator) {
      }
   }
}
