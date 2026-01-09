package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Iterator;
import java.util.Optional;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.predicate.NumberRange;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;

public record EnchantmentPredicate(Optional enchantments, NumberRange.IntRange levels) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(RegistryCodecs.entryList(RegistryKeys.ENCHANTMENT).optionalFieldOf("enchantments").forGetter(EnchantmentPredicate::enchantments), NumberRange.IntRange.CODEC.optionalFieldOf("levels", NumberRange.IntRange.ANY).forGetter(EnchantmentPredicate::levels)).apply(instance, EnchantmentPredicate::new);
   });

   public EnchantmentPredicate(RegistryEntry enchantment, NumberRange.IntRange levels) {
      this(Optional.of(RegistryEntryList.of(enchantment)), levels);
   }

   public EnchantmentPredicate(RegistryEntryList enchantments, NumberRange.IntRange levels) {
      this(Optional.of(enchantments), levels);
   }

   public EnchantmentPredicate(Optional optional, NumberRange.IntRange intRange) {
      this.enchantments = optional;
      this.levels = intRange;
   }

   public boolean test(ItemEnchantmentsComponent enchantmentsComponent) {
      Iterator var2;
      if (this.enchantments.isPresent()) {
         var2 = ((RegistryEntryList)this.enchantments.get()).iterator();

         RegistryEntry registryEntry;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            registryEntry = (RegistryEntry)var2.next();
         } while(!this.testLevel(enchantmentsComponent, registryEntry));

         return true;
      } else if (this.levels != NumberRange.IntRange.ANY) {
         var2 = enchantmentsComponent.getEnchantmentEntries().iterator();

         Object2IntMap.Entry entry;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            entry = (Object2IntMap.Entry)var2.next();
         } while(!this.levels.test(entry.getIntValue()));

         return true;
      } else {
         return !enchantmentsComponent.isEmpty();
      }
   }

   private boolean testLevel(ItemEnchantmentsComponent enchantmentsComponent, RegistryEntry enchantment) {
      int i = enchantmentsComponent.getLevel(enchantment);
      if (i == 0) {
         return false;
      } else {
         return this.levels == NumberRange.IntRange.ANY ? true : this.levels.test(i);
      }
   }

   public Optional enchantments() {
      return this.enchantments;
   }

   public NumberRange.IntRange levels() {
      return this.levels;
   }
}
