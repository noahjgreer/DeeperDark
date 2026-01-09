package net.minecraft.component.type;

import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;

public final class ContainerComponent implements TooltipAppender {
   private static final int ALL_SLOTS_EMPTY = -1;
   private static final int MAX_SLOTS = 256;
   public static final ContainerComponent DEFAULT = new ContainerComponent(DefaultedList.of());
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;
   private final DefaultedList stacks;
   private final int hashCode;

   private ContainerComponent(DefaultedList stacks) {
      if (stacks.size() > 256) {
         throw new IllegalArgumentException("Got " + stacks.size() + " items, but maximum is 256");
      } else {
         this.stacks = stacks;
         this.hashCode = ItemStack.listHashCode(stacks);
      }
   }

   private ContainerComponent(int size) {
      this(DefaultedList.ofSize(size, ItemStack.EMPTY));
   }

   private ContainerComponent(List stacks) {
      this(stacks.size());

      for(int i = 0; i < stacks.size(); ++i) {
         this.stacks.set(i, (ItemStack)stacks.get(i));
      }

   }

   private static ContainerComponent fromSlots(List slots) {
      OptionalInt optionalInt = slots.stream().mapToInt(Slot::index).max();
      if (optionalInt.isEmpty()) {
         return DEFAULT;
      } else {
         ContainerComponent containerComponent = new ContainerComponent(optionalInt.getAsInt() + 1);
         Iterator var3 = slots.iterator();

         while(var3.hasNext()) {
            Slot slot = (Slot)var3.next();
            containerComponent.stacks.set(slot.index(), slot.item());
         }

         return containerComponent;
      }
   }

   public static ContainerComponent fromStacks(List stacks) {
      int i = findLastNonEmptyIndex(stacks);
      if (i == -1) {
         return DEFAULT;
      } else {
         ContainerComponent containerComponent = new ContainerComponent(i + 1);

         for(int j = 0; j <= i; ++j) {
            containerComponent.stacks.set(j, ((ItemStack)stacks.get(j)).copy());
         }

         return containerComponent;
      }
   }

   private static int findLastNonEmptyIndex(List stacks) {
      for(int i = stacks.size() - 1; i >= 0; --i) {
         if (!((ItemStack)stacks.get(i)).isEmpty()) {
            return i;
         }
      }

      return -1;
   }

   private List collectSlots() {
      List list = new ArrayList();

      for(int i = 0; i < this.stacks.size(); ++i) {
         ItemStack itemStack = (ItemStack)this.stacks.get(i);
         if (!itemStack.isEmpty()) {
            list.add(new Slot(i, itemStack));
         }
      }

      return list;
   }

   public void copyTo(DefaultedList stacks) {
      for(int i = 0; i < stacks.size(); ++i) {
         ItemStack itemStack = i < this.stacks.size() ? (ItemStack)this.stacks.get(i) : ItemStack.EMPTY;
         stacks.set(i, itemStack.copy());
      }

   }

   public ItemStack copyFirstStack() {
      return this.stacks.isEmpty() ? ItemStack.EMPTY : ((ItemStack)this.stacks.get(0)).copy();
   }

   public Stream stream() {
      return this.stacks.stream().map(ItemStack::copy);
   }

   public Stream streamNonEmpty() {
      return this.stacks.stream().filter((stack) -> {
         return !stack.isEmpty();
      }).map(ItemStack::copy);
   }

   public Iterable iterateNonEmpty() {
      return Iterables.filter(this.stacks, (stack) -> {
         return !stack.isEmpty();
      });
   }

   public Iterable iterateNonEmptyCopy() {
      return Iterables.transform(this.iterateNonEmpty(), ItemStack::copy);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         boolean var10000;
         if (o instanceof ContainerComponent) {
            ContainerComponent containerComponent = (ContainerComponent)o;
            if (ItemStack.stacksEqual(this.stacks, containerComponent.stacks)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.hashCode;
   }

   public void appendTooltip(Item.TooltipContext context, Consumer textConsumer, TooltipType type, ComponentsAccess components) {
      int i = 0;
      int j = 0;
      Iterator var7 = this.iterateNonEmpty().iterator();

      while(var7.hasNext()) {
         ItemStack itemStack = (ItemStack)var7.next();
         ++j;
         if (i <= 4) {
            ++i;
            textConsumer.accept(Text.translatable("item.container.item_count", itemStack.getName(), itemStack.getCount()));
         }
      }

      if (j - i > 0) {
         textConsumer.accept(Text.translatable("item.container.more_items", j - i).formatted(Formatting.ITALIC));
      }

   }

   static {
      CODEC = ContainerComponent.Slot.CODEC.sizeLimitedListOf(256).xmap(ContainerComponent::fromSlots, ContainerComponent::collectSlots);
      PACKET_CODEC = ItemStack.OPTIONAL_PACKET_CODEC.collect(PacketCodecs.toList(256)).xmap(ContainerComponent::new, (component) -> {
         return component.stacks;
      });
   }

   private static record Slot(int index, ItemStack item) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.intRange(0, 255).fieldOf("slot").forGetter(Slot::index), ItemStack.CODEC.fieldOf("item").forGetter(Slot::item)).apply(instance, Slot::new);
      });

      Slot(int i, ItemStack itemStack) {
         this.index = i;
         this.item = itemStack;
      }

      public int index() {
         return this.index;
      }

      public ItemStack item() {
         return this.item;
      }
   }
}
