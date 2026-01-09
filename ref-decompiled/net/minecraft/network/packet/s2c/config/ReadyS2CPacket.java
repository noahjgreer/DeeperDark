package net.minecraft.network.packet.s2c.config;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.packet.ConfigPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public class ReadyS2CPacket implements Packet {
   public static final ReadyS2CPacket INSTANCE = new ReadyS2CPacket();
   public static final PacketCodec CODEC;

   private ReadyS2CPacket() {
   }

   public PacketType getPacketType() {
      return ConfigPackets.FINISH_CONFIGURATION_S2C;
   }

   public void apply(ClientConfigurationPacketListener clientConfigurationPacketListener) {
      clientConfigurationPacketListener.onReady(this);
   }

   public boolean transitionsNetworkState() {
      return true;
   }

   static {
      CODEC = PacketCodec.unit(INSTANCE);
   }
}
