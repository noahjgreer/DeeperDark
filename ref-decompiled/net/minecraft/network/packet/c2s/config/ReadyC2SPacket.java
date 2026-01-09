package net.minecraft.network.packet.c2s.config;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerConfigurationPacketListener;
import net.minecraft.network.packet.ConfigPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public class ReadyC2SPacket implements Packet {
   public static final ReadyC2SPacket INSTANCE = new ReadyC2SPacket();
   public static final PacketCodec CODEC;

   private ReadyC2SPacket() {
   }

   public PacketType getPacketType() {
      return ConfigPackets.FINISH_CONFIGURATION_C2S;
   }

   public void apply(ServerConfigurationPacketListener serverConfigurationPacketListener) {
      serverConfigurationPacketListener.onReady(this);
   }

   public boolean transitionsNetworkState() {
      return true;
   }

   static {
      CODEC = PacketCodec.unit(INSTANCE);
   }
}
