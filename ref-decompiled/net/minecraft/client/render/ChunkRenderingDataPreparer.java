package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.AbstractChunkRenderData;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.Octree;
import net.minecraft.server.network.ChunkFilter;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.HeightLimitView;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ChunkRenderingDataPreparer {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Direction[] DIRECTIONS = Direction.values();
   private static final int DEFAULT_SECTION_DISTANCE = 60;
   private static final int SECTION_DISTANCE = ChunkSectionPos.getSectionCoord(60);
   private static final double CHUNK_INNER_DIAGONAL_LENGTH = Math.ceil(Math.sqrt(3.0) * 16.0);
   private boolean terrainUpdateScheduled = true;
   @Nullable
   private Future terrainUpdateFuture;
   @Nullable
   private BuiltChunkStorage builtChunkStorage;
   private final AtomicReference state = new AtomicReference();
   private final AtomicReference events = new AtomicReference();
   private final AtomicBoolean needsUpdate = new AtomicBoolean(false);

   public void setStorage(@Nullable BuiltChunkStorage storage) {
      if (this.terrainUpdateFuture != null) {
         try {
            this.terrainUpdateFuture.get();
            this.terrainUpdateFuture = null;
         } catch (Exception var3) {
            LOGGER.warn("Full update failed", var3);
         }
      }

      this.builtChunkStorage = storage;
      if (storage != null) {
         this.state.set(new PreparerState(storage));
         this.scheduleTerrainUpdate();
      } else {
         this.state.set((Object)null);
      }

   }

   public void scheduleTerrainUpdate() {
      this.terrainUpdateScheduled = true;
   }

   public void collectChunks(Frustum frustum, List builtChunks, List nearbyChunks) {
      ((PreparerState)this.state.get()).storage().octree.visit((node, skipVisibilityCheck, depth, nearCenter) -> {
         ChunkBuilder.BuiltChunk builtChunk = node.getBuiltChunk();
         if (builtChunk != null) {
            builtChunks.add(builtChunk);
            if (nearCenter) {
               nearbyChunks.add(builtChunk);
            }
         }

      }, frustum, 32);
   }

   public boolean updateFrustum() {
      return this.needsUpdate.compareAndSet(true, false);
   }

   public void addNeighbors(ChunkPos chunkPos) {
      Events events = (Events)this.events.get();
      if (events != null) {
         this.addNeighbors(events, chunkPos);
      }

      Events events2 = ((PreparerState)this.state.get()).events;
      if (events2 != events) {
         this.addNeighbors(events2, chunkPos);
      }

   }

   public void schedulePropagationFrom(ChunkBuilder.BuiltChunk builtChunk) {
      Events events = (Events)this.events.get();
      if (events != null) {
         events.sectionsToPropagateFrom.add(builtChunk);
      }

      Events events2 = ((PreparerState)this.state.get()).events;
      if (events2 != events) {
         events2.sectionsToPropagateFrom.add(builtChunk);
      }

   }

   public void updateSectionOcclusionGraph(boolean cullChunks, Camera camera, Frustum frustum, List builtChunk, LongOpenHashSet activeSections) {
      Vec3d vec3d = camera.getPos();
      if (this.terrainUpdateScheduled && (this.terrainUpdateFuture == null || this.terrainUpdateFuture.isDone())) {
         this.updateTerrain(cullChunks, camera, vec3d, activeSections);
      }

      this.updateNow(cullChunks, frustum, builtChunk, vec3d, activeSections);
   }

   private void updateTerrain(boolean cullChunks, Camera camera, Vec3d cameraPos, LongOpenHashSet activeSections) {
      this.terrainUpdateScheduled = false;
      LongOpenHashSet longOpenHashSet = activeSections.clone();
      this.terrainUpdateFuture = CompletableFuture.runAsync(() -> {
         PreparerState preparerState = new PreparerState(this.builtChunkStorage);
         this.events.set(preparerState.events);
         Queue queue = Queues.newArrayDeque();
         this.scheduleLater(camera, queue);
         queue.forEach((info) -> {
            preparerState.storage.infoList.setInfo(info.chunk, info);
         });
         this.update(preparerState.storage, cameraPos, queue, cullChunks, (builtChunk) -> {
         }, longOpenHashSet);
         this.state.set(preparerState);
         this.events.set((Object)null);
         this.needsUpdate.set(true);
      }, Util.getMainWorkerExecutor());
   }

   private void updateNow(boolean cullChunks, Frustum frustum, List builtChunks, Vec3d cameraPos, LongOpenHashSet activeSections) {
      PreparerState preparerState = (PreparerState)this.state.get();
      this.scheduleNew(preparerState);
      if (!preparerState.events.sectionsToPropagateFrom.isEmpty()) {
         Queue queue = Queues.newArrayDeque();

         while(!preparerState.events.sectionsToPropagateFrom.isEmpty()) {
            ChunkBuilder.BuiltChunk builtChunk = (ChunkBuilder.BuiltChunk)preparerState.events.sectionsToPropagateFrom.poll();
            ChunkInfo chunkInfo = preparerState.storage.infoList.getInfo(builtChunk);
            if (chunkInfo != null && chunkInfo.chunk == builtChunk) {
               queue.add(chunkInfo);
            }
         }

         Frustum frustum2 = WorldRenderer.offsetFrustum(frustum);
         Consumer consumer = (builtChunkx) -> {
            if (frustum2.isVisible(builtChunkx.getBoundingBox())) {
               this.needsUpdate.set(true);
            }

         };
         this.update(preparerState.storage, cameraPos, queue, cullChunks, consumer, activeSections);
      }

   }

   private void scheduleNew(PreparerState preparerState) {
      LongIterator longIterator = preparerState.events.chunksWhichReceivedNeighbors.iterator();

      while(longIterator.hasNext()) {
         long l = longIterator.nextLong();
         List list = (List)preparerState.storage.queue.get(l);
         if (list != null && ((ChunkBuilder.BuiltChunk)list.get(0)).shouldBuild()) {
            preparerState.events.sectionsToPropagateFrom.addAll(list);
            preparerState.storage.queue.remove(l);
         }
      }

      preparerState.events.chunksWhichReceivedNeighbors.clear();
   }

   private void addNeighbors(Events events, ChunkPos chunkPos) {
      events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x - 1, chunkPos.z));
      events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x, chunkPos.z - 1));
      events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x + 1, chunkPos.z));
      events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x, chunkPos.z + 1));
      events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x - 1, chunkPos.z - 1));
      events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x - 1, chunkPos.z + 1));
      events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x + 1, chunkPos.z - 1));
      events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong(chunkPos.x + 1, chunkPos.z + 1));
   }

   private void scheduleLater(Camera camera, Queue queue) {
      BlockPos blockPos = camera.getBlockPos();
      long l = ChunkSectionPos.toLong(blockPos);
      int i = ChunkSectionPos.unpackY(l);
      ChunkBuilder.BuiltChunk builtChunk = this.builtChunkStorage.getRenderedChunk(l);
      if (builtChunk == null) {
         HeightLimitView heightLimitView = this.builtChunkStorage.getWorld();
         boolean bl = i < heightLimitView.getBottomSectionCoord();
         int j = bl ? heightLimitView.getBottomSectionCoord() : heightLimitView.getTopSectionCoord();
         int k = this.builtChunkStorage.getViewDistance();
         List list = Lists.newArrayList();
         int m = ChunkSectionPos.unpackX(l);
         int n = ChunkSectionPos.unpackZ(l);

         for(int o = -k; o <= k; ++o) {
            for(int p = -k; p <= k; ++p) {
               ChunkBuilder.BuiltChunk builtChunk2 = this.builtChunkStorage.getRenderedChunk(ChunkSectionPos.asLong(o + m, j, p + n));
               if (builtChunk2 != null && this.isWithinViewDistance(l, builtChunk2.getSectionPos())) {
                  Direction direction = bl ? Direction.UP : Direction.DOWN;
                  ChunkInfo chunkInfo = new ChunkInfo(builtChunk2, direction, 0);
                  chunkInfo.updateCullingState(chunkInfo.cullingState, direction);
                  if (o > 0) {
                     chunkInfo.updateCullingState(chunkInfo.cullingState, Direction.EAST);
                  } else if (o < 0) {
                     chunkInfo.updateCullingState(chunkInfo.cullingState, Direction.WEST);
                  }

                  if (p > 0) {
                     chunkInfo.updateCullingState(chunkInfo.cullingState, Direction.SOUTH);
                  } else if (p < 0) {
                     chunkInfo.updateCullingState(chunkInfo.cullingState, Direction.NORTH);
                  }

                  list.add(chunkInfo);
               }
            }
         }

         list.sort(Comparator.comparingDouble((chunkInfox) -> {
            return blockPos.getSquaredDistance(ChunkSectionPos.from(chunkInfox.chunk.getSectionPos()).getCenterPos());
         }));
         queue.addAll(list);
      } else {
         queue.add(new ChunkInfo(builtChunk, (Direction)null, 0));
      }

   }

   private void update(RenderableChunks renderableChunks, Vec3d pos, Queue queue, boolean cullChunks, Consumer consumer, LongOpenHashSet longOpenHashSet) {
      ChunkSectionPos chunkSectionPos = ChunkSectionPos.from((Position)pos);
      long l = chunkSectionPos.asLong();
      BlockPos blockPos = chunkSectionPos.getCenterPos();

      while(!queue.isEmpty()) {
         ChunkInfo chunkInfo = (ChunkInfo)queue.poll();
         ChunkBuilder.BuiltChunk builtChunk = chunkInfo.chunk;
         if (!longOpenHashSet.contains(chunkInfo.chunk.getSectionPos())) {
            if (renderableChunks.octree.add(chunkInfo.chunk)) {
               consumer.accept(chunkInfo.chunk);
            }
         } else {
            chunkInfo.chunk.currentRenderData.compareAndSet(ChunkRenderData.HIDDEN, ChunkRenderData.READY);
         }

         long m = builtChunk.getSectionPos();
         boolean bl = Math.abs(ChunkSectionPos.unpackX(m) - chunkSectionPos.getSectionX()) > SECTION_DISTANCE || Math.abs(ChunkSectionPos.unpackY(m) - chunkSectionPos.getSectionY()) > SECTION_DISTANCE || Math.abs(ChunkSectionPos.unpackZ(m) - chunkSectionPos.getSectionZ()) > SECTION_DISTANCE;
         Direction[] var16 = DIRECTIONS;
         int var17 = var16.length;

         for(int var18 = 0; var18 < var17; ++var18) {
            Direction direction = var16[var18];
            ChunkBuilder.BuiltChunk builtChunk2 = this.getRenderedChunk(l, builtChunk, direction);
            if (builtChunk2 != null && (!cullChunks || !chunkInfo.canCull(direction.getOpposite()))) {
               int i;
               if (cullChunks && chunkInfo.hasAnyDirection()) {
                  AbstractChunkRenderData abstractChunkRenderData = builtChunk.getCurrentRenderData();
                  boolean bl2 = false;

                  for(i = 0; i < DIRECTIONS.length; ++i) {
                     if (chunkInfo.hasDirection(i) && abstractChunkRenderData.isVisibleThrough(DIRECTIONS[i].getOpposite(), direction)) {
                        bl2 = true;
                        break;
                     }
                  }

                  if (!bl2) {
                     continue;
                  }
               }

               if (cullChunks && bl) {
                  int j = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackX(m));
                  int k = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackY(m));
                  i = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackZ(m));
                  boolean bl3 = direction.getAxis() == Direction.Axis.X ? blockPos.getX() > j : blockPos.getX() < j;
                  boolean bl4 = direction.getAxis() == Direction.Axis.Y ? blockPos.getY() > k : blockPos.getY() < k;
                  boolean bl5 = direction.getAxis() == Direction.Axis.Z ? blockPos.getZ() > i : blockPos.getZ() < i;
                  Vector3d vector3d = new Vector3d((double)(j + (bl3 ? 16 : 0)), (double)(k + (bl4 ? 16 : 0)), (double)(i + (bl5 ? 16 : 0)));
                  Vector3d vector3d2 = (new Vector3d(pos.x, pos.y, pos.z)).sub(vector3d).normalize().mul(CHUNK_INNER_DIAGONAL_LENGTH);
                  boolean bl6 = true;

                  label138: {
                     ChunkBuilder.BuiltChunk builtChunk3;
                     do {
                        if (!(vector3d.distanceSquared(pos.x, pos.y, pos.z) > 3600.0)) {
                           break label138;
                        }

                        vector3d.add(vector3d2);
                        HeightLimitView heightLimitView = this.builtChunkStorage.getWorld();
                        if (vector3d.y > (double)heightLimitView.getTopYInclusive() || vector3d.y < (double)heightLimitView.getBottomY()) {
                           break label138;
                        }

                        builtChunk3 = this.builtChunkStorage.getRenderedChunk(BlockPos.ofFloored(vector3d.x, vector3d.y, vector3d.z));
                     } while(builtChunk3 != null && renderableChunks.infoList.getInfo(builtChunk3) != null);

                     bl6 = false;
                  }

                  if (!bl6) {
                     continue;
                  }
               }

               ChunkInfo chunkInfo2 = renderableChunks.infoList.getInfo(builtChunk2);
               if (chunkInfo2 != null) {
                  chunkInfo2.addDirection(direction);
               } else {
                  ChunkInfo chunkInfo3 = new ChunkInfo(builtChunk2, direction, chunkInfo.propagationLevel + 1);
                  chunkInfo3.updateCullingState(chunkInfo.cullingState, direction);
                  if (builtChunk2.shouldBuild()) {
                     queue.add(chunkInfo3);
                     renderableChunks.infoList.setInfo(builtChunk2, chunkInfo3);
                  } else if (this.isWithinViewDistance(l, builtChunk2.getSectionPos())) {
                     renderableChunks.infoList.setInfo(builtChunk2, chunkInfo3);
                     long n = ChunkSectionPos.toChunkPos(builtChunk2.getSectionPos());
                     ((List)renderableChunks.queue.computeIfAbsent(n, (lx) -> {
                        return new ArrayList();
                     })).add(builtChunk2);
                  }
               }
            }
         }
      }

   }

   private boolean isWithinViewDistance(long centerSectionPos, long otherSectionPos) {
      return ChunkFilter.isWithinDistanceExcludingEdge(ChunkSectionPos.unpackX(centerSectionPos), ChunkSectionPos.unpackZ(centerSectionPos), this.builtChunkStorage.getViewDistance(), ChunkSectionPos.unpackX(otherSectionPos), ChunkSectionPos.unpackZ(otherSectionPos));
   }

   @Nullable
   private ChunkBuilder.BuiltChunk getRenderedChunk(long sectionPos, ChunkBuilder.BuiltChunk chunk, Direction direction) {
      long l = chunk.getOffsetSectionPos(direction);
      if (!this.isWithinViewDistance(sectionPos, l)) {
         return null;
      } else {
         return MathHelper.abs(ChunkSectionPos.unpackY(sectionPos) - ChunkSectionPos.unpackY(l)) > this.builtChunkStorage.getViewDistance() ? null : this.builtChunkStorage.getRenderedChunk(l);
      }
   }

   @Nullable
   @Debug
   public ChunkInfo getInfo(ChunkBuilder.BuiltChunk chunk) {
      return ((PreparerState)this.state.get()).storage.infoList.getInfo(chunk);
   }

   public Octree getOctree() {
      return ((PreparerState)this.state.get()).storage.octree;
   }

   @Environment(EnvType.CLIENT)
   private static record PreparerState(RenderableChunks storage, Events events) {
      final RenderableChunks storage;
      final Events events;

      PreparerState(BuiltChunkStorage storage) {
         this(new RenderableChunks(storage), new Events());
      }

      private PreparerState(RenderableChunks renderableChunks, Events events) {
         this.storage = renderableChunks;
         this.events = events;
      }

      public RenderableChunks storage() {
         return this.storage;
      }

      public Events events() {
         return this.events;
      }
   }

   @Environment(EnvType.CLIENT)
   private static class RenderableChunks {
      public final ChunkInfoList infoList;
      public final Octree octree;
      public final Long2ObjectMap queue;

      public RenderableChunks(BuiltChunkStorage storage) {
         this.infoList = new ChunkInfoList(storage.chunks.length);
         this.octree = new Octree(storage.getSectionPos(), storage.getViewDistance(), storage.sizeY, storage.world.getBottomY());
         this.queue = new Long2ObjectOpenHashMap();
      }
   }

   @Environment(EnvType.CLIENT)
   private static record Events(LongSet chunksWhichReceivedNeighbors, BlockingQueue sectionsToPropagateFrom) {
      final LongSet chunksWhichReceivedNeighbors;
      final BlockingQueue sectionsToPropagateFrom;

      Events() {
         this(new LongOpenHashSet(), new LinkedBlockingQueue());
      }

      private Events(LongSet longSet, BlockingQueue blockingQueue) {
         this.chunksWhichReceivedNeighbors = longSet;
         this.sectionsToPropagateFrom = blockingQueue;
      }

      public LongSet chunksWhichReceivedNeighbors() {
         return this.chunksWhichReceivedNeighbors;
      }

      public BlockingQueue sectionsToPropagateFrom() {
         return this.sectionsToPropagateFrom;
      }
   }

   @Environment(EnvType.CLIENT)
   private static class ChunkInfoList {
      private final ChunkInfo[] current;

      ChunkInfoList(int size) {
         this.current = new ChunkInfo[size];
      }

      public void setInfo(ChunkBuilder.BuiltChunk chunk, ChunkInfo info) {
         this.current[chunk.index] = info;
      }

      @Nullable
      public ChunkInfo getInfo(ChunkBuilder.BuiltChunk chunk) {
         int i = chunk.index;
         return i >= 0 && i < this.current.length ? this.current[i] : null;
      }
   }

   @Environment(EnvType.CLIENT)
   @Debug
   public static class ChunkInfo {
      @Debug
      protected final ChunkBuilder.BuiltChunk chunk;
      private byte direction;
      byte cullingState;
      @Debug
      public final int propagationLevel;

      ChunkInfo(ChunkBuilder.BuiltChunk chunk, @Nullable Direction direction, int propagationLevel) {
         this.chunk = chunk;
         if (direction != null) {
            this.addDirection(direction);
         }

         this.propagationLevel = propagationLevel;
      }

      void updateCullingState(byte parentCullingState, Direction from) {
         this.cullingState = (byte)(this.cullingState | parentCullingState | 1 << from.ordinal());
      }

      boolean canCull(Direction from) {
         return (this.cullingState & 1 << from.ordinal()) > 0;
      }

      void addDirection(Direction direction) {
         this.direction = (byte)(this.direction | this.direction | 1 << direction.ordinal());
      }

      @Debug
      public boolean hasDirection(int ordinal) {
         return (this.direction & 1 << ordinal) > 0;
      }

      boolean hasAnyDirection() {
         return this.direction != 0;
      }

      public int hashCode() {
         return Long.hashCode(this.chunk.getSectionPos());
      }

      public boolean equals(Object o) {
         if (!(o instanceof ChunkInfo chunkInfo)) {
            return false;
         } else {
            return this.chunk.getSectionPos() == chunkInfo.chunk.getSectionPos();
         }
      }
   }
}
