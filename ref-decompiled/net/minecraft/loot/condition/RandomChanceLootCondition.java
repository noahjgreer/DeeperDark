package net.minecraft.loot.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;

public record RandomChanceLootCondition(LootNumberProvider chance) implements LootCondition {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(LootNumberProviderTypes.CODEC.fieldOf("chance").forGetter(RandomChanceLootCondition::chance)).apply(instance, RandomChanceLootCondition::new);
   });

   public RandomChanceLootCondition(LootNumberProvider lootNumberProvider) {
      this.chance = lootNumberProvider;
   }

   public LootConditionType getType() {
      return LootConditionTypes.RANDOM_CHANCE;
   }

   public boolean test(LootContext lootContext) {
      float f = this.chance.nextFloat(lootContext);
      return lootContext.getRandom().nextFloat() < f;
   }

   public static LootCondition.Builder builder(float chance) {
      return () -> {
         return new RandomChanceLootCondition(ConstantLootNumberProvider.create(chance));
      };
   }

   public static LootCondition.Builder builder(LootNumberProvider chance) {
      return () -> {
         return new RandomChanceLootCondition(chance);
      };
   }

   public LootNumberProvider chance() {
      return this.chance;
   }

   // $FF: synthetic method
   public boolean test(final Object context) {
      return this.test((LootContext)context);
   }
}
