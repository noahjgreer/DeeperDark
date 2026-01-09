package net.minecraft.world.chunk.light;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkNibbleArray;
import org.jetbrains.annotations.Nullable;

public interface ChunkLightingView extends LightingView {
   @Nullable
   ChunkNibbleArray getLightSection(ChunkSectionPos pos);

   int getLightLevel(BlockPos pos);

   public static enum Empty implements ChunkLightingView {
      INSTANCE;

      @Nullable
      public ChunkNibbleArray getLightSection(ChunkSectionPos pos) {
         return null;
      }

      public int getLightLevel(BlockPos pos) {
         return 0;
      }

      public void checkBlock(BlockPos pos) {
      }

      public boolean hasUpdates() {
         return false;
      }

      public int doLightUpdates() {
         return 0;
      }

      public void setSectionStatus(ChunkSectionPos pos, boolean notReady) {
      }

      public void setColumnEnabled(ChunkPos pos, boolean retainData) {
      }

      public void propagateLight(ChunkPos chunkPos) {
      }

      // $FF: synthetic method
      private static Empty[] method_36763() {
         return new Empty[]{INSTANCE};
      }
   }
}
