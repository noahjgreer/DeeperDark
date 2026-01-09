package net.minecraft.inventory;

import com.mojang.serialization.Codec;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public record ContainerLock(ItemPredicate predicate) {
   public static final ContainerLock EMPTY = new ContainerLock(ItemPredicate.Builder.create().build());
   public static final Codec CODEC;
   public static final String LOCK_KEY = "lock";

   public ContainerLock(ItemPredicate itemPredicate) {
      this.predicate = itemPredicate;
   }

   public boolean canOpen(ItemStack stack) {
      return this.predicate.test(stack);
   }

   public void write(WriteView view) {
      if (this != EMPTY) {
         view.put("lock", CODEC, this);
      }

   }

   public static ContainerLock read(ReadView view) {
      return (ContainerLock)view.read("lock", CODEC).orElse(EMPTY);
   }

   public ItemPredicate predicate() {
      return this.predicate;
   }

   static {
      CODEC = ItemPredicate.CODEC.xmap(ContainerLock::new, ContainerLock::predicate);
   }
}
