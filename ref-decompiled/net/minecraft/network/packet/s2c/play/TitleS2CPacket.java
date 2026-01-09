package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record TitleS2CPacket(Text text) implements Packet {
   public static final PacketCodec CODEC;

   public TitleS2CPacket(Text text) {
      this.text = text;
   }

   public PacketType getPacketType() {
      return PlayPackets.SET_TITLE_TEXT;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onTitle(this);
   }

   public Text text() {
      return this.text;
   }

   static {
      CODEC = PacketCodec.tuple(TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, TitleS2CPacket::text, TitleS2CPacket::new);
   }
}
