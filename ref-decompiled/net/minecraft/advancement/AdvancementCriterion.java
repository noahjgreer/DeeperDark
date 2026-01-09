package net.minecraft.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.util.dynamic.Codecs;

public record AdvancementCriterion(Criterion trigger, CriterionConditions conditions) {
   private static final MapCodec MAP_CODEC;
   public static final Codec CODEC;

   public AdvancementCriterion(Criterion criterion, CriterionConditions criterionConditions) {
      this.trigger = criterion;
      this.conditions = criterionConditions;
   }

   private static Codec getCodec(Criterion criterion) {
      return criterion.getConditionsCodec().xmap((conditions) -> {
         return new AdvancementCriterion(criterion, conditions);
      }, AdvancementCriterion::conditions);
   }

   public Criterion trigger() {
      return this.trigger;
   }

   public CriterionConditions conditions() {
      return this.conditions;
   }

   static {
      MAP_CODEC = Codecs.parameters("trigger", "conditions", Criteria.CODEC, AdvancementCriterion::trigger, AdvancementCriterion::getCodec);
      CODEC = MAP_CODEC.codec();
   }
}
