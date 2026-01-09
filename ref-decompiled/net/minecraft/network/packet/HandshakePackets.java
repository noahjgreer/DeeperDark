package net.minecraft.network.packet;

import net.minecraft.network.NetworkSide;
import net.minecraft.util.Identifier;

public class HandshakePackets {
   public static final PacketType INTENTION = c2s("intention");

   private static PacketType c2s(String id) {
      return new PacketType(NetworkSide.SERVERBOUND, Identifier.ofVanilla(id));
   }
}
