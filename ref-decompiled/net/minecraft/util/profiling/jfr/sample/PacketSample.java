package net.minecraft.util.profiling.jfr.sample;

import jdk.jfr.consumer.RecordedEvent;

public record PacketSample(String side, String protocolId, String packetId) {
   public PacketSample(String string, String string2, String string3) {
      this.side = string;
      this.protocolId = string2;
      this.packetId = string3;
   }

   public static PacketSample fromEvent(RecordedEvent event) {
      return new PacketSample(event.getString("packetDirection"), event.getString("protocolId"), event.getString("packetId"));
   }

   public String side() {
      return this.side;
   }

   public String protocolId() {
      return this.protocolId;
   }

   public String packetId() {
      return this.packetId;
   }
}
