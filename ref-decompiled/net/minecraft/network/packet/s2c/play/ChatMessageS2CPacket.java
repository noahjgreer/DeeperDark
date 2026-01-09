package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.message.FilterMask;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.jetbrains.annotations.Nullable;

public record ChatMessageS2CPacket(int globalIndex, UUID sender, int index, @Nullable MessageSignatureData signature, MessageBody.Serialized body, @Nullable Text unsignedContent, FilterMask filterMask, MessageType.Parameters serializedParameters) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(ChatMessageS2CPacket::write, ChatMessageS2CPacket::new);

   private ChatMessageS2CPacket(RegistryByteBuf buf) {
      this(buf.readVarInt(), buf.readUuid(), buf.readVarInt(), (MessageSignatureData)buf.readNullable(MessageSignatureData::fromBuf), new MessageBody.Serialized(buf), (Text)PacketByteBuf.readNullable(buf, TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC), FilterMask.readMask(buf), (MessageType.Parameters)MessageType.Parameters.CODEC.decode(buf));
   }

   public ChatMessageS2CPacket(int i, UUID uUID, int j, @Nullable MessageSignatureData messageSignatureData, MessageBody.Serialized serialized, @Nullable Text text, FilterMask filterMask, MessageType.Parameters parameters) {
      this.globalIndex = i;
      this.sender = uUID;
      this.index = j;
      this.signature = messageSignatureData;
      this.body = serialized;
      this.unsignedContent = text;
      this.filterMask = filterMask;
      this.serializedParameters = parameters;
   }

   private void write(RegistryByteBuf buf) {
      buf.writeVarInt(this.globalIndex);
      buf.writeUuid(this.sender);
      buf.writeVarInt(this.index);
      buf.writeNullable(this.signature, MessageSignatureData::write);
      this.body.write(buf);
      PacketByteBuf.writeNullable(buf, this.unsignedContent, TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC);
      FilterMask.writeMask(buf, this.filterMask);
      MessageType.Parameters.CODEC.encode(buf, this.serializedParameters);
   }

   public PacketType getPacketType() {
      return PlayPackets.PLAYER_CHAT;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onChatMessage(this);
   }

   public boolean isWritingErrorSkippable() {
      return true;
   }

   public int globalIndex() {
      return this.globalIndex;
   }

   public UUID sender() {
      return this.sender;
   }

   public int index() {
      return this.index;
   }

   @Nullable
   public MessageSignatureData signature() {
      return this.signature;
   }

   public MessageBody.Serialized body() {
      return this.body;
   }

   @Nullable
   public Text unsignedContent() {
      return this.unsignedContent;
   }

   public FilterMask filterMask() {
      return this.filterMask;
   }

   public MessageType.Parameters serializedParameters() {
      return this.serializedParameters;
   }
}
