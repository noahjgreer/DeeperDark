package net.minecraft.client.render.chunk;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.VertexSorter;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;
import net.minecraft.util.thread.NameableExecutor;
import net.minecraft.util.thread.SimpleConsecutiveExecutor;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ChunkBuilder {
   private final ChunkRenderTaskScheduler scheduler = new ChunkRenderTaskScheduler();
   private final Queue uploadQueue = Queues.newConcurrentLinkedQueue();
   final Executor uploadExecutor;
   final Queue renderQueue;
   final BlockBufferAllocatorStorage buffers;
   private final BlockBufferBuilderPool buffersPool;
   volatile boolean stopped;
   private final SimpleConsecutiveExecutor consecutiveExecutor;
   private final NameableExecutor executor;
   ClientWorld world;
   final WorldRenderer worldRenderer;
   Vec3d cameraPosition;
   final SectionBuilder sectionBuilder;

   public ChunkBuilder(ClientWorld world, WorldRenderer worldRenderer, NameableExecutor executor, BufferBuilderStorage bufferBuilderStorage, BlockRenderManager blockRenderManager, BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
      Queue var10001 = this.uploadQueue;
      Objects.requireNonNull(var10001);
      this.uploadExecutor = var10001::add;
      this.renderQueue = Queues.newConcurrentLinkedQueue();
      this.cameraPosition = Vec3d.ZERO;
      this.world = world;
      this.worldRenderer = worldRenderer;
      this.buffers = bufferBuilderStorage.getBlockBufferBuilders();
      this.buffersPool = bufferBuilderStorage.getBlockBufferBuildersPool();
      this.executor = executor;
      this.consecutiveExecutor = new SimpleConsecutiveExecutor(executor, "Section Renderer");
      this.consecutiveExecutor.send(this::scheduleRunTasks);
      this.sectionBuilder = new SectionBuilder(blockRenderManager, blockEntityRenderDispatcher);
   }

   public void setWorld(ClientWorld world) {
      this.world = world;
   }

   private void scheduleRunTasks() {
      if (!this.stopped && !this.buffersPool.hasNoAvailableBuilder()) {
         BuiltChunk.Task task = this.scheduler.dequeueNearest(this.cameraPosition);
         if (task != null) {
            BlockBufferAllocatorStorage blockBufferAllocatorStorage = (BlockBufferAllocatorStorage)Objects.requireNonNull(this.buffersPool.acquire());
            CompletableFuture.supplyAsync(() -> {
               return task.run(blockBufferAllocatorStorage);
            }, this.executor.named(task.getName())).thenCompose((future) -> {
               return future;
            }).whenComplete((result, throwable) -> {
               if (throwable != null) {
                  MinecraftClient.getInstance().setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Batching sections"));
               } else {
                  task.finished.set(true);
                  this.consecutiveExecutor.send(() -> {
                     if (result == ChunkBuilder.Result.SUCCESSFUL) {
                        blockBufferAllocatorStorage.clear();
                     } else {
                        blockBufferAllocatorStorage.reset();
                     }

                     this.buffersPool.release(blockBufferAllocatorStorage);
                     this.scheduleRunTasks();
                  });
               }
            });
         }
      }
   }

   public void setCameraPosition(Vec3d cameraPosition) {
      this.cameraPosition = cameraPosition;
   }

   public void upload() {
      Runnable runnable;
      while((runnable = (Runnable)this.uploadQueue.poll()) != null) {
         runnable.run();
      }

      AbstractChunkRenderData abstractChunkRenderData;
      while((abstractChunkRenderData = (AbstractChunkRenderData)this.renderQueue.poll()) != null) {
         abstractChunkRenderData.close();
      }

   }

   public void rebuild(BuiltChunk chunk, ChunkRendererRegionBuilder builder) {
      chunk.rebuild(builder);
   }

   public void send(BuiltChunk.Task task) {
      if (!this.stopped) {
         this.consecutiveExecutor.send(() -> {
            if (!this.stopped) {
               this.scheduler.enqueue(task);
               this.scheduleRunTasks();
            }
         });
      }
   }

   public void cancelAllTasks() {
      this.scheduler.cancelAll();
   }

   public boolean isEmpty() {
      return this.scheduler.size() == 0 && this.uploadQueue.isEmpty();
   }

   public void stop() {
      this.stopped = true;
      this.cancelAllTasks();
      this.upload();
   }

   @Debug
   public String getDebugString() {
      return String.format(Locale.ROOT, "pC: %03d, pU: %02d, aB: %02d", this.scheduler.size(), this.uploadQueue.size(), this.buffersPool.getAvailableBuilderCount());
   }

   @Debug
   public int getScheduledTaskCount() {
      return this.scheduler.size();
   }

   @Debug
   public int getChunksToUpload() {
      return this.uploadQueue.size();
   }

   @Debug
   public int getFreeBufferCount() {
      return this.buffersPool.getAvailableBuilderCount();
   }

   @Environment(EnvType.CLIENT)
   public class BuiltChunk {
      public static final int CHUNK_SIZE = 16;
      public final int index;
      public final AtomicReference currentRenderData;
      @Nullable
      private RebuildTask rebuildTask;
      @Nullable
      private SortTask sortTask;
      private Box boundingBox;
      private boolean needsRebuild;
      volatile long sectionPos;
      final BlockPos.Mutable origin;
      private boolean needsImportantRebuild;

      public BuiltChunk(final int index, final long sectionPos) {
         this.currentRenderData = new AtomicReference(ChunkRenderData.HIDDEN);
         this.needsRebuild = true;
         this.sectionPos = ChunkSectionPos.asLong(-1, -1, -1);
         this.origin = new BlockPos.Mutable(-1, -1, -1);
         this.index = index;
         this.setSectionPos(sectionPos);
      }

      private boolean isChunkNonEmpty(long sectionPos) {
         Chunk chunk = ChunkBuilder.this.world.getChunk(ChunkSectionPos.unpackX(sectionPos), ChunkSectionPos.unpackZ(sectionPos), ChunkStatus.FULL, false);
         return chunk != null && ChunkBuilder.this.world.getLightingProvider().isLightingEnabled(ChunkSectionPos.withZeroY(sectionPos));
      }

      public boolean shouldBuild() {
         return this.isChunkNonEmpty(ChunkSectionPos.offset(this.sectionPos, Direction.WEST)) && this.isChunkNonEmpty(ChunkSectionPos.offset(this.sectionPos, Direction.NORTH)) && this.isChunkNonEmpty(ChunkSectionPos.offset(this.sectionPos, Direction.EAST)) && this.isChunkNonEmpty(ChunkSectionPos.offset(this.sectionPos, Direction.SOUTH)) && this.isChunkNonEmpty(ChunkSectionPos.offset(this.sectionPos, -1, 0, -1)) && this.isChunkNonEmpty(ChunkSectionPos.offset(this.sectionPos, -1, 0, 1)) && this.isChunkNonEmpty(ChunkSectionPos.offset(this.sectionPos, 1, 0, -1)) && this.isChunkNonEmpty(ChunkSectionPos.offset(this.sectionPos, 1, 0, 1));
      }

      public Box getBoundingBox() {
         return this.boundingBox;
      }

      public CompletableFuture uploadLayer(Map buffersByLayer, ChunkRenderData renderData) {
         if (ChunkBuilder.this.stopped) {
            buffersByLayer.values().forEach(BuiltBuffer::close);
            return CompletableFuture.completedFuture((Object)null);
         } else {
            return CompletableFuture.runAsync(() -> {
               buffersByLayer.forEach((layer, buffer) -> {
                  ScopedProfiler scopedProfiler = Profilers.get().scoped("Upload Section Layer");

                  try {
                     renderData.upload(layer, buffer, this.sectionPos);
                     buffer.close();
                  } catch (Throwable var8) {
                     if (scopedProfiler != null) {
                        try {
                           scopedProfiler.close();
                        } catch (Throwable var7) {
                           var8.addSuppressed(var7);
                        }
                     }

                     throw var8;
                  }

                  if (scopedProfiler != null) {
                     scopedProfiler.close();
                  }

               });
            }, ChunkBuilder.this.uploadExecutor);
         }
      }

      public CompletableFuture uploadIndices(ChunkRenderData data, BufferAllocator.CloseableBuffer buffer, BlockRenderLayer layer) {
         if (ChunkBuilder.this.stopped) {
            buffer.close();
            return CompletableFuture.completedFuture((Object)null);
         } else {
            return CompletableFuture.runAsync(() -> {
               ScopedProfiler scopedProfiler = Profilers.get().scoped("Upload Section Indices");

               try {
                  data.uploadIndexBuffer(layer, buffer, this.sectionPos);
                  buffer.close();
               } catch (Throwable var8) {
                  if (scopedProfiler != null) {
                     try {
                        scopedProfiler.close();
                     } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                     }
                  }

                  throw var8;
               }

               if (scopedProfiler != null) {
                  scopedProfiler.close();
               }

            }, ChunkBuilder.this.uploadExecutor);
         }
      }

      public void setSectionPos(long sectionPos) {
         this.clear();
         this.sectionPos = sectionPos;
         int i = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackX(sectionPos));
         int j = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackY(sectionPos));
         int k = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackZ(sectionPos));
         this.origin.set(i, j, k);
         this.boundingBox = new Box((double)i, (double)j, (double)k, (double)(i + 16), (double)(j + 16), (double)(k + 16));
      }

      public AbstractChunkRenderData getCurrentRenderData() {
         return (AbstractChunkRenderData)this.currentRenderData.get();
      }

      public void clear() {
         this.cancel();
         ((AbstractChunkRenderData)this.currentRenderData.getAndSet(ChunkRenderData.HIDDEN)).close();
         this.needsRebuild = true;
      }

      public BlockPos getOrigin() {
         return this.origin;
      }

      public long getSectionPos() {
         return this.sectionPos;
      }

      public void scheduleRebuild(boolean important) {
         boolean bl = this.needsRebuild;
         this.needsRebuild = true;
         this.needsImportantRebuild = important | (bl && this.needsImportantRebuild);
      }

      public void cancelRebuild() {
         this.needsRebuild = false;
         this.needsImportantRebuild = false;
      }

      public boolean needsRebuild() {
         return this.needsRebuild;
      }

      public boolean needsImportantRebuild() {
         return this.needsRebuild && this.needsImportantRebuild;
      }

      public long getOffsetSectionPos(Direction direction) {
         return ChunkSectionPos.offset(this.sectionPos, direction);
      }

      public void scheduleSort(ChunkBuilder builder) {
         AbstractChunkRenderData var3 = this.getCurrentRenderData();
         if (var3 instanceof ChunkRenderData chunkRenderData) {
            this.sortTask = new SortTask(chunkRenderData);
            builder.send(this.sortTask);
         }

      }

      public boolean hasTranslucentLayer() {
         return this.getCurrentRenderData().hasTranslucentLayers();
      }

      public boolean isCurrentlySorting() {
         return this.sortTask != null && !this.sortTask.finished.get();
      }

      protected void cancel() {
         if (this.rebuildTask != null) {
            this.rebuildTask.cancel();
            this.rebuildTask = null;
         }

         if (this.sortTask != null) {
            this.sortTask.cancel();
            this.sortTask = null;
         }

      }

      public Task createRebuildTask(ChunkRendererRegionBuilder builder) {
         this.cancel();
         ChunkRendererRegion chunkRendererRegion = builder.build(ChunkBuilder.this.world, this.sectionPos);
         boolean bl = this.currentRenderData.get() != ChunkRenderData.HIDDEN;
         this.rebuildTask = new RebuildTask(chunkRendererRegion, bl);
         return this.rebuildTask;
      }

      public void scheduleRebuild(ChunkRendererRegionBuilder builder) {
         Task task = this.createRebuildTask(builder);
         ChunkBuilder.this.send(task);
      }

      public void rebuild(ChunkRendererRegionBuilder builder) {
         Task task = this.createRebuildTask(builder);
         task.run(ChunkBuilder.this.buffers);
      }

      void setCurrentRenderData(AbstractChunkRenderData data) {
         AbstractChunkRenderData abstractChunkRenderData = (AbstractChunkRenderData)this.currentRenderData.getAndSet(data);
         ChunkBuilder.this.renderQueue.add(abstractChunkRenderData);
         ChunkBuilder.this.worldRenderer.addBuiltChunk(this);
      }

      VertexSorter getVertexSorter(ChunkSectionPos sectionPos) {
         Vec3d vec3d = ChunkBuilder.this.cameraPosition;
         return VertexSorter.byDistance((float)(vec3d.x - (double)sectionPos.getMinX()), (float)(vec3d.y - (double)sectionPos.getMinY()), (float)(vec3d.z - (double)sectionPos.getMinZ()));
      }

      @Environment(EnvType.CLIENT)
      private class SortTask extends Task {
         private final ChunkRenderData renderData;

         public SortTask(final ChunkRenderData data) {
            super(true);
            this.renderData = data;
         }

         protected String getName() {
            return "rend_chk_sort";
         }

         public CompletableFuture run(BlockBufferAllocatorStorage buffers) {
            if (this.cancelled.get()) {
               return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
            } else {
               BuiltBuffer.SortState sortState = this.renderData.getTranslucencySortingData();
               if (sortState != null && !this.renderData.containsLayer(BlockRenderLayer.TRANSLUCENT)) {
                  long l = BuiltChunk.this.sectionPos;
                  VertexSorter vertexSorter = BuiltChunk.this.getVertexSorter(ChunkSectionPos.from(l));
                  NormalizedRelativePos normalizedRelativePos = NormalizedRelativePos.of(ChunkBuilder.this.cameraPosition, l);
                  if (!this.renderData.hasPosition(normalizedRelativePos) && !normalizedRelativePos.isOnCameraAxis()) {
                     return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
                  } else {
                     BufferAllocator.CloseableBuffer closeableBuffer = sortState.sortAndStore(buffers.get(BlockRenderLayer.TRANSLUCENT), vertexSorter);
                     if (closeableBuffer == null) {
                        return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
                     } else if (this.cancelled.get()) {
                        closeableBuffer.close();
                        return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
                     } else {
                        CompletableFuture completableFuture = BuiltChunk.this.uploadIndices(this.renderData, closeableBuffer, BlockRenderLayer.TRANSLUCENT);
                        return completableFuture.handle((void_, throwable) -> {
                           if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
                              MinecraftClient.getInstance().setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Rendering section"));
                           }

                           if (this.cancelled.get()) {
                              return ChunkBuilder.Result.CANCELLED;
                           } else {
                              this.renderData.setPos(normalizedRelativePos);
                              return ChunkBuilder.Result.SUCCESSFUL;
                           }
                        });
                     }
                  }
               } else {
                  return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
               }
            }
         }

         public void cancel() {
            this.cancelled.set(true);
         }
      }

      @Environment(EnvType.CLIENT)
      public abstract class Task {
         protected final AtomicBoolean cancelled = new AtomicBoolean(false);
         protected final AtomicBoolean finished = new AtomicBoolean(false);
         protected final boolean prioritized;

         public Task(final boolean prioritized) {
            this.prioritized = prioritized;
         }

         public abstract CompletableFuture run(BlockBufferAllocatorStorage buffers);

         public abstract void cancel();

         protected abstract String getName();

         public boolean isPrioritized() {
            return this.prioritized;
         }

         public BlockPos getOrigin() {
            return BuiltChunk.this.origin;
         }
      }

      @Environment(EnvType.CLIENT)
      private class RebuildTask extends Task {
         protected final ChunkRendererRegion region;

         public RebuildTask(final ChunkRendererRegion region, final boolean prioritized) {
            super(prioritized);
            this.region = region;
         }

         protected String getName() {
            return "rend_chk_rebuild";
         }

         public CompletableFuture run(BlockBufferAllocatorStorage buffers) {
            if (this.cancelled.get()) {
               return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
            } else {
               long l = BuiltChunk.this.sectionPos;
               ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(l);
               if (this.cancelled.get()) {
                  return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
               } else {
                  ScopedProfiler scopedProfiler = Profilers.get().scoped("Compile Section");

                  SectionBuilder.RenderData renderData;
                  try {
                     renderData = ChunkBuilder.this.sectionBuilder.build(chunkSectionPos, this.region, BuiltChunk.this.getVertexSorter(chunkSectionPos), buffers);
                  } catch (Throwable var10) {
                     if (scopedProfiler != null) {
                        try {
                           scopedProfiler.close();
                        } catch (Throwable var9) {
                           var10.addSuppressed(var9);
                        }
                     }

                     throw var10;
                  }

                  if (scopedProfiler != null) {
                     scopedProfiler.close();
                  }

                  NormalizedRelativePos normalizedRelativePos = NormalizedRelativePos.of(ChunkBuilder.this.cameraPosition, l);
                  if (this.cancelled.get()) {
                     renderData.close();
                     return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
                  } else {
                     ChunkRenderData chunkRenderData = new ChunkRenderData(normalizedRelativePos, renderData);
                     CompletableFuture completableFuture = BuiltChunk.this.uploadLayer(renderData.buffers, chunkRenderData);
                     return completableFuture.handle((void_, throwable) -> {
                        if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
                           MinecraftClient.getInstance().setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Rendering section"));
                        }

                        if (!this.cancelled.get() && !ChunkBuilder.this.stopped) {
                           BuiltChunk.this.setCurrentRenderData(chunkRenderData);
                           return ChunkBuilder.Result.SUCCESSFUL;
                        } else {
                           ChunkBuilder.this.renderQueue.add(chunkRenderData);
                           return ChunkBuilder.Result.CANCELLED;
                        }
                     });
                  }
               }
            }
         }

         public void cancel() {
            if (this.cancelled.compareAndSet(false, true)) {
               BuiltChunk.this.scheduleRebuild(false);
            }

         }
      }
   }

   @Environment(EnvType.CLIENT)
   static enum Result {
      SUCCESSFUL,
      CANCELLED;

      // $FF: synthetic method
      private static Result[] method_36923() {
         return new Result[]{SUCCESSFUL, CANCELLED};
      }
   }
}
