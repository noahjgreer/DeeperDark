/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.chunk;

import com.mojang.blaze3d.systems.VertexSorter;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.chunk.BlockBufferAllocatorStorage;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.NormalizedRelativePos;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.ChunkSectionPos;

@Environment(value=EnvType.CLIENT)
class ChunkBuilder.BuiltChunk.SortTask
extends ChunkBuilder.BuiltChunk.Task {
    private final ChunkRenderData renderData;

    public ChunkBuilder.BuiltChunk.SortTask(ChunkRenderData data) {
        super(BuiltChunk.this, true);
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
        NormalizedRelativePos normalizedRelativePos = NormalizedRelativePos.of(BuiltChunk.this.field_20833.cameraPosition, l);
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
