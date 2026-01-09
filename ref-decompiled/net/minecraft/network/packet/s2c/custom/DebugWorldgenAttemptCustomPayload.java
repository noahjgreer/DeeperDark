package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DebugWorldgenAttemptCustomPayload(BlockPos pos, float scale, float red, float green, float blue, float alpha) implements CustomPayload {
   public static final PacketCodec CODEC = CustomPayload.codecOf(DebugWorldgenAttemptCustomPayload::write, DebugWorldgenAttemptCustomPayload::new);
   public static final CustomPayload.Id ID = CustomPayload.id("debug/worldgen_attempt");

   private DebugWorldgenAttemptCustomPayload(PacketByteBuf buf) {
      this(buf.readBlockPos(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
   }

   public DebugWorldgenAttemptCustomPayload(BlockPos blockPos, float f, float g, float h, float i, float j) {
      this.pos = blockPos;
      this.scale = f;
      this.red = g;
      this.green = h;
      this.blue = i;
      this.alpha = j;
   }

   private void write(PacketByteBuf buf) {
      buf.writeBlockPos(this.pos);
      buf.writeFloat(this.scale);
      buf.writeFloat(this.red);
      buf.writeFloat(this.green);
      buf.writeFloat(this.blue);
      buf.writeFloat(this.alpha);
   }

   public CustomPayload.Id getId() {
      return ID;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public float scale() {
      return this.scale;
   }

   public float red() {
      return this.red;
   }

   public float green() {
      return this.green;
   }

   public float blue() {
      return this.blue;
   }

   public float alpha() {
      return this.alpha;
   }
}
