package net.minecraft.inventory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;

public record StackWithSlot(int slot, ItemStack stack) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codecs.UNSIGNED_BYTE.fieldOf("Slot").orElse(0).forGetter(StackWithSlot::slot), ItemStack.MAP_CODEC.forGetter(StackWithSlot::stack)).apply(instance, StackWithSlot::new);
   });

   public StackWithSlot(int i, ItemStack itemStack) {
      this.slot = i;
      this.stack = itemStack;
   }

   public boolean isValidSlot(int slots) {
      return this.slot >= 0 && this.slot < slots;
   }

   public int slot() {
      return this.slot;
   }

   public ItemStack stack() {
      return this.stack;
   }
}
