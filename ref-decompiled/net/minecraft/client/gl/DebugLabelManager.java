/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.CompiledShader
 *  net.minecraft.client.gl.DebugLabelManager
 *  net.minecraft.client.gl.DebugLabelManager$EXTDebugLabelManager
 *  net.minecraft.client.gl.DebugLabelManager$KHRDebugLabelManager
 *  net.minecraft.client.gl.DebugLabelManager$NoOpDebugLabelManager
 *  net.minecraft.client.gl.GlBackend
 *  net.minecraft.client.gl.GlGpuBuffer
 *  net.minecraft.client.gl.ShaderProgram
 *  net.minecraft.client.gl.VertexBufferManager$AllocatedBuffer
 *  net.minecraft.client.texture.GlTexture
 *  org.lwjgl.opengl.GLCapabilities
 *  org.slf4j.Logger
 */
package net.minecraft.client.gl;

import com.mojang.logging.LogUtils;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.CompiledShader;
import net.minecraft.client.gl.DebugLabelManager;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.gl.GlGpuBuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBufferManager;
import net.minecraft.client.texture.GlTexture;
import org.lwjgl.opengl.GLCapabilities;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public abstract class DebugLabelManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    public void labelGlGpuBuffer(GlGpuBuffer buffer) {
    }

    public void labelGlTexture(GlTexture texture) {
    }

    public void labelCompiledShader(CompiledShader shader) {
    }

    public void labelShaderProgram(ShaderProgram program) {
    }

    public void labelAllocatedBuffer(VertexBufferManager.AllocatedBuffer buffer) {
    }

    public void pushDebugGroup(Supplier<String> labelGetter) {
    }

    public void popDebugGroup() {
    }

    public static DebugLabelManager create(GLCapabilities capabilities, boolean debugEnabled, Set<String> usedCapabilities) {
        if (debugEnabled) {
            if (capabilities.GL_KHR_debug && GlBackend.allowGlKhrDebug) {
                usedCapabilities.add("GL_KHR_debug");
                return new KHRDebugLabelManager();
            }
            if (capabilities.GL_EXT_debug_label && GlBackend.allowExtDebugLabel) {
                usedCapabilities.add("GL_EXT_debug_label");
                return new EXTDebugLabelManager();
            }
            LOGGER.warn("Debug labels unavailable: neither KHR_debug nor EXT_debug_label are supported");
        }
        return new NoOpDebugLabelManager();
    }

    public boolean isUsable() {
        return false;
    }
}

