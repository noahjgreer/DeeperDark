package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;

public class EnchantedCountIncreaseLootFunction extends ConditionalLootFunction {
   public static final int DEFAULT_LIMIT = 0;
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(instance.group(Enchantment.ENTRY_CODEC.fieldOf("enchantment").forGetter((function) -> {
         return function.enchantment;
      }), LootNumberProviderTypes.CODEC.fieldOf("count").forGetter((function) -> {
         return function.count;
      }), Codec.INT.optionalFieldOf("limit", 0).forGetter((function) -> {
         return function.limit;
      }))).apply(instance, EnchantedCountIncreaseLootFunction::new);
   });
   private final RegistryEntry enchantment;
   private final LootNumberProvider count;
   private final int limit;

   EnchantedCountIncreaseLootFunction(List conditions, RegistryEntry enchantment, LootNumberProvider count, int limit) {
      super(conditions);
      this.enchantment = enchantment;
      this.count = count;
      this.limit = limit;
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.ENCHANTED_COUNT_INCREASE;
   }

   public Set getAllowedParameters() {
      return Sets.union(ImmutableSet.of(LootContextParameters.ATTACKING_ENTITY), this.count.getAllowedParameters());
   }

   private boolean hasLimit() {
      return this.limit > 0;
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      Entity entity = (Entity)context.get(LootContextParameters.ATTACKING_ENTITY);
      if (entity instanceof LivingEntity livingEntity) {
         int i = EnchantmentHelper.getEquipmentLevel(this.enchantment, livingEntity);
         if (i == 0) {
            return stack;
         }

         float f = (float)i * this.count.nextFloat(context);
         stack.increment(Math.round(f));
         if (this.hasLimit()) {
            stack.capCount(this.limit);
         }
      }

      return stack;
   }

   public static Builder builder(RegistryWrapper.WrapperLookup registries, LootNumberProvider count) {
      RegistryWrapper.Impl impl = registries.getOrThrow(RegistryKeys.ENCHANTMENT);
      return new Builder(impl.getOrThrow(Enchantments.LOOTING), count);
   }

   public static class Builder extends ConditionalLootFunction.Builder {
      private final RegistryEntry enchantment;
      private final LootNumberProvider count;
      private int limit = 0;

      public Builder(RegistryEntry enchantment, LootNumberProvider count) {
         this.enchantment = enchantment;
         this.count = count;
      }

      protected Builder getThisBuilder() {
         return this;
      }

      public Builder withLimit(int limit) {
         this.limit = limit;
         return this;
      }

      public LootFunction build() {
         return new EnchantedCountIncreaseLootFunction(this.getConditions(), this.enchantment, this.count, this.limit);
      }

      // $FF: synthetic method
      protected ConditionalLootFunction.Builder getThisBuilder() {
         return this.getThisBuilder();
      }
   }
}
