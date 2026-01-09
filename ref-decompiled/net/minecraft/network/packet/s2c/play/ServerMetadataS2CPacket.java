package net.minecraft.network.packet.s2c.play;

import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record ServerMetadataS2CPacket(Text description, Optional favicon) implements Packet {
   public static final PacketCodec CODEC;

   public ServerMetadataS2CPacket(Text text, Optional optional) {
      this.description = text;
      this.favicon = optional;
   }

   public PacketType getPacketType() {
      return PlayPackets.SERVER_DATA;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onServerMetadata(this);
   }

   public Text description() {
      return this.description;
   }

   public Optional favicon() {
      return this.favicon;
   }

   static {
      CODEC = PacketCodec.tuple(TextCodecs.PACKET_CODEC, ServerMetadataS2CPacket::description, PacketCodecs.BYTE_ARRAY.collect(PacketCodecs::optional), ServerMetadataS2CPacket::favicon, ServerMetadataS2CPacket::new);
   }
}
