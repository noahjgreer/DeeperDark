package net.minecraft.loot.entry;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.math.MathHelper;

public abstract class LeafEntry extends LootPoolEntry {
   public static final int DEFAULT_WEIGHT = 1;
   public static final int DEFAULT_QUALITY = 0;
   protected final int weight;
   protected final int quality;
   protected final List functions;
   final BiFunction compiledFunctions;
   private final LootChoice choice = new Choice() {
      public void generateLoot(Consumer lootConsumer, LootContext context) {
         LeafEntry.this.generateLoot(LootFunction.apply(LeafEntry.this.compiledFunctions, lootConsumer, context), context);
      }
   };

   protected LeafEntry(int weight, int quality, List conditions, List functions) {
      super(conditions);
      this.weight = weight;
      this.quality = quality;
      this.functions = functions;
      this.compiledFunctions = LootFunctionTypes.join(functions);
   }

   protected static Products.P4 addLeafFields(RecordCodecBuilder.Instance instance) {
      return instance.group(Codec.INT.optionalFieldOf("weight", 1).forGetter((entry) -> {
         return entry.weight;
      }), Codec.INT.optionalFieldOf("quality", 0).forGetter((entry) -> {
         return entry.quality;
      })).and(addConditionsField(instance).t1()).and(LootFunctionTypes.CODEC.listOf().optionalFieldOf("functions", List.of()).forGetter((entry) -> {
         return entry.functions;
      }));
   }

   public void validate(LootTableReporter reporter) {
      super.validate(reporter);

      for(int i = 0; i < this.functions.size(); ++i) {
         ((LootFunction)this.functions.get(i)).validate(reporter.makeChild(new ErrorReporter.NamedListElementContext("functions", i)));
      }

   }

   protected abstract void generateLoot(Consumer lootConsumer, LootContext context);

   public boolean expand(LootContext lootContext, Consumer consumer) {
      if (this.test(lootContext)) {
         consumer.accept(this.choice);
         return true;
      } else {
         return false;
      }
   }

   public static Builder builder(Factory factory) {
      return new BasicBuilder(factory);
   }

   private static class BasicBuilder extends Builder {
      private final Factory factory;

      public BasicBuilder(Factory factory) {
         this.factory = factory;
      }

      protected BasicBuilder getThisBuilder() {
         return this;
      }

      public LootPoolEntry build() {
         return this.factory.build(this.weight, this.quality, this.getConditions(), this.getFunctions());
      }

      // $FF: synthetic method
      protected LootPoolEntry.Builder getThisBuilder() {
         return this.getThisBuilder();
      }
   }

   @FunctionalInterface
   protected interface Factory {
      LeafEntry build(int weight, int quality, List conditions, List functions);
   }

   public abstract static class Builder extends LootPoolEntry.Builder implements LootFunctionConsumingBuilder {
      protected int weight = 1;
      protected int quality = 0;
      private final ImmutableList.Builder functions = ImmutableList.builder();

      public Builder apply(LootFunction.Builder builder) {
         this.functions.add(builder.build());
         return (Builder)this.getThisBuilder();
      }

      protected List getFunctions() {
         return this.functions.build();
      }

      public Builder weight(int weight) {
         this.weight = weight;
         return (Builder)this.getThisBuilder();
      }

      public Builder quality(int quality) {
         this.quality = quality;
         return (Builder)this.getThisBuilder();
      }

      // $FF: synthetic method
      public LootFunctionConsumingBuilder getThisFunctionConsumingBuilder() {
         return (LootFunctionConsumingBuilder)super.getThisConditionConsumingBuilder();
      }

      // $FF: synthetic method
      public LootFunctionConsumingBuilder apply(final LootFunction.Builder function) {
         return this.apply(function);
      }
   }

   protected abstract class Choice implements LootChoice {
      public int getWeight(float luck) {
         return Math.max(MathHelper.floor((float)LeafEntry.this.weight + (float)LeafEntry.this.quality * luck), 0);
      }
   }
}
