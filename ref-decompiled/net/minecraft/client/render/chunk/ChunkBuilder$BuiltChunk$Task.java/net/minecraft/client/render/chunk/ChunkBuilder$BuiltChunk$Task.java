/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.chunk;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.BlockBufferAllocatorStorage;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public abstract class ChunkBuilder.BuiltChunk.Task {
    protected final AtomicBoolean cancelled = new AtomicBoolean(false);
    protected final AtomicBoolean finished = new AtomicBoolean(false);
    protected final boolean prioritized;

    public ChunkBuilder.BuiltChunk.Task(boolean prioritized) {
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
