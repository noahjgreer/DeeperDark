package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.PlayerInput;

public record PlayerInputC2SPacket(PlayerInput input) implements Packet {
   public static final PacketCodec CODEC;

   public PlayerInputC2SPacket(PlayerInput playerInput) {
      this.input = playerInput;
   }

   public PacketType getPacketType() {
      return PlayPackets.PLAYER_INPUT;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onPlayerInput(this);
   }

   public PlayerInput input() {
      return this.input;
   }

   static {
      CODEC = PacketCodec.tuple(PlayerInput.PACKET_CODEC, PlayerInputC2SPacket::input, PlayerInputC2SPacket::new);
   }
}
