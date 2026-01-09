package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class EnterReconfigurationS2CPacket implements Packet {
   public static final EnterReconfigurationS2CPacket INSTANCE = new EnterReconfigurationS2CPacket();
   public static final PacketCodec CODEC;

   private EnterReconfigurationS2CPacket() {
   }

   public PacketType getPacketType() {
      return PlayPackets.START_CONFIGURATION;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onEnterReconfiguration(this);
   }

   public boolean transitionsNetworkState() {
      return true;
   }

   static {
      CODEC = PacketCodec.unit(INSTANCE);
   }
}
