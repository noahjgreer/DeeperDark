/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.chunk;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.chunk.BlockBufferAllocatorStorage;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.chunk.NormalizedRelativePos;
import net.minecraft.client.render.chunk.SectionBuilder;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;

@Environment(value=EnvType.CLIENT)
class ChunkBuilder.BuiltChunk.RebuildTask
extends ChunkBuilder.BuiltChunk.Task {
    protected final ChunkRendererRegion region;

    public ChunkBuilder.BuiltChunk.RebuildTask(ChunkRendererRegion region, boolean prioritized) {
        super(BuiltChunk.this, prioritized);
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
            renderData = BuiltChunk.this.field_20833.sectionBuilder.build(chunkSectionPos, this.region, BuiltChunk.this.getVertexSorter(chunkSectionPos), buffers);
        }
        NormalizedRelativePos normalizedRelativePos = NormalizedRelativePos.of(BuiltChunk.this.field_20833.cameraPosition, l);
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
            if (this.cancelled.get() || BuiltChunk.this.field_20833.stopped) {
                BuiltChunk.this.field_20833.renderQueue.add(chunkRenderData);
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
