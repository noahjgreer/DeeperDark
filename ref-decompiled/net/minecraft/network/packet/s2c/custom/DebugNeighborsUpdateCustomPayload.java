package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DebugNeighborsUpdateCustomPayload(long time, BlockPos pos) implements CustomPayload {
   public static final PacketCodec CODEC = CustomPayload.codecOf(DebugNeighborsUpdateCustomPayload::write, DebugNeighborsUpdateCustomPayload::new);
   public static final CustomPayload.Id ID = CustomPayload.id("debug/neighbors_update");

   private DebugNeighborsUpdateCustomPayload(PacketByteBuf buf) {
      this(buf.readVarLong(), buf.readBlockPos());
   }

   public DebugNeighborsUpdateCustomPayload(long l, BlockPos blockPos) {
      this.time = l;
      this.pos = blockPos;
   }

   private void write(PacketByteBuf buf) {
      buf.writeVarLong(this.time);
      buf.writeBlockPos(this.pos);
   }

   public CustomPayload.Id getId() {
      return ID;
   }

   public long time() {
      return this.time;
   }

   public BlockPos pos() {
      return this.pos;
   }
}
