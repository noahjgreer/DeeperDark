package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkToNibbleArrayMap;

public class BlockLightStorage extends LightStorage {
   protected BlockLightStorage(ChunkProvider chunkProvider) {
      super(LightType.BLOCK, chunkProvider, new Data(new Long2ObjectOpenHashMap()));
   }

   protected int getLight(long blockPos) {
      long l = ChunkSectionPos.fromBlockPos(blockPos);
      ChunkNibbleArray chunkNibbleArray = this.getLightSection(l, false);
      return chunkNibbleArray == null ? 0 : chunkNibbleArray.get(ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(blockPos)));
   }

   protected static final class Data extends ChunkToNibbleArrayMap {
      public Data(Long2ObjectOpenHashMap long2ObjectOpenHashMap) {
         super(long2ObjectOpenHashMap);
      }

      public Data copy() {
         return new Data(this.arrays.clone());
      }

      // $FF: synthetic method
      public ChunkToNibbleArrayMap copy() {
         return this.copy();
      }
   }
}
