package net.minecraft.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;

public class ItemStackParticleEffect implements ParticleEffect {
   private static final Codec ITEM_STACK_CODEC;
   private final ParticleType type;
   private final ItemStack stack;

   public static MapCodec createCodec(ParticleType type) {
      return ITEM_STACK_CODEC.xmap((stack) -> {
         return new ItemStackParticleEffect(type, stack);
      }, (effect) -> {
         return effect.stack;
      }).fieldOf("item");
   }

   public static PacketCodec createPacketCodec(ParticleType type) {
      return ItemStack.PACKET_CODEC.xmap((stack) -> {
         return new ItemStackParticleEffect(type, stack);
      }, (effect) -> {
         return effect.stack;
      });
   }

   public ItemStackParticleEffect(ParticleType type, ItemStack stack) {
      if (stack.isEmpty()) {
         throw new IllegalArgumentException("Empty stacks are not allowed");
      } else {
         this.type = type;
         this.stack = stack;
      }
   }

   public ParticleType getType() {
      return this.type;
   }

   public ItemStack getItemStack() {
      return this.stack;
   }

   static {
      ITEM_STACK_CODEC = Codec.withAlternative(ItemStack.UNCOUNTED_CODEC, Item.ENTRY_CODEC, ItemStack::new);
   }
}
