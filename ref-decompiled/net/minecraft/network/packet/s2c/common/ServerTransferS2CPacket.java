package net.minecraft.network.packet.s2c.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public record ServerTransferS2CPacket(String host, int port) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(ServerTransferS2CPacket::write, ServerTransferS2CPacket::new);

   private ServerTransferS2CPacket(PacketByteBuf buf) {
      this(buf.readString(), buf.readVarInt());
   }

   public ServerTransferS2CPacket(String string, int i) {
      this.host = string;
      this.port = i;
   }

   private void write(PacketByteBuf buf) {
      buf.writeString(this.host);
      buf.writeVarInt(this.port);
   }

   public PacketType getPacketType() {
      return CommonPackets.TRANSFER;
   }

   public void apply(ClientCommonPacketListener clientCommonPacketListener) {
      clientCommonPacketListener.onServerTransfer(this);
   }

   public String host() {
      return this.host;
   }

   public int port() {
      return this.port;
   }
}
