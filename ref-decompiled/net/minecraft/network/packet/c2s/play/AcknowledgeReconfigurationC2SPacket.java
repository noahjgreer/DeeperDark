package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class AcknowledgeReconfigurationC2SPacket implements Packet {
   public static final AcknowledgeReconfigurationC2SPacket INSTANCE = new AcknowledgeReconfigurationC2SPacket();
   public static final PacketCodec CODEC;

   private AcknowledgeReconfigurationC2SPacket() {
   }

   public PacketType getPacketType() {
      return PlayPackets.CONFIGURATION_ACKNOWLEDGED;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onAcknowledgeReconfiguration(this);
   }

   public boolean transitionsNetworkState() {
      return true;
   }

   static {
      CODEC = PacketCodec.unit(INSTANCE);
   }
}
