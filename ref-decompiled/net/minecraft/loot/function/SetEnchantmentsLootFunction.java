package net.minecraft.loot.function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;

public class SetEnchantmentsLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(instance.group(Codec.unboundedMap(Enchantment.ENTRY_CODEC, LootNumberProviderTypes.CODEC).optionalFieldOf("enchantments", Map.of()).forGetter((function) -> {
         return function.enchantments;
      }), Codec.BOOL.fieldOf("add").orElse(false).forGetter((function) -> {
         return function.add;
      }))).apply(instance, SetEnchantmentsLootFunction::new);
   });
   private final Map enchantments;
   private final boolean add;

   SetEnchantmentsLootFunction(List conditions, Map enchantments, boolean add) {
      super(conditions);
      this.enchantments = Map.copyOf(enchantments);
      this.add = add;
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.SET_ENCHANTMENTS;
   }

   public Set getAllowedParameters() {
      return (Set)this.enchantments.values().stream().flatMap((numberProvider) -> {
         return numberProvider.getAllowedParameters().stream();
      }).collect(ImmutableSet.toImmutableSet());
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      if (stack.isOf(Items.BOOK)) {
         stack = stack.withItem(Items.ENCHANTED_BOOK);
      }

      EnchantmentHelper.apply(stack, (builder) -> {
         if (this.add) {
            this.enchantments.forEach((enchantment, level) -> {
               builder.set(enchantment, MathHelper.clamp(builder.getLevel(enchantment) + level.nextInt(context), 0, 255));
            });
         } else {
            this.enchantments.forEach((enchantment, level) -> {
               builder.set(enchantment, MathHelper.clamp(level.nextInt(context), 0, 255));
            });
         }

      });
      return stack;
   }

   public static class Builder extends ConditionalLootFunction.Builder {
      private final ImmutableMap.Builder enchantments;
      private final boolean add;

      public Builder() {
         this(false);
      }

      public Builder(boolean add) {
         this.enchantments = ImmutableMap.builder();
         this.add = add;
      }

      protected Builder getThisBuilder() {
         return this;
      }

      public Builder enchantment(RegistryEntry enchantment, LootNumberProvider level) {
         this.enchantments.put(enchantment, level);
         return this;
      }

      public LootFunction build() {
         return new SetEnchantmentsLootFunction(this.getConditions(), this.enchantments.build(), this.add);
      }

      // $FF: synthetic method
      protected ConditionalLootFunction.Builder getThisBuilder() {
         return this.getThisBuilder();
      }
   }
}
