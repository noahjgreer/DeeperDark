package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Util;

public abstract class ConditionalLootFunction implements LootFunction {
   protected final List conditions;
   private final Predicate predicate;

   protected ConditionalLootFunction(List conditions) {
      this.conditions = conditions;
      this.predicate = Util.allOf(conditions);
   }

   public abstract LootFunctionType getType();

   protected static Products.P1 addConditionsField(RecordCodecBuilder.Instance instance) {
      return instance.group(LootCondition.CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter((function) -> {
         return function.conditions;
      }));
   }

   public final ItemStack apply(ItemStack itemStack, LootContext lootContext) {
      return this.predicate.test(lootContext) ? this.process(itemStack, lootContext) : itemStack;
   }

   protected abstract ItemStack process(ItemStack stack, LootContext context);

   public void validate(LootTableReporter reporter) {
      LootFunction.super.validate(reporter);

      for(int i = 0; i < this.conditions.size(); ++i) {
         ((LootCondition)this.conditions.get(i)).validate(reporter.makeChild(new ErrorReporter.NamedListElementContext("conditions", i)));
      }

   }

   protected static Builder builder(Function joiner) {
      return new Joiner(joiner);
   }

   // $FF: synthetic method
   public Object apply(final Object itemStack, final Object context) {
      return this.apply((ItemStack)itemStack, (LootContext)context);
   }

   private static final class Joiner extends Builder {
      private final Function joiner;

      public Joiner(Function joiner) {
         this.joiner = joiner;
      }

      protected Joiner getThisBuilder() {
         return this;
      }

      public LootFunction build() {
         return (LootFunction)this.joiner.apply(this.getConditions());
      }

      // $FF: synthetic method
      protected Builder getThisBuilder() {
         return this.getThisBuilder();
      }
   }

   public abstract static class Builder implements LootFunction.Builder, LootConditionConsumingBuilder {
      private final ImmutableList.Builder conditionList = ImmutableList.builder();

      public Builder conditionally(LootCondition.Builder builder) {
         this.conditionList.add(builder.build());
         return this.getThisBuilder();
      }

      public final Builder getThisConditionConsumingBuilder() {
         return this.getThisBuilder();
      }

      protected abstract Builder getThisBuilder();

      protected List getConditions() {
         return this.conditionList.build();
      }

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
