package net.minecraft.screen.sync;

import com.mojang.datafixers.DataFixUtils;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

public interface ItemStackHash {
   ItemStackHash EMPTY = new ItemStackHash() {
      public String toString() {
         return "<empty>";
      }

      public boolean hashEquals(ItemStack stack, ComponentChangesHash.ComponentHasher hasher) {
         return stack.isEmpty();
      }
   };
   PacketCodec PACKET_CODEC = PacketCodecs.optional(ItemStackHash.Impl.PACKET_CODEC).xmap((hash) -> {
      return (ItemStackHash)DataFixUtils.orElse(hash, EMPTY);
   }, (hash) -> {
      Optional var10000;
      if (hash instanceof Impl impl) {
         var10000 = Optional.of(impl);
      } else {
         var10000 = Optional.empty();
      }

      return var10000;
   });

   boolean hashEquals(ItemStack stack, ComponentChangesHash.ComponentHasher hasher);

   static ItemStackHash fromItemStack(ItemStack stack, ComponentChangesHash.ComponentHasher hasher) {
      return (ItemStackHash)(stack.isEmpty() ? EMPTY : new Impl(stack.getRegistryEntry(), stack.getCount(), ComponentChangesHash.fromComponents(stack.getComponentChanges(), hasher)));
   }

   public static record Impl(RegistryEntry item, int count, ComponentChangesHash components) implements ItemStackHash {
      public static final PacketCodec PACKET_CODEC;

      public Impl(RegistryEntry registryEntry, int i, ComponentChangesHash componentChangesHash) {
         this.item = registryEntry;
         this.count = i;
         this.components = componentChangesHash;
      }

      public boolean hashEquals(ItemStack stack, ComponentChangesHash.ComponentHasher hasher) {
         if (this.count != stack.getCount()) {
            return false;
         } else {
            return !this.item.equals(stack.getRegistryEntry()) ? false : this.components.hashEquals(stack.getComponentChanges(), hasher);
         }
      }

      public RegistryEntry item() {
         return this.item;
      }

      public int count() {
         return this.count;
      }

      public ComponentChangesHash components() {
         return this.components;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(PacketCodecs.registryEntry(RegistryKeys.ITEM), Impl::item, PacketCodecs.VAR_INT, Impl::count, ComponentChangesHash.PACKET_CODEC, Impl::components, Impl::new);
      }
   }
}
