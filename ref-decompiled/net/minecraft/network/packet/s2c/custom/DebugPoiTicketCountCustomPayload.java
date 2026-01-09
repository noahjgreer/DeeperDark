package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DebugPoiTicketCountCustomPayload(BlockPos pos, int freeTicketCount) implements CustomPayload {
   public static final PacketCodec CODEC = CustomPayload.codecOf(DebugPoiTicketCountCustomPayload::write, DebugPoiTicketCountCustomPayload::new);
   public static final CustomPayload.Id ID = CustomPayload.id("debug/poi_ticket_count");

   private DebugPoiTicketCountCustomPayload(PacketByteBuf buf) {
      this(buf.readBlockPos(), buf.readInt());
   }

   public DebugPoiTicketCountCustomPayload(BlockPos blockPos, int i) {
      this.pos = blockPos;
      this.freeTicketCount = i;
   }

   private void write(PacketByteBuf buf) {
      buf.writeBlockPos(this.pos);
      buf.writeInt(this.freeTicketCount);
   }

   public CustomPayload.Id getId() {
      return ID;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public int freeTicketCount() {
      return this.freeTicketCount;
   }
}
