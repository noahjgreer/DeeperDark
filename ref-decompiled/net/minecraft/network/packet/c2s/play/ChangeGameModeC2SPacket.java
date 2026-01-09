package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.world.GameMode;

public record ChangeGameModeC2SPacket(GameMode mode) implements Packet {
   public static final PacketCodec CODEC;

   public ChangeGameModeC2SPacket(GameMode gameMode) {
      this.mode = gameMode;
   }

   public PacketType getPacketType() {
      return PlayPackets.CHANGE_GAME_MODE;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onChangeGameMode(this);
   }

   public GameMode mode() {
      return this.mode;
   }

   static {
      CODEC = PacketCodec.tuple(GameMode.PACKET_CODEC, ChangeGameModeC2SPacket::mode, ChangeGameModeC2SPacket::new);
   }
}
