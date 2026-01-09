package net.minecraft.network.packet;

import net.minecraft.network.NetworkSide;
import net.minecraft.util.Identifier;

public class StatusPackets {
   public static final PacketType STATUS_RESPONSE = s2c("status_response");
   public static final PacketType STATUS_REQUEST = c2s("status_request");

   private static PacketType s2c(String id) {
      return new PacketType(NetworkSide.CLIENTBOUND, Identifier.ofVanilla(id));
   }

   private static PacketType c2s(String id) {
      return new PacketType(NetworkSide.SERVERBOUND, Identifier.ofVanilla(id));
   }
}
