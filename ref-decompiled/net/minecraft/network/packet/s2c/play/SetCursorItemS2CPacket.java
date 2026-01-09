package net.minecraft.network.packet.s2c.play;

import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record SetCursorItemS2CPacket(ItemStack contents) implements Packet {
   public static final PacketCodec CODEC;

   public SetCursorItemS2CPacket(ItemStack itemStack) {
      this.contents = itemStack;
   }

   public PacketType getPacketType() {
      return PlayPackets.SET_CURSOR_ITEM;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onSetCursorItem(this);
   }

   public ItemStack contents() {
      return this.contents;
   }

   static {
      CODEC = PacketCodec.tuple(ItemStack.OPTIONAL_PACKET_CODEC, SetCursorItemS2CPacket::contents, SetCursorItemS2CPacket::new);
   }
}
