package net.minecraft.network.packet.c2s.play;

import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record CreativeInventoryActionC2SPacket(short slot, ItemStack stack) implements Packet {
   public static final PacketCodec CODEC;

   public CreativeInventoryActionC2SPacket(int slot, ItemStack stack) {
      this((short)slot, stack);
   }

   public CreativeInventoryActionC2SPacket(short s, ItemStack itemStack) {
      this.slot = s;
      this.stack = itemStack;
   }

   public PacketType getPacketType() {
      return PlayPackets.SET_CREATIVE_MODE_SLOT;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onCreativeInventoryAction(this);
   }

   public short slot() {
      return this.slot;
   }

   public ItemStack stack() {
      return this.stack;
   }

   static {
      CODEC = PacketCodec.tuple(PacketCodecs.SHORT, CreativeInventoryActionC2SPacket::slot, ItemStack.createExtraValidatingPacketCodec(ItemStack.LENGTH_PREPENDED_OPTIONAL_PACKET_CODEC), CreativeInventoryActionC2SPacket::stack, CreativeInventoryActionC2SPacket::new);
   }
}
