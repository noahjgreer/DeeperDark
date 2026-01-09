package net.minecraft.server.world;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntSupplier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.thread.SimpleConsecutiveExecutor;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ServerLightingProvider extends LightingProvider implements AutoCloseable {
   public static final int field_44692 = 1000;
   private static final Logger LOGGER = LogUtils.getLogger();
   private final SimpleConsecutiveExecutor processor;
   private final ObjectList pendingTasks = new ObjectArrayList();
   private final ServerChunkLoadingManager chunkLoadingManager;
   private final ChunkTaskScheduler executor;
   private final int taskBatchSize = 1000;
   private final AtomicBoolean ticking = new AtomicBoolean();

   public ServerLightingProvider(ChunkProvider chunkProvider, ServerChunkLoadingManager chunkLoadingManager, boolean hasBlockLight, SimpleConsecutiveExecutor processor, ChunkTaskScheduler executor) {
      super(chunkProvider, true, hasBlockLight);
      this.chunkLoadingManager = chunkLoadingManager;
      this.executor = executor;
      this.processor = processor;
   }

   public void close() {
   }

   public int doLightUpdates() {
      throw (UnsupportedOperationException)Util.getFatalOrPause(new UnsupportedOperationException("Ran automatically on a different thread!"));
   }

   public void checkBlock(BlockPos pos) {
      BlockPos blockPos = pos.toImmutable();
      this.enqueue(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()), ServerLightingProvider.Stage.PRE_UPDATE, Util.debugRunnable(() -> {
         super.checkBlock(blockPos);
      }, () -> {
         return "checkBlock " + String.valueOf(blockPos);
      }));
   }

   protected void updateChunkStatus(ChunkPos pos) {
      this.enqueue(pos.x, pos.z, () -> {
         return 0;
      }, ServerLightingProvider.Stage.PRE_UPDATE, Util.debugRunnable(() -> {
         super.setRetainData(pos, false);
         super.setColumnEnabled(pos, false);

         int i;
         for(i = this.getBottomY(); i < this.getTopY(); ++i) {
            super.enqueueSectionData(LightType.BLOCK, ChunkSectionPos.from(pos, i), (ChunkNibbleArray)null);
            super.enqueueSectionData(LightType.SKY, ChunkSectionPos.from(pos, i), (ChunkNibbleArray)null);
         }

         for(i = this.world.getBottomSectionCoord(); i <= this.world.getTopSectionCoord(); ++i) {
            super.setSectionStatus(ChunkSectionPos.from(pos, i), true);
         }

      }, () -> {
         return "updateChunkStatus " + String.valueOf(pos) + " true";
      }));
   }

   public void setSectionStatus(ChunkSectionPos pos, boolean notReady) {
      this.enqueue(pos.getSectionX(), pos.getSectionZ(), () -> {
         return 0;
      }, ServerLightingProvider.Stage.PRE_UPDATE, Util.debugRunnable(() -> {
         super.setSectionStatus(pos, notReady);
      }, () -> {
         String var10000 = String.valueOf(pos);
         return "updateSectionStatus " + var10000 + " " + notReady;
      }));
   }

   public void propagateLight(ChunkPos chunkPos) {
      this.enqueue(chunkPos.x, chunkPos.z, ServerLightingProvider.Stage.PRE_UPDATE, Util.debugRunnable(() -> {
         super.propagateLight(chunkPos);
      }, () -> {
         return "propagateLight " + String.valueOf(chunkPos);
      }));
   }

   public void setColumnEnabled(ChunkPos pos, boolean retainData) {
      this.enqueue(pos.x, pos.z, ServerLightingProvider.Stage.PRE_UPDATE, Util.debugRunnable(() -> {
         super.setColumnEnabled(pos, retainData);
      }, () -> {
         String var10000 = String.valueOf(pos);
         return "enableLight " + var10000 + " " + retainData;
      }));
   }

   public void enqueueSectionData(LightType lightType, ChunkSectionPos pos, @Nullable ChunkNibbleArray nibbles) {
      this.enqueue(pos.getSectionX(), pos.getSectionZ(), () -> {
         return 0;
      }, ServerLightingProvider.Stage.PRE_UPDATE, Util.debugRunnable(() -> {
         super.enqueueSectionData(lightType, pos, nibbles);
      }, () -> {
         return "queueData " + String.valueOf(pos);
      }));
   }

   private void enqueue(int x, int z, Stage stage, Runnable task) {
      this.enqueue(x, z, this.chunkLoadingManager.getCompletedLevelSupplier(ChunkPos.toLong(x, z)), stage, task);
   }

   private void enqueue(int x, int z, IntSupplier completedLevelSupplier, Stage stage, Runnable task) {
      this.executor.add(() -> {
         this.pendingTasks.add(Pair.of(stage, task));
         if (this.pendingTasks.size() >= 1000) {
            this.runTasks();
         }

      }, ChunkPos.toLong(x, z), completedLevelSupplier);
   }

   public void setRetainData(ChunkPos pos, boolean retainData) {
      this.enqueue(pos.x, pos.z, () -> {
         return 0;
      }, ServerLightingProvider.Stage.PRE_UPDATE, Util.debugRunnable(() -> {
         super.setRetainData(pos, retainData);
      }, () -> {
         return "retainData " + String.valueOf(pos);
      }));
   }

   public CompletableFuture initializeLight(Chunk chunk, boolean bl) {
      ChunkPos chunkPos = chunk.getPos();
      this.enqueue(chunkPos.x, chunkPos.z, ServerLightingProvider.Stage.PRE_UPDATE, Util.debugRunnable(() -> {
         ChunkSection[] chunkSections = chunk.getSectionArray();

         for(int i = 0; i < chunk.countVerticalSections(); ++i) {
            ChunkSection chunkSection = chunkSections[i];
            if (!chunkSection.isEmpty()) {
               int j = this.world.sectionIndexToCoord(i);
               super.setSectionStatus(ChunkSectionPos.from(chunkPos, j), false);
            }
         }

      }, () -> {
         return "initializeLight: " + String.valueOf(chunkPos);
      }));
      return CompletableFuture.supplyAsync(() -> {
         super.setColumnEnabled(chunkPos, bl);
         super.setRetainData(chunkPos, false);
         return chunk;
      }, (task) -> {
         this.enqueue(chunkPos.x, chunkPos.z, ServerLightingProvider.Stage.POST_UPDATE, task);
      });
   }

   public CompletableFuture light(Chunk chunk, boolean excludeBlocks) {
      ChunkPos chunkPos = chunk.getPos();
      chunk.setLightOn(false);
      this.enqueue(chunkPos.x, chunkPos.z, ServerLightingProvider.Stage.PRE_UPDATE, Util.debugRunnable(() -> {
         if (!excludeBlocks) {
            super.propagateLight(chunkPos);
         }

      }, () -> {
         String var10000 = String.valueOf(chunkPos);
         return "lightChunk " + var10000 + " " + excludeBlocks;
      }));
      return CompletableFuture.supplyAsync(() -> {
         chunk.setLightOn(true);
         return chunk;
      }, (task) -> {
         this.enqueue(chunkPos.x, chunkPos.z, ServerLightingProvider.Stage.POST_UPDATE, task);
      });
   }

   public void tick() {
      if ((!this.pendingTasks.isEmpty() || super.hasUpdates()) && this.ticking.compareAndSet(false, true)) {
         this.processor.send(() -> {
            this.runTasks();
            this.ticking.set(false);
         });
      }

   }

   private void runTasks() {
      int i = Math.min(this.pendingTasks.size(), 1000);
      ObjectListIterator objectListIterator = this.pendingTasks.iterator();

      int j;
      Pair pair;
      for(j = 0; objectListIterator.hasNext() && j < i; ++j) {
         pair = (Pair)objectListIterator.next();
         if (pair.getFirst() == ServerLightingProvider.Stage.PRE_UPDATE) {
            ((Runnable)pair.getSecond()).run();
         }
      }

      objectListIterator.back(j);
      super.doLightUpdates();

      for(j = 0; objectListIterator.hasNext() && j < i; ++j) {
         pair = (Pair)objectListIterator.next();
         if (pair.getFirst() == ServerLightingProvider.Stage.POST_UPDATE) {
            ((Runnable)pair.getSecond()).run();
         }

         objectListIterator.remove();
      }

   }

   public CompletableFuture enqueue(int x, int z) {
      return CompletableFuture.runAsync(() -> {
      }, (callback) -> {
         this.enqueue(x, z, ServerLightingProvider.Stage.POST_UPDATE, callback);
      });
   }

   private static enum Stage {
      PRE_UPDATE,
      POST_UPDATE;

      // $FF: synthetic method
      private static Stage[] method_36577() {
         return new Stage[]{PRE_UPDATE, POST_UPDATE};
      }
   }
}
