package net.minecraft.recipe;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.recipe.v1.ingredient.FabricIngredient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryListCodec;
import net.minecraft.util.dynamic.Codecs;

public final class Ingredient implements RecipeMatcher.RawIngredient, Predicate, FabricIngredient {
   public static final PacketCodec PACKET_CODEC;
   public static final PacketCodec OPTIONAL_PACKET_CODEC;
   public static final Codec ENTRIES_CODEC;
   public static final Codec CODEC;
   private final RegistryEntryList entries;

   private Ingredient(RegistryEntryList entries) {
      entries.getStorage().ifRight((list) -> {
         if (list.isEmpty()) {
            throw new UnsupportedOperationException("Ingredients can't be empty");
         } else if (list.contains(Items.AIR.getRegistryEntry())) {
            throw new UnsupportedOperationException("Ingredient can't contain air");
         }
      });
      this.entries = entries;
   }

   public static boolean matches(Optional ingredient, ItemStack stack) {
      Optional var10000 = ingredient.map((ingredient2) -> {
         return ingredient2.test(stack);
      });
      Objects.requireNonNull(stack);
      return (Boolean)var10000.orElseGet(stack::isEmpty);
   }

   /** @deprecated */
   @Deprecated
   public Stream getMatchingItems() {
      return this.entries.stream();
   }

   public boolean isEmpty() {
      return this.entries.size() == 0;
   }

   public boolean test(ItemStack itemStack) {
      return itemStack.isIn(this.entries);
   }

   public boolean acceptsItem(RegistryEntry registryEntry) {
      return this.entries.contains(registryEntry);
   }

   public boolean equals(Object o) {
      if (o instanceof Ingredient ingredient) {
         return Objects.equals(this.entries, ingredient.entries);
      } else {
         return false;
      }
   }

   public static Ingredient ofItem(ItemConvertible item) {
      return new Ingredient(RegistryEntryList.of(item.asItem().getRegistryEntry()));
   }

   public static Ingredient ofItems(ItemConvertible... items) {
      return ofItems(Arrays.stream(items));
   }

   public static Ingredient ofItems(Stream stacks) {
      return new Ingredient(RegistryEntryList.of(stacks.map((item) -> {
         return item.asItem().getRegistryEntry();
      }).toList()));
   }

   public static Ingredient ofTag(RegistryEntryList tag) {
      return new Ingredient(tag);
   }

   public SlotDisplay toDisplay() {
      return (SlotDisplay)this.entries.getStorage().map(SlotDisplay.TagSlotDisplay::new, (items) -> {
         return new SlotDisplay.CompositeSlotDisplay(items.stream().map(Ingredient::createDisplayWithRemainder).toList());
      });
   }

   public static SlotDisplay toDisplay(Optional ingredient) {
      return (SlotDisplay)ingredient.map(Ingredient::toDisplay).orElse(SlotDisplay.EmptySlotDisplay.INSTANCE);
   }

   private static SlotDisplay createDisplayWithRemainder(RegistryEntry displayedItem) {
      SlotDisplay slotDisplay = new SlotDisplay.ItemSlotDisplay(displayedItem);
      ItemStack itemStack = ((Item)displayedItem.value()).getRecipeRemainder();
      if (!itemStack.isEmpty()) {
         SlotDisplay slotDisplay2 = new SlotDisplay.StackSlotDisplay(itemStack);
         return new SlotDisplay.WithRemainderSlotDisplay(slotDisplay, slotDisplay2);
      } else {
         return slotDisplay;
      }
   }

   // $FF: synthetic method
   public boolean test(final Object stack) {
      return this.test((ItemStack)stack);
   }

   // $FF: synthetic method
   public boolean acceptsItem(final Object object) {
      return this.acceptsItem((RegistryEntry)object);
   }

   static {
      PACKET_CODEC = PacketCodecs.registryEntryList(RegistryKeys.ITEM).xmap(Ingredient::new, (ingredient) -> {
         return ingredient.entries;
      });
      OPTIONAL_PACKET_CODEC = PacketCodecs.registryEntryList(RegistryKeys.ITEM).xmap((entries) -> {
         return entries.size() == 0 ? Optional.empty() : Optional.of(new Ingredient(entries));
      }, (optional) -> {
         return (RegistryEntryList)optional.map((ingredient) -> {
            return ingredient.entries;
         }).orElse(RegistryEntryList.of());
      });
      ENTRIES_CODEC = RegistryEntryListCodec.create(RegistryKeys.ITEM, Item.ENTRY_CODEC, false);
      CODEC = Codecs.nonEmptyEntryList(ENTRIES_CODEC).xmap(Ingredient::new, (ingredient) -> {
         return ingredient.entries;
      });
   }
}
