package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.world.event.PositionSource;

public record DebugGameEventListenersCustomPayload(PositionSource listenerPos, int listenerRange) implements CustomPayload {
   public static final PacketCodec PACKET_CODEC;
   public static final CustomPayload.Id ID;

   public DebugGameEventListenersCustomPayload(PositionSource positionSource, int i) {
      this.listenerPos = positionSource;
      this.listenerRange = i;
   }

   public CustomPayload.Id getId() {
      return ID;
   }

   public PositionSource listenerPos() {
      return this.listenerPos;
   }

   public int listenerRange() {
      return this.listenerRange;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PositionSource.PACKET_CODEC, DebugGameEventListenersCustomPayload::listenerPos, PacketCodecs.VAR_INT, DebugGameEventListenersCustomPayload::listenerRange, DebugGameEventListenersCustomPayload::new);
      ID = CustomPayload.id("debug/game_event_listeners");
   }
}
