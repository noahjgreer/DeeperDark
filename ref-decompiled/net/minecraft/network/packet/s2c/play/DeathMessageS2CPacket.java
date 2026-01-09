package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record DeathMessageS2CPacket(int playerId, Text message) implements Packet {
   public static final PacketCodec CODEC;

   public DeathMessageS2CPacket(int i, Text text) {
      this.playerId = i;
      this.message = text;
   }

   public PacketType getPacketType() {
      return PlayPackets.PLAYER_COMBAT_KILL;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onDeathMessage(this);
   }

   public boolean isWritingErrorSkippable() {
      return true;
   }

   public int playerId() {
      return this.playerId;
   }

   public Text message() {
      return this.message;
   }

   static {
      CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, DeathMessageS2CPacket::playerId, TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, DeathMessageS2CPacket::message, DeathMessageS2CPacket::new);
   }
}
