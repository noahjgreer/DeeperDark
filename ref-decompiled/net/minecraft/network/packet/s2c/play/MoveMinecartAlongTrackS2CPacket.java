package net.minecraft.network.packet.s2c.play;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public record MoveMinecartAlongTrackS2CPacket(int entityId, List lerpSteps) implements Packet {
   public static final PacketCodec PACKET_CODEC;

   public MoveMinecartAlongTrackS2CPacket(int i, List list) {
      this.entityId = i;
      this.lerpSteps = list;
   }

   public PacketType getPacketType() {
      return PlayPackets.MOVE_MINECART_ALONG_TRACK;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onMoveMinecartAlongTrack(this);
   }

   @Nullable
   public Entity getEntity(World world) {
      return world.getEntityById(this.entityId);
   }

   public int entityId() {
      return this.entityId;
   }

   public List lerpSteps() {
      return this.lerpSteps;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, MoveMinecartAlongTrackS2CPacket::entityId, ExperimentalMinecartController.Step.PACKET_CODEC.collect(PacketCodecs.toList()), MoveMinecartAlongTrackS2CPacket::lerpSteps, MoveMinecartAlongTrackS2CPacket::new);
   }
}
