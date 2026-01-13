/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBuffer$Usage
 *  com.mojang.blaze3d.opengl.GlStateManager
 *  com.mojang.jtracy.MemoryPool
 *  com.mojang.jtracy.TracyClient
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.BufferManager
 *  net.minecraft.client.gl.GlGpuBuffer
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.jtracy.MemoryPool;
import com.mojang.jtracy.TracyClient;
import java.nio.ByteBuffer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.BufferManager;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GlGpuBuffer
extends GpuBuffer {
    protected static final MemoryPool POOL = TracyClient.createMemoryPool((String)"GPU Buffers");
    protected boolean closed;
    protected final @Nullable Supplier<String> debugLabelSupplier;
    private final BufferManager bufferManager;
    protected final int id;
    protected @Nullable ByteBuffer backingBuffer;

    protected GlGpuBuffer(@Nullable Supplier<String> debugLabelSupplier, BufferManager bufferManager, @GpuBuffer.Usage int usage, long size, int id, @Nullable ByteBuffer backingBuffer) {
        super(usage, size);
        this.debugLabelSupplier = debugLabelSupplier;
        this.bufferManager = bufferManager;
        this.id = id;
        this.backingBuffer = backingBuffer;
        int i = (int)Math.min(size, Integer.MAX_VALUE);
        POOL.malloc((long)id, i);
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        if (this.backingBuffer != null) {
            this.bufferManager.unmapBuffer(this.id, this.usage());
            this.backingBuffer = null;
        }
        GlStateManager._glDeleteBuffers((int)this.id);
        POOL.free((long)this.id);
    }
}

