package net.minecraft.network.packet.s2c.common;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record DisconnectS2CPacket(Text reason) implements Packet {
   public static final PacketCodec CODEC;

   public DisconnectS2CPacket(Text text) {
      this.reason = text;
   }

   public PacketType getPacketType() {
      return CommonPackets.DISCONNECT;
   }

   public void apply(ClientCommonPacketListener clientCommonPacketListener) {
      clientCommonPacketListener.onDisconnect(this);
   }

   public Text reason() {
      return this.reason;
   }

   static {
      CODEC = TextCodecs.PACKET_CODEC.xmap(DisconnectS2CPacket::new, DisconnectS2CPacket::reason);
   }
}
