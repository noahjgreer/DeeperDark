package net.minecraft.loot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.loot.v3.FabricLootPoolBuilder;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.mutable.MutableInt;

public class LootPool {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(LootPoolEntryTypes.CODEC.listOf().fieldOf("entries").forGetter((pool) -> {
         return pool.entries;
      }), LootCondition.CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter((pool) -> {
         return pool.conditions;
      }), LootFunctionTypes.CODEC.listOf().optionalFieldOf("functions", List.of()).forGetter((pool) -> {
         return pool.functions;
      }), LootNumberProviderTypes.CODEC.fieldOf("rolls").forGetter((pool) -> {
         return pool.rolls;
      }), LootNumberProviderTypes.CODEC.fieldOf("bonus_rolls").orElse(ConstantLootNumberProvider.create(0.0F)).forGetter((pool) -> {
         return pool.bonusRolls;
      })).apply(instance, LootPool::new);
   });
   public final List entries;
   public final List conditions;
   private final Predicate predicate;
   public final List functions;
   private final BiFunction javaFunctions;
   public final LootNumberProvider rolls;
   public final LootNumberProvider bonusRolls;

   LootPool(List entries, List conditions, List functions, LootNumberProvider rolls, LootNumberProvider bonusRolls) {
      this.entries = entries;
      this.conditions = conditions;
      this.predicate = Util.allOf(conditions);
      this.functions = functions;
      this.javaFunctions = LootFunctionTypes.join(functions);
      this.rolls = rolls;
      this.bonusRolls = bonusRolls;
   }

   private void supplyOnce(Consumer lootConsumer, LootContext context) {
      Random random = context.getRandom();
      List list = Lists.newArrayList();
      MutableInt mutableInt = new MutableInt();
      Iterator var6 = this.entries.iterator();

      while(var6.hasNext()) {
         LootPoolEntry lootPoolEntry = (LootPoolEntry)var6.next();
         lootPoolEntry.expand(context, (choice) -> {
            int i = choice.getWeight(context.getLuck());
            if (i > 0) {
               list.add(choice);
               mutableInt.add(i);
            }

         });
      }

      int i = list.size();
      if (mutableInt.intValue() != 0 && i != 0) {
         if (i == 1) {
            ((LootChoice)list.get(0)).generateLoot(lootConsumer, context);
         } else {
            int j = random.nextInt(mutableInt.intValue());
            Iterator var8 = list.iterator();

            LootChoice lootChoice;
            do {
               if (!var8.hasNext()) {
                  return;
               }

               lootChoice = (LootChoice)var8.next();
               j -= lootChoice.getWeight(context.getLuck());
            } while(j >= 0);

            lootChoice.generateLoot(lootConsumer, context);
         }
      }
   }

   public void addGeneratedLoot(Consumer lootConsumer, LootContext context) {
      if (this.predicate.test(context)) {
         Consumer consumer = LootFunction.apply(this.javaFunctions, lootConsumer, context);
         int i = this.rolls.nextInt(context) + MathHelper.floor(this.bonusRolls.nextFloat(context) * context.getLuck());

         for(int j = 0; j < i; ++j) {
            this.supplyOnce(consumer, context);
         }

      }
   }

   public void validate(LootTableReporter reporter) {
      int i;
      for(i = 0; i < this.conditions.size(); ++i) {
         ((LootCondition)this.conditions.get(i)).validate(reporter.makeChild(new ErrorReporter.NamedListElementContext("conditions", i)));
      }

      for(i = 0; i < this.functions.size(); ++i) {
         ((LootFunction)this.functions.get(i)).validate(reporter.makeChild(new ErrorReporter.NamedListElementContext("functions", i)));
      }

      for(i = 0; i < this.entries.size(); ++i) {
         ((LootPoolEntry)this.entries.get(i)).validate(reporter.makeChild(new ErrorReporter.NamedListElementContext("entries", i)));
      }

      this.rolls.validate(reporter.makeChild(new ErrorReporter.MapElementContext("rolls")));
      this.bonusRolls.validate(reporter.makeChild(new ErrorReporter.MapElementContext("bonus_rolls")));
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder implements LootFunctionConsumingBuilder, LootConditionConsumingBuilder, FabricLootPoolBuilder {
      private final ImmutableList.Builder entries = ImmutableList.builder();
      private final ImmutableList.Builder conditions = ImmutableList.builder();
      private final ImmutableList.Builder functions = ImmutableList.builder();
      private LootNumberProvider rolls = ConstantLootNumberProvider.create(1.0F);
      private LootNumberProvider bonusRollsRange = ConstantLootNumberProvider.create(0.0F);

      public Builder rolls(LootNumberProvider rolls) {
         this.rolls = rolls;
         return this;
      }

      public Builder getThisFunctionConsumingBuilder() {
         return this;
      }

      public Builder bonusRolls(LootNumberProvider bonusRolls) {
         this.bonusRollsRange = bonusRolls;
         return this;
      }

      public Builder with(LootPoolEntry.Builder entry) {
         this.entries.add(entry.build());
         return this;
      }

      public Builder conditionally(LootCondition.Builder builder) {
         this.conditions.add(builder.build());
         return this;
      }

      public Builder apply(LootFunction.Builder builder) {
         this.functions.add(builder.build());
         return this;
      }

      public LootPool build() {
         return new LootPool(this.entries.build(), this.conditions.build(), this.functions.build(), this.rolls, this.bonusRollsRange);
      }

      // $FF: synthetic method
      public LootFunctionConsumingBuilder getThisFunctionConsumingBuilder() {
         return this.getThisFunctionConsumingBuilder();
      }

      // $FF: synthetic method
      public LootFunctionConsumingBuilder apply(final LootFunction.Builder function) {
         return this.apply(function);
      }

      // $FF: synthetic method
      public LootConditionConsumingBuilder getThisConditionConsumingBuilder() {
         return this.getThisFunctionConsumingBuilder();
      }

      // $FF: synthetic method
      public LootConditionConsumingBuilder conditionally(final LootCondition.Builder condition) {
         return this.conditionally(condition);
      }
   }
}
