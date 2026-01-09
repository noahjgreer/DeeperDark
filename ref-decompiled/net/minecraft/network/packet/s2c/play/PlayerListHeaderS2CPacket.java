package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record PlayerListHeaderS2CPacket(Text header, Text footer) implements Packet {
   public static final PacketCodec CODEC;

   public PlayerListHeaderS2CPacket(Text text, Text text2) {
      this.header = text;
      this.footer = text2;
   }

   public PacketType getPacketType() {
      return PlayPackets.TAB_LIST;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onPlayerListHeader(this);
   }

   public Text header() {
      return this.header;
   }

   public Text footer() {
      return this.footer;
   }

   static {
      CODEC = PacketCodec.tuple(TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, PlayerListHeaderS2CPacket::header, TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, PlayerListHeaderS2CPacket::footer, PlayerListHeaderS2CPacket::new);
   }
}
