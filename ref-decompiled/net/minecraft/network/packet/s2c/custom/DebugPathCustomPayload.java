package net.minecraft.network.packet.s2c.custom;

import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record DebugPathCustomPayload(int entityId, Path path, float maxNodeDistance) implements CustomPayload {
   public static final PacketCodec CODEC = CustomPayload.codecOf(DebugPathCustomPayload::write, DebugPathCustomPayload::new);
   public static final CustomPayload.Id ID = CustomPayload.id("debug/path");

   private DebugPathCustomPayload(PacketByteBuf buf) {
      this(buf.readInt(), Path.fromBuf(buf), buf.readFloat());
   }

   public DebugPathCustomPayload(int i, Path path, float f) {
      this.entityId = i;
      this.path = path;
      this.maxNodeDistance = f;
   }

   private void write(PacketByteBuf buf) {
      buf.writeInt(this.entityId);
      this.path.toBuf(buf);
      buf.writeFloat(this.maxNodeDistance);
   }

   public CustomPayload.Id getId() {
      return ID;
   }

   public int entityId() {
      return this.entityId;
   }

   public Path path() {
      return this.path;
   }

   public float maxNodeDistance() {
      return this.maxNodeDistance;
   }
}
