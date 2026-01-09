package net.minecraft.network.packet.s2c.play;

import java.util.Set;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record PlayerPositionLookS2CPacket(int teleportId, PlayerPosition change, Set relatives) implements Packet {
   public static final PacketCodec CODEC;

   public PlayerPositionLookS2CPacket(int i, PlayerPosition playerPosition, Set set) {
      this.teleportId = i;
      this.change = playerPosition;
      this.relatives = set;
   }

   public static PlayerPositionLookS2CPacket of(int teleportId, PlayerPosition pos, Set flags) {
      return new PlayerPositionLookS2CPacket(teleportId, pos, flags);
   }

   public PacketType getPacketType() {
      return PlayPackets.PLAYER_POSITION;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onPlayerPositionLook(this);
   }

   public int teleportId() {
      return this.teleportId;
   }

   public PlayerPosition change() {
      return this.change;
   }

   public Set relatives() {
      return this.relatives;
   }

   static {
      CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, PlayerPositionLookS2CPacket::teleportId, PlayerPosition.PACKET_CODEC, PlayerPositionLookS2CPacket::change, PositionFlag.PACKET_CODEC, PlayerPositionLookS2CPacket::relatives, PlayerPositionLookS2CPacket::new);
   }
}
