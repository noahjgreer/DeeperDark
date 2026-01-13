/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.DebugLabelManager
 *  net.minecraft.client.gl.GlBackend
 *  net.minecraft.client.gl.GlGpuBuffer
 *  net.minecraft.client.gl.VertexBufferManager
 *  net.minecraft.client.gl.VertexBufferManager$ARBVertexBufferManager
 *  net.minecraft.client.gl.VertexBufferManager$DefaultVertexBufferManager
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.opengl.GLCapabilities
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.DebugLabelManager;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.gl.GlGpuBuffer;
import net.minecraft.client.gl.VertexBufferManager;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GLCapabilities;

@Environment(value=EnvType.CLIENT)
public abstract class VertexBufferManager {
    public static VertexBufferManager create(GLCapabilities capabilities, DebugLabelManager labeler, Set<String> usedCapabilities) {
        if (capabilities.GL_ARB_vertex_attrib_binding && GlBackend.allowGlArbVABinding) {
            usedCapabilities.add("GL_ARB_vertex_attrib_binding");
            return new ARBVertexBufferManager(labeler);
        }
        return new DefaultVertexBufferManager(labeler);
    }

    public abstract void setupBuffer(VertexFormat var1, @Nullable GlGpuBuffer var2);
}

