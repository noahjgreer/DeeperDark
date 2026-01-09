package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record MessageAcknowledgmentC2SPacket(int offset) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(MessageAcknowledgmentC2SPacket::write, MessageAcknowledgmentC2SPacket::new);

   private MessageAcknowledgmentC2SPacket(PacketByteBuf buf) {
      this(buf.readVarInt());
   }

   public MessageAcknowledgmentC2SPacket(int i) {
      this.offset = i;
   }

   private void write(PacketByteBuf buf) {
      buf.writeVarInt(this.offset);
   }

   public PacketType getPacketType() {
      return PlayPackets.CHAT_ACK;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onMessageAcknowledgment(this);
   }

   public int offset() {
      return this.offset;
   }
}
