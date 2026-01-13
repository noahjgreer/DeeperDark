/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlGpuBuffer;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class VertexBufferManager.AllocatedBuffer {
    final int glId;
    final VertexFormat vertexFormat;
    @Nullable GlGpuBuffer buffer;

    VertexBufferManager.AllocatedBuffer(int glId, VertexFormat vertexFormat, @Nullable GlGpuBuffer buffer) {
        this.glId = glId;
        this.vertexFormat = vertexFormat;
        this.buffer = buffer;
    }
}
