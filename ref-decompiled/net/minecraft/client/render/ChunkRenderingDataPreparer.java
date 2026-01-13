/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.BuiltChunkStorage
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.ChunkRenderingDataPreparer
 *  net.minecraft.client.render.ChunkRenderingDataPreparer$ChunkInfo
 *  net.minecraft.client.render.ChunkRenderingDataPreparer$Events
 *  net.minecraft.client.render.ChunkRenderingDataPreparer$PreparerState
 *  net.minecraft.client.render.ChunkRenderingDataPreparer$RenderableChunks
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.WorldRenderer
 *  net.minecraft.client.render.chunk.AbstractChunkRenderData
 *  net.minecraft.client.render.chunk.ChunkBuilder$BuiltChunk
 *  net.minecraft.client.render.chunk.ChunkRenderData
 *  net.minecraft.client.render.chunk.Octree
 *  net.minecraft.server.network.ChunkFilter
 *  net.minecraft.util.Util
 *  net.minecraft.util.annotation.Debug
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.util.math.ChunkSectionPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.HeightLimitView
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.ChunkRenderingDataPreparer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.WorldRenderer;
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
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.HeightLimitView;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ChunkRenderingDataPreparer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final int DEFAULT_SECTION_DISTANCE = 60;
    private static final int SECTION_DISTANCE = ChunkSectionPos.getSectionCoord((int)60);
    private static final double CHUNK_INNER_DIAGONAL_LENGTH = Math.ceil(Math.sqrt(3.0) * 16.0);
    private boolean terrainUpdateScheduled = true;
    private @Nullable Future<?> terrainUpdateFuture;
    private @Nullable BuiltChunkStorage builtChunkStorage;
    private final AtomicReference<// Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ChunkRenderingDataPreparer.PreparerState> state = new AtomicReference();
    private final AtomicReference<// Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ChunkRenderingDataPreparer.Events> events = new AtomicReference();
    private final AtomicBoolean needsUpdate = new AtomicBoolean(false);

    public void setStorage(@Nullable BuiltChunkStorage storage) {
        if (this.terrainUpdateFuture != null) {
            try {
                this.terrainUpdateFuture.get();
                this.terrainUpdateFuture = null;
            }
            catch (Exception exception) {
                LOGGER.warn("Full update failed", (Throwable)exception);
            }
        }
        this.builtChunkStorage = storage;
        if (storage != null) {
            this.state.set(new PreparerState(storage));
            this.scheduleTerrainUpdate();
        } else {
            this.state.set(null);
        }
    }

    public void scheduleTerrainUpdate() {
        this.terrainUpdateScheduled = true;
    }

    public void collectChunks(Frustum frustum, List<ChunkBuilder.BuiltChunk> builtChunks, List<ChunkBuilder.BuiltChunk> nearbyChunks) {
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
        Events events2;
        Events events = (Events)this.events.get();
        if (events != null) {
            this.addNeighbors(events, chunkPos);
        }
        if ((events2 = ((PreparerState)this.state.get()).events) != events) {
            this.addNeighbors(events2, chunkPos);
        }
    }

    public void schedulePropagationFrom(ChunkBuilder.BuiltChunk builtChunk) {
        Events events2;
        Events events = (Events)this.events.get();
        if (events != null) {
            events.sectionsToPropagateFrom.add(builtChunk);
        }
        if ((events2 = ((PreparerState)this.state.get()).events) != events) {
            events2.sectionsToPropagateFrom.add(builtChunk);
        }
    }

    public void updateSectionOcclusionGraph(boolean cullChunks, Camera camera, Frustum frustum, List<ChunkBuilder.BuiltChunk> builtChunk, LongOpenHashSet activeSections) {
        Vec3d vec3d = camera.getCameraPos();
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
            ArrayDeque queue = Queues.newArrayDeque();
            this.scheduleLater(camera, (Queue)queue);
            queue.forEach(info -> preparerState.storage.infoList.setInfo(info.chunk, info));
            this.update(preparerState.storage, cameraPos, (Queue)queue, cullChunks, builtChunk -> {}, longOpenHashSet);
            this.state.set(preparerState);
            this.events.set(null);
            this.needsUpdate.set(true);
        }, (Executor)Util.getMainWorkerExecutor());
    }

    private void updateNow(boolean cullChunks, Frustum frustum, List<ChunkBuilder.BuiltChunk> builtChunks, Vec3d cameraPos, LongOpenHashSet activeSections) {
        PreparerState preparerState = (PreparerState)this.state.get();
        this.scheduleNew(preparerState);
        if (!preparerState.events.sectionsToPropagateFrom.isEmpty()) {
            ArrayDeque queue = Queues.newArrayDeque();
            while (!preparerState.events.sectionsToPropagateFrom.isEmpty()) {
                ChunkBuilder.BuiltChunk builtChunk2 = (ChunkBuilder.BuiltChunk)preparerState.events.sectionsToPropagateFrom.poll();
                ChunkInfo chunkInfo = preparerState.storage.infoList.getInfo(builtChunk2);
                if (chunkInfo == null || chunkInfo.chunk != builtChunk2) continue;
                queue.add(chunkInfo);
            }
            Frustum frustum2 = WorldRenderer.offsetFrustum((Frustum)frustum);
            Consumer<ChunkBuilder.BuiltChunk> consumer = builtChunk -> {
                if (frustum2.isVisible(builtChunk.getBoundingBox())) {
                    this.needsUpdate.set(true);
                }
            };
            this.update(preparerState.storage, cameraPos, (Queue)queue, cullChunks, consumer, activeSections);
        }
    }

    private void scheduleNew(PreparerState preparerState) {
        LongIterator longIterator = preparerState.events.chunksWhichReceivedNeighbors.iterator();
        while (longIterator.hasNext()) {
            long l = longIterator.nextLong();
            List list = (List)preparerState.storage.queue.get(l);
            if (list == null || !((ChunkBuilder.BuiltChunk)list.get(0)).shouldBuild()) continue;
            preparerState.events.sectionsToPropagateFrom.addAll(list);
            preparerState.storage.queue.remove(l);
        }
        preparerState.events.chunksWhichReceivedNeighbors.clear();
    }

    private void addNeighbors(Events events, ChunkPos chunkPos) {
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong((int)(chunkPos.x - 1), (int)chunkPos.z));
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong((int)chunkPos.x, (int)(chunkPos.z - 1)));
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong((int)(chunkPos.x + 1), (int)chunkPos.z));
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong((int)chunkPos.x, (int)(chunkPos.z + 1)));
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong((int)(chunkPos.x - 1), (int)(chunkPos.z - 1)));
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong((int)(chunkPos.x - 1), (int)(chunkPos.z + 1)));
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong((int)(chunkPos.x + 1), (int)(chunkPos.z - 1)));
        events.chunksWhichReceivedNeighbors.add(ChunkPos.toLong((int)(chunkPos.x + 1), (int)(chunkPos.z + 1)));
    }

    private void scheduleLater(Camera camera, Queue<ChunkInfo> queue) {
        BlockPos blockPos = camera.getBlockPos();
        long l = ChunkSectionPos.toLong((BlockPos)blockPos);
        int i = ChunkSectionPos.unpackY((long)l);
        ChunkBuilder.BuiltChunk builtChunk = this.builtChunkStorage.getRenderedChunk(l);
        if (builtChunk == null) {
            HeightLimitView heightLimitView = this.builtChunkStorage.getWorld();
            boolean bl = i < heightLimitView.getBottomSectionCoord();
            int j = bl ? heightLimitView.getBottomSectionCoord() : heightLimitView.getTopSectionCoord();
            int k = this.builtChunkStorage.getViewDistance();
            ArrayList list = Lists.newArrayList();
            int m = ChunkSectionPos.unpackX((long)l);
            int n = ChunkSectionPos.unpackZ((long)l);
            for (int o = -k; o <= k; ++o) {
                for (int p = -k; p <= k; ++p) {
                    ChunkBuilder.BuiltChunk builtChunk2 = this.builtChunkStorage.getRenderedChunk(ChunkSectionPos.asLong((int)(o + m), (int)j, (int)(p + n)));
                    if (builtChunk2 == null || !this.isWithinViewDistance(l, builtChunk2.getSectionPos())) continue;
                    Direction direction = bl ? Direction.UP : Direction.DOWN;
                    ChunkInfo chunkInfo2 = new ChunkInfo(builtChunk2, direction, 0);
                    chunkInfo2.updateCullingState(chunkInfo2.cullingState, direction);
                    if (o > 0) {
                        chunkInfo2.updateCullingState(chunkInfo2.cullingState, Direction.EAST);
                    } else if (o < 0) {
                        chunkInfo2.updateCullingState(chunkInfo2.cullingState, Direction.WEST);
                    }
                    if (p > 0) {
                        chunkInfo2.updateCullingState(chunkInfo2.cullingState, Direction.SOUTH);
                    } else if (p < 0) {
                        chunkInfo2.updateCullingState(chunkInfo2.cullingState, Direction.NORTH);
                    }
                    list.add(chunkInfo2);
                }
            }
            list.sort(Comparator.comparingDouble(chunkInfo -> blockPos.getSquaredDistance((Vec3i)ChunkSectionPos.from((long)chunkInfo.chunk.getSectionPos()).getCenterPos())));
            queue.addAll(list);
        } else {
            queue.add(new ChunkInfo(builtChunk, null, 0));
        }
    }

    private void update(RenderableChunks renderableChunks, Vec3d pos, Queue<ChunkInfo> queue, boolean cullChunks, Consumer<ChunkBuilder.BuiltChunk> consumer, LongOpenHashSet longOpenHashSet) {
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from((Position)pos);
        long l2 = chunkSectionPos.asLong();
        BlockPos blockPos = chunkSectionPos.getCenterPos();
        while (!queue.isEmpty()) {
            long m;
            ChunkInfo chunkInfo = queue.poll();
            ChunkBuilder.BuiltChunk builtChunk = chunkInfo.chunk;
            if (!longOpenHashSet.contains(chunkInfo.chunk.getSectionPos())) {
                if (renderableChunks.octree.add(chunkInfo.chunk)) {
                    consumer.accept(chunkInfo.chunk);
                }
            } else {
                chunkInfo.chunk.currentRenderData.compareAndSet(ChunkRenderData.HIDDEN, ChunkRenderData.READY);
            }
            boolean bl = Math.abs(ChunkSectionPos.unpackX((long)(m = builtChunk.getSectionPos())) - chunkSectionPos.getSectionX()) > SECTION_DISTANCE || Math.abs(ChunkSectionPos.unpackY((long)m) - chunkSectionPos.getSectionY()) > SECTION_DISTANCE || Math.abs(ChunkSectionPos.unpackZ((long)m) - chunkSectionPos.getSectionZ()) > SECTION_DISTANCE;
            for (Direction direction : DIRECTIONS) {
                ChunkInfo chunkInfo2;
                int i;
                ChunkBuilder.BuiltChunk builtChunk2 = this.getRenderedChunk(l2, builtChunk, direction);
                if (builtChunk2 == null || cullChunks && chunkInfo.canCull(direction.getOpposite())) continue;
                if (cullChunks && chunkInfo.hasAnyDirection()) {
                    AbstractChunkRenderData abstractChunkRenderData = builtChunk.getCurrentRenderData();
                    boolean bl2 = false;
                    for (i = 0; i < DIRECTIONS.length; ++i) {
                        if (!chunkInfo.hasDirection(i) || !abstractChunkRenderData.isVisibleThrough(DIRECTIONS[i].getOpposite(), direction)) continue;
                        bl2 = true;
                        break;
                    }
                    if (!bl2) continue;
                }
                if (cullChunks && bl) {
                    boolean bl4;
                    boolean bl3;
                    int j = ChunkSectionPos.getBlockCoord((int)ChunkSectionPos.unpackX((long)m));
                    int k = ChunkSectionPos.getBlockCoord((int)ChunkSectionPos.unpackY((long)m));
                    i = ChunkSectionPos.getBlockCoord((int)ChunkSectionPos.unpackZ((long)m));
                    boolean bl2 = direction.getAxis() == Direction.Axis.X ? blockPos.getX() > j : (bl3 = blockPos.getX() < j);
                    boolean bl5 = direction.getAxis() == Direction.Axis.Y ? blockPos.getY() > k : (bl4 = blockPos.getY() < k);
                    boolean bl52 = direction.getAxis() == Direction.Axis.Z ? blockPos.getZ() > i : blockPos.getZ() < i;
                    Vector3d vector3d = new Vector3d((double)(j + (bl3 ? 16 : 0)), (double)(k + (bl4 ? 16 : 0)), (double)(i + (bl52 ? 16 : 0)));
                    Vector3d vector3d2 = new Vector3d(pos.x, pos.y, pos.z).sub((Vector3dc)vector3d).normalize().mul(CHUNK_INNER_DIAGONAL_LENGTH);
                    boolean bl6 = true;
                    while (vector3d.distanceSquared(pos.x, pos.y, pos.z) > 3600.0) {
                        vector3d.add((Vector3dc)vector3d2);
                        HeightLimitView heightLimitView = this.builtChunkStorage.getWorld();
                        if (vector3d.y > (double)heightLimitView.getTopYInclusive() || vector3d.y < (double)heightLimitView.getBottomY()) break;
                        ChunkBuilder.BuiltChunk builtChunk3 = this.builtChunkStorage.getRenderedChunk(BlockPos.ofFloored((double)vector3d.x, (double)vector3d.y, (double)vector3d.z));
                        if (builtChunk3 != null && renderableChunks.infoList.getInfo(builtChunk3) != null) continue;
                        bl6 = false;
                        break;
                    }
                    if (!bl6) continue;
                }
                if ((chunkInfo2 = renderableChunks.infoList.getInfo(builtChunk2)) != null) {
                    chunkInfo2.addDirection(direction);
                    continue;
                }
                ChunkInfo chunkInfo3 = new ChunkInfo(builtChunk2, direction, chunkInfo.propagationLevel + 1);
                chunkInfo3.updateCullingState(chunkInfo.cullingState, direction);
                if (builtChunk2.shouldBuild()) {
                    queue.add(chunkInfo3);
                    renderableChunks.infoList.setInfo(builtChunk2, chunkInfo3);
                    continue;
                }
                if (!this.isWithinViewDistance(l2, builtChunk2.getSectionPos())) continue;
                renderableChunks.infoList.setInfo(builtChunk2, chunkInfo3);
                long n = ChunkSectionPos.toChunkPos((long)builtChunk2.getSectionPos());
                ((List)renderableChunks.queue.computeIfAbsent(n, l -> new ArrayList())).add(builtChunk2);
            }
        }
    }

    private boolean isWithinViewDistance(long centerSectionPos, long otherSectionPos) {
        return ChunkFilter.isWithinDistanceExcludingEdge((int)ChunkSectionPos.unpackX((long)centerSectionPos), (int)ChunkSectionPos.unpackZ((long)centerSectionPos), (int)this.builtChunkStorage.getViewDistance(), (int)ChunkSectionPos.unpackX((long)otherSectionPos), (int)ChunkSectionPos.unpackZ((long)otherSectionPos));
    }

    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ChunkBuilder.BuiltChunk getRenderedChunk(long sectionPos, ChunkBuilder.BuiltChunk chunk, Direction direction) {
        long l = chunk.getOffsetSectionPos(direction);
        if (!this.isWithinViewDistance(sectionPos, l)) {
            return null;
        }
        if (MathHelper.abs((int)(ChunkSectionPos.unpackY((long)sectionPos) - ChunkSectionPos.unpackY((long)l))) > this.builtChunkStorage.getViewDistance()) {
            return null;
        }
        return this.builtChunkStorage.getRenderedChunk(l);
    }

    @Debug
    public // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ChunkRenderingDataPreparer.ChunkInfo getInfo(ChunkBuilder.BuiltChunk chunk) {
        return ((PreparerState)this.state.get()).storage.infoList.getInfo(chunk);
    }

    public Octree getOctree() {
        return ((PreparerState)this.state.get()).storage.octree;
    }
}

