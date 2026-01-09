package net.minecraft.recipe.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.SmithingTrimRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.math.random.Random;

public interface SlotDisplay {
   Codec CODEC = Registries.SLOT_DISPLAY.getCodec().dispatch(SlotDisplay::serializer, Serializer::codec);
   PacketCodec PACKET_CODEC = PacketCodecs.registryValue(RegistryKeys.SLOT_DISPLAY).dispatch(SlotDisplay::serializer, Serializer::streamCodec);

   Stream appendStacks(ContextParameterMap parameters, DisplayedItemFactory factory);

   Serializer serializer();

   default boolean isEnabled(FeatureSet features) {
      return true;
   }

   default List getStacks(ContextParameterMap parameters) {
      return this.appendStacks(parameters, SlotDisplay.NoopDisplayedItemFactory.INSTANCE).toList();
   }

   default ItemStack getFirst(ContextParameterMap context) {
      return (ItemStack)this.appendStacks(context, SlotDisplay.NoopDisplayedItemFactory.INSTANCE).findFirst().orElse(ItemStack.EMPTY);
   }

   public static class NoopDisplayedItemFactory implements DisplayedItemFactory.FromStack {
      public static final NoopDisplayedItemFactory INSTANCE = new NoopDisplayedItemFactory();

      public ItemStack toDisplayed(ItemStack itemStack) {
         return itemStack;
      }

      // $FF: synthetic method
      public Object toDisplayed(final ItemStack stack) {
         return this.toDisplayed(stack);
      }
   }

   public static record WithRemainderSlotDisplay(SlotDisplay input, SlotDisplay remainder) implements SlotDisplay {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(SlotDisplay.CODEC.fieldOf("input").forGetter(WithRemainderSlotDisplay::input), SlotDisplay.CODEC.fieldOf("remainder").forGetter(WithRemainderSlotDisplay::remainder)).apply(instance, WithRemainderSlotDisplay::new);
      });
      public static final PacketCodec PACKET_CODEC;
      public static final Serializer SERIALIZER;

      public WithRemainderSlotDisplay(SlotDisplay slotDisplay, SlotDisplay slotDisplay2) {
         this.input = slotDisplay;
         this.remainder = slotDisplay2;
      }

      public Serializer serializer() {
         return SERIALIZER;
      }

      public Stream appendStacks(ContextParameterMap parameters, DisplayedItemFactory factory) {
         if (factory instanceof DisplayedItemFactory.FromRemainder fromRemainder) {
            List list = this.remainder.appendStacks(parameters, factory).toList();
            return this.input.appendStacks(parameters, factory).map((input) -> {
               return fromRemainder.toDisplayed(input, list);
            });
         } else {
            return this.input.appendStacks(parameters, factory);
         }
      }

      public boolean isEnabled(FeatureSet features) {
         return this.input.isEnabled(features) && this.remainder.isEnabled(features);
      }

      public SlotDisplay input() {
         return this.input;
      }

      public SlotDisplay remainder() {
         return this.remainder;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(SlotDisplay.PACKET_CODEC, WithRemainderSlotDisplay::input, SlotDisplay.PACKET_CODEC, WithRemainderSlotDisplay::remainder, WithRemainderSlotDisplay::new);
         SERIALIZER = new Serializer(CODEC, PACKET_CODEC);
      }
   }

   public static record CompositeSlotDisplay(List contents) implements SlotDisplay {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(SlotDisplay.CODEC.listOf().fieldOf("contents").forGetter(CompositeSlotDisplay::contents)).apply(instance, CompositeSlotDisplay::new);
      });
      public static final PacketCodec PACKET_CODEC;
      public static final Serializer SERIALIZER;

      public CompositeSlotDisplay(List list) {
         this.contents = list;
      }

      public Serializer serializer() {
         return SERIALIZER;
      }

      public Stream appendStacks(ContextParameterMap parameters, DisplayedItemFactory factory) {
         return this.contents.stream().flatMap((display) -> {
            return display.appendStacks(parameters, factory);
         });
      }

      public boolean isEnabled(FeatureSet features) {
         return this.contents.stream().allMatch((child) -> {
            return child.isEnabled(features);
         });
      }

      public List contents() {
         return this.contents;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(SlotDisplay.PACKET_CODEC.collect(PacketCodecs.toList()), CompositeSlotDisplay::contents, CompositeSlotDisplay::new);
         SERIALIZER = new Serializer(CODEC, PACKET_CODEC);
      }
   }

   public static record TagSlotDisplay(TagKey tag) implements SlotDisplay {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(TagKey.unprefixedCodec(RegistryKeys.ITEM).fieldOf("tag").forGetter(TagSlotDisplay::tag)).apply(instance, TagSlotDisplay::new);
      });
      public static final PacketCodec PACKET_CODEC;
      public static final Serializer SERIALIZER;

      public TagSlotDisplay(TagKey tagKey) {
         this.tag = tagKey;
      }

      public Serializer serializer() {
         return SERIALIZER;
      }

      public Stream appendStacks(ContextParameterMap parameters, DisplayedItemFactory factory) {
         if (factory instanceof DisplayedItemFactory.FromStack fromStack) {
            RegistryWrapper.WrapperLookup wrapperLookup = (RegistryWrapper.WrapperLookup)parameters.getNullable(SlotDisplayContexts.REGISTRIES);
            if (wrapperLookup != null) {
               return wrapperLookup.getOrThrow(RegistryKeys.ITEM).getOptional(this.tag).map((tag) -> {
                  Stream var10000 = tag.stream();
                  Objects.requireNonNull(fromStack);
                  return var10000.map(fromStack::toDisplayed);
               }).stream().flatMap((values) -> {
                  return values;
               });
            }
         }

         return Stream.empty();
      }

      public TagKey tag() {
         return this.tag;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(TagKey.packetCodec(RegistryKeys.ITEM), TagSlotDisplay::tag, TagSlotDisplay::new);
         SERIALIZER = new Serializer(CODEC, PACKET_CODEC);
      }
   }

   public static record StackSlotDisplay(ItemStack stack) implements SlotDisplay {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(ItemStack.VALIDATED_CODEC.fieldOf("item").forGetter(StackSlotDisplay::stack)).apply(instance, StackSlotDisplay::new);
      });
      public static final PacketCodec PACKET_CODEC;
      public static final Serializer SERIALIZER;

      public StackSlotDisplay(ItemStack itemStack) {
         this.stack = itemStack;
      }

      public Serializer serializer() {
         return SERIALIZER;
      }

      public Stream appendStacks(ContextParameterMap parameters, DisplayedItemFactory factory) {
         if (factory instanceof DisplayedItemFactory.FromStack fromStack) {
            return Stream.of(fromStack.toDisplayed(this.stack));
         } else {
            return Stream.empty();
         }
      }

      public boolean equals(Object o) {
         boolean var10000;
         if (this != o) {
            label26: {
               if (o instanceof StackSlotDisplay) {
                  StackSlotDisplay stackSlotDisplay = (StackSlotDisplay)o;
                  if (ItemStack.areEqual(this.stack, stackSlotDisplay.stack)) {
                     break label26;
                  }
               }

               var10000 = false;
               return var10000;
            }
         }

         var10000 = true;
         return var10000;
      }

      public boolean isEnabled(FeatureSet features) {
         return this.stack.getItem().isEnabled(features);
      }

      public ItemStack stack() {
         return this.stack;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(ItemStack.PACKET_CODEC, StackSlotDisplay::stack, StackSlotDisplay::new);
         SERIALIZER = new Serializer(CODEC, PACKET_CODEC);
      }
   }

   public static record ItemSlotDisplay(RegistryEntry item) implements SlotDisplay {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Item.ENTRY_CODEC.fieldOf("item").forGetter(ItemSlotDisplay::item)).apply(instance, ItemSlotDisplay::new);
      });
      public static final PacketCodec PACKET_CODEC;
      public static final Serializer SERIALIZER;

      public ItemSlotDisplay(Item item) {
         this((RegistryEntry)item.getRegistryEntry());
      }

      public ItemSlotDisplay(RegistryEntry registryEntry) {
         this.item = registryEntry;
      }

      public Serializer serializer() {
         return SERIALIZER;
      }

      public Stream appendStacks(ContextParameterMap parameters, DisplayedItemFactory factory) {
         if (factory instanceof DisplayedItemFactory.FromStack fromStack) {
            return Stream.of(fromStack.toDisplayed(this.item));
         } else {
            return Stream.empty();
         }
      }

      public boolean isEnabled(FeatureSet features) {
         return ((Item)this.item.value()).isEnabled(features);
      }

      public RegistryEntry item() {
         return this.item;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(Item.ENTRY_PACKET_CODEC, ItemSlotDisplay::item, ItemSlotDisplay::new);
         SERIALIZER = new Serializer(CODEC, PACKET_CODEC);
      }
   }

   public static record SmithingTrimSlotDisplay(SlotDisplay base, SlotDisplay material, RegistryEntry pattern) implements SlotDisplay {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(SlotDisplay.CODEC.fieldOf("base").forGetter(SmithingTrimSlotDisplay::base), SlotDisplay.CODEC.fieldOf("material").forGetter(SmithingTrimSlotDisplay::material), ArmorTrimPattern.ENTRY_CODEC.fieldOf("pattern").forGetter(SmithingTrimSlotDisplay::pattern)).apply(instance, SmithingTrimSlotDisplay::new);
      });
      public static final PacketCodec PACKET_CODEC;
      public static final Serializer SERIALIZER;

      public SmithingTrimSlotDisplay(SlotDisplay slotDisplay, SlotDisplay slotDisplay2, RegistryEntry registryEntry) {
         this.base = slotDisplay;
         this.material = slotDisplay2;
         this.pattern = registryEntry;
      }

      public Serializer serializer() {
         return SERIALIZER;
      }

      public Stream appendStacks(ContextParameterMap parameters, DisplayedItemFactory factory) {
         if (factory instanceof DisplayedItemFactory.FromStack fromStack) {
            RegistryWrapper.WrapperLookup wrapperLookup = (RegistryWrapper.WrapperLookup)parameters.getNullable(SlotDisplayContexts.REGISTRIES);
            if (wrapperLookup != null) {
               Random random = Random.create((long)System.identityHashCode(this));
               List list = this.base.getStacks(parameters);
               if (list.isEmpty()) {
                  return Stream.empty();
               }

               List list2 = this.material.getStacks(parameters);
               if (list2.isEmpty()) {
                  return Stream.empty();
               }

               Stream var10000 = Stream.generate(() -> {
                  ItemStack itemStack = (ItemStack)Util.getRandom(list, random);
                  ItemStack itemStack2 = (ItemStack)Util.getRandom(list2, random);
                  return SmithingTrimRecipe.craft(wrapperLookup, itemStack, itemStack2, this.pattern);
               }).limit(256L).filter((stack) -> {
                  return !stack.isEmpty();
               }).limit(16L);
               Objects.requireNonNull(fromStack);
               return var10000.map(fromStack::toDisplayed);
            }
         }

         return Stream.empty();
      }

      public SlotDisplay base() {
         return this.base;
      }

      public SlotDisplay material() {
         return this.material;
      }

      public RegistryEntry pattern() {
         return this.pattern;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(SlotDisplay.PACKET_CODEC, SmithingTrimSlotDisplay::base, SlotDisplay.PACKET_CODEC, SmithingTrimSlotDisplay::material, ArmorTrimPattern.ENTRY_PACKET_CODEC, SmithingTrimSlotDisplay::pattern, SmithingTrimSlotDisplay::new);
         SERIALIZER = new Serializer(CODEC, PACKET_CODEC);
      }
   }

   public static class AnyFuelSlotDisplay implements SlotDisplay {
      public static final AnyFuelSlotDisplay INSTANCE = new AnyFuelSlotDisplay();
      public static final MapCodec CODEC;
      public static final PacketCodec PACKET_CODEC;
      public static final Serializer SERIALIZER;

      private AnyFuelSlotDisplay() {
      }

      public Serializer serializer() {
         return SERIALIZER;
      }

      public String toString() {
         return "<any fuel>";
      }

      public Stream appendStacks(ContextParameterMap parameters, DisplayedItemFactory factory) {
         if (factory instanceof DisplayedItemFactory.FromStack fromStack) {
            FuelRegistry fuelRegistry = (FuelRegistry)parameters.getNullable(SlotDisplayContexts.FUEL_REGISTRY);
            if (fuelRegistry != null) {
               Stream var10000 = fuelRegistry.getFuelItems().stream();
               Objects.requireNonNull(fromStack);
               return var10000.map(fromStack::toDisplayed);
            }
         }

         return Stream.empty();
      }

      static {
         CODEC = MapCodec.unit(INSTANCE);
         PACKET_CODEC = PacketCodec.unit(INSTANCE);
         SERIALIZER = new Serializer(CODEC, PACKET_CODEC);
      }
   }

   public static class EmptySlotDisplay implements SlotDisplay {
      public static final EmptySlotDisplay INSTANCE = new EmptySlotDisplay();
      public static final MapCodec CODEC;
      public static final PacketCodec PACKET_CODEC;
      public static final Serializer SERIALIZER;

      private EmptySlotDisplay() {
      }

      public Serializer serializer() {
         return SERIALIZER;
      }

      public String toString() {
         return "<empty>";
      }

      public Stream appendStacks(ContextParameterMap parameters, DisplayedItemFactory factory) {
         return Stream.empty();
      }

      static {
         CODEC = MapCodec.unit(INSTANCE);
         PACKET_CODEC = PacketCodec.unit(INSTANCE);
         SERIALIZER = new Serializer(CODEC, PACKET_CODEC);
      }
   }

   public static record Serializer(MapCodec codec, PacketCodec streamCodec) {
      public Serializer(MapCodec mapCodec, PacketCodec packetCodec) {
         this.codec = mapCodec;
         this.streamCodec = packetCodec;
      }

      public MapCodec codec() {
         return this.codec;
      }

      public PacketCodec streamCodec() {
         return this.streamCodec;
      }
   }
}
