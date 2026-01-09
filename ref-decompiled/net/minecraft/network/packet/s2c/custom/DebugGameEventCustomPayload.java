package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.Vec3d;

public record DebugGameEventCustomPayload(RegistryKey gameEventType, Vec3d pos) implements CustomPayload {
   public static final PacketCodec CODEC = CustomPayload.codecOf(DebugGameEventCustomPayload::write, DebugGameEventCustomPayload::new);
   public static final CustomPayload.Id ID = CustomPayload.id("debug/game_event");

   private DebugGameEventCustomPayload(PacketByteBuf buf) {
      this(buf.readRegistryKey(RegistryKeys.GAME_EVENT), buf.readVec3d());
   }

   public DebugGameEventCustomPayload(RegistryKey registryKey, Vec3d vec3d) {
      this.gameEventType = registryKey;
      this.pos = vec3d;
   }

   private void write(PacketByteBuf buf) {
      buf.writeRegistryKey(this.gameEventType);
      buf.writeVec3d(this.pos);
   }

   public CustomPayload.Id getId() {
      return ID;
   }

   public RegistryKey gameEventType() {
      return this.gameEventType;
   }

   public Vec3d pos() {
      return this.pos;
   }
}
