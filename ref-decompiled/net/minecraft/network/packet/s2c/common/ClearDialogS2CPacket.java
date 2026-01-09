package net.minecraft.network.packet.s2c.common;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public class ClearDialogS2CPacket implements Packet {
   public static final ClearDialogS2CPacket INSTANCE = new ClearDialogS2CPacket();
   public static final PacketCodec CODEC;

   private ClearDialogS2CPacket() {
   }

   public PacketType getPacketType() {
      return CommonPackets.CLEAR_DIALOG;
   }

   public void apply(ClientCommonPacketListener clientCommonPacketListener) {
      clientCommonPacketListener.onClearDialog(this);
   }

   static {
      CODEC = PacketCodec.unit(INSTANCE);
   }
}
