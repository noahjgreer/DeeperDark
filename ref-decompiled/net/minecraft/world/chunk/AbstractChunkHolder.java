package net.minecraft.world.chunk;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ChunkLevelType;
import net.minecraft.server.world.ChunkLevels;
import net.minecraft.server.world.OptionalChunk;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.collection.BoundedRegionArray;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkLoadingManager;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractChunkHolder {
   private static final List STATUSES = ChunkStatus.createOrderedList();
   private static final OptionalChunk NOT_DONE = OptionalChunk.of("Not done yet");
   public static final OptionalChunk UNLOADED = OptionalChunk.of("Unloaded chunk");
   public static final CompletableFuture UNLOADED_FUTURE;
   protected final ChunkPos pos;
   @Nullable
   private volatile ChunkStatus status;
   private final AtomicReference currentStatus = new AtomicReference();
   private final AtomicReferenceArray chunkFuturesByStatus;
   private final AtomicReference chunkLoader;
   private final AtomicInteger refCount;
   private volatile CompletableFuture referenceFuture;

   public AbstractChunkHolder(ChunkPos pos) {
      this.chunkFuturesByStatus = new AtomicReferenceArray(STATUSES.size());
      this.chunkLoader = new AtomicReference();
      this.refCount = new AtomicInteger();
      this.referenceFuture = CompletableFuture.completedFuture((Object)null);
      this.pos = pos;
      if (pos.getChebyshevDistance(ChunkPos.ORIGIN) > ChunkPos.MAX_COORDINATE) {
         throw new IllegalStateException("Trying to create chunk out of reasonable bounds: " + String.valueOf(pos));
      }
   }

   public CompletableFuture load(ChunkStatus requestedStatus, ServerChunkLoadingManager chunkLoadingManager) {
      if (this.cannotBeLoaded(requestedStatus)) {
         return UNLOADED_FUTURE;
      } else {
         CompletableFuture completableFuture = this.getOrCreateFuture(requestedStatus);
         if (completableFuture.isDone()) {
            return completableFuture;
         } else {
            ChunkLoader chunkLoader = (ChunkLoader)this.chunkLoader.get();
            if (chunkLoader == null || requestedStatus.isLaterThan(chunkLoader.targetStatus)) {
               this.createLoader(chunkLoadingManager, requestedStatus);
            }

            return completableFuture;
         }
      }
   }

   CompletableFuture generate(ChunkGenerationStep step, ChunkLoadingManager chunkLoadingManager, BoundedRegionArray chunks) {
      if (this.cannotBeLoaded(step.targetStatus())) {
         return UNLOADED_FUTURE;
      } else {
         return this.progressStatus(step.targetStatus()) ? chunkLoadingManager.generate(this, step, chunks).handle((chunk, throwable) -> {
            if (throwable != null) {
               CrashReport crashReport = CrashReport.create(throwable, "Exception chunk generation/loading");
               MinecraftServer.setWorldGenException(new CrashException(crashReport));
            } else {
               this.completeChunkFuture(step.targetStatus(), chunk);
            }

            return OptionalChunk.of((Object)chunk);
         }) : this.getOrCreateFuture(step.targetStatus());
      }
   }

   protected void updateStatus(ServerChunkLoadingManager chunkLoadingManager) {
      ChunkStatus chunkStatus = this.status;
      ChunkStatus chunkStatus2 = ChunkLevels.getStatus(this.getLevel());
      this.status = chunkStatus2;
      boolean bl = chunkStatus != null && (chunkStatus2 == null || chunkStatus2.isEarlierThan(chunkStatus));
      if (bl) {
         this.unload(chunkStatus2, chunkStatus);
         if (this.chunkLoader.get() != null) {
            this.createLoader(chunkLoadingManager, this.getMaxPendingStatus(chunkStatus2));
         }
      }

   }

   public void replaceWith(WrapperProtoChunk chunk) {
      CompletableFuture completableFuture = CompletableFuture.completedFuture(OptionalChunk.of((Object)chunk));

      for(int i = 0; i < this.chunkFuturesByStatus.length() - 1; ++i) {
         CompletableFuture completableFuture2 = (CompletableFuture)this.chunkFuturesByStatus.get(i);
         Objects.requireNonNull(completableFuture2);
         Chunk chunk2 = (Chunk)((OptionalChunk)completableFuture2.getNow(NOT_DONE)).orElse((Object)null);
         if (!(chunk2 instanceof ProtoChunk)) {
            throw new IllegalStateException("Trying to replace a ProtoChunk, but found " + String.valueOf(chunk2));
         }

         if (!this.chunkFuturesByStatus.compareAndSet(i, completableFuture2, completableFuture)) {
            throw new IllegalStateException("Future changed by other thread while trying to replace it");
         }
      }

   }

   void clearLoader(ChunkLoader loader) {
      this.chunkLoader.compareAndSet(loader, (Object)null);
   }

   private void createLoader(ServerChunkLoadingManager chunkLoadingManager, @Nullable ChunkStatus requestedStatus) {
      ChunkLoader chunkLoader;
      if (requestedStatus != null) {
         chunkLoader = chunkLoadingManager.createLoader(requestedStatus, this.getPos());
      } else {
         chunkLoader = null;
      }

      ChunkLoader chunkLoader2 = (ChunkLoader)this.chunkLoader.getAndSet(chunkLoader);
      if (chunkLoader2 != null) {
         chunkLoader2.markPendingDisposal();
      }

   }

   private CompletableFuture getOrCreateFuture(ChunkStatus status) {
      if (this.cannotBeLoaded(status)) {
         return UNLOADED_FUTURE;
      } else {
         int i = status.getIndex();
         CompletableFuture completableFuture = (CompletableFuture)this.chunkFuturesByStatus.get(i);

         CompletableFuture completableFuture2;
         do {
            if (completableFuture != null) {
               return completableFuture;
            }

            completableFuture2 = new CompletableFuture();
            completableFuture = (CompletableFuture)this.chunkFuturesByStatus.compareAndExchange(i, (Object)null, completableFuture2);
         } while(completableFuture != null);

         if (this.cannotBeLoaded(status)) {
            this.unload(i, completableFuture2);
            return UNLOADED_FUTURE;
         } else {
            return completableFuture2;
         }
      }
   }

   private void unload(@Nullable ChunkStatus from, ChunkStatus to) {
      int i = from == null ? 0 : from.getIndex() + 1;
      int j = to.getIndex();

      for(int k = i; k <= j; ++k) {
         CompletableFuture completableFuture = (CompletableFuture)this.chunkFuturesByStatus.get(k);
         if (completableFuture != null) {
            this.unload(k, completableFuture);
         }
      }

   }

   private void unload(int statusIndex, CompletableFuture previousFuture) {
      if (previousFuture.complete(UNLOADED) && !this.chunkFuturesByStatus.compareAndSet(statusIndex, previousFuture, (Object)null)) {
         throw new IllegalStateException("Nothing else should replace the future here");
      }
   }

   private void completeChunkFuture(ChunkStatus status, Chunk chunk) {
      OptionalChunk optionalChunk = OptionalChunk.of((Object)chunk);
      int i = status.getIndex();

      do {
         while(true) {
            CompletableFuture completableFuture = (CompletableFuture)this.chunkFuturesByStatus.get(i);
            if (completableFuture == null) {
               break;
            }

            if (completableFuture.complete(optionalChunk)) {
               return;
            }

            if (((OptionalChunk)completableFuture.getNow(NOT_DONE)).isPresent()) {
               throw new IllegalStateException("Trying to complete a future but found it to be completed successfully already");
            }

            Thread.yield();
         }
      } while(!this.chunkFuturesByStatus.compareAndSet(i, (Object)null, CompletableFuture.completedFuture(optionalChunk)));

   }

   @Nullable
   private ChunkStatus getMaxPendingStatus(@Nullable ChunkStatus checkUpperBound) {
      if (checkUpperBound == null) {
         return null;
      } else {
         ChunkStatus chunkStatus = checkUpperBound;

         for(ChunkStatus chunkStatus2 = (ChunkStatus)this.currentStatus.get(); chunkStatus2 == null || chunkStatus.isLaterThan(chunkStatus2); chunkStatus = chunkStatus.getPrevious()) {
            if (this.chunkFuturesByStatus.get(chunkStatus.getIndex()) != null) {
               return chunkStatus;
            }

            if (chunkStatus == ChunkStatus.EMPTY) {
               break;
            }
         }

         return null;
      }
   }

   private boolean progressStatus(ChunkStatus nextStatus) {
      ChunkStatus chunkStatus = nextStatus == ChunkStatus.EMPTY ? null : nextStatus.getPrevious();
      ChunkStatus chunkStatus2 = (ChunkStatus)this.currentStatus.compareAndExchange(chunkStatus, nextStatus);
      if (chunkStatus2 == chunkStatus) {
         return true;
      } else if (chunkStatus2 != null && !nextStatus.isLaterThan(chunkStatus2)) {
         return false;
      } else {
         String var10002 = String.valueOf(chunkStatus2);
         throw new IllegalStateException("Unexpected last startedWork status: " + var10002 + " while trying to start: " + String.valueOf(nextStatus));
      }
   }

   private boolean cannotBeLoaded(ChunkStatus status) {
      ChunkStatus chunkStatus = this.status;
      return chunkStatus == null || status.isLaterThan(chunkStatus);
   }

   protected abstract void combineSavingFuture(CompletableFuture savingFuture);

   public void incrementRefCount() {
      if (this.refCount.getAndIncrement() == 0) {
         this.referenceFuture = new CompletableFuture();
         this.combineSavingFuture(this.referenceFuture);
      }

   }

   public void decrementRefCount() {
      CompletableFuture completableFuture = this.referenceFuture;
      int i = this.refCount.decrementAndGet();
      if (i == 0) {
         completableFuture.complete((Object)null);
      }

      if (i < 0) {
         throw new IllegalStateException("More releases than claims. Count: " + i);
      }
   }

   @Nullable
   public Chunk getUncheckedOrNull(ChunkStatus requestedStatus) {
      CompletableFuture completableFuture = (CompletableFuture)this.chunkFuturesByStatus.get(requestedStatus.getIndex());
      return completableFuture == null ? null : (Chunk)((OptionalChunk)completableFuture.getNow(NOT_DONE)).orElse((Object)null);
   }

   @Nullable
   public Chunk getOrNull(ChunkStatus requestedStatus) {
      return this.cannotBeLoaded(requestedStatus) ? null : this.getUncheckedOrNull(requestedStatus);
   }

   @Nullable
   public Chunk getLatest() {
      ChunkStatus chunkStatus = (ChunkStatus)this.currentStatus.get();
      if (chunkStatus == null) {
         return null;
      } else {
         Chunk chunk = this.getUncheckedOrNull(chunkStatus);
         return chunk != null ? chunk : this.getUncheckedOrNull(chunkStatus.getPrevious());
      }
   }

   @Nullable
   public ChunkStatus getActualStatus() {
      CompletableFuture completableFuture = (CompletableFuture)this.chunkFuturesByStatus.get(ChunkStatus.EMPTY.getIndex());
      Chunk chunk = completableFuture == null ? null : (Chunk)((OptionalChunk)completableFuture.getNow(NOT_DONE)).orElse((Object)null);
      return chunk == null ? null : chunk.getStatus();
   }

   public ChunkPos getPos() {
      return this.pos;
   }

   public ChunkLevelType getLevelType() {
      return ChunkLevels.getType(this.getLevel());
   }

   public abstract int getLevel();

   public abstract int getCompletedLevel();

   @Debug
   public List enumerateFutures() {
      List list = new ArrayList();

      for(int i = 0; i < STATUSES.size(); ++i) {
         list.add(Pair.of((ChunkStatus)STATUSES.get(i), (CompletableFuture)this.chunkFuturesByStatus.get(i)));
      }

      return list;
   }

   @Nullable
   @Debug
   public ChunkStatus getLatestStatus() {
      for(int i = STATUSES.size() - 1; i >= 0; --i) {
         ChunkStatus chunkStatus = (ChunkStatus)STATUSES.get(i);
         Chunk chunk = this.getUncheckedOrNull(chunkStatus);
         if (chunk != null) {
            return chunkStatus;
         }
      }

      return null;
   }

   static {
      UNLOADED_FUTURE = CompletableFuture.completedFuture(UNLOADED);
   }
}
