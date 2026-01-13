/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.opengl.GLCapabilities
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.opengl.GlStateManager;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.BufferManager;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.gl.GlGpuBuffer;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public abstract class GpuBufferManager {
    public static GpuBufferManager create(GLCapabilities capabilities, Set<String> usedCapabilities) {
        if (capabilities.GL_ARB_buffer_storage && GlBackend.allowGlBufferStorage) {
            usedCapabilities.add("GL_ARB_buffer_storage");
            return new ARBGpuBufferManager();
        }
        return new DirectGpuBufferManager();
    }

    public abstract GlGpuBuffer createBuffer(BufferManager var1, @Nullable Supplier<String> var2, @GpuBuffer.Usage int var3, long var4);

    public abstract GlGpuBuffer createBuffer(BufferManager var1, @Nullable Supplier<String> var2, @GpuBuffer.Usage int var3, ByteBuffer var4);

    public abstract GlGpuBuffer.Mapped mapBufferRange(BufferManager var1, GlGpuBuffer var2, long var3, long var5, int var7);

    @Environment(value=EnvType.CLIENT)
    static class ARBGpuBufferManager
    extends GpuBufferManager {
        ARBGpuBufferManager() {
        }

        @Override
        public GlGpuBuffer createBuffer(BufferManager bufferManager, @Nullable Supplier<String> debugLabelSupplier, @GpuBuffer.Usage int usage, long size) {
            int i = bufferManager.createBuffer();
            bufferManager.setBufferStorage(i, size, usage);
            ByteBuffer byteBuffer = this.mapBufferRange(bufferManager, usage, i, size);
            return new GlGpuBuffer(debugLabelSupplier, bufferManager, usage, size, i, byteBuffer);
        }

        @Override
        public GlGpuBuffer createBuffer(BufferManager bufferManager, @Nullable Supplier<String> debugLabelSupplier, @GpuBuffer.Usage int usage, ByteBuffer data) {
            int i = bufferManager.createBuffer();
            int j = data.remaining();
            bufferManager.setBufferStorage(i, data, usage);
            ByteBuffer byteBuffer = this.mapBufferRange(bufferManager, usage, i, j);
            return new GlGpuBuffer(debugLabelSupplier, bufferManager, usage, j, i, byteBuffer);
        }

        private @Nullable ByteBuffer mapBufferRange(BufferManager bufferManager, @GpuBuffer.Usage int usage, int buffer, long length) {
            ByteBuffer byteBuffer;
            int i = 0;
            if ((usage & 1) != 0) {
                i |= 1;
            }
            if ((usage & 2) != 0) {
                i |= 0x12;
            }
            if (i != 0) {
                GlStateManager.clearGlErrors();
                byteBuffer = bufferManager.mapBufferRange(buffer, 0L, length, i | 0x40, usage);
                if (byteBuffer == null) {
                    throw new IllegalStateException("Can't persistently map buffer, opengl error " + GlStateManager._getError());
                }
            } else {
                byteBuffer = null;
            }
            return byteBuffer;
        }

        @Override
        public GlGpuBuffer.Mapped mapBufferRange(BufferManager bufferManager, GlGpuBuffer buffer, long offset, long length, int flags) {
            if (buffer.backingBuffer == null) {
                throw new IllegalStateException("Somehow trying to map an unmappable buffer");
            }
            if (offset > Integer.MAX_VALUE || length > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Mapping buffers larger than 2GB is not supported");
            }
            if (offset < 0L || length < 0L) {
                throw new IllegalArgumentException("Offset or length must be positive integer values");
            }
            return new GlGpuBuffer.Mapped(() -> {
                if ((flags & 2) != 0) {
                    bufferManager.flushMappedBufferRange(glGpuBuffer.id, offset, length, buffer.usage());
                }
            }, buffer, MemoryUtil.memSlice((ByteBuffer)buffer.backingBuffer, (int)((int)offset), (int)((int)length)));
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class DirectGpuBufferManager
    extends GpuBufferManager {
        DirectGpuBufferManager() {
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
}
