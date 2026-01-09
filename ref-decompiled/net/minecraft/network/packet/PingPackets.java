package net.minecraft.network.packet;

import net.minecraft.network.NetworkSide;
import net.minecraft.util.Identifier;

public class PingPackets {
   public static final PacketType PONG_RESPONSE = s2c("pong_response");
   public static final PacketType PING_REQUEST = c2s("ping_request");

   private static PacketType s2c(String id) {
      return new PacketType(NetworkSide.CLIENTBOUND, Identifier.ofVanilla(id));
   }

   private static PacketType c2s(String id) {
      return new PacketType(NetworkSide.SERVERBOUND, Identifier.ofVanilla(id));
   }
}
