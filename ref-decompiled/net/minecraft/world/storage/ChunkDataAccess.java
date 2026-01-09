package net.minecraft.world.storage;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import net.minecraft.util.math.ChunkPos;

public interface ChunkDataAccess extends AutoCloseable {
   CompletableFuture readChunkData(ChunkPos pos);

   void writeChunkData(ChunkDataList dataList);

   void awaitAll(boolean sync);

   default void close() throws IOException {
   }
}
