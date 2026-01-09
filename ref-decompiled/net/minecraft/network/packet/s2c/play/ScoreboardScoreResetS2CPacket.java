package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import org.jetbrains.annotations.Nullable;

public record ScoreboardScoreResetS2CPacket(String scoreHolderName, @Nullable String objectiveName) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(ScoreboardScoreResetS2CPacket::write, ScoreboardScoreResetS2CPacket::new);

   private ScoreboardScoreResetS2CPacket(PacketByteBuf buf) {
      this(buf.readString(), (String)buf.readNullable(PacketByteBuf::readString));
   }

   public ScoreboardScoreResetS2CPacket(String string, @Nullable String string2) {
      this.scoreHolderName = string;
      this.objectiveName = string2;
   }

   private void write(PacketByteBuf buf) {
      buf.writeString(this.scoreHolderName);
      buf.writeNullable(this.objectiveName, PacketByteBuf::writeString);
   }

   public PacketType getPacketType() {
      return PlayPackets.RESET_SCORE;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onScoreboardScoreReset(this);
   }

   public String scoreHolderName() {
      return this.scoreHolderName;
   }

   @Nullable
   public String objectiveName() {
      return this.objectiveName;
   }
}
