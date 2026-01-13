/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer$Usage
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.BufferManager
 *  net.minecraft.client.gl.BufferManager$ARBBufferManager
 *  net.minecraft.client.gl.BufferManager$DefaultBufferManager
 *  net.minecraft.client.gl.GlBackend
 *  net.minecraft.client.gl.GpuDeviceInfo
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.opengl.GLCapabilities
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import java.nio.ByteBuffer;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.BufferManager;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.gl.GpuDeviceInfo;
import org.jspecify.annotations.Nullable;
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
}

