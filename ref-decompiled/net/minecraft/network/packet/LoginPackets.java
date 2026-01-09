package net.minecraft.network.packet;

import net.minecraft.network.NetworkSide;
import net.minecraft.util.Identifier;

public class LoginPackets {
   public static final PacketType CUSTOM_QUERY = s2c("custom_query");
   public static final PacketType LOGIN_FINISHED = s2c("login_finished");
   public static final PacketType HELLO_S2C = s2c("hello");
   public static final PacketType LOGIN_COMPRESSION = s2c("login_compression");
   public static final PacketType LOGIN_DISCONNECT = s2c("login_disconnect");
   public static final PacketType CUSTOM_QUERY_ANSWER = c2s("custom_query_answer");
   public static final PacketType HELLO_C2S = c2s("hello");
   public static final PacketType KEY = c2s("key");
   public static final PacketType LOGIN_ACKNOWLEDGED = c2s("login_acknowledged");

   private static PacketType s2c(String id) {
      return new PacketType(NetworkSide.CLIENTBOUND, Identifier.ofVanilla(id));
   }

   private static PacketType c2s(String id) {
      return new PacketType(NetworkSide.SERVERBOUND, Identifier.ofVanilla(id));
   }
}
