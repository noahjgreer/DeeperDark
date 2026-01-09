package net.minecraft.network.packet.c2s.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerCookieResponsePacketListener;
import net.minecraft.network.packet.CookiePackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.s2c.common.StoreCookieS2CPacket;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record CookieResponseC2SPacket(Identifier key, @Nullable byte[] payload) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(CookieResponseC2SPacket::write, CookieResponseC2SPacket::new);

   private CookieResponseC2SPacket(PacketByteBuf buf) {
      this(buf.readIdentifier(), (byte[])buf.readNullable(StoreCookieS2CPacket.COOKIE_PACKET_CODEC));
   }

   public CookieResponseC2SPacket(Identifier identifier, @Nullable byte[] bs) {
      this.key = identifier;
      this.payload = bs;
   }

   private void write(PacketByteBuf buf) {
      buf.writeIdentifier(this.key);
      buf.writeNullable(this.payload, StoreCookieS2CPacket.COOKIE_PACKET_CODEC);
   }

   public PacketType getPacketType() {
      return CookiePackets.COOKIE_RESPONSE;
   }

   public void apply(ServerCookieResponsePacketListener serverCookieResponsePacketListener) {
      serverCookieResponsePacketListener.onCookieResponse(this);
   }

   public Identifier key() {
      return this.key;
   }

   @Nullable
   public byte[] payload() {
      return this.payload;
   }
}
