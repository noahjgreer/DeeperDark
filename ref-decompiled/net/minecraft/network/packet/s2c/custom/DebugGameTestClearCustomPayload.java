package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record DebugGameTestClearCustomPayload() implements CustomPayload {
   public static final PacketCodec CODEC = CustomPayload.codecOf(DebugGameTestClearCustomPayload::write, DebugGameTestClearCustomPayload::new);
   public static final CustomPayload.Id ID = CustomPayload.id("debug/game_test_clear");

   private DebugGameTestClearCustomPayload(PacketByteBuf buf) {
      this();
   }

   public DebugGameTestClearCustomPayload() {
   }

   private void write(PacketByteBuf buf) {
   }

   public CustomPayload.Id getId() {
      return ID;
   }
}
