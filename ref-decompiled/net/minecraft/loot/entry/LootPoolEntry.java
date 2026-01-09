package net.minecraft.loot.entry;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Util;

public abstract class LootPoolEntry implements EntryCombiner {
   protected final List conditions;
   private final Predicate conditionPredicate;

   protected LootPoolEntry(List conditions) {
      this.conditions = conditions;
      this.conditionPredicate = Util.allOf(conditions);
   }

   protected static Products.P1 addConditionsField(RecordCodecBuilder.Instance instance) {
      return instance.group(LootCondition.CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter((entry) -> {
         return entry.conditions;
      }));
   }

   public void validate(LootTableReporter reporter) {
      for(int i = 0; i < this.conditions.size(); ++i) {
         ((LootCondition)this.conditions.get(i)).validate(reporter.makeChild(new ErrorReporter.NamedListElementContext("conditions", i)));
      }

   }

   protected final boolean test(LootContext context) {
      return this.conditionPredicate.test(context);
   }

   public abstract LootPoolEntryType getType();

   public abstract static class Builder implements LootConditionConsumingBuilder {
      private final ImmutableList.Builder conditions = ImmutableList.builder();

      protected abstract Builder getThisBuilder();

      public Builder conditionally(LootCondition.Builder builder) {
         this.conditions.add(builder.build());
         return this.getThisBuilder();
      }

      public final Builder getThisConditionConsumingBuilder() {
         return this.getThisBuilder();
      }

      protected List getConditions() {
         return this.conditions.build();
      }

      public AlternativeEntry.Builder alternatively(Builder builder) {
         return new AlternativeEntry.Builder(new Builder[]{this, builder});
      }

      public GroupEntry.Builder groupEntry(Builder entry) {
         return new GroupEntry.Builder(new Builder[]{this, entry});
      }

      public SequenceEntry.Builder sequenceEntry(Builder entry) {
         return new SequenceEntry.Builder(new Builder[]{this, entry});
      }

      public abstract LootPoolEntry build();

      // $FF: synthetic method
      public LootConditionConsumingBuilder getThisConditionConsumingBuilder() {
         return this.getThisConditionConsumingBuilder();
      }

      // $FF: synthetic method
      public LootConditionConsumingBuilder conditionally(final LootCondition.Builder condition) {
         return this.conditionally(condition);
      }
   }
}
