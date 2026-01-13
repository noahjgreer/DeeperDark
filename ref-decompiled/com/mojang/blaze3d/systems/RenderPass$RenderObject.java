/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record RenderPass.RenderObject<T>(int slot, GpuBuffer vertexBuffer, @Nullable GpuBuffer indexBuffer,  @Nullable VertexFormat.IndexType indexType, int firstIndex, int indexCount, @Nullable BiConsumer<T, RenderPass.UniformUploader> uniformUploaderConsumer) {
    public RenderPass.RenderObject(int slot, GpuBuffer vertexBuffer, GpuBuffer indexBuffer, VertexFormat.IndexType indexType, int firstIndex, int indexCount) {
        this(slot, vertexBuffer, indexBuffer, indexType, firstIndex, indexCount, null);
    }
}
