package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record PlayerSessionC2SPacket(PublicPlayerSession.Serialized chatSession) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(PlayerSessionC2SPacket::write, PlayerSessionC2SPacket::new);

   private PlayerSessionC2SPacket(PacketByteBuf buf) {
      this(PublicPlayerSession.Serialized.fromBuf(buf));
   }

   public PlayerSessionC2SPacket(PublicPlayerSession.Serialized serialized) {
      this.chatSession = serialized;
   }

   private void write(PacketByteBuf buf) {
      PublicPlayerSession.Serialized.write(buf, this.chatSession);
   }

   public PacketType getPacketType() {
      return PlayPackets.CHAT_SESSION_UPDATE;
   }

   public void apply(ServerPlayPacketListener serverPlayPacketListener) {
      serverPlayPacketListener.onPlayerSession(this);
   }

   public PublicPlayerSession.Serialized chatSession() {
      return this.chatSession;
   }
}
