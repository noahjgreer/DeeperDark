package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record GameMessageS2CPacket(Text content, boolean overlay) implements Packet {
   public static final PacketCodec CODEC;

   public GameMessageS2CPacket(Text text, boolean bl) {
      this.content = text;
      this.overlay = bl;
   }

   public PacketType getPacketType() {
      return PlayPackets.SYSTEM_CHAT;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onGameMessage(this);
   }

   public boolean isWritingErrorSkippable() {
      return true;
   }

   public Text content() {
      return this.content;
   }

   public boolean overlay() {
      return this.overlay;
   }

   static {
      CODEC = PacketCodec.tuple(TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, GameMessageS2CPacket::content, PacketCodecs.BOOLEAN, GameMessageS2CPacket::overlay, GameMessageS2CPacket::new);
   }
}
