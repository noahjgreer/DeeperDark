package net.minecraft.loot.condition;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.util.Util;

public class AnyOfLootCondition extends AlternativeLootCondition {
   public static final MapCodec CODEC = createCodec(AnyOfLootCondition::new);

   AnyOfLootCondition(List terms) {
      super(terms, Util.anyOf(terms));
   }

   public LootConditionType getType() {
      return LootConditionTypes.ANY_OF;
   }

   public static Builder builder(LootCondition.Builder... terms) {
      return new Builder(terms);
   }

   public static class Builder extends AlternativeLootCondition.Builder {
      public Builder(LootCondition.Builder... builders) {
         super(builders);
      }

      public Builder or(LootCondition.Builder builder) {
         this.add(builder);
         return this;
      }

      protected LootCondition build(List terms) {
         return new AnyOfLootCondition(terms);
      }
   }
}
