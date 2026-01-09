package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record OverlayMessageS2CPacket(Text text) implements Packet {
   public static final PacketCodec CODEC;

   public OverlayMessageS2CPacket(Text text) {
      this.text = text;
   }

   public PacketType getPacketType() {
      return PlayPackets.SET_ACTION_BAR_TEXT;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onOverlayMessage(this);
   }

   public Text text() {
      return this.text;
   }

   static {
      CODEC = PacketCodec.tuple(TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, OverlayMessageS2CPacket::text, OverlayMessageS2CPacket::new);
   }
}
