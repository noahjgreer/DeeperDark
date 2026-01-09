package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.world.Difficulty;

public record UpdateDifficultyC2SPacket(Difficulty difficulty) implements Packet {
   public static final PacketCodec CODEC;

   public UpdateDifficultyC2SPacket(Difficulty difficulty) {
      this.difficulty = difficulty;
   }

   public PacketType getPacketType() {
      return PlayPackets.CHANGE_DIFFICULTY_C2S;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onUpdateDifficulty(this);
   }

   public Difficulty difficulty() {
      return this.difficulty;
   }

   static {
      CODEC = PacketCodec.tuple(Difficulty.PACKET_CODEC, UpdateDifficultyC2SPacket::difficulty, UpdateDifficultyC2SPacket::new);
   }
}
