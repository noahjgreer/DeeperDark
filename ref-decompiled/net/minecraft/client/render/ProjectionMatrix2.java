/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.buffers.Std140Builder
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.ProjectionMatrix2
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.lwjgl.system.MemoryStack
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.system.MemoryStack;

@Environment(value=EnvType.CLIENT)
public class ProjectionMatrix2
implements AutoCloseable {
    private final GpuBuffer buffer;
    private final GpuBufferSlice slice;
    private final float nearZ;
    private final float farZ;
    private final boolean invertY;
    private float width;
    private float height;

    public ProjectionMatrix2(String name, float nearZ, float farZ, boolean invertY) {
        this.nearZ = nearZ;
        this.farZ = farZ;
        this.invertY = invertY;
        GpuDevice gpuDevice = RenderSystem.getDevice();
        this.buffer = gpuDevice.createBuffer(() -> "Projection matrix UBO " + name, 136, (long)RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
        this.slice = this.buffer.slice(0L, (long)RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
    }

    public GpuBufferSlice set(float width, float height) {
        if (this.width != width || this.height != height) {
            Matrix4f matrix4f = this.getMatrix(width, height);
            try (MemoryStack memoryStack = MemoryStack.stackPush();){
                ByteBuffer byteBuffer = Std140Builder.onStack((MemoryStack)memoryStack, (int)RenderSystem.PROJECTION_MATRIX_UBO_SIZE).putMat4f((Matrix4fc)matrix4f).get();
                RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), byteBuffer);
            }
            this.width = width;
            this.height = height;
        }
        return this.slice;
    }

    private Matrix4f getMatrix(float width, float height) {
        return new Matrix4f().setOrtho(0.0f, width, this.invertY ? height : 0.0f, this.invertY ? 0.0f : height, this.nearZ, this.farZ);
    }

    @Override
    public void close() {
        this.buffer.close();
    }
}

