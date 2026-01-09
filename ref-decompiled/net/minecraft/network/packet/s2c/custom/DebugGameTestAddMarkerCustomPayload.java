package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DebugGameTestAddMarkerCustomPayload(BlockPos pos, int color, String text, int durationMs) implements CustomPayload {
   public static final PacketCodec CODEC = CustomPayload.codecOf(DebugGameTestAddMarkerCustomPayload::write, DebugGameTestAddMarkerCustomPayload::new);
   public static final CustomPayload.Id ID = CustomPayload.id("debug/game_test_add_marker");

   private DebugGameTestAddMarkerCustomPayload(PacketByteBuf buf) {
      this(buf.readBlockPos(), buf.readInt(), buf.readString(), buf.readInt());
   }

   public DebugGameTestAddMarkerCustomPayload(BlockPos blockPos, int i, String string, int j) {
      this.pos = blockPos;
      this.color = i;
      this.text = string;
      this.durationMs = j;
   }

   private void write(PacketByteBuf buf) {
      buf.writeBlockPos(this.pos);
      buf.writeInt(this.color);
      buf.writeString(this.text);
      buf.writeInt(this.durationMs);
   }

   public CustomPayload.Id getId() {
      return ID;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public int color() {
      return this.color;
   }

   public String text() {
      return this.text;
   }

   public int durationMs() {
      return this.durationMs;
   }
}
