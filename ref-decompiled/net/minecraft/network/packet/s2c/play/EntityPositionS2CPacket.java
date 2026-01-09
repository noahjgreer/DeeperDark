package net.minecraft.network.packet.s2c.play;

import java.util.Set;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record EntityPositionS2CPacket(int entityId, PlayerPosition change, Set relatives, boolean onGround) implements Packet {
   public static final PacketCodec CODEC;

   public EntityPositionS2CPacket(int i, PlayerPosition playerPosition, Set set, boolean bl) {
      this.entityId = i;
      this.change = playerPosition;
      this.relatives = set;
      this.onGround = bl;
   }

   public static EntityPositionS2CPacket create(int entityId, PlayerPosition change, Set relatives, boolean onGround) {
      return new EntityPositionS2CPacket(entityId, change, relatives, onGround);
   }

   public PacketType getPacketType() {
      return PlayPackets.TELEPORT_ENTITY;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onEntityPosition(this);
   }

   public int entityId() {
      return this.entityId;
   }

   public PlayerPosition change() {
      return this.change;
   }

   public Set relatives() {
      return this.relatives;
   }

   public boolean onGround() {
      return this.onGround;
   }

   static {
      CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, EntityPositionS2CPacket::entityId, PlayerPosition.PACKET_CODEC, EntityPositionS2CPacket::change, PositionFlag.PACKET_CODEC, EntityPositionS2CPacket::relatives, PacketCodecs.BOOLEAN, EntityPositionS2CPacket::onGround, EntityPositionS2CPacket::new);
   }
}
