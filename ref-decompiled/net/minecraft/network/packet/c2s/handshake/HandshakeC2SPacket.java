package net.minecraft.network.packet.c2s.handshake;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.network.packet.HandshakePackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public record HandshakeC2SPacket(int protocolVersion, String address, int port, ConnectionIntent intendedState) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(HandshakeC2SPacket::write, HandshakeC2SPacket::new);
   private static final int MAX_ADDRESS_LENGTH = 255;

   /** @deprecated */
   @Deprecated
   public HandshakeC2SPacket(int i, String string, int j, ConnectionIntent connectionIntent) {
      this.protocolVersion = i;
      this.address = string;
      this.port = j;
      this.intendedState = connectionIntent;
   }

   private HandshakeC2SPacket(PacketByteBuf buf) {
      this(buf.readVarInt(), buf.readString(255), buf.readUnsignedShort(), ConnectionIntent.byId(buf.readVarInt()));
   }

   private void write(PacketByteBuf buf) {
      buf.writeVarInt(this.protocolVersion);
      buf.writeString(this.address);
      buf.writeShort(this.port);
      buf.writeVarInt(this.intendedState.getId());
   }

   public PacketType getPacketType() {
      return HandshakePackets.INTENTION;
   }

   public void apply(ServerHandshakePacketListener serverHandshakePacketListener) {
      serverHandshakePacketListener.onHandshake(this);
   }

   public boolean transitionsNetworkState() {
      return true;
   }

   public int protocolVersion() {
      return this.protocolVersion;
   }

   public String address() {
      return this.address;
   }

   public int port() {
      return this.port;
   }

   public ConnectionIntent intendedState() {
      return this.intendedState;
   }
}
