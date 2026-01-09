package net.minecraft.network.packet.s2c.custom;

import java.util.List;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.block.WireOrientation;

public record DebugRedstoneUpdateOrderCustomPayload(long time, List wires) implements CustomPayload {
   public static final CustomPayload.Id ID = CustomPayload.id("debug/redstone_update_order");
   public static final PacketCodec PACKET_CODEC;

   public DebugRedstoneUpdateOrderCustomPayload(long l, List list) {
      this.time = l;
      this.wires = list;
   }

   public CustomPayload.Id getId() {
      return ID;
   }

   public long time() {
      return this.time;
   }

   public List wires() {
      return this.wires;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.VAR_LONG, DebugRedstoneUpdateOrderCustomPayload::time, DebugRedstoneUpdateOrderCustomPayload.Wire.PACKET_CODEC.collect(PacketCodecs.toList()), DebugRedstoneUpdateOrderCustomPayload::wires, DebugRedstoneUpdateOrderCustomPayload::new);
   }

   public static record Wire(BlockPos pos, WireOrientation orientation) {
      public static final PacketCodec PACKET_CODEC;

      public Wire(BlockPos blockPos, WireOrientation wireOrientation) {
         this.pos = blockPos;
         this.orientation = wireOrientation;
      }

      public BlockPos pos() {
         return this.pos;
      }

      public WireOrientation orientation() {
         return this.orientation;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, Wire::pos, WireOrientation.PACKET_CODEC, Wire::orientation, Wire::new);
      }
   }
}
