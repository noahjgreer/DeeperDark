package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

public class ChunkData {
   private static final PacketCodec HEIGHTMAPS_PACKET_CODEC;
   private static final int MAX_SECTIONS_DATA_SIZE = 2097152;
   private final Map heightmap;
   private final byte[] sectionsData;
   private final List blockEntities;

   public ChunkData(WorldChunk chunk) {
      this.heightmap = (Map)chunk.getHeightmaps().stream().filter((entryx) -> {
         return ((Heightmap.Type)entryx.getKey()).shouldSendToClient();
      }).collect(Collectors.toMap(Map.Entry::getKey, (entryx) -> {
         return (long[])((Heightmap)entryx.getValue()).asLongArray().clone();
      }));
      this.sectionsData = new byte[getSectionsPacketSize(chunk)];
      writeSections(new PacketByteBuf(this.getWritableSectionsDataBuf()), chunk);
      this.blockEntities = Lists.newArrayList();
      Iterator var2 = chunk.getBlockEntities().entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry entry = (Map.Entry)var2.next();
         this.blockEntities.add(ChunkData.BlockEntityData.of((BlockEntity)entry.getValue()));
      }

   }

   public ChunkData(RegistryByteBuf buf, int x, int z) {
      this.heightmap = (Map)HEIGHTMAPS_PACKET_CODEC.decode(buf);
      int i = buf.readVarInt();
      if (i > 2097152) {
         throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
      } else {
         this.sectionsData = new byte[i];
         buf.readBytes(this.sectionsData);
         this.blockEntities = (List)ChunkData.BlockEntityData.LIST_PACKET_CODEC.decode(buf);
      }
   }

   public void write(RegistryByteBuf buf) {
      HEIGHTMAPS_PACKET_CODEC.encode(buf, this.heightmap);
      buf.writeVarInt(this.sectionsData.length);
      buf.writeBytes(this.sectionsData);
      ChunkData.BlockEntityData.LIST_PACKET_CODEC.encode(buf, this.blockEntities);
   }

   private static int getSectionsPacketSize(WorldChunk chunk) {
      int i = 0;
      ChunkSection[] var2 = chunk.getSectionArray();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ChunkSection chunkSection = var2[var4];
         i += chunkSection.getPacketSize();
      }

      return i;
   }

   private ByteBuf getWritableSectionsDataBuf() {
      ByteBuf byteBuf = Unpooled.wrappedBuffer(this.sectionsData);
      byteBuf.writerIndex(0);
      return byteBuf;
   }

   public static void writeSections(PacketByteBuf buf, WorldChunk chunk) {
      ChunkSection[] var2 = chunk.getSectionArray();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ChunkSection chunkSection = var2[var4];
         chunkSection.toPacket(buf);
      }

      if (buf.writerIndex() != buf.capacity()) {
         int var10002 = buf.capacity();
         throw new IllegalStateException("Didn't fill chunk buffer: expected " + var10002 + " bytes, got " + buf.writerIndex());
      }
   }

   public Consumer getBlockEntities(int x, int z) {
      return (visitor) -> {
         this.iterateBlockEntities(visitor, x, z);
      };
   }

   private void iterateBlockEntities(BlockEntityVisitor consumer, int x, int z) {
      int i = 16 * x;
      int j = 16 * z;
      BlockPos.Mutable mutable = new BlockPos.Mutable();
      Iterator var7 = this.blockEntities.iterator();

      while(var7.hasNext()) {
         BlockEntityData blockEntityData = (BlockEntityData)var7.next();
         int k = i + ChunkSectionPos.getLocalCoord(blockEntityData.localXz >> 4);
         int l = j + ChunkSectionPos.getLocalCoord(blockEntityData.localXz);
         mutable.set(k, blockEntityData.y, l);
         consumer.accept(mutable, blockEntityData.type, blockEntityData.nbt);
      }

   }

   public PacketByteBuf getSectionsDataBuf() {
      return new PacketByteBuf(Unpooled.wrappedBuffer(this.sectionsData));
   }

   public Map getHeightmap() {
      return this.heightmap;
   }

   static {
      HEIGHTMAPS_PACKET_CODEC = PacketCodecs.map((size) -> {
         return new EnumMap(Heightmap.Type.class);
      }, Heightmap.Type.PACKET_CODEC, PacketCodecs.LONG_ARRAY);
   }

   static class BlockEntityData {
      public static final PacketCodec PACKET_CODEC = PacketCodec.of(BlockEntityData::write, BlockEntityData::new);
      public static final PacketCodec LIST_PACKET_CODEC;
      final int localXz;
      final int y;
      final BlockEntityType type;
      @Nullable
      final NbtCompound nbt;

      private BlockEntityData(int localXz, int y, BlockEntityType type, @Nullable NbtCompound nbt) {
         this.localXz = localXz;
         this.y = y;
         this.type = type;
         this.nbt = nbt;
      }

      private BlockEntityData(RegistryByteBuf buf) {
         this.localXz = buf.readByte();
         this.y = buf.readShort();
         this.type = (BlockEntityType)PacketCodecs.registryValue(RegistryKeys.BLOCK_ENTITY_TYPE).decode(buf);
         this.nbt = buf.readNbt();
      }

      private void write(RegistryByteBuf buf) {
         buf.writeByte(this.localXz);
         buf.writeShort(this.y);
         PacketCodecs.registryValue(RegistryKeys.BLOCK_ENTITY_TYPE).encode(buf, this.type);
         buf.writeNbt(this.nbt);
      }

      static BlockEntityData of(BlockEntity blockEntity) {
         NbtCompound nbtCompound = blockEntity.toInitialChunkDataNbt(blockEntity.getWorld().getRegistryManager());
         BlockPos blockPos = blockEntity.getPos();
         int i = ChunkSectionPos.getLocalCoord(blockPos.getX()) << 4 | ChunkSectionPos.getLocalCoord(blockPos.getZ());
         return new BlockEntityData(i, blockPos.getY(), blockEntity.getType(), nbtCompound.isEmpty() ? null : nbtCompound);
      }

      static {
         LIST_PACKET_CODEC = PACKET_CODEC.collect(PacketCodecs.toList());
      }
   }

   @FunctionalInterface
   public interface BlockEntityVisitor {
      void accept(BlockPos pos, BlockEntityType type, @Nullable NbtCompound nbt);
   }
}
