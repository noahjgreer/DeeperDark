package net.minecraft.network.packet;

import net.minecraft.network.NetworkSide;
import net.minecraft.util.Identifier;

public class CommonPackets {
   public static final PacketType CLEAR_DIALOG = s2c("clear_dialog");
   public static final PacketType CUSTOM_PAYLOAD_S2C = s2c("custom_payload");
   public static final PacketType CUSTOM_REPORT_DETAILS = s2c("custom_report_details");
   public static final PacketType DISCONNECT = s2c("disconnect");
   public static final PacketType KEEP_ALIVE_S2C = s2c("keep_alive");
   public static final PacketType PING = s2c("ping");
   public static final PacketType RESOURCE_PACK_POP = s2c("resource_pack_pop");
   public static final PacketType RESOURCE_PACK_PUSH = s2c("resource_pack_push");
   public static final PacketType SERVER_LINKS = s2c("server_links");
   public static final PacketType SHOW_DIALOG = s2c("show_dialog");
   public static final PacketType STORE_COOKIE = s2c("store_cookie");
   public static final PacketType TRANSFER = s2c("transfer");
   public static final PacketType UPDATE_TAGS = s2c("update_tags");
   public static final PacketType CLIENT_INFORMATION = c2s("client_information");
   public static final PacketType CUSTOM_PAYLOAD_C2S = c2s("custom_payload");
   public static final PacketType KEEP_ALIVE_C2S = c2s("keep_alive");
   public static final PacketType PONG = c2s("pong");
   public static final PacketType RESOURCE_PACK = c2s("resource_pack");
   public static final PacketType CUSTOM_CLICK_ACTION = c2s("custom_click_action");

   private static PacketType s2c(String id) {
      return new PacketType(NetworkSide.CLIENTBOUND, Identifier.ofVanilla(id));
   }

   private static PacketType c2s(String id) {
      return new PacketType(NetworkSide.SERVERBOUND, Identifier.ofVanilla(id));
   }
}
