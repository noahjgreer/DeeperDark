package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.Entity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.math.Vec3d;

public record VehicleMoveS2CPacket(Vec3d position, float yaw, float pitch) implements Packet {
   public static final PacketCodec CODEC;

   public VehicleMoveS2CPacket(Vec3d vec3d, float f, float g) {
      this.position = vec3d;
      this.yaw = f;
      this.pitch = g;
   }

   public static VehicleMoveS2CPacket fromVehicle(Entity vehicle) {
      return new VehicleMoveS2CPacket(vehicle.getPos(), vehicle.getYaw(), vehicle.getPitch());
   }

   public PacketType getPacketType() {
      return PlayPackets.MOVE_VEHICLE_S2C;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onVehicleMove(this);
   }

   public Vec3d position() {
      return this.position;
   }

   public float yaw() {
      return this.yaw;
   }

   public float pitch() {
      return this.pitch;
   }

   static {
      CODEC = PacketCodec.tuple(Vec3d.PACKET_CODEC, VehicleMoveS2CPacket::position, PacketCodecs.FLOAT, VehicleMoveS2CPacket::yaw, PacketCodecs.FLOAT, VehicleMoveS2CPacket::pitch, VehicleMoveS2CPacket::new);
   }
}
