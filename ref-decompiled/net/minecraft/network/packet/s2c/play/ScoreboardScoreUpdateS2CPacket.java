package net.minecraft.network.packet.s2c.play;

import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.scoreboard.number.NumberFormatTypes;
import net.minecraft.text.TextCodecs;

public record ScoreboardScoreUpdateS2CPacket(String scoreHolderName, String objectiveName, int score, Optional display, Optional numberFormat) implements Packet {
   public static final PacketCodec CODEC;

   public ScoreboardScoreUpdateS2CPacket(String string, String string2, int i, Optional optional, Optional optional2) {
      this.scoreHolderName = string;
      this.objectiveName = string2;
      this.score = i;
      this.display = optional;
      this.numberFormat = optional2;
   }

   public PacketType getPacketType() {
      return PlayPackets.SET_SCORE;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onScoreboardScoreUpdate(this);
   }

   public String scoreHolderName() {
      return this.scoreHolderName;
   }

   public String objectiveName() {
      return this.objectiveName;
   }

   public int score() {
      return this.score;
   }

   public Optional display() {
      return this.display;
   }

   public Optional numberFormat() {
      return this.numberFormat;
   }

   static {
      CODEC = PacketCodec.tuple(PacketCodecs.STRING, ScoreboardScoreUpdateS2CPacket::scoreHolderName, PacketCodecs.STRING, ScoreboardScoreUpdateS2CPacket::objectiveName, PacketCodecs.VAR_INT, ScoreboardScoreUpdateS2CPacket::score, TextCodecs.OPTIONAL_UNLIMITED_REGISTRY_PACKET_CODEC, ScoreboardScoreUpdateS2CPacket::display, NumberFormatTypes.OPTIONAL_PACKET_CODEC, ScoreboardScoreUpdateS2CPacket::numberFormat, ScoreboardScoreUpdateS2CPacket::new);
   }
}
