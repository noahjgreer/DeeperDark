package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

public record ItemPredicate(Optional items, NumberRange.IntRange count, ComponentsPredicate components) implements Predicate {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(RegistryCodecs.entryList(RegistryKeys.ITEM).optionalFieldOf("items").forGetter(ItemPredicate::items), NumberRange.IntRange.CODEC.optionalFieldOf("count", NumberRange.IntRange.ANY).forGetter(ItemPredicate::count), ComponentsPredicate.CODEC.forGetter(ItemPredicate::components)).apply(instance, ItemPredicate::new);
   });

   public ItemPredicate(Optional optional, NumberRange.IntRange intRange, ComponentsPredicate componentsPredicate) {
      this.items = optional;
      this.count = intRange;
      this.components = componentsPredicate;
   }

   public boolean test(ItemStack stack) {
      if (this.items.isPresent() && !stack.isIn((RegistryEntryList)this.items.get())) {
         return false;
      } else if (!this.count.test(stack.getCount())) {
         return false;
      } else {
         return this.components.test((ComponentsAccess)stack);
      }
   }

   public Optional items() {
      return this.items;
   }

   public NumberRange.IntRange count() {
      return this.count;
   }

   public ComponentsPredicate components() {
      return this.components;
   }

   // $FF: synthetic method
   public boolean test(final Object stack) {
      return this.test((ItemStack)stack);
   }

   public static class Builder {
      private Optional item = Optional.empty();
      private NumberRange.IntRange count;
      private ComponentsPredicate components;

      public Builder() {
         this.count = NumberRange.IntRange.ANY;
         this.components = ComponentsPredicate.EMPTY;
      }

      public static Builder create() {
         return new Builder();
      }

      public Builder items(RegistryEntryLookup itemRegistry, ItemConvertible... items) {
         this.item = Optional.of(RegistryEntryList.of((Function)((item) -> {
            return item.asItem().getRegistryEntry();
         }), (Object[])items));
         return this;
      }

      public Builder tag(RegistryEntryLookup itemRegistry, TagKey tag) {
         this.item = Optional.of(itemRegistry.getOrThrow(tag));
         return this;
      }

      public Builder count(NumberRange.IntRange count) {
         this.count = count;
         return this;
      }

      public Builder components(ComponentsPredicate components) {
         this.components = components;
         return this;
      }

      public ItemPredicate build() {
         return new ItemPredicate(this.item, this.count, this.components);
      }
   }
}
