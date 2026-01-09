package net.minecraft.loot.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.util.Util;

public class AllOfLootCondition extends AlternativeLootCondition {
   public static final MapCodec CODEC = createCodec(AllOfLootCondition::new);
   public static final Codec INLINE_CODEC = createInlineCodec(AllOfLootCondition::new);

   AllOfLootCondition(List terms) {
      super(terms, Util.allOf(terms));
   }

   public static AllOfLootCondition create(List terms) {
      return new AllOfLootCondition(List.copyOf(terms));
   }

   public LootConditionType getType() {
      return LootConditionTypes.ALL_OF;
   }

   public static Builder builder(LootCondition.Builder... terms) {
      return new Builder(terms);
   }

   public static class Builder extends AlternativeLootCondition.Builder {
      public Builder(LootCondition.Builder... builders) {
         super(builders);
      }

      public Builder and(LootCondition.Builder builder) {
         this.add(builder);
         return this;
      }

      protected LootCondition build(List terms) {
         return new AllOfLootCondition(terms);
      }
   }
}
