/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.command;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.MappableRingBuffer;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class LayeredCustomCommandRenderer.VerticesCache
implements AutoCloseable {
    private @Nullable MappableRingBuffer ringBuffer;

    public void write(ByteBuffer byteBuffer) {
        if (this.ringBuffer == null || this.ringBuffer.size() < byteBuffer.remaining()) {
            if (this.ringBuffer != null) {
                this.ringBuffer.close();
            }
            this.ringBuffer = new MappableRingBuffer(() -> "Particle Vertices", 34, byteBuffer.remaining());
        }
        try (GpuBuffer.MappedView mappedView = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.ringBuffer.getBlocking().slice(), false, true);){
            mappedView.data().put(byteBuffer);
        }
    }

    public GpuBuffer get() {
        if (this.ringBuffer == null) {
            throw new IllegalStateException("Can't get buffer before it's made");
        }
        return this.ringBuffer.getBlocking();
    }

    void rotate() {
        if (this.ringBuffer != null) {
            this.ringBuffer.rotate();
        }
    }

    @Override
    public void close() {
        if (this.ringBuffer != null) {
            this.ringBuffer.close();
        }
    }
}
