package net.minecraft.network.packet.s2c.config;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.packet.ConfigPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public class ResetChatS2CPacket implements Packet {
   public static final ResetChatS2CPacket INSTANCE = new ResetChatS2CPacket();
   public static final PacketCodec CODEC;

   private ResetChatS2CPacket() {
   }

   public PacketType getPacketType() {
      return ConfigPackets.RESET_CHAT;
   }

   public void apply(ClientConfigurationPacketListener clientConfigurationPacketListener) {
      clientConfigurationPacketListener.onResetChat(this);
   }

   static {
      CODEC = PacketCodec.unit(INSTANCE);
   }
}
