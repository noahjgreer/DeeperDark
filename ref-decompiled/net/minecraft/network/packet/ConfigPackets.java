package net.minecraft.network.packet;

import net.minecraft.network.NetworkSide;
import net.minecraft.util.Identifier;

public class ConfigPackets {
   public static final PacketType FINISH_CONFIGURATION_S2C = s2c("finish_configuration");
   public static final PacketType REGISTRY_DATA = s2c("registry_data");
   public static final PacketType UPDATE_ENABLED_FEATURES = s2c("update_enabled_features");
   public static final PacketType SELECT_KNOWN_PACKS_S2C = s2c("select_known_packs");
   public static final PacketType RESET_CHAT = s2c("reset_chat");
   public static final PacketType FINISH_CONFIGURATION_C2S = c2s("finish_configuration");
   public static final PacketType SELECT_KNOWN_PACKS_C2S = c2s("select_known_packs");

   private static PacketType s2c(String id) {
      return new PacketType(NetworkSide.CLIENTBOUND, Identifier.ofVanilla(id));
   }

   private static PacketType c2s(String id) {
      return new PacketType(NetworkSide.SERVERBOUND, Identifier.ofVanilla(id));
   }
}
