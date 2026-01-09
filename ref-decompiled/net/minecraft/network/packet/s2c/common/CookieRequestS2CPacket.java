package net.minecraft.network.packet.s2c.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientCookieRequestPacketListener;
import net.minecraft.network.packet.CookiePackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.Identifier;

public record CookieRequestS2CPacket(Identifier key) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(CookieRequestS2CPacket::write, CookieRequestS2CPacket::new);

   private CookieRequestS2CPacket(PacketByteBuf buf) {
      this(buf.readIdentifier());
   }

   public CookieRequestS2CPacket(Identifier identifier) {
      this.key = identifier;
   }

   private void write(PacketByteBuf buf) {
      buf.writeIdentifier(this.key);
   }

   public PacketType getPacketType() {
      return CookiePackets.COOKIE_REQUEST;
   }

   public void apply(ClientCookieRequestPacketListener clientCookieRequestPacketListener) {
      clientCookieRequestPacketListener.onCookieRequest(this);
   }

   public Identifier key() {
      return this.key;
   }
}
