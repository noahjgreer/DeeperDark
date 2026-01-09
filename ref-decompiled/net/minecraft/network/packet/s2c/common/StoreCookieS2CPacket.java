package net.minecraft.network.packet.s2c.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.Identifier;

public record StoreCookieS2CPacket(Identifier key, byte[] payload) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(StoreCookieS2CPacket::write, StoreCookieS2CPacket::new);
   private static final int MAX_COOKIE_LENGTH = 5120;
   public static final PacketCodec COOKIE_PACKET_CODEC = PacketCodecs.byteArray(5120);

   private StoreCookieS2CPacket(PacketByteBuf buf) {
      this(buf.readIdentifier(), (byte[])COOKIE_PACKET_CODEC.decode(buf));
   }

   public StoreCookieS2CPacket(Identifier identifier, byte[] bs) {
      this.key = identifier;
      this.payload = bs;
   }

   private void write(PacketByteBuf buf) {
      buf.writeIdentifier(this.key);
      COOKIE_PACKET_CODEC.encode(buf, this.payload);
   }

   public PacketType getPacketType() {
      return CommonPackets.STORE_COOKIE;
   }

   public void apply(ClientCommonPacketListener clientCommonPacketListener) {
      clientCommonPacketListener.onStoreCookie(this);
   }

   public Identifier key() {
      return this.key;
   }

   public byte[] payload() {
      return this.payload;
   }
}
