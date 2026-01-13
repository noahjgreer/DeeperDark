/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.chunk;

import com.mojang.blaze3d.systems.VertexSorter;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.chunk.AbstractChunkRenderData;
import net.minecraft.client.render.chunk.BlockBufferAllocatorStorage;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.chunk.ChunkRendererRegionBuilder;
import net.minecraft.client.render.chunk.NormalizedRelativePos;
import net.minecraft.client.render.chunk.SectionBuilder;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChunkBuilder.BuiltChunk {
    public static final int CHUNK_SIZE = 16;
    public final int index;
    public final AtomicReference<AbstractChunkRenderData> currentRenderData = new AtomicReference<AbstractChunkRenderData>(ChunkRenderData.HIDDEN);
    private @Nullable RebuildTask rebuildTask;
    private @Nullable SortTask sortTask;
    private Box boundingBox;
    private boolean needsRebuild = true;
    volatile long sectionPos = ChunkSectionPos.asLong(-1, -1, -1);
    final BlockPos.Mutable origin = new BlockPos.Mutable(-1, -1, -1);
    private boolean needsImportantRebuild;
    private long field_64231;
    private long field_64453;
    private boolean field_64454;

    public ChunkBuilder.BuiltChunk(int index, long sectionPos) {
        this.index = index;
        this.setSectionPos(sectionPos);
    }

    public float method_76298(long l) {
        long m = l - this.field_64231;
        if (m >= this.field_64453) {
            return 1.0f;
        }
        return (float)m / (float)this.field_64453;
    }

    public void method_76548(long l) {
        this.field_64453 = l;
    }

    public void method_76547(boolean bl) {
        this.field_64454 = bl;
    }

    public boolean method_76546() {
        return this.field_64454;
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

    public CompletableFuture<Void> uploadLayer(Map<BlockRenderLayer, BuiltBuffer> buffersByLayer, ChunkRenderData renderData) {
        if (ChunkBuilder.this.stopped) {
            buffersByLayer.values().forEach(BuiltBuffer::close);
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.runAsync(() -> buffersByLayer.forEach((layer, buffer) -> {
            try (ScopedProfiler scopedProfiler = Profilers.get().scoped("Upload Section Layer");){
                renderData.upload((BlockRenderLayer)((Object)((Object)layer)), (BuiltBuffer)buffer, this.sectionPos);
                buffer.close();
            }
            if (this.field_64231 == 0L) {
                this.field_64231 = Util.getMeasuringTimeMs();
            }
        }), ChunkBuilder.this.uploadExecutor);
    }

    public CompletableFuture<Void> uploadIndices(ChunkRenderData data, BufferAllocator.CloseableBuffer buffer, BlockRenderLayer layer) {
        if (ChunkBuilder.this.stopped) {
            buffer.close();
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.runAsync(() -> {
            try (ScopedProfiler scopedProfiler = Profilers.get().scoped("Upload Section Indices");){
                data.uploadIndexBuffer(layer, buffer, this.sectionPos);
                buffer.close();
            }
        }, ChunkBuilder.this.uploadExecutor);
    }

    public void setSectionPos(long sectionPos) {
        this.clear();
        this.sectionPos = sectionPos;
        int i = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackX(sectionPos));
        int j = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackY(sectionPos));
        int k = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackZ(sectionPos));
        this.origin.set(i, j, k);
        this.boundingBox = new Box(i, j, k, i + 16, j + 16, k + 16);
    }

    public AbstractChunkRenderData getCurrentRenderData() {
        return this.currentRenderData.get();
    }

    public void clear() {
        this.cancel();
        this.currentRenderData.getAndSet(ChunkRenderData.HIDDEN).close();
        this.needsRebuild = true;
        this.field_64231 = 0L;
        this.field_64454 = false;
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
        AbstractChunkRenderData abstractChunkRenderData = this.getCurrentRenderData();
        if (abstractChunkRenderData instanceof ChunkRenderData) {
            ChunkRenderData chunkRenderData = (ChunkRenderData)abstractChunkRenderData;
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
        AbstractChunkRenderData abstractChunkRenderData = this.currentRenderData.getAndSet(data);
        ChunkBuilder.this.renderQueue.add(abstractChunkRenderData);
        ChunkBuilder.this.worldRenderer.addBuiltChunk(this);
    }

    VertexSorter getVertexSorter(ChunkSectionPos sectionPos) {
        Vec3d vec3d = ChunkBuilder.this.cameraPosition;
        return VertexSorter.byDistance((float)(vec3d.x - (double)sectionPos.getMinX()), (float)(vec3d.y - (double)sectionPos.getMinY()), (float)(vec3d.z - (double)sectionPos.getMinZ()));
    }

    @Environment(value=EnvType.CLIENT)
    class SortTask
    extends Task {
        private final ChunkRenderData renderData;

        public SortTask(ChunkRenderData data) {
            super(true);
            this.renderData = data;
        }

        @Override
        protected String getName() {
            return "rend_chk_sort";
        }

        @Override
        public CompletableFuture<ChunkBuilder.Result> run(BlockBufferAllocatorStorage buffers) {
            if (this.cancelled.get()) {
                return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
            }
            BuiltBuffer.SortState sortState = this.renderData.getTranslucencySortingData();
            if (sortState == null || this.renderData.containsLayer(BlockRenderLayer.TRANSLUCENT)) {
                return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
            }
            long l = BuiltChunk.this.sectionPos;
            VertexSorter vertexSorter = BuiltChunk.this.getVertexSorter(ChunkSectionPos.from(l));
            NormalizedRelativePos normalizedRelativePos = NormalizedRelativePos.of(ChunkBuilder.this.cameraPosition, l);
            if (!this.renderData.hasPosition(normalizedRelativePos) && !normalizedRelativePos.isOnCameraAxis()) {
                return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
            }
            BufferAllocator.CloseableBuffer closeableBuffer = sortState.sortAndStore(buffers.get(BlockRenderLayer.TRANSLUCENT), vertexSorter);
            if (closeableBuffer == null) {
                return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
            }
            if (this.cancelled.get()) {
                closeableBuffer.close();
                return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
            }
            CompletableFuture<Void> completableFuture = BuiltChunk.this.uploadIndices(this.renderData, closeableBuffer, BlockRenderLayer.TRANSLUCENT);
            return completableFuture.handle((void_, throwable) -> {
                if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
                    MinecraftClient.getInstance().setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Rendering section"));
                }
                if (this.cancelled.get()) {
                    return ChunkBuilder.Result.CANCELLED;
                }
                this.renderData.setPos(normalizedRelativePos);
                return ChunkBuilder.Result.SUCCESSFUL;
            });
        }

        @Override
        public void cancel() {
            this.cancelled.set(true);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public abstract class Task {
        protected final AtomicBoolean cancelled = new AtomicBoolean(false);
        protected final AtomicBoolean finished = new AtomicBoolean(false);
        protected final boolean prioritized;

        public Task(boolean prioritized) {
            this.prioritized = prioritized;
        }

        public abstract CompletableFuture<ChunkBuilder.Result> run(BlockBufferAllocatorStorage var1);

        public abstract void cancel();

        protected abstract String getName();

        public boolean isPrioritized() {
            return this.prioritized;
        }

        public BlockPos getOrigin() {
            return BuiltChunk.this.origin;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class RebuildTask
    extends Task {
        protected final ChunkRendererRegion region;

        public RebuildTask(ChunkRendererRegion region, boolean prioritized) {
            super(prioritized);
            this.region = region;
        }

        @Override
        protected String getName() {
            return "rend_chk_rebuild";
        }

        @Override
        public CompletableFuture<ChunkBuilder.Result> run(BlockBufferAllocatorStorage buffers) {
            SectionBuilder.RenderData renderData;
            if (this.cancelled.get()) {
                return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
            }
            long l = BuiltChunk.this.sectionPos;
            ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(l);
            if (this.cancelled.get()) {
                return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
            }
            try (ScopedProfiler scopedProfiler = Profilers.get().scoped("Compile Section");){
                renderData = ChunkBuilder.this.sectionBuilder.build(chunkSectionPos, this.region, BuiltChunk.this.getVertexSorter(chunkSectionPos), buffers);
            }
            NormalizedRelativePos normalizedRelativePos = NormalizedRelativePos.of(ChunkBuilder.this.cameraPosition, l);
            if (this.cancelled.get()) {
                renderData.close();
                return CompletableFuture.completedFuture(ChunkBuilder.Result.CANCELLED);
            }
            ChunkRenderData chunkRenderData = new ChunkRenderData(normalizedRelativePos, renderData);
            CompletableFuture<Void> completableFuture = BuiltChunk.this.uploadLayer(renderData.buffers, chunkRenderData);
            return completableFuture.handle((void_, throwable) -> {
                if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
                    MinecraftClient.getInstance().setCrashReportSupplierAndAddDetails(CrashReport.create(throwable, "Rendering section"));
                }
                if (this.cancelled.get() || ChunkBuilder.this.stopped) {
                    ChunkBuilder.this.renderQueue.add(chunkRenderData);
                    return ChunkBuilder.Result.CANCELLED;
                }
                BuiltChunk.this.setCurrentRenderData(chunkRenderData);
                return ChunkBuilder.Result.SUCCESSFUL;
            });
        }

        @Override
        public void cancel() {
            if (this.cancelled.compareAndSet(false, true)) {
                BuiltChunk.this.scheduleRebuild(false);
            }
        }
    }
}
