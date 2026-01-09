package net.minecraft.network.packet.s2c.query;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPingResultPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PingPackets;

public record PingResultS2CPacket(long startTime) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(PingResultS2CPacket::write, PingResultS2CPacket::new);

   private PingResultS2CPacket(PacketByteBuf buf) {
      this(buf.readLong());
   }

   public PingResultS2CPacket(long startTime) {
      this.startTime = startTime;
   }

   private void write(PacketByteBuf buf) {
      buf.writeLong(this.startTime);
   }

   public PacketType getPacketType() {
      return PingPackets.PONG_RESPONSE;
   }

   public void apply(ClientPingResultPacketListener clientPingResultPacketListener) {
      clientPingResultPacketListener.onPingResult(this);
   }

   public long startTime() {
      return this.startTime;
   }
}
