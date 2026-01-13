/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.BufferBuilderStorage
 *  net.minecraft.client.render.WorldRenderer
 *  net.minecraft.client.render.block.BlockRenderManager
 *  net.minecraft.client.render.block.entity.BlockEntityRenderManager
 *  net.minecraft.client.render.chunk.AbstractChunkRenderData
 *  net.minecraft.client.render.chunk.BlockBufferAllocatorStorage
 *  net.minecraft.client.render.chunk.BlockBufferBuilderPool
 *  net.minecraft.client.render.chunk.ChunkBuilder
 *  net.minecraft.client.render.chunk.ChunkBuilder$BuiltChunk
 *  net.minecraft.client.render.chunk.ChunkBuilder$BuiltChunk$Task
 *  net.minecraft.client.render.chunk.ChunkBuilder$Result
 *  net.minecraft.client.render.chunk.ChunkRenderTaskScheduler
 *  net.minecraft.client.render.chunk.ChunkRendererRegionBuilder
 *  net.minecraft.client.render.chunk.SectionBuilder
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.annotation.Debug
 *  net.minecraft.util.crash.CrashReport
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.thread.NameableExecutor
 *  net.minecraft.util.thread.SimpleConsecutiveExecutor
 */
package net.minecraft.client.render.chunk;

import com.google.common.collect.Queues;
import java.util.Locale;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderManager;
import net.minecraft.client.render.chunk.AbstractChunkRenderData;
import net.minecraft.client.render.chunk.BlockBufferAllocatorStorage;
import net.minecraft.client.render.chunk.BlockBufferBuilderPool;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkRenderTaskScheduler;
import net.minecraft.client.render.chunk.ChunkRendererRegionBuilder;
import net.minecraft.client.render.chunk.SectionBuilder;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.thread.NameableExecutor;
import net.minecraft.util.thread.SimpleConsecutiveExecutor;

@Environment(value=EnvType.CLIENT)
public class ChunkBuilder {
    private final ChunkRenderTaskScheduler scheduler = new ChunkRenderTaskScheduler();
    private final Queue<Runnable> uploadQueue = Queues.newConcurrentLinkedQueue();
    final Executor uploadExecutor = this.uploadQueue::add;
    final Queue<AbstractChunkRenderData> renderQueue = Queues.newConcurrentLinkedQueue();
    final BlockBufferAllocatorStorage buffers;
    private final BlockBufferBuilderPool buffersPool;
    volatile boolean stopped;
    private final SimpleConsecutiveExecutor consecutiveExecutor;
    private final NameableExecutor executor;
    ClientWorld world;
    final WorldRenderer worldRenderer;
    Vec3d cameraPosition = Vec3d.ZERO;
    final SectionBuilder sectionBuilder;

    public ChunkBuilder(ClientWorld world, WorldRenderer worldRenderer, NameableExecutor executor, BufferBuilderStorage bufferBuilderStorage, BlockRenderManager blockRenderManager, BlockEntityRenderManager blockEntityRenderDispatcher) {
        this.world = world;
        this.worldRenderer = worldRenderer;
        this.buffers = bufferBuilderStorage.getBlockBufferBuilders();
        this.buffersPool = bufferBuilderStorage.getBlockBufferBuildersPool();
        this.executor = executor;
        this.consecutiveExecutor = new SimpleConsecutiveExecutor((Executor)executor, "Section Renderer");
        this.consecutiveExecutor.send(() -> this.scheduleRunTasks());
        this.sectionBuilder = new SectionBuilder(blockRenderManager, blockEntityRenderDispatcher);
    }

    public void setWorld(ClientWorld world) {
        this.world = world;
    }

    private void scheduleRunTasks() {
        if (this.stopped || this.buffersPool.hasNoAvailableBuilder()) {
            return;
        }
        BuiltChunk.Task task = this.scheduler.dequeueNearest(this.cameraPosition);
        if (task == null) {
            return;
        }
        BlockBufferAllocatorStorage blockBufferAllocatorStorage = Objects.requireNonNull(this.buffersPool.acquire());
        ((CompletableFuture)CompletableFuture.supplyAsync(() -> task.run(blockBufferAllocatorStorage), this.executor.named(task.getName())).thenCompose(future -> future)).whenComplete((result, throwable) -> {
            if (throwable != null) {
                MinecraftClient.getInstance().setCrashReportSupplierAndAddDetails(CrashReport.create((Throwable)throwable, (String)"Batching sections"));
                return;
            }
            task.finished.set(true);
            this.consecutiveExecutor.send(() -> {
                if (result == Result.SUCCESSFUL) {
                    blockBufferAllocatorStorage.clear();
                } else {
                    blockBufferAllocatorStorage.reset();
                }
                this.buffersPool.release(blockBufferAllocatorStorage);
                this.scheduleRunTasks();
            });
        });
    }

    public void setCameraPosition(Vec3d cameraPosition) {
        this.cameraPosition = cameraPosition;
    }

    public void upload() {
        AbstractChunkRenderData abstractChunkRenderData;
        Runnable runnable;
        while ((runnable = (Runnable)this.uploadQueue.poll()) != null) {
            runnable.run();
        }
        while ((abstractChunkRenderData = (AbstractChunkRenderData)this.renderQueue.poll()) != null) {
            abstractChunkRenderData.close();
        }
    }

    public void rebuild(BuiltChunk chunk, ChunkRendererRegionBuilder builder) {
        chunk.rebuild(builder);
    }

    public void send(BuiltChunk.Task task) {
        if (this.stopped) {
            return;
        }
        this.consecutiveExecutor.send(() -> {
            if (this.stopped) {
                return;
            }
            this.scheduler.enqueue(task);
            this.scheduleRunTasks();
        });
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
}

