package net.minecraft.network.packet.c2s.play;

import java.time.Instant;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.message.ArgumentSignatureDataMap;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record ChatCommandSignedC2SPacket(String command, Instant timestamp, long salt, ArgumentSignatureDataMap argumentSignatures, LastSeenMessageList.Acknowledgment lastSeenMessages) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(ChatCommandSignedC2SPacket::write, ChatCommandSignedC2SPacket::new);

   private ChatCommandSignedC2SPacket(PacketByteBuf buf) {
      this(buf.readString(), buf.readInstant(), buf.readLong(), new ArgumentSignatureDataMap(buf), new LastSeenMessageList.Acknowledgment(buf));
   }

   public ChatCommandSignedC2SPacket(String string, Instant instant, long l, ArgumentSignatureDataMap argumentSignatureDataMap, LastSeenMessageList.Acknowledgment acknowledgment) {
      this.command = string;
      this.timestamp = instant;
      this.salt = l;
      this.argumentSignatures = argumentSignatureDataMap;
      this.lastSeenMessages = acknowledgment;
   }

   private void write(PacketByteBuf buf) {
      buf.writeString(this.command);
      buf.writeInstant(this.timestamp);
      buf.writeLong(this.salt);
      this.argumentSignatures.write(buf);
      this.lastSeenMessages.write(buf);
   }

   public PacketType getPacketType() {
      return PlayPackets.CHAT_COMMAND_SIGNED;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onChatCommandSigned(this);
   }

   public String command() {
      return this.command;
   }

   public Instant timestamp() {
      return this.timestamp;
   }

   public long salt() {
      return this.salt;
   }

   public ArgumentSignatureDataMap argumentSignatures() {
      return this.argumentSignatures;
   }

   public LastSeenMessageList.Acknowledgment lastSeenMessages() {
      return this.lastSeenMessages;
   }
}
