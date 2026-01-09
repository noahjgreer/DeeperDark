package net.minecraft.network.packet.c2s.play;

import java.time.Instant;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import org.jetbrains.annotations.Nullable;

public record ChatMessageC2SPacket(String chatMessage, Instant timestamp, long salt, @Nullable MessageSignatureData signature, LastSeenMessageList.Acknowledgment acknowledgment) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(ChatMessageC2SPacket::write, ChatMessageC2SPacket::new);

   private ChatMessageC2SPacket(PacketByteBuf buf) {
      this(buf.readString(256), buf.readInstant(), buf.readLong(), (MessageSignatureData)buf.readNullable(MessageSignatureData::fromBuf), new LastSeenMessageList.Acknowledgment(buf));
   }

   public ChatMessageC2SPacket(String string, Instant timestamp, long salt, @Nullable MessageSignatureData signature, LastSeenMessageList.Acknowledgment acknowledgment) {
      this.chatMessage = string;
      this.timestamp = timestamp;
      this.salt = salt;
      this.signature = signature;
      this.acknowledgment = acknowledgment;
   }

   private void write(PacketByteBuf buf) {
      buf.writeString(this.chatMessage, 256);
      buf.writeInstant(this.timestamp);
      buf.writeLong(this.salt);
      buf.writeNullable(this.signature, MessageSignatureData::write);
      this.acknowledgment.write(buf);
   }

   public PacketType getPacketType() {
      return PlayPackets.CHAT;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onChatMessage(this);
   }

   public String chatMessage() {
      return this.chatMessage;
   }

   public Instant timestamp() {
      return this.timestamp;
   }

   public long salt() {
      return this.salt;
   }

   @Nullable
   public MessageSignatureData signature() {
      return this.signature;
   }

   public LastSeenMessageList.Acknowledgment acknowledgment() {
      return this.acknowledgment;
   }
}
