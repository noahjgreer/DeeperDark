package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record ChunkSentS2CPacket(int batchSize) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(ChunkSentS2CPacket::write, ChunkSentS2CPacket::new);

   private ChunkSentS2CPacket(PacketByteBuf buf) {
      this(buf.readVarInt());
   }

   public ChunkSentS2CPacket(int i) {
      this.batchSize = i;
   }

   private void write(PacketByteBuf buf) {
      buf.writeVarInt(this.batchSize);
   }

   public PacketType getPacketType() {
      return PlayPackets.CHUNK_BATCH_FINISHED;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onChunkSent(this);
   }

   public int batchSize() {
      return this.batchSize;
   }
}
