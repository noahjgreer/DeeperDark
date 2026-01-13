/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer$Usage
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.BufferManager
 *  net.minecraft.client.gl.GlBackend
 *  net.minecraft.client.gl.GlGpuBuffer
 *  net.minecraft.client.gl.GlGpuBuffer$Mapped
 *  net.minecraft.client.gl.GpuBufferManager
 *  net.minecraft.client.gl.GpuBufferManager$ARBGpuBufferManager
 *  net.minecraft.client.gl.GpuBufferManager$DirectGpuBufferManager
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.opengl.GLCapabilities
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.BufferManager;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.gl.GlGpuBuffer;
import net.minecraft.client.gl.GpuBufferManager;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GLCapabilities;

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
}

