package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;

public interface Criterion {
   void beginTrackingCondition(PlayerAdvancementTracker manager, ConditionsContainer conditions);

   void endTrackingCondition(PlayerAdvancementTracker manager, ConditionsContainer conditions);

   void endTracking(PlayerAdvancementTracker tracker);

   Codec getConditionsCodec();

   default AdvancementCriterion create(CriterionConditions conditions) {
      return new AdvancementCriterion(this, conditions);
   }

   public static record ConditionsContainer(CriterionConditions conditions, AdvancementEntry advancement, String id) {
      public ConditionsContainer(CriterionConditions conditions, AdvancementEntry advancementEntry, String id) {
         this.conditions = conditions;
         this.advancement = advancementEntry;
         this.id = id;
      }

      public void grant(PlayerAdvancementTracker tracker) {
         tracker.grantCriterion(this.advancement, this.id);
      }

      public CriterionConditions conditions() {
         return this.conditions;
      }

      public AdvancementEntry advancement() {
         return this.advancement;
      }

      public String id() {
         return this.id;
      }
   }
}
