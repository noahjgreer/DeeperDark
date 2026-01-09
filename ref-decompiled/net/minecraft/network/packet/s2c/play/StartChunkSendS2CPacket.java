package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class StartChunkSendS2CPacket implements Packet {
   public static final StartChunkSendS2CPacket INSTANCE = new StartChunkSendS2CPacket();
   public static final PacketCodec CODEC;

   private StartChunkSendS2CPacket() {
   }

   public PacketType getPacketType() {
      return PlayPackets.CHUNK_BATCH_START;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onStartChunkSend(this);
   }

   static {
      CODEC = PacketCodec.unit(INSTANCE);
   }
}
