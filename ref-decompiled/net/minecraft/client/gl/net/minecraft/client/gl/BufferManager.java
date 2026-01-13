/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.opengl.ARBBufferStorage
 *  org.lwjgl.opengl.ARBDirectStateAccess
 *  org.lwjgl.opengl.GL30
 *  org.lwjgl.opengl.GL31
 *  org.lwjgl.opengl.GLCapabilities
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import java.nio.ByteBuffer;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.gl.GpuDeviceInfo;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.ARBBufferStorage;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GLCapabilities;

@Environment(value=EnvType.CLIENT)
public abstract class BufferManager {
    public static BufferManager create(GLCapabilities capabilities, Set<String> usedCapabilities, GpuDeviceInfo deviceInfo) {
        if (capabilities.GL_ARB_direct_state_access && GlBackend.allowGlArbDirectAccess && !deviceInfo.shouldDisableArbDirectAccess()) {
            usedCapabilities.add("GL_ARB_direct_state_access");
            return new ARBBufferManager();
        }
        return new DefaultBufferManager();
    }

    abstract int createBuffer();

    abstract void setBufferData(int var1, long var2, @GpuBuffer.Usage int var4);

    abstract void setBufferData(int var1, ByteBuffer var2, @GpuBuffer.Usage int var3);

    abstract void setBufferSubData(int var1, long var2, ByteBuffer var4, @GpuBuffer.Usage int var5);

    abstract void setBufferStorage(int var1, long var2, @GpuBuffer.Usage int var4);

    abstract void setBufferStorage(int var1, ByteBuffer var2, @GpuBuffer.Usage int var3);

    abstract @Nullable ByteBuffer mapBufferRange(int var1, long var2, long var4, int var6, @GpuBuffer.Usage int var7);

    abstract void unmapBuffer(int var1, @GpuBuffer.Usage int var2);

    abstract int createFramebuffer();

    abstract void setupFramebuffer(int var1, int var2, int var3, int var4, int var5);

    abstract void setupBlitFramebuffer(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12);

    abstract void flushMappedBufferRange(int var1, long var2, long var4, @GpuBuffer.Usage int var6);

    abstract void copyBufferSubData(int var1, int var2, long var3, long var5, long var7);

    @Environment(value=EnvType.CLIENT)
    static class ARBBufferManager
    extends BufferManager {
        ARBBufferManager() {
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

    @Environment(value=EnvType.CLIENT)
    static class DefaultBufferManager
    extends BufferManager {
        DefaultBufferManager() {
        }

        private int getTarget(@GpuBuffer.Usage int i) {
            if ((i & 0x20) != 0) {
                return 34962;
            }
            if ((i & 0x40) != 0) {
                return 34963;
            }
            if ((i & 0x80) != 0) {
                return 35345;
            }
            return 36663;
        }

        @Override
        int createBuffer() {
            return GlStateManager._glGenBuffers();
        }

        @Override
        void setBufferData(int buffer, long size, @GpuBuffer.Usage int usage) {
            int i = this.getTarget(usage);
            GlStateManager._glBindBuffer(i, buffer);
            GlStateManager._glBufferData(i, size, GlConst.bufferUsageToGlEnum(usage));
            GlStateManager._glBindBuffer(i, 0);
        }

        @Override
        void setBufferData(int buffer, ByteBuffer data, @GpuBuffer.Usage int usage) {
            int i = this.getTarget(usage);
            GlStateManager._glBindBuffer(i, buffer);
            GlStateManager._glBufferData(i, data, GlConst.bufferUsageToGlEnum(usage));
            GlStateManager._glBindBuffer(i, 0);
        }

        @Override
        void setBufferSubData(int buffer, long offset, ByteBuffer data, @GpuBuffer.Usage int usage) {
            int i = this.getTarget(usage);
            GlStateManager._glBindBuffer(i, buffer);
            GlStateManager._glBufferSubData(i, offset, data);
            GlStateManager._glBindBuffer(i, 0);
        }

        @Override
        void setBufferStorage(int buffer, long size, @GpuBuffer.Usage int usage) {
            int i = this.getTarget(usage);
            GlStateManager._glBindBuffer(i, buffer);
            ARBBufferStorage.glBufferStorage((int)i, (long)size, (int)GlConst.bufferUsageToGlFlag(usage));
            GlStateManager._glBindBuffer(i, 0);
        }

        @Override
        void setBufferStorage(int buffer, ByteBuffer data, @GpuBuffer.Usage int usage) {
            int i = this.getTarget(usage);
            GlStateManager._glBindBuffer(i, buffer);
            ARBBufferStorage.glBufferStorage((int)i, (ByteBuffer)data, (int)GlConst.bufferUsageToGlFlag(usage));
            GlStateManager._glBindBuffer(i, 0);
        }

        @Override
        @Nullable ByteBuffer mapBufferRange(int buffer, long offset, long length, int access, @GpuBuffer.Usage int usage) {
            int i = this.getTarget(usage);
            GlStateManager._glBindBuffer(i, buffer);
            ByteBuffer byteBuffer = GlStateManager._glMapBufferRange(i, offset, length, access);
            GlStateManager._glBindBuffer(i, 0);
            return byteBuffer;
        }

        @Override
        void unmapBuffer(int buffer, @GpuBuffer.Usage int usage) {
            int i = this.getTarget(usage);
            GlStateManager._glBindBuffer(i, buffer);
            GlStateManager._glUnmapBuffer(i);
            GlStateManager._glBindBuffer(i, 0);
        }

        @Override
        void flushMappedBufferRange(int buffer, long offset, long length, @GpuBuffer.Usage int usage) {
            int i = this.getTarget(usage);
            GlStateManager._glBindBuffer(i, buffer);
            GL30.glFlushMappedBufferRange((int)i, (long)offset, (long)length);
            GlStateManager._glBindBuffer(i, 0);
        }

        @Override
        void copyBufferSubData(int fromBuffer, int toBuffer, long readOffset, long writeOffset, long size) {
            GlStateManager._glBindBuffer(36662, fromBuffer);
            GlStateManager._glBindBuffer(36663, toBuffer);
            GL31.glCopyBufferSubData((int)36662, (int)36663, (long)readOffset, (long)writeOffset, (long)size);
            GlStateManager._glBindBuffer(36662, 0);
            GlStateManager._glBindBuffer(36663, 0);
        }

        @Override
        public int createFramebuffer() {
            return GlStateManager.glGenFramebuffers();
        }

        @Override
        public void setupFramebuffer(int framebuffer, int colorAttachment, int depthAttachment, int mipLevel, int bindTarget) {
            int i = bindTarget == 0 ? 36009 : bindTarget;
            int j = GlStateManager.getFrameBuffer(i);
            GlStateManager._glBindFramebuffer(i, framebuffer);
            GlStateManager._glFramebufferTexture2D(i, 36064, 3553, colorAttachment, mipLevel);
            GlStateManager._glFramebufferTexture2D(i, 36096, 3553, depthAttachment, mipLevel);
            if (bindTarget == 0) {
                GlStateManager._glBindFramebuffer(i, j);
            }
        }

        @Override
        public void setupBlitFramebuffer(int readFramebuffer, int writeFramebuffer, int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
            int i = GlStateManager.getFrameBuffer(36008);
            int j = GlStateManager.getFrameBuffer(36009);
            GlStateManager._glBindFramebuffer(36008, readFramebuffer);
            GlStateManager._glBindFramebuffer(36009, writeFramebuffer);
            GlStateManager._glBlitFrameBuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
            GlStateManager._glBindFramebuffer(36008, i);
            GlStateManager._glBindFramebuffer(36009, j);
        }
    }
}
