package net.minecraft.network.packet.s2c.play;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public record ChunkBiomeDataS2CPacket(List chunkBiomeData) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(ChunkBiomeDataS2CPacket::write, ChunkBiomeDataS2CPacket::new);
   private static final int MAX_SIZE = 2097152;

   private ChunkBiomeDataS2CPacket(PacketByteBuf buf) {
      this(buf.readList(Serialized::new));
   }

   public ChunkBiomeDataS2CPacket(List list) {
      this.chunkBiomeData = list;
   }

   public static ChunkBiomeDataS2CPacket create(List chunks) {
      return new ChunkBiomeDataS2CPacket(chunks.stream().map(Serialized::new).toList());
   }

   private void write(PacketByteBuf buf) {
      buf.writeCollection(this.chunkBiomeData, (bufx, data) -> {
         data.write(bufx);
      });
   }

   public PacketType getPacketType() {
      return PlayPackets.CHUNKS_BIOMES;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onChunkBiomeData(this);
   }

   public List chunkBiomeData() {
      return this.chunkBiomeData;
   }

   public static record Serialized(ChunkPos pos, byte[] buffer) {
      public Serialized(WorldChunk chunk) {
         this(chunk.getPos(), new byte[getTotalPacketSize(chunk)]);
         write(new PacketByteBuf(this.toWritingBuf()), chunk);
      }

      public Serialized(PacketByteBuf buf) {
         this(buf.readChunkPos(), buf.readByteArray(2097152));
      }

      public Serialized(ChunkPos chunkPos, byte[] bs) {
         this.pos = chunkPos;
         this.buffer = bs;
      }

      private static int getTotalPacketSize(WorldChunk chunk) {
         int i = 0;
         ChunkSection[] var2 = chunk.getSectionArray();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            ChunkSection chunkSection = var2[var4];
            i += chunkSection.getBiomeContainer().getPacketSize();
         }

         return i;
      }

      public PacketByteBuf toReadingBuf() {
         return new PacketByteBuf(Unpooled.wrappedBuffer(this.buffer));
      }

      private ByteBuf toWritingBuf() {
         ByteBuf byteBuf = Unpooled.wrappedBuffer(this.buffer);
         byteBuf.writerIndex(0);
         return byteBuf;
      }

      public static void write(PacketByteBuf buf, WorldChunk chunk) {
         ChunkSection[] var2 = chunk.getSectionArray();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            ChunkSection chunkSection = var2[var4];
            chunkSection.getBiomeContainer().writePacket(buf);
         }

         if (buf.writerIndex() != buf.capacity()) {
            int var10002 = buf.capacity();
            throw new IllegalStateException("Didn't fill biome buffer: expected " + var10002 + " bytes, got " + buf.writerIndex());
         }
      }

      public void write(PacketByteBuf buf) {
         buf.writeChunkPos(this.pos);
         buf.writeByteArray(this.buffer);
      }

      public ChunkPos pos() {
         return this.pos;
      }

      public byte[] buffer() {
         return this.buffer;
      }
   }
}
