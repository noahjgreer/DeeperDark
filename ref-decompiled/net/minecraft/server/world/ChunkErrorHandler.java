package net.minecraft.server.world;

import java.util.Objects;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.StorageKey;

public interface ChunkErrorHandler {
   void onChunkLoadFailure(Throwable exception, StorageKey key, ChunkPos chunkPos);

   void onChunkSaveFailure(Throwable exception, StorageKey key, ChunkPos chunkPos);

   static CrashException createMisplacementException(ChunkPos actualPos, ChunkPos expectedPos) {
      String var10002 = String.valueOf(actualPos);
      CrashReport crashReport = CrashReport.create(new IllegalStateException("Retrieved chunk position " + var10002 + " does not match requested " + String.valueOf(expectedPos)), "Chunk found in invalid location");
      CrashReportSection crashReportSection = crashReport.addElement("Misplaced Chunk");
      Objects.requireNonNull(actualPos);
      crashReportSection.add("Stored Position", actualPos::toString);
      return new CrashException(crashReport);
   }

   default void onChunkMisplacement(ChunkPos actualPos, ChunkPos expectedPos, StorageKey key) {
      this.onChunkLoadFailure(createMisplacementException(actualPos, expectedPos), key, expectedPos);
   }
}
