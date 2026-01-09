package net.minecraft.loot.function;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;

public class EnchantRandomlyLootFunction extends ConditionalLootFunction {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(instance.group(RegistryCodecs.entryList(RegistryKeys.ENCHANTMENT).optionalFieldOf("options").forGetter((function) -> {
         return function.options;
      }), Codec.BOOL.optionalFieldOf("only_compatible", true).forGetter((function) -> {
         return function.onlyCompatible;
      }))).apply(instance, EnchantRandomlyLootFunction::new);
   });
   private final Optional options;
   private final boolean onlyCompatible;

   EnchantRandomlyLootFunction(List conditions, Optional options, boolean onlyCompatible) {
      super(conditions);
      this.options = options;
      this.onlyCompatible = onlyCompatible;
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.ENCHANT_RANDOMLY;
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      Random random = context.getRandom();
      boolean bl = stack.isOf(Items.BOOK);
      boolean bl2 = !bl && this.onlyCompatible;
      Stream stream = ((Stream)this.options.map(RegistryEntryList::stream).orElseGet(() -> {
         return context.getWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).streamEntries().map(Function.identity());
      })).filter((entry) -> {
         return !bl2 || ((Enchantment)entry.value()).isAcceptableItem(stack);
      });
      List list = stream.toList();
      Optional optional = Util.getRandomOrEmpty(list, random);
      if (optional.isEmpty()) {
         LOGGER.warn("Couldn't find a compatible enchantment for {}", stack);
         return stack;
      } else {
         return addEnchantmentToStack(stack, (RegistryEntry)optional.get(), random);
      }
   }

   private static ItemStack addEnchantmentToStack(ItemStack stack, RegistryEntry enchantment, Random random) {
      int i = MathHelper.nextInt(random, ((Enchantment)enchantment.value()).getMinLevel(), ((Enchantment)enchantment.value()).getMaxLevel());
      if (stack.isOf(Items.BOOK)) {
         stack = new ItemStack(Items.ENCHANTED_BOOK);
      }

      stack.addEnchantment(enchantment, i);
      return stack;
   }

   public static Builder create() {
      return new Builder();
   }

   public static Builder builder(RegistryWrapper.WrapperLookup registries) {
      return create().options(registries.getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(EnchantmentTags.ON_RANDOM_LOOT));
   }

   public static class Builder extends ConditionalLootFunction.Builder {
      private Optional options = Optional.empty();
      private boolean onlyCompatible = true;

      protected Builder getThisBuilder() {
         return this;
      }

      public Builder option(RegistryEntry enchantment) {
         this.options = Optional.of(RegistryEntryList.of(enchantment));
         return this;
      }

      public Builder options(RegistryEntryList options) {
         this.options = Optional.of(options);
         return this;
      }

      public Builder allowIncompatible() {
         this.onlyCompatible = false;
         return this;
      }

      public LootFunction build() {
         return new EnchantRandomlyLootFunction(this.getConditions(), this.options, this.onlyCompatible);
      }

      // $FF: synthetic method
      protected ConditionalLootFunction.Builder getThisBuilder() {
         return this.getThisBuilder();
      }
   }
}
