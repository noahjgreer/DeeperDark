/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.opengl.ARBDirectStateAccess
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.BufferManager;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.ARBDirectStateAccess;

@Environment(value=EnvType.CLIENT)
static class BufferManager.ARBBufferManager
extends BufferManager {
    BufferManager.ARBBufferManager() {
    }

    @Override
    int createBuffer() {
        GlStateManager.incrementTrackedBuffers();
        return ARBDirectStateAccess.glCreateBuffers();
    }

    @Override
    void setBufferData(int buffer, long size, @GpuBuffer.Usage int usage) {
        ARBDirectStateAccess.glNamedBufferData((int)buffer, (long)size, (int)GlConst.bufferUsageToGlEnum(usage));
    }

    @Override
    void setBufferData(int buffer, ByteBuffer data, @GpuBuffer.Usage int usage) {
        ARBDirectStateAccess.glNamedBufferData((int)buffer, (ByteBuffer)data, (int)GlConst.bufferUsageToGlEnum(usage));
    }

    @Override
    void setBufferSubData(int buffer, long offset, ByteBuffer data, @GpuBuffer.Usage int usage) {
        ARBDirectStateAccess.glNamedBufferSubData((int)buffer, (long)offset, (ByteBuffer)data);
    }

    @Override
    void setBufferStorage(int buffer, long size, @GpuBuffer.Usage int usage) {
        ARBDirectStateAccess.glNamedBufferStorage((int)buffer, (long)size, (int)GlConst.bufferUsageToGlFlag(usage));
    }

    @Override
    void setBufferStorage(int buffer, ByteBuffer data, @GpuBuffer.Usage int usage) {
        ARBDirectStateAccess.glNamedBufferStorage((int)buffer, (ByteBuffer)data, (int)GlConst.bufferUsageToGlFlag(usage));
    }

    @Override
    @Nullable ByteBuffer mapBufferRange(int buffer, long offset, long length, int access, @GpuBuffer.Usage int usage) {
        return ARBDirectStateAccess.glMapNamedBufferRange((int)buffer, (long)offset, (long)length, (int)access);
    }

    @Override
    void unmapBuffer(int buffer, int usage) {
        ARBDirectStateAccess.glUnmapNamedBuffer((int)buffer);
    }

    @Override
    public int createFramebuffer() {
        return ARBDirectStateAccess.glCreateFramebuffers();
    }

    @Override
    public void setupFramebuffer(int framebuffer, int colorAttachment, int depthAttachment, int mipLevel, @GpuBuffer.Usage int bindTarget) {
        ARBDirectStateAccess.glNamedFramebufferTexture((int)framebuffer, (int)36064, (int)colorAttachment, (int)mipLevel);
        ARBDirectStateAccess.glNamedFramebufferTexture((int)framebuffer, (int)36096, (int)depthAttachment, (int)mipLevel);
        if (bindTarget != 0) {
            GlStateManager._glBindFramebuffer(bindTarget, framebuffer);
        }
    }

    @Override
    public void setupBlitFramebuffer(int readFramebuffer, int writeFramebuffer, int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        ARBDirectStateAccess.glBlitNamedFramebuffer((int)readFramebuffer, (int)writeFramebuffer, (int)srcX0, (int)srcY0, (int)srcX1, (int)srcY1, (int)dstX0, (int)dstY0, (int)dstX1, (int)dstY1, (int)mask, (int)filter);
    }

    @Override
    void flushMappedBufferRange(int buffer, long offset, long length, @GpuBuffer.Usage int usage) {
        ARBDirectStateAccess.glFlushMappedNamedBufferRange((int)buffer, (long)offset, (long)length);
    }

    @Override
    void copyBufferSubData(int fromBuffer, int toBuffer, long readOffset, long writeOffset, long size) {
        ARBDirectStateAccess.glCopyNamedBufferSubData((int)fromBuffer, (int)toBuffer, (long)readOffset, (long)writeOffset, (long)size);
    }
}
