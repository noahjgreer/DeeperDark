package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.world.Difficulty;

public record DifficultyS2CPacket(Difficulty difficulty, boolean difficultyLocked) implements Packet {
   public static final PacketCodec CODEC;

   public DifficultyS2CPacket(Difficulty difficulty, boolean bl) {
      this.difficulty = difficulty;
      this.difficultyLocked = bl;
   }

   public PacketType getPacketType() {
      return PlayPackets.CHANGE_DIFFICULTY_S2C;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onDifficulty(this);
   }

   public Difficulty difficulty() {
      return this.difficulty;
   }

   public boolean difficultyLocked() {
      return this.difficultyLocked;
   }

   static {
      CODEC = PacketCodec.tuple(Difficulty.PACKET_CODEC, DifficultyS2CPacket::difficulty, PacketCodecs.BOOLEAN, DifficultyS2CPacket::difficultyLocked, DifficultyS2CPacket::new);
   }
}
