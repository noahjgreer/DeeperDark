package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.util.math.random.Random;

public class EnchantWithLevelsLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(instance.group(LootNumberProviderTypes.CODEC.fieldOf("levels").forGetter((function) -> {
         return function.levels;
      }), RegistryCodecs.entryList(RegistryKeys.ENCHANTMENT).optionalFieldOf("options").forGetter((function) -> {
         return function.options;
      }))).apply(instance, EnchantWithLevelsLootFunction::new);
   });
   private final LootNumberProvider levels;
   private final Optional options;

   EnchantWithLevelsLootFunction(List conditions, LootNumberProvider levels, Optional options) {
      super(conditions);
      this.levels = levels;
      this.options = options;
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.ENCHANT_WITH_LEVELS;
   }

   public Set getAllowedParameters() {
      return this.levels.getAllowedParameters();
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      Random random = context.getRandom();
      DynamicRegistryManager dynamicRegistryManager = context.getWorld().getRegistryManager();
      return EnchantmentHelper.enchant(random, stack, this.levels.nextInt(context), dynamicRegistryManager, this.options);
   }

   public static Builder builder(RegistryWrapper.WrapperLookup registries, LootNumberProvider levels) {
      return (new Builder(levels)).options(registries.getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(EnchantmentTags.ON_RANDOM_LOOT));
   }

   public static class Builder extends ConditionalLootFunction.Builder {
      private final LootNumberProvider levels;
      private Optional options = Optional.empty();

      public Builder(LootNumberProvider levels) {
         this.levels = levels;
      }

      protected Builder getThisBuilder() {
         return this;
      }

      public Builder options(RegistryEntryList options) {
         this.options = Optional.of(options);
         return this;
      }

      public LootFunction build() {
         return new EnchantWithLevelsLootFunction(this.getConditions(), this.levels, this.options);
      }

      // $FF: synthetic method
      protected ConditionalLootFunction.Builder getThisBuilder() {
         return this.getThisBuilder();
      }
   }
}
