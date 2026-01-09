package net.minecraft.network.packet.c2s.login;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.packet.LoginPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public class EnterConfigurationC2SPacket implements Packet {
   public static final EnterConfigurationC2SPacket INSTANCE = new EnterConfigurationC2SPacket();
   public static final PacketCodec CODEC;

   private EnterConfigurationC2SPacket() {
   }

   public PacketType getPacketType() {
      return LoginPackets.LOGIN_ACKNOWLEDGED;
   }

   public void apply(ServerLoginPacketListener serverLoginPacketListener) {
      serverLoginPacketListener.onEnterConfiguration(this);
   }

   public boolean transitionsNetworkState() {
      return true;
   }

   static {
      CODEC = PacketCodec.unit(INSTANCE);
   }
}
