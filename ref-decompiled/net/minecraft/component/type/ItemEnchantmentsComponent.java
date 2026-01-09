package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;

public class ItemEnchantmentsComponent implements TooltipAppender {
   public static final ItemEnchantmentsComponent DEFAULT = new ItemEnchantmentsComponent(new Object2IntOpenHashMap());
   private static final Codec ENCHANTMENT_LEVEL_CODEC = Codec.intRange(1, 255);
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;
   final Object2IntOpenHashMap enchantments;

   ItemEnchantmentsComponent(Object2IntOpenHashMap enchantments) {
      this.enchantments = enchantments;
      ObjectIterator var2 = enchantments.object2IntEntrySet().iterator();

      Object2IntMap.Entry entry;
      int i;
      do {
         if (!var2.hasNext()) {
            return;
         }

         entry = (Object2IntMap.Entry)var2.next();
         i = entry.getIntValue();
      } while(i >= 0 && i <= 255);

      String var10002 = String.valueOf(entry.getKey());
      throw new IllegalArgumentException("Enchantment " + var10002 + " has invalid level " + i);
   }

   public int getLevel(RegistryEntry enchantment) {
      return this.enchantments.getInt(enchantment);
   }

   public void appendTooltip(Item.TooltipContext context, Consumer textConsumer, TooltipType type, ComponentsAccess components) {
      RegistryWrapper.WrapperLookup wrapperLookup = context.getRegistryLookup();
      RegistryEntryList registryEntryList = getTooltipOrderList(wrapperLookup, RegistryKeys.ENCHANTMENT, EnchantmentTags.TOOLTIP_ORDER);
      Iterator var7 = registryEntryList.iterator();

      while(var7.hasNext()) {
         RegistryEntry registryEntry = (RegistryEntry)var7.next();
         int i = this.enchantments.getInt(registryEntry);
         if (i > 0) {
            textConsumer.accept(Enchantment.getName(registryEntry, i));
         }
      }

      ObjectIterator var10 = this.enchantments.object2IntEntrySet().iterator();

      while(var10.hasNext()) {
         Object2IntMap.Entry entry = (Object2IntMap.Entry)var10.next();
         RegistryEntry registryEntry2 = (RegistryEntry)entry.getKey();
         if (!registryEntryList.contains(registryEntry2)) {
            textConsumer.accept(Enchantment.getName((RegistryEntry)entry.getKey(), entry.getIntValue()));
         }
      }

   }

   private static RegistryEntryList getTooltipOrderList(@Nullable RegistryWrapper.WrapperLookup registries, RegistryKey registryRef, TagKey tooltipOrderTag) {
      if (registries != null) {
         Optional optional = registries.getOrThrow(registryRef).getOptional(tooltipOrderTag);
         if (optional.isPresent()) {
            return (RegistryEntryList)optional.get();
         }
      }

      return RegistryEntryList.of();
   }

   public Set getEnchantments() {
      return Collections.unmodifiableSet(this.enchantments.keySet());
   }

   public Set getEnchantmentEntries() {
      return Collections.unmodifiableSet(this.enchantments.object2IntEntrySet());
   }

   public int getSize() {
      return this.enchantments.size();
   }

   public boolean isEmpty() {
      return this.enchantments.isEmpty();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o instanceof ItemEnchantmentsComponent) {
         ItemEnchantmentsComponent itemEnchantmentsComponent = (ItemEnchantmentsComponent)o;
         return this.enchantments.equals(itemEnchantmentsComponent.enchantments);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.enchantments.hashCode();
   }

   public String toString() {
      return "ItemEnchantments{enchantments=" + String.valueOf(this.enchantments) + "}";
   }

   static {
      CODEC = Codec.unboundedMap(Enchantment.ENTRY_CODEC, ENCHANTMENT_LEVEL_CODEC).xmap((map) -> {
         return new ItemEnchantmentsComponent(new Object2IntOpenHashMap(map));
      }, (itemEnchantmentsComponent) -> {
         return itemEnchantmentsComponent.enchantments;
      });
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.map(Object2IntOpenHashMap::new, Enchantment.ENTRY_PACKET_CODEC, PacketCodecs.VAR_INT), (component) -> {
         return component.enchantments;
      }, ItemEnchantmentsComponent::new);
   }

   public static class Builder {
      private final Object2IntOpenHashMap enchantments = new Object2IntOpenHashMap();

      public Builder(ItemEnchantmentsComponent enchantmentsComponent) {
         this.enchantments.putAll(enchantmentsComponent.enchantments);
      }

      public void set(RegistryEntry enchantment, int level) {
         if (level <= 0) {
            this.enchantments.removeInt(enchantment);
         } else {
            this.enchantments.put(enchantment, Math.min(level, 255));
         }

      }

      public void add(RegistryEntry enchantment, int level) {
         if (level > 0) {
            this.enchantments.merge(enchantment, Math.min(level, 255), Integer::max);
         }

      }

      public void remove(Predicate predicate) {
         this.enchantments.keySet().removeIf(predicate);
      }

      public int getLevel(RegistryEntry enchantment) {
         return this.enchantments.getOrDefault(enchantment, 0);
      }

      public Set getEnchantments() {
         return this.enchantments.keySet();
      }

      public ItemEnchantmentsComponent build() {
         return new ItemEnchantmentsComponent(this.enchantments);
      }
   }
}
