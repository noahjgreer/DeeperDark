package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DebugPoiAddedCustomPayload(BlockPos pos, String poiType, int freeTicketCount) implements CustomPayload {
   public static final PacketCodec CODEC = CustomPayload.codecOf(DebugPoiAddedCustomPayload::write, DebugPoiAddedCustomPayload::new);
   public static final CustomPayload.Id ID = CustomPayload.id("debug/poi_added");

   private DebugPoiAddedCustomPayload(PacketByteBuf buf) {
      this(buf.readBlockPos(), buf.readString(), buf.readInt());
   }

   public DebugPoiAddedCustomPayload(BlockPos blockPos, String string, int i) {
      this.pos = blockPos;
      this.poiType = string;
      this.freeTicketCount = i;
   }

   private void write(PacketByteBuf buf) {
      buf.writeBlockPos(this.pos);
      buf.writeString(this.poiType);
      buf.writeInt(this.freeTicketCount);
   }

   public CustomPayload.Id getId() {
      return ID;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public String poiType() {
      return this.poiType;
   }

   public int freeTicketCount() {
      return this.freeTicketCount;
   }
}
