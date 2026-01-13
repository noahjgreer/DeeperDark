/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlGpuBuffer;

@Environment(value=EnvType.CLIENT)
public static class GlGpuBuffer.Mapped
implements GpuBuffer.MappedView {
    private final Runnable closer;
    private final GlGpuBuffer backingBuffer;
    private final ByteBuffer data;
    private boolean closed;

    protected GlGpuBuffer.Mapped(Runnable closer, GlGpuBuffer backingBuffer, ByteBuffer data) {
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
