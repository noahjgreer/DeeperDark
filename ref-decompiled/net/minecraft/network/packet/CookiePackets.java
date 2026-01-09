package net.minecraft.network.packet;

import net.minecraft.network.NetworkSide;
import net.minecraft.util.Identifier;

public class CookiePackets {
   public static final PacketType COOKIE_REQUEST = s2c("cookie_request");
   public static final PacketType COOKIE_RESPONSE = c2s("cookie_response");

   private static PacketType s2c(String id) {
      return new PacketType(NetworkSide.CLIENTBOUND, Identifier.ofVanilla(id));
   }

   private static PacketType c2s(String id) {
      return new PacketType(NetworkSide.SERVERBOUND, Identifier.ofVanilla(id));
   }
}
