package net.minecraft.world.storage;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.math.ChunkPos;

public class ChunkDataList {
   private final ChunkPos pos;
   private final List backingList;

   public ChunkDataList(ChunkPos pos, List list) {
      this.pos = pos;
      this.backingList = list;
   }

   public ChunkPos getChunkPos() {
      return this.pos;
   }

   public Stream stream() {
      return this.backingList.stream();
   }

   public boolean isEmpty() {
      return this.backingList.isEmpty();
   }
}
