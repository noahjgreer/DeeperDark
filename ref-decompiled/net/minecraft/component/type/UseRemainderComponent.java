package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;

public record UseRemainderComponent(ItemStack convertInto) {
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;

   public UseRemainderComponent(ItemStack itemStack) {
      this.convertInto = itemStack;
   }

   public ItemStack convert(ItemStack stack, int oldCount, boolean inCreative, StackInserter inserter) {
      if (inCreative) {
         return stack;
      } else if (stack.getCount() >= oldCount) {
         return stack;
      } else {
         ItemStack itemStack = this.convertInto.copy();
         if (stack.isEmpty()) {
            return itemStack;
         } else {
            inserter.apply(itemStack);
            return stack;
         }
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         UseRemainderComponent useRemainderComponent = (UseRemainderComponent)o;
         return ItemStack.areEqual(this.convertInto, useRemainderComponent.convertInto);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return ItemStack.hashCode(this.convertInto);
   }

   public ItemStack convertInto() {
      return this.convertInto;
   }

   static {
      CODEC = ItemStack.CODEC.xmap(UseRemainderComponent::new, UseRemainderComponent::convertInto);
      PACKET_CODEC = PacketCodec.tuple(ItemStack.PACKET_CODEC, UseRemainderComponent::convertInto, UseRemainderComponent::new);
   }

   @FunctionalInterface
   public interface StackInserter {
      void apply(ItemStack stack);
   }
}
