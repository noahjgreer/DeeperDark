package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.math.ChunkPos;

public record UnloadChunkS2CPacket(ChunkPos pos) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(UnloadChunkS2CPacket::write, UnloadChunkS2CPacket::new);

   private UnloadChunkS2CPacket(PacketByteBuf buf) {
      this(buf.readChunkPos());
   }

   public UnloadChunkS2CPacket(ChunkPos chunkPos) {
      this.pos = chunkPos;
   }

   private void write(PacketByteBuf buf) {
      buf.writeChunkPos(this.pos);
   }

   public PacketType getPacketType() {
      return PlayPackets.FORGET_LEVEL_CHUNK;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onUnloadChunk(this);
   }

   public ChunkPos pos() {
      return this.pos;
   }
}
