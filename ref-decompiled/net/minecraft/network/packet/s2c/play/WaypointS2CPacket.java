package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.minecraft.world.waypoint.TrackedWaypointHandler;
import net.minecraft.world.waypoint.Waypoint;
import net.minecraft.world.waypoint.WaypointHandler;

public record WaypointS2CPacket(Operation operation, TrackedWaypoint waypoint) implements Packet {
   public static final PacketCodec CODEC;

   public WaypointS2CPacket(Operation operation, TrackedWaypoint trackedWaypoint) {
      this.operation = operation;
      this.waypoint = trackedWaypoint;
   }

   public static WaypointS2CPacket untrack(UUID source) {
      return new WaypointS2CPacket(WaypointS2CPacket.Operation.UNTRACK, TrackedWaypoint.empty(source));
   }

   public static WaypointS2CPacket trackPos(UUID source, Waypoint.Config config, Vec3i pos) {
      return new WaypointS2CPacket(WaypointS2CPacket.Operation.TRACK, TrackedWaypoint.ofPos(source, config, pos));
   }

   public static WaypointS2CPacket updatePos(UUID source, Waypoint.Config config, Vec3i pos) {
      return new WaypointS2CPacket(WaypointS2CPacket.Operation.UPDATE, TrackedWaypoint.ofPos(source, config, pos));
   }

   public static WaypointS2CPacket trackChunk(UUID source, Waypoint.Config config, ChunkPos chunkPos) {
      return new WaypointS2CPacket(WaypointS2CPacket.Operation.TRACK, TrackedWaypoint.ofChunk(source, config, chunkPos));
   }

   public static WaypointS2CPacket updateChunk(UUID source, Waypoint.Config config, ChunkPos chunkPos) {
      return new WaypointS2CPacket(WaypointS2CPacket.Operation.UPDATE, TrackedWaypoint.ofChunk(source, config, chunkPos));
   }

   public static WaypointS2CPacket trackAzimuth(UUID source, Waypoint.Config config, float azimuth) {
      return new WaypointS2CPacket(WaypointS2CPacket.Operation.TRACK, TrackedWaypoint.ofAzimuth(source, config, azimuth));
   }

   public static WaypointS2CPacket updateAzimuth(UUID source, Waypoint.Config config, float azimuth) {
      return new WaypointS2CPacket(WaypointS2CPacket.Operation.UPDATE, TrackedWaypoint.ofAzimuth(source, config, azimuth));
   }

   public PacketType getPacketType() {
      return PlayPackets.WAYPOINT;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onWaypoint(this);
   }

   public void apply(TrackedWaypointHandler handler) {
      this.operation.handler.accept(handler, this.waypoint);
   }

   public Operation operation() {
      return this.operation;
   }

   public TrackedWaypoint waypoint() {
      return this.waypoint;
   }

   static {
      CODEC = PacketCodec.tuple(WaypointS2CPacket.Operation.PACKET_CODEC, WaypointS2CPacket::operation, TrackedWaypoint.PACKET_CODEC, WaypointS2CPacket::waypoint, WaypointS2CPacket::new);
   }

   static enum Operation {
      TRACK(WaypointHandler::onTrack),
      UNTRACK(WaypointHandler::onUntrack),
      UPDATE(WaypointHandler::onUpdate);

      final BiConsumer handler;
      public static final IntFunction BY_INDEX = ValueLists.createIndexToValueFunction(Enum::ordinal, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.WRAP);
      public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(BY_INDEX, Enum::ordinal);

      private Operation(final BiConsumer handler) {
         this.handler = handler;
      }

      // $FF: synthetic method
      private static Operation[] method_70591() {
         return new Operation[]{TRACK, UNTRACK, UPDATE};
      }
   }
}
