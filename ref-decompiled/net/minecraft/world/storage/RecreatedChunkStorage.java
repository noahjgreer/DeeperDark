package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.util.math.ChunkPos;
import org.apache.commons.io.FileUtils;

public class RecreatedChunkStorage extends VersionedChunkStorage {
   private final StorageIoWorker recreationWorker;
   private final Path outputDirectory;

   public RecreatedChunkStorage(StorageKey storageKey, Path directory, StorageKey outputStorageKey, Path outputDirectory, DataFixer dataFixer, boolean dsync) {
      super(storageKey, directory, dataFixer, dsync);
      this.outputDirectory = outputDirectory;
      this.recreationWorker = new StorageIoWorker(outputStorageKey, outputDirectory, dsync);
   }

   public CompletableFuture setNbt(ChunkPos chunkPos, Supplier nbtSupplier) {
      this.markFeatureUpdateResolved(chunkPos);
      return this.recreationWorker.setResult(chunkPos, nbtSupplier);
   }

   public void close() throws IOException {
      super.close();
      this.recreationWorker.close();
      if (this.outputDirectory.toFile().exists()) {
         FileUtils.deleteDirectory(this.outputDirectory.toFile());
      }

   }
}
