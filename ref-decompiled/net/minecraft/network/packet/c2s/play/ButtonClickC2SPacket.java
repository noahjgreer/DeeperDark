package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record ButtonClickC2SPacket(int syncId, int buttonId) implements Packet {
   public static final PacketCodec CODEC;

   public ButtonClickC2SPacket(int i, int j) {
      this.syncId = i;
      this.buttonId = j;
   }

   public PacketType getPacketType() {
      return PlayPackets.CONTAINER_BUTTON_CLICK;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onButtonClick(this);
   }

   public int syncId() {
      return this.syncId;
   }

   public int buttonId() {
      return this.buttonId;
   }

   static {
      CODEC = PacketCodec.tuple(PacketCodecs.SYNC_ID, ButtonClickC2SPacket::syncId, PacketCodecs.VAR_INT, ButtonClickC2SPacket::buttonId, ButtonClickC2SPacket::new);
   }
}
