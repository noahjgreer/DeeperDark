/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.opengl.GlStateManager;
import java.nio.ByteBuffer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.BufferManager;
import net.minecraft.client.gl.GlGpuBuffer;
import net.minecraft.client.gl.GpuBufferManager;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class GpuBufferManager.DirectGpuBufferManager
extends GpuBufferManager {
    GpuBufferManager.DirectGpuBufferManager() {
    }

    @Override
    public GlGpuBuffer createBuffer(BufferManager bufferManager, @Nullable Supplier<String> debugLabelSupplier, @GpuBuffer.Usage int usage, long size) {
        int i = bufferManager.createBuffer();
        bufferManager.setBufferData(i, size, usage);
        return new GlGpuBuffer(debugLabelSupplier, bufferManager, usage, size, i, null);
    }

    @Override
    public GlGpuBuffer createBuffer(BufferManager bufferManager, @Nullable Supplier<String> debugLabelSupplier, @GpuBuffer.Usage int usage, ByteBuffer data) {
        int i = bufferManager.createBuffer();
        int j = data.remaining();
        bufferManager.setBufferData(i, data, usage);
        return new GlGpuBuffer(debugLabelSupplier, bufferManager, usage, j, i, null);
    }

    @Override
    public GlGpuBuffer.Mapped mapBufferRange(BufferManager bufferManager, GlGpuBuffer buffer, long offset, long length, int flags) {
        GlStateManager.clearGlErrors();
        ByteBuffer byteBuffer = bufferManager.mapBufferRange(buffer.id, offset, length, flags, buffer.usage());
        if (byteBuffer == null) {
            throw new IllegalStateException("Can't map buffer, opengl error " + GlStateManager._getError());
        }
        return new GlGpuBuffer.Mapped(() -> bufferManager.unmapBuffer(glGpuBuffer.id, buffer.usage()), buffer, byteBuffer);
    }
}
