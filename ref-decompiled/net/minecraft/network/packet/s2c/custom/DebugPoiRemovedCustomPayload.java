package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DebugPoiRemovedCustomPayload(BlockPos pos) implements CustomPayload {
   public static final PacketCodec CODEC = CustomPayload.codecOf(DebugPoiRemovedCustomPayload::write, DebugPoiRemovedCustomPayload::new);
   public static final CustomPayload.Id ID = CustomPayload.id("debug/poi_removed");

   private DebugPoiRemovedCustomPayload(PacketByteBuf buf) {
      this(buf.readBlockPos());
   }

   public DebugPoiRemovedCustomPayload(BlockPos blockPos) {
      this.pos = blockPos;
   }

   private void write(PacketByteBuf buf) {
      buf.writeBlockPos(this.pos);
   }

   public CustomPayload.Id getId() {
      return ID;
   }

   public BlockPos pos() {
      return this.pos;
   }
}
