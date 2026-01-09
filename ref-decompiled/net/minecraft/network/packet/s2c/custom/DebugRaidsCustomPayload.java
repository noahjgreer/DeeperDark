package net.minecraft.network.packet.s2c.custom;

import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DebugRaidsCustomPayload(List raidCenters) implements CustomPayload {
   public static final PacketCodec CODEC = CustomPayload.codecOf(DebugRaidsCustomPayload::write, DebugRaidsCustomPayload::new);
   public static final CustomPayload.Id ID = CustomPayload.id("debug/raids");

   private DebugRaidsCustomPayload(PacketByteBuf buf) {
      this(buf.readList(BlockPos.PACKET_CODEC));
   }

   public DebugRaidsCustomPayload(List list) {
      this.raidCenters = list;
   }

   private void write(PacketByteBuf buf) {
      buf.writeCollection(this.raidCenters, BlockPos.PACKET_CODEC);
   }

   public CustomPayload.Id getId() {
      return ID;
   }

   public List raidCenters() {
      return this.raidCenters;
   }
}
