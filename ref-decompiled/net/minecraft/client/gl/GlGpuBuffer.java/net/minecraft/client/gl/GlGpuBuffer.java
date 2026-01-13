/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.jtracy.MemoryPool
 *  com.mojang.jtracy.TracyClient
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        if (this.backingBuffer != null) {
            this.bufferManager.unmapBuffer(this.id, this.usage());
            this.backingBuffer = null;
        }
        GlStateManager._glDeleteBuffers(this.id);
        POOL.free((long)this.id);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Mapped
    implements GpuBuffer.MappedView {
        private final Runnable closer;
        private final GlGpuBuffer backingBuffer;
        private final ByteBuffer data;
        private boolean closed;

        protected Mapped(Runnable closer, GlGpuBuffer backingBuffer, ByteBuffer data) {
            this.closer = closer;
            this.backingBuffer = backingBuffer;
            this.data = data;
        }

        @Override
        public ByteBuffer data() {
            return this.data;
        }

        @Override
        public void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            this.closer.run();
        }
    }
}
