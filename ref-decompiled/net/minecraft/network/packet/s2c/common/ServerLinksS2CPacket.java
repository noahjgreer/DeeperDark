package net.minecraft.network.packet.s2c.common;

import java.util.List;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.server.ServerLinks;

public record ServerLinksS2CPacket(List links) implements Packet {
   public static final PacketCodec CODEC;

   public ServerLinksS2CPacket(List list) {
      this.links = list;
   }

   public PacketType getPacketType() {
      return CommonPackets.SERVER_LINKS;
   }

   public void apply(ClientCommonPacketListener clientCommonPacketListener) {
      clientCommonPacketListener.onServerLinks(this);
   }

   public List links() {
      return this.links;
   }

   static {
      CODEC = PacketCodec.tuple(ServerLinks.LIST_CODEC, ServerLinksS2CPacket::links, ServerLinksS2CPacket::new);
   }
}
