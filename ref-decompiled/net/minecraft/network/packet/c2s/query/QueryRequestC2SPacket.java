package net.minecraft.network.packet.c2s.query;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerQueryPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.StatusPackets;

public class QueryRequestC2SPacket implements Packet {
   public static final QueryRequestC2SPacket INSTANCE = new QueryRequestC2SPacket();
   public static final PacketCodec CODEC;

   private QueryRequestC2SPacket() {
   }

   public PacketType getPacketType() {
      return StatusPackets.STATUS_REQUEST;
   }

   public void apply(ServerQueryPacketListener serverQueryPacketListener) {
      serverQueryPacketListener.onRequest(this);
   }

   static {
      CODEC = PacketCodec.unit(INSTANCE);
   }
}
