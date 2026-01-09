package net.minecraft.loot.condition;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;

public record ValueCheckLootCondition(LootNumberProvider value, BoundedIntUnaryOperator range) implements LootCondition {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(LootNumberProviderTypes.CODEC.fieldOf("value").forGetter(ValueCheckLootCondition::value), BoundedIntUnaryOperator.CODEC.fieldOf("range").forGetter(ValueCheckLootCondition::range)).apply(instance, ValueCheckLootCondition::new);
   });

   public ValueCheckLootCondition(LootNumberProvider value, BoundedIntUnaryOperator range) {
      this.value = value;
      this.range = range;
   }

   public LootConditionType getType() {
      return LootConditionTypes.VALUE_CHECK;
   }

   public Set getAllowedParameters() {
      return Sets.union(this.value.getAllowedParameters(), this.range.getRequiredParameters());
   }

   public boolean test(LootContext lootContext) {
      return this.range.test(lootContext, this.value.nextInt(lootContext));
   }

   public static LootCondition.Builder builder(LootNumberProvider value, BoundedIntUnaryOperator range) {
      return () -> {
         return new ValueCheckLootCondition(value, range);
      };
   }

   public LootNumberProvider value() {
      return this.value;
   }

   public BoundedIntUnaryOperator range() {
      return this.range;
   }

   // $FF: synthetic method
   public boolean test(final Object context) {
      return this.test((LootContext)context);
   }
}
